package com.israel.kola.ui.all_transactions

import android.net.Uri
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.israel.kola.data.local.Transaction
import com.israel.kola.data.local.TransactionDataSource
import com.israel.kola.utils.TransactionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(private var transactionDataSource: TransactionDataSource): ViewModel() {
    val transactions = MutableLiveData<List<Transaction>>()
    val loading = MutableLiveData<Boolean>()
    val total = MutableLiveData<Int>()
    val credit = MutableLiveData<Int>()
    val deposit = MutableLiveData<Int>()
    val transfer = MutableLiveData<Int>()
    val internet = MutableLiveData<Int>()
    val withdraw = MutableLiveData<Int>()



    init {
        transactionDataSource.getAllTransactions {
            viewModelScope.launch {
                it.collect {
                    transactions.value = it.sortedWith(compareByDescending{ t -> t.date})
                    getTotal()
                    getStateTotal(TransactionState.DEPOSIT, deposit)
                    getStateTotal(TransactionState.WITHDRAW, withdraw)
                    getStateTotal(TransactionState.CREDIT, credit)
                    getStateTotal(TransactionState.TRANSFER, transfer)
                    getStateTotal(TransactionState.INTERNET, internet)
                }
            }
        }
    }

    fun fetchTransactions(activity: FragmentActivity){
        val messageUri = Uri.parse("content://sms/inbox")
        val cr = activity.contentResolver
        val cursor = cr.query(messageUri, null, null, null, null)
        activity.startManagingCursor(cursor)

        val allTransactions = arrayListOf<Transaction>()
        val smsCount = cursor?.count
        Log.e("COUNT", smsCount.toString())
        if (cursor == null)return
        var i = 0
        var j = 0

        if(cursor.moveToFirst()){
            try{
                while (i < smsCount!!){
                    val sender = cursor.getString(cursor.getColumnIndex("address"))
                    var body = cursor.getString(cursor.getColumnIndex("body")).replace(" ", "")
                    val date = cursor.getString(cursor.getColumnIndex("date"))

                    if(sender.equals("OrangeMoney") && (body.contains("IDtransaction") || body.contains("Nodetransaction") || body.contains("Referencedetransaction"))){
                        j++
                        val matcherCredit = Pattern.compile("RC\\d+.\\d+.\\w+").matcher(body)
                        val matcherTransfer = Pattern.compile("PP\\d+.\\d+.\\w+").matcher(body)
                        val matcherWithdraw = Pattern.compile("CO\\d+.\\d+.\\w+").matcher(body)
                        val matcherDeposit = Pattern.compile("CI\\d+.\\d+.\\w+").matcher(body)

                        when{
                            matcherCredit.find() -> {
                                val transaction = msgToTransaction("Montantdelatransaction:\\d+FCFA", body, matcherCredit.group(), TransactionState.CREDIT, date)
                                transaction?.let {
                                    allTransactions.add(it)
                                }
                            }
                            matcherTransfer.find() -> {
                                var state = TransactionState.TRANSFER
                                if (body.contains("Details:")){
                                    state = TransactionState.DEPOSIT
                                }
                                val transaction = msgToTransaction("MontantTransaction:\\d+FCFA", body, matcherTransfer.group(), state, date)
                                transaction?.let {
                                    allTransactions.add(it)
                                }
                            }
                            matcherWithdraw.find() -> {
                                val transaction = msgToTransaction("Montant:\\d+FCFA", body, matcherWithdraw.group(), TransactionState.WITHDRAW, date)
                                transaction?.let {
                                    allTransactions.add(it)
                                }
                            }
                            matcherDeposit.find() -> {
                                var regex = ""
                                if (body.contains("Montantdetransaction")){
                                    regex = "Montantdetransaction:\\d+FCFA"
                                }else if (body.contains("Vousavezrecuavecsucces")){
                                    body = body.replace("Vousavezrecuavecsucces", "Vousavezrecuavecsucces:")
                                    regex = "Vousavezrecuavecsucces:\\d+FCFA"
                                }
                                val transaction = msgToTransaction(regex, body, matcherDeposit.group(), TransactionState.DEPOSIT, date)
                                transaction?.let {
                                    allTransactions.add(it)
                                }
                            }
                        }
                    }
                    i++
                    cursor.moveToNext()
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
        for (t in allTransactions){
            addTransaction(t)
        }
    }

    private fun msgToTransaction(regex: String, body: String, transactionId: String, state: TransactionState, date: String): Transaction?{
        if (body.contains("Vousavezrecuavecsucces")){
            Log.e("Body", body)
        }
        val p = Pattern.compile(regex)
        val m = p.matcher(body)
        if (m.find()){
            val amountFCFA = m.group().split(":")[1].replace("FCFA", "")
            val amount = amountFCFA.toInt()
            if (body.contains("Vousavezrecuavecsucces")){
                Log.e("AMOUNT", amount.toString())
            }

            return Transaction(
                transactionId,
                state.toString(),
                date,
                amount,
                getBalance(body.toLowerCase(Locale.ROOT))
            )
        }else{
            Log.e("STATE_ERROR", state.toString())
        }
        return null
    }

    private fun getBalance(body: String): String?{
        var result: String? = null
        var p = Pattern.compile("nouveausolde:\\d+ *(.+\\d)?fcfa?")
        var m = p.matcher(body)
        if (m.find()){
            result = m.group().split(":")[1].replace("fcfa", "")
        } else if(body.contains("votrenouveausoldeestde")){
            val newBody = body.replace("votrenouveausoldeestde", "votrenouveausoldeestde:")
            p = Pattern.compile("votrenouveausoldeestde:\\d+ *(.+\\d)?fcfa?")
            m = p.matcher(newBody)
            if (m.find()){
                result = m.group().split(":")[1].replace("fcfa", "")
            }
        }
        return result
    }

    private fun addTransaction(transaction: Transaction){
        transactionDataSource.addTransaction(transaction)
    }

    private fun getTotal(){
        val transactionList = transactions.value
        var totalAmount = 0
        for (t in transactionList!!){
            totalAmount += t.amount
        }
        total.value = totalAmount
    }

    private fun getStateTotal(state: TransactionState, liveData: MutableLiveData<Int>){
        val transactionList = transactions.value
        var total = 0
        for (t in transactionList!!) {
            if (t.state == state.toString()) {
                total += t.amount
            }
        }
        liveData.value = total
    }

    fun sortTransactionsByState(state: TransactionState, sort: Boolean): List<Transaction>{
        val list = arrayListOf<Transaction>()
        if (sort){
            for(t in transactions.value!!){
                if (t.state == state.toString()){
                    list.add(t)
                }
            }
        }else{
            list.addAll(transactions.value!!)
        }
        return list
    }
}