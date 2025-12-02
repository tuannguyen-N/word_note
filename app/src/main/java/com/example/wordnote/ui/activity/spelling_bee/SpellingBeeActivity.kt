package com.example.wordnote.ui.activity.spelling_bee

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wordnote.R
import com.example.wordnote.adapter.MeaningAdapter
import com.example.wordnote.data.AppDatabase
import com.example.wordnote.data.mapper.toListMeaningData
import com.example.wordnote.data.repository.WordRepository
import com.example.wordnote.databinding.ActivitySpellingBeeBinding
import com.example.wordnote.domain.model.SpellingBeeState
import com.example.wordnote.domain.model.WordData
import com.example.wordnote.domain.usecase.LocalWordUseCase
import com.example.wordnote.manager.SpeakingManager
import com.example.wordnote.ui.activity.BaseActivity
import kotlinx.coroutines.launch

class SpellingBeeActivity :
    BaseActivity<ActivitySpellingBeeBinding>(ActivitySpellingBeeBinding::inflate) {
    companion object {
        fun goToActivity(context: Context, categoryId: Int) {
            val intent = Intent(context, SpellingBeeActivity::class.java)
            intent.putExtra("CATEGORY_ID", categoryId)
            context.startActivity(intent)
        }
    }

    private val speakingManager by lazy { SpeakingManager(this) }
    private val sbViewModel: SpellingBeeViewModel by viewModels {
        SpellingBeeViewModelFactory(
            LocalWordUseCase(
                WordRepository(
                    AppDatabase.getInstance(this).wordDao
                )
            ),
            speakingManager
        )
    }

    private val meaningAdapter = MeaningAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleInit()
        initRecyclerView()
        setupClickListeners()
        collectingViewModel()
    }

    private fun setupClickListeners() {
        binding.apply {
            btnBack.setOnClickListener { finish() }

            btnSpellIt.setOnClickListener {
                sbViewModel.onAction(SpellingBeeAction.OnSubmit(etSpelling.text.toString()))
            }

            btnSpeaking.setOnClickListener {
                sbViewModel.onAction(SpellingBeeAction.OnSpeakingCurrentWord)
            }

            etSpelling.setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    submit()
                    true
                } else false
            }

            btnShowAnswers.setOnClickListener {
                sbViewModel.onAction(SpellingBeeAction.OnShowAnswers)
            }
        }
    }

    private fun submit() {
        sbViewModel.onAction(SpellingBeeAction.OnSubmit(binding.etSpelling.text.toString()))
    }

    private fun collectingViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    sbViewModel.state.collect { state ->
                        enableSubmitButton(state.isSubmitEnabled)
                        setMeaningAdapter(state)
                        handleButtonAnswersUI(state.isShowAnswers)
                    }
                }

                launch {
                    sbViewModel.uiEvent.collect { event ->
                        when (event) {
                            is SpellingBeeUIEvent.OnCorrect -> showCorrectUI()
                            is SpellingBeeUIEvent.OnInCorrect -> showIncorrectUI()
                            is SpellingBeeUIEvent.OnNextWord -> showNextWordUI()
                            is SpellingBeeUIEvent.OnFinish -> showFinishUI()
                            is SpellingBeeUIEvent.ShowAnswersUI -> showAnswersUI(event.answer)
                        }
                    }
                }
            }
        }
    }

    private fun setMeaningAdapter(state: SpellingBeeState) {
        state.currentWord?.let {
            meaningAdapter.setItemList(it.toListMeaningData(3))
        }
    }

    private fun showAnswersUI(answer: String) {
        binding.etSpelling.setText(answer)
    }

    private fun handleButtonAnswersUI(isShowAnswer: Boolean) {
        binding.btnShowAnswers.visibility = if (isShowAnswer) View.VISIBLE else View.GONE
    }

    private fun showFinishUI() {
        showToast("End !!")
    }

    private fun enableSubmitButton(shouldEnable: Boolean) {
        binding.btnSpellIt.isEnabled = shouldEnable
    }

    private fun showNextWordUI() = with(binding) {
        etSpelling.text.clear()
        etSpelling.setBackgroundResource(R.drawable.bg_input_spelling)
        tvIncorrect.visibility = View.GONE
        tvCorrect.visibility = View.GONE
    }

    private fun showIncorrectUI() = with(binding) {
        etSpelling.setBackgroundResource(R.drawable.bg_input_spelling_incorrect)
        tvIncorrect.visibility = View.VISIBLE
    }

    private fun showCorrectUI() = with(binding) {
        etSpelling.setBackgroundResource(R.drawable.bg_input_spelling_correct)
        tvIncorrect.visibility = View.GONE
        tvCorrect.visibility = View.VISIBLE
    }

    private fun handleInit() {
        val categoryId = intent.getIntExtra("CATEGORY_ID", -1)
        if (categoryId != -1) {
            sbViewModel.onAction(SpellingBeeAction.InitWord(categoryId))
        } else {
            finish()
        }
    }

    private fun initRecyclerView() {
        binding.rvDefinitions.apply {
            adapter = meaningAdapter
            layoutManager = LinearLayoutManager(this@SpellingBeeActivity)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        speakingManager.destroy()
    }
}