package com.israel.kola.ui.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewPagerAdapter(supportFragmentManager: FragmentManager): FragmentPagerAdapter(supportFragmentManager) {
     var fragmentList : ArrayList<Fragment> = arrayListOf()

    override fun getCount() = fragmentList.size


    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }
}