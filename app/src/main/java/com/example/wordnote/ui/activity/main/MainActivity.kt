package com.example.wordnote.ui.activity.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.wordnote.R
import com.example.wordnote.adapter.MainPagerAdapter
import com.example.wordnote.data.AppPreferences
import com.example.wordnote.databinding.ActivityMainBinding
import com.example.wordnote.domain.model.item.PageItem
import com.example.wordnote.ui.activity.BaseActivity
import com.example.wordnote.util.NotificationPermissionLauncher
import com.example.wordnote.util.PermissionResult
import com.google.android.material.navigation.NavigationBarView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory()
    }
    private lateinit var notificationPermissionLauncher: NotificationPermissionLauncher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupPager()
        listenUIEvent()
        initPermission()
        handleIntent()
    }

    private fun listenUIEvent() {
        lifecycleScope.launch(Dispatchers.Main) {
            mainViewModel.uiEvent.collect { event ->
                when (event) {
                    else -> {}
                }
            }
        }
    }

    private fun initPermission() {
        notificationPermissionLauncher = NotificationPermissionLauncher(
            caller = this,
            activityProvider = { this },
            onResult = { result ->
                when (result) {
                    PermissionResult.Denied -> showToast("Was not Granted")
                    PermissionResult.Granted -> {
                        showToast("Granted")
                        AppPreferences.canPostNotifications = true
                    }

                    PermissionResult.NeedOpenSettings -> showToast("Need Open Settings")
                    PermissionResult.ShowRationaleDialog -> showToast("Show Rationale Dialog")
                }
            }
        )
        if (!notificationPermissionLauncher.isPermissionGranted()) notificationPermissionLauncher.requestPermission()
    }

    private fun handleIntent() {
//        val wordFromNotification = intent.getStringExtra("WORD_FROM_NOTIFICATION")
//        if (!wordFromNotification.isNullOrEmpty()) {
//            mainViewModel.onAction(MainAction.SendWordFromNotification(wordFromNotification))
//        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setupPager() {
        val pages = listOf(
            PageItem(R.id.learn, "Mother day", 0),
            PageItem(R.id.setting, "Note Settings", 1)
        )

        binding.bottomNavView.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_UNLABELED

        binding.viewPager.apply {
            adapter = MainPagerAdapter(this@MainActivity)
        }

        binding.bottomNavView.setOnItemSelectedListener { item ->
            pages.find { it.menuId == item.itemId }?.let { page ->
                binding.tvApplicationBar.text = page.title
                binding.viewPager.setCurrentItem(page.index, true)
            }
            true
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.bottomNavView.selectedItemId = pages[position].menuId
                binding.tvApplicationBar.text = pages[position].title
            }
        })
    }
}