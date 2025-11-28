package com.example.wordnote.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import com.example.wordnote.R
import com.example.wordnote.databinding.DialogCatBinding
import com.example.wordnote.utils.loadGlideImage

class CatDialog : BaseDialog<DialogCatBinding>(DialogCatBinding::inflate) {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setWindowAnimations(0)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        binding.iv.loadGlideImage(R.drawable.img_cat_looking)

        val window = dialog?.window ?: return
        window.setWindowAnimations(0)

        window.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val view = window.decorView
        view.alpha = 0f
        view.scaleX = 0.9f
        view.scaleY = 0.9f

        // fade-in + scale up
        view.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(500)
            .withEndAction {
                // fade-out
                view.animate()
                    .alpha(0f)
                    .setStartDelay(1500)
                    .setDuration(500)
                    .withEndAction { dismissAllowingStateLoss() }
                    .start()
            }
            .start()
    }
}
