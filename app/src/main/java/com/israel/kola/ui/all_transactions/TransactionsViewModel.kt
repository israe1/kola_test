package com.israel.kola.ui.all_transactions

import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.israel.kola.models.Transaction
import com.israel.kola.models.TransactionState
import java.util.regex.Pattern

class TransactionsViewModel: ViewModel() {
    val transactions = MutableLiveData<List<Transaction>>()
    val loading = MutableLiveData<Boolean>()

    fun fetchTransactions(activity: FragmentActivity){
        loading.value = true
        val messageUri = Uri.parse("content://sms/")
        val cr = activity.contentResolver
        val cursor = cr.query(messageUri, null, null, null, null)
        activity.startManagingCursor(cursor)

        val allTransactions = arrayListOf<Transaction>()
        val smsCount = cursor?.count
        Log.e("COUNT", smsCount.toString())
        if (cursor == null)return
        var i = 0

        if(cursor.moveToFirst()){
            try{
                while (i < smsCount!!){
                    val sender = cursor.getString(cursor.getColumnIndex("address"))
                    val body = cursor.getString(cursor.getColumnIndex("body")).trim()
                    if(sender.equals("OrangeMoney") && (body.contains("IDtransaction") || body.contains("Nodetransaction"))){
                        val matcherCredit = Pattern.compile("RC\\d+.\\d+.\\w+").matcher(body)
                        val matcherTransfer = Pattern.compile("PP\\d+.\\d+.\\w+").matcher(body)
                        val matcherWithdraw = Pattern.compile("CO\\d+.\\d+.\\w+").matcher(body)
                        val matcherDeposit = Pattern.compile("CI\\d+.\\d+.\\w+").matcher(body)

                        when{
                            matcherCredit.find() -> {
                                val transaction = msgToTransaction("Montantdelatransaction:\\d+FCFA", body, matcherCredit.group(), TransactionState.CREDIT)
                                transaction?.let {
                                    allTransactions.add(it)
                                }
                            }
                            matcherTransfer.find() -> {
                                val transaction = msgToTransaction("MontantTransaction :\\d+FCFA", body, matcherTransfer.group(), TransactionState.TRANSFER)
                                transaction?.let {
                                    allTransactions.add(it)
                                }
                            }
                            matcherWithdraw.find() -> {
                                val transaction = msgToTransaction("Montant:\\d+FCFA", body, matcherWithdraw.group(), TransactionState.WITHDRAW)
                                transaction?.let {
                                    allTransactions.add(it)
                                }
                            }
                            matcherDeposit.find() -> {
                                val transaction = msgToTransaction("Montantdetransaction:\\d+FCFA", body, matcherDeposit.group(), TransactionState.DEPOSIT)
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

            Log.e("Number", allTransactions.size.toString())
        }

        transactions.value = allTransactions
        loading.value = false
    }

    private fun msgToTransaction(regex: String, body: String, transactionId: String, state: TransactionState): Transaction?{
        val p = Pattern.compile(regex)
        val m = p.matcher(body)
        if (m.find()){
            val amountFCFA = m.group().split(":")[1].replace("FCFA", "")
            val amount = amountFCFA.toInt()
            return Transaction(transactionId, state, amount)
        }
        return null
    }
}