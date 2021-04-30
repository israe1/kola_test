package com.israel.kola.ui.home

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Telephony
import android.telephony.SmsMessage
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.israel.kola.R
import com.israel.kola.ui.all_transactions.TransactionsViewModel
import com.israel.kola.ui.home.goal.GoalFragment
import com.israel.kola.ui.home.money.MoneyFragment
import com.israel.kola.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.*
import javax.inject.Inject

private const val SMS_REQUEST_CODE = 111
@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private val viewModel: TransactionsViewModel by viewModels()
    @Inject lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        buttonExit.setOnClickListener{
            auth.signOut().also {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }

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

        viewPager.setPagingEnabled(false)
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
        viewModel.fetchTransactions(this)
        val br = object: BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                for (sms: SmsMessage in Telephony.Sms.Intents.getMessagesFromIntent(intent)){
                    viewModel.fetchTransactions(this@HomeActivity)
                }
            }
        }

        registerReceiver(br, IntentFilter("android.provider.Telephony.SMS_RECEIVED"))
    }
}