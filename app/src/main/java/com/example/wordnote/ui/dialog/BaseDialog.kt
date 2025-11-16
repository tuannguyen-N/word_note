package com.example.wordnote.ui.dialog

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.example.wordnote.R

open class BaseDialog<VBinding : ViewBinding>(private val bindingProvider: (LayoutInflater) -> VBinding) :
    DialogFragment() {
    protected lateinit var binding: VBinding
    var doOnDismiss: (() -> Unit)? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = bindingProvider(layoutInflater)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        hideSystemBars()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun getTheme(): Int {
        return R.style.AppBaseDialogTheme
    }
    fun hideSystemBars(behaviorSticky: Boolean = true) {
        val dialogWindow = dialog?.window ?: return
        WindowCompat.setDecorFitsSystemWindows(dialogWindow, false)
        val controller = WindowInsetsControllerCompat(dialogWindow, dialogWindow.decorView)
        controller.systemBarsBehavior = if (behaviorSticky)
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        else
            WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
        controller.hide(WindowInsetsCompat.Type.navigationBars())
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        doOnDismiss?.invoke()
    }
}