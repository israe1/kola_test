package com.israel.kola.di

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.israel.kola.data.local.LocalDatabase
import com.israel.kola.data.local.Transaction
import com.israel.kola.data.local.TransactionDao
import com.israel.kola.models.TransactionState
import com.israel.kola.utils.doAsync
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule{
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): LocalDatabase{
        return Room.databaseBuilder(
            appContext,
            LocalDatabase::class.java,
            "kola.db"
        ).addCallback(object : RoomDatabase.Callback(){
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)


            }
        }).build()
    }

    @Provides
    fun provideTransactionDao(database: LocalDatabase): TransactionDao{
        return database.transactionDao()
    }

    private fun fetchTransactions(activity: FragmentActivity): List<Transaction>{
        val messageUri = Uri.parse("content://sms/")
        val cr = activity.contentResolver
        val cursor = cr.query(messageUri, null, null, null, null)
        activity.startManagingCursor(cursor)

        val allTransactions = arrayListOf<Transaction>()
        val smsCount = cursor?.count
        Log.e("COUNT", smsCount.toString())
        if (cursor == null)return arrayListOf()
        var i = 0
        var j = 0

        if(cursor.moveToFirst()){
            try{
                while (i < smsCount!!){
                    val sender = cursor.getString(cursor.getColumnIndex("address"))
                    val body = cursor.getString(cursor.getColumnIndex("body")).replace(" ", "")
                    if(sender.equals("OrangeMoney") && (body.contains("IDtransaction") || body.contains("Nodetransaction"))){
                        j++
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
                                val transaction = msgToTransaction("MontantTransaction:\\d+FCFA", body, matcherTransfer.group(), TransactionState.TRANSFER)
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
        }
        return allTransactions
    }

    private fun msgToTransaction(regex: String, body: String, transactionId: String, state: TransactionState): Transaction?{
        val p = Pattern.compile(regex)
        val m = p.matcher(body)
        if (m.find()){
            val amountFCFA = m.group().split(":")[1].replace("FCFA", "")
            val amount = amountFCFA.toInt()
            return Transaction(
                transactionId,
                state.toString(),
                amount
            )
        }else{
            Log.e("STATE_ERROR", state.toString())
        }
        return null
    }
}