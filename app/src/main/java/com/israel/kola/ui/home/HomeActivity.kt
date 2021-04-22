package com.israel.kola.ui.home

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.israel.kola.R
import com.israel.kola.models.Transaction
import com.israel.kola.models.TransactionState
import com.israel.kola.ui.home.goal.GoalFragment
import com.israel.kola.ui.home.money.MoneyFragment
import kotlinx.android.synthetic.main.activity_home.*
import java.util.regex.Pattern

private const val SMS_REQUEST_CODE = 111
class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupBottomNav()

        checkSmsPermission()
    }

    private fun checkSmsPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS), SMS_REQUEST_CODE)
        } else {
            receiveMessages()
        }
    }

    private fun setupBottomNav() {
        navigationBar.setOnItemSelectedListener { id ->
            when(id){
                R.id.nav_money -> viewPager.currentItem = 0
                R.id.nav_goal -> viewPager.currentItem = 1
            }
        }

        navigationBar.setItemSelected(R.id.nav_money)



        viewPager.adapter = ViewPagerAdapter(supportFragmentManager).apply {
            fragmentList = ArrayList<Fragment>().apply {
                add(MoneyFragment())
                add(GoalFragment())
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SMS_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
        {
            receiveMessages()
        } else {
            Toast.makeText(applicationContext, "Grant all permissions", Toast.LENGTH_LONG).show()
        }
    }

    private fun receiveMessages() {
        var br = object: BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                for (sms: SmsMessage in Telephony.Sms.Intents.getMessagesFromIntent(intent)){
                    Log.e("Test", "get")
                    Toast.makeText(applicationContext, sms.displayMessageBody, Toast.LENGTH_LONG).show()
                }
            }
        }

        registerReceiver(br, IntentFilter("android.provider.Telephony.SMS_RECEIVED"))
        //getAllMessages()
    }

    private fun getAllMessages(){
        val messageUri = Uri.parse("content://sms/")
        val cr = contentResolver
        val c = cr.query(messageUri, null, null, null, null)
        startManagingCursor(c)
        val allTransactions = arrayListOf<Transaction>()
        val totalSMS = c?.count
        if (c == null)return
        var i = 0
        var j = 0
        if (c.moveToFirst()){
            try {
                while (i < totalSMS!!){
                    val sender = c.getString(c.getColumnIndex("address"))
                    val body = c.getString(c.getColumnIndex("body")).replace(" ", "")
                    if (sender.equals("OrangeMoney") && (body.contains("IDtransaction") || body.contains("Nodetransaction"))){
                        val pCredit = Pattern.compile("RC\\d+.\\d+.\\w+")
                        val pTransfer = Pattern .compile("PP\\d+.\\d+.\\w+")
                        val pWithdraw = Pattern .compile("CO\\d+.\\d+.\\w+")
                        val pDeposit = Pattern .compile("CI\\d+.\\d+.\\w+")
                        val mCredit = pCredit.matcher(body)
                        val mTransfer = pTransfer.matcher(body)
                        val mWithdraw = pWithdraw.matcher(body)
                        val mDeposit = pDeposit.matcher(body)
                        when {
                            mCredit.find() -> {
                                val p = Pattern.compile("Montantdelatransaction:\\d+FCFA")
                                val m = p.matcher(body)
                                if (m.find()){
                                    val amountFCFA = m.group().split(":")[1].replace("FCFA", "")
                                    val amount = amountFCFA.toInt()
                                    allTransactions.add(Transaction(mCredit.group(), TransactionState.CREDIT, amount))
                                }
                            }
                            mTransfer.find() -> {
                                Log.e("Body", body)
                                val p = Pattern.compile("MontantTransaction :\\d+FCFA")
                                val m = p.matcher(body)
                                if (m.find()){
                                    val amountFCFA = m.group().split(":")[1].replace("FCFA", "")
                                    val amount = amountFCFA.toInt()
                                    allTransactions.add(Transaction(mTransfer.group(), TransactionState.TRANSFER, amount))
                                }
                            }
                            mWithdraw.find() -> {
                                val p = Pattern.compile("Montant:\\d+FCFA")
                                val m = p.matcher(body)
                                if (m.find()){
                                    val amountFCFA = m.group().split(":")[1].replace("FCFA", "")
                                    val amount = amountFCFA.toInt()
                                    allTransactions.add(Transaction(mWithdraw.group(), TransactionState.WITHDRAW, amount))
                                }


                            }
                            mDeposit.find() -> {
                                val p = Pattern.compile("Montantdetransaction:\\d+FCFA")
                                val m = p.matcher(body)
                                if (m.find()){
                                    val amountFCFA = m.group().split(":")[1].replace("FCFA", "")
                                    val amount = amountFCFA.toInt()
                                    allTransactions.add(Transaction(mDeposit.group(), TransactionState.DEPOSIT, amount))
                                }

                                j++
                            }
                        }



                    }
                    i++
                    c.moveToNext()
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
        Log.e("NUMBER", j.toString())
    }
}