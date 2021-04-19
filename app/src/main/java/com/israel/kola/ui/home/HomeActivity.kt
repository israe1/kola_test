package com.israel.kola.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.israel.kola.R
import com.israel.kola.ui.home.goal.GoalFragment
import com.israel.kola.ui.home.money.MoneyFragment
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

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
}