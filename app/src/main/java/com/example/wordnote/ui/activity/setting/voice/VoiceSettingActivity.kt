package com.example.wordnote.ui.activity.setting.voice

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Typeface
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.example.wordnote.R
import com.example.wordnote.data.AppPreferences
import com.example.wordnote.databinding.ActivityVoiceSettingBinding
import com.example.wordnote.manager.SpeakingManager
import com.example.wordnote.ui.activity.BaseActivity
import com.example.wordnote.utils.loadGlideImage
import pl.droidsonroids.gif.GifDrawable

class VoiceSettingActivity :
    BaseActivity<ActivityVoiceSettingBinding>(ActivityVoiceSettingBinding::inflate) {
    private lateinit var speakingManager: SpeakingManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        speakingManager = SpeakingManager(this) { }
        setUpView()
        setOnClick()
    }

    private fun setOnClick() {
        binding.apply {
            btnBack.setOnClickListener { finish() }

            switchVoiceNotifying.setOnCheckedChangeListener { _, isChecked ->
                AppPreferences.canSpeakingVoiceNotification = isChecked
            }

            btnUKVoice.setOnClickListener {
                changeCountryVoice("uk")
                selectOnly(imgUK, tvUK)
                speakingManager.speak("For us there are only two possibilities")
            }

            btnUSVoice.setOnClickListener {
                changeCountryVoice("us")
                selectOnly(imgUS, tvUS)
                speakingManager.speak("For us there are only two possibilities")
            }

            btnKoreanVoice.setOnClickListener {
                changeCountryVoice("korean")
                selectOnly(imgKorean, tvKorean)
                speakingManager.speak("미안해, 미안해하지 마, 내가 초라해지잖아")
            }
        }
    }

    private fun setUpView() {
        binding.apply {
            switchVoiceNotifying.isChecked = AppPreferences.canSpeakingVoiceNotification
            imgUK.loadGlideImage(R.drawable.image_uk)
        }
        when (AppPreferences.codeVoice) {
            "uk" -> selectOnly(binding.imgUK, binding.tvUK)
            "us" -> selectOnly(binding.imgUS, binding.tvUS)
            "korean" -> selectOnly(binding.imgKorean, binding.tvKorean)
        }
    }

    private fun selectOnly(target: ImageView, tv: TextView) {
        val gifUS = binding.imgUS.drawable as? GifDrawable
        val gifKorean = binding.imgKorean.drawable as? GifDrawable

        gifUS?.stop()
        gifKorean?.stop()

        setGray(binding.imgUK)
        setGray(binding.imgUS)
        setGray(binding.imgKorean)

        binding.tvUK.setTypeface(null, Typeface.NORMAL)
        binding.tvUS.setTypeface(null, Typeface.NORMAL)
        binding.tvKorean.setTypeface(null, Typeface.NORMAL)

        when (target) {
            binding.imgUS -> gifUS?.start()
            binding.imgKorean -> gifKorean?.start()
        }

        setColor(target, tv)
    }

    private fun changeCountryVoice(code: String) {
        AppPreferences.codeVoice = code
    }

    private fun setGray(image: ImageView) {
        val matrix = ColorMatrix().apply { setSaturation(0f) }
        image.colorFilter = ColorMatrixColorFilter(matrix)
    }

    private fun setColor(image: ImageView, tv: TextView) {
        image.clearColorFilter()
        tv.setTypeface(null, Typeface.BOLD)
    }

    override fun onDestroy() {
        super.onDestroy()
        speakingManager.destroy()
    }
}