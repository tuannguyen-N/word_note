package com.example.wordnote.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.wordnote.ui.fragment.setting.SettingFragment
import com.example.wordnote.ui.fragment.word_list.WordListFragment

class MainPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> WordListFragment()
            1 -> SettingFragment()
            else -> WordListFragment()
        }
    }
    override fun getItemCount(): Int = 2
}