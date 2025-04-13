package com.pigmanms.personaldb.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class TabAdapter(act: FragmentActivity, private val frags: List<Fragment>) : FragmentStateAdapter(act) {
    override fun getItemCount() = frags.size
    override fun createFragment(pos: Int) = frags[pos]
}