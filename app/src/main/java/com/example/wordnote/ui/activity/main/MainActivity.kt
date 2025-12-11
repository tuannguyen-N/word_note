package com.example.wordnote.ui.activity.main

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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
    private var shakeAnimation: Animation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupPager()
        listenUIEvent()
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

    private fun setupPager() {
        val pages = listOf(
            PageItem(R.id.learn, R.string.good_morning, 0, R.drawable.image_teacher),
            PageItem(R.id.focus, R.string.focuss, 1, R.drawable.cat),
            PageItem(R.id.setting, R.string.note_settings, 2, R.drawable.cat)
        )
        binding.bottomNavView.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_UNLABELED
        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.apply {
            adapter = MainPagerAdapter(this@MainActivity)
        }

        binding.bottomNavView.setOnItemSelectedListener { item ->
            pages.find { it.menuId == item.itemId }?.let { page ->
                binding.tvApplicationBar.setText(page.title)
                binding.viewPager.setCurrentItem(page.index, true)
                binding.ivCat.loadGlideImage(page.imageRes)
            }
            true
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.bottomNavView.selectedItemId = pages[position].menuId
                binding.tvApplicationBar.setText(pages[position].title)

                if (position == 1)
                    binding.applicationBar.visibility = View.GONE
                else
                    binding.applicationBar.visibility = View.VISIBLE

                if (position == 0) {
                    startShakeForever()
                } else {
                    stopShake()
                }
            }
        })
    }

    private fun startShakeForever() {
        if (shakeAnimation == null) {
            shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake_infinite)
        }
        binding.ivCat.startAnimation(shakeAnimation)
    }

    private fun stopShake() {
        binding.ivCat.clearAnimation()
    }
}