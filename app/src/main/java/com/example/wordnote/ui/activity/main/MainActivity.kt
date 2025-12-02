package com.example.wordnote.ui.activity.main

import android.os.Bundle
import android.view.View
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
import com.example.wordnote.utils.NotificationPermissionLauncher
import com.example.wordnote.utils.PermissionResult
import com.example.wordnote.utils.loadGlideImage
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
        setUpView()
        setupPager()
        listenUIEvent()
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
            PageItem(R.id.learn, "Your Category", 0),
            PageItem(R.id.focus, "", 1),
            PageItem(R.id.setting, "Note Settings", 2)
        )
        binding.bottomNavView.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_UNLABELED
        binding.viewPager.isUserInputEnabled = false
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
                if (position == 1)
                    binding.applicationBar.visibility = View.GONE
                else
                    binding.applicationBar.visibility = View.VISIBLE
            }
        })
    }

    private fun setUpView(){
        binding.apply {
            ivCat.loadGlideImage(R.drawable.cat)
        }
    }
}