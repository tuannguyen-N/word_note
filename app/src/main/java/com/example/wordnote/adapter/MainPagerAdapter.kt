package com.example.wordnote.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.wordnote.ui.fragment.focus.FocusFragment
import com.example.wordnote.ui.fragment.setting.SettingFragment
import com.example.wordnote.ui.fragment.category.CategoryFragment

class MainPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CategoryFragment()
            1 -> FocusFragment()
            2 -> SettingFragment()
            else -> CategoryFragment()
        }
    }

    override fun getItemCount(): Int = 3
}