package com.example.wordnote.ui.activity.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.wordnote.R
import com.example.wordnote.adapter.MainPagerAdapter
import com.example.wordnote.databinding.ActivityMainBinding
import com.example.wordnote.ui.activity.BaseActivity
import com.google.android.material.navigation.NavigationBarView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupPager()
        listenUIEvent()
    }

    private fun listenUIEvent() {
        lifecycleScope.launch(Dispatchers.Main) {
            mainViewModel.uiEvent.collect { event ->
            }
        }
    }

    private fun setupPager() {
        binding.bottomNavView.labelVisibilityMode =
            NavigationBarView.LABEL_VISIBILITY_UNLABELED

        binding.viewPager.apply {
            adapter = MainPagerAdapter(this@MainActivity)
            isUserInputEnabled = false
        }
        binding.bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.learn -> binding.viewPager.setCurrentItem(0, true)
                R.id.setting -> binding.viewPager.setCurrentItem(1, true)
            }
            true
        }
    }
}