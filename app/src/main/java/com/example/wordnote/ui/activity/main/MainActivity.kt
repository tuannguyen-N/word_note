package com.example.wordnote.ui.activity.main

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.example.wordnote.R
import com.example.wordnote.adapter.MainPagerAdapter
import com.example.wordnote.databinding.ActivityMainBinding
import com.example.wordnote.domain.model.item.PageItem
import com.example.wordnote.ui.activity.BaseActivity
import com.example.wordnote.utils.loadGlideImage
import com.example.wordnote.utils.setSafeOnClickListener
import com.google.android.material.navigation.NavigationBarView
import kotlinx.coroutines.launch

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory()
    }
    private var shakeAnimator: ObjectAnimator? = null
    val pages = listOf(
        PageItem(R.id.learn, R.string.good_morning, 0, R.drawable.cat),
        PageItem(R.id.focus, R.string.focuss, 1, R.drawable.cat),
        PageItem(R.id.setting, R.string.note_settings, 2, R.drawable.cat)
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupPager()
        listenUIEvent()
        collectMainUIState()
        setOnClickListener()
    }

    private fun collectMainUIState(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
            }
        }
    }

    private fun listenUIEvent() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                mainViewModel.uiEvent.collect { event ->
                    when (event) {
                        is MainViewUIEvent.ChangeDeleteMode -> changeDeleteMode(event.isDeleteMode)
                        is MainViewUIEvent.RequestDelete -> Unit
                    }
                }
            }
        }
    }

    private fun changeDeleteMode(isDeleteMode: Boolean){
        binding.bottomNavView.visibility = if (isDeleteMode) View.GONE else View.VISIBLE
        binding.btnDelete.visibility = if (isDeleteMode) View.VISIBLE else View.GONE
    }

    private fun setupPager() {

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
                renderPage(position)
            }
            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    if (binding.viewPager.currentItem == 0) {
                        startShakeForever()
                    } else {
                        stopShake()
                    }
                }
            }
        })
    }

    private fun renderPage(position: Int) = with(binding) {
        tvApplicationBar.setText(pages[position].title)

        when (position) {
            0 -> {
                applicationBar.visibility = View.VISIBLE
                tvHaveYouReview.visibility = View.VISIBLE
            }
            1 -> {
                applicationBar.visibility = View.GONE
            }
            else -> {
                applicationBar.visibility = View.VISIBLE
                tvHaveYouReview.visibility = View.GONE
            }
        }
    }

    private fun startShakeForever() {
        if (shakeAnimator?.isRunning == true) return
        if (shakeAnimator == null) {
            shakeAnimator = ObjectAnimator.ofFloat(
                binding.ivCat,
                View.TRANSLATION_X,
                -10f, 10f
            ).apply {
                duration = 500
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.REVERSE
            }
        }
        shakeAnimator?.start()
    }

    private fun stopShake() {
        shakeAnimator?.cancel()
        binding.ivCat.translationX = 0f
    }

    private fun setOnClickListener(){
        binding.apply {
            btnDelete.setSafeOnClickListener {
                mainViewModel.onAction(MainAction.RequestDelete)
            }
        }
    }
}