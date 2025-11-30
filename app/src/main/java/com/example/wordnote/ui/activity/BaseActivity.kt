package com.example.wordnote.ui.activity

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.activity.SystemBarStyle
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.viewbinding.ViewBinding
import com.example.wordnote.databinding.ActivityBaseBinding

open class BaseActivity<VBinding : ViewBinding>(private val bindingProvider: (LayoutInflater) -> VBinding) :
    AppCompatActivity() {
    lateinit var binding: VBinding
    lateinit var mBaseBinding: ActivityBaseBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)
        setupContentView()
//        setupSystemBarColor()
        setupEdge()
        hideSystemBars()
    }

    fun hideSystemBars(behaviorSticky: Boolean = true) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.systemBarsBehavior = if (behaviorSticky)
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        else
            WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
        controller.hide(WindowInsetsCompat.Type.navigationBars())
    }

    fun showSystemBars() {
        WindowInsetsControllerCompat(window, window.decorView)
            .show(WindowInsetsCompat.Type.systemBars())
    }

    private fun setupContentView() {
        binding = bindingProvider(layoutInflater)
        val baseBinding = ActivityBaseBinding.inflate(layoutInflater)
        baseBinding.baseMainContentContainer.removeAllViews()
        baseBinding.baseMainContentContainer.addView(binding.root)
        mBaseBinding = baseBinding
        setContentView(baseBinding.root)
    }

    private fun setupSystemBarColor() {
        binding.apply {
            WindowCompat.getInsetsController(window, root).isAppearanceLightStatusBars =
                isLightStatusBar()
            WindowCompat.getInsetsController(window, root).isAppearanceLightNavigationBars =
                isLightStatusBar()
        }
    }

    open fun isLightStatusBar(): Boolean {
        return false
    }

    protected open  fun setupEdge() {
        binding.apply {
            ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                val navigationBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())

                onInsets(systemBars, navigationBars)
                insets
            }
        }
    }

    fun setTopBarCoverViewColor(colorId: Int) {
        mBaseBinding.topBarCoverView.setBackgroundColor(getColor(colorId))
    }

    open fun onInsets(systemBars: Insets, navigationBars: Insets) {
        mBaseBinding.topBarCoverView.updateLayoutParams { height = systemBars.top }
        mBaseBinding.bottomBarCoverView.updateLayoutParams { height = 0 }
    }
    protected open fun hideSystemNavigationBar(){
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.R){
            val window = this.window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT

            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        }else{
            val windowInsetsController =  WindowInsetsControllerCompat(window, window.decorView)
            windowInsetsController.isAppearanceLightNavigationBars = true
            windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())
            windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, 0, 0, 0)
            WindowInsetsCompat.CONSUMED
        }
    }
}