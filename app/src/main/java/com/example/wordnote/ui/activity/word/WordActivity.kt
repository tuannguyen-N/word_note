package com.example.wordnote.ui.activity.word

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wordnote.adapter.WordAdapter
import com.example.wordnote.alarm.AlarmScheduler
import com.example.wordnote.data.AppDatabase
import com.example.wordnote.data.api.RetrofitInstance
import com.example.wordnote.data.repository.WordRepository
import com.example.wordnote.databinding.ActivityWordBinding
import com.example.wordnote.domain.model.CategoryData
import com.example.wordnote.domain.model.WordData
import com.example.wordnote.domain.model.WordState
import com.example.wordnote.domain.usecase.LocalWordUseCase
import com.example.wordnote.ui.activity.BaseActivity
import com.example.wordnote.ui.activity.setting.note_alerts.NoteAlertSettingActivity
import com.example.wordnote.ui.dialog.AddWordDialog
import com.example.wordnote.ui.dialog.DetailDefinitionDialog
import com.example.wordnote.ui.dialog.FullWordsBottomSheet
import com.example.wordnote.utils.SortType
import com.example.wordnote.manager.SpeakingManager
import com.example.wordnote.ui.dialog.ExistWordDialog
import com.example.wordnote.utils.Utils
import com.example.wordnote.utils.animateDown
import com.example.wordnote.utils.animateUp
import com.example.wordnote.utils.followKeyboard
import kotlinx.coroutines.launch

class WordActivity : BaseActivity<ActivityWordBinding>(ActivityWordBinding::inflate) {
    companion object {
        fun goToActivity(context: Context, category: CategoryData) {
            val intent = Intent(context, WordActivity::class.java)
            intent.putExtra("CATEGORY_ID", category.id)
            intent.putExtra("CATEGORY_NAME", category.name)
            context.startActivity(intent)
        }
    }

    private val wordViewModel: WordViewModel by viewModels {
        WordViewModelFactory(
            LocalWordUseCase(
                WordRepository(
                    AppDatabase.getInstance(this).wordDao,
                    AppDatabase.getInstance(this).wordCategoryDao,
                    RetrofitInstance.api
                ),
                AlarmScheduler(this)
            ),
            SpeakingManager(this),
        )
    }

    private val wordAdapter = WordAdapter(
        onAction = {
            wordViewModel.onAction(WordAction.OnOpenDetailWordDialog(it))
        },
        onClickTvWord = { word ->
            wordViewModel.onAction(WordAction.OnSpeakingWord(word))
        },
        onStartStudying = { word ->
            wordViewModel.onAction(WordAction.OnStartStudying(word))
        },
        onStopStudying = { wordId ->
            wordViewModel.onAction(WordAction.OnStopStudying(wordId))
        },
        onSelectedMode = {
            if (it) showDeleteButton()
            else hideDeleteButton()
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpView()
        handleIntent()
        setUpRecyclerView()
        setOnClick()
        collectState()
        collectUIEvent()
        setupHideKeyboardOnTouchOutside()
    }

    private fun setUpView() {
//        binding.root.followKeyboard(binding.containerSearch)
        binding.tvNameCategory.text =
            intent.getStringExtra("CATEGORY_NAME").toString().replaceFirstChar { it.uppercase() }
    }

    private fun setOnClick() {
        binding.apply {
            btnBack.setOnClickListener { finish() }

            btnAddWord.setOnClickListener {
                wordViewModel.onAction(WordAction.OnShowAddWordDialog)
            }

            btnSort.setOnClickListener {
                levelContainer.root.visibility =
                    if (levelContainer.root.isVisible) View.GONE else View.VISIBLE
            }

            btnDelete.setOnClickListener {
                wordViewModel.onAction(WordAction.OnDeleteWords(wordAdapter.getTickedItem()))
            }

            btnSearch.setOnClickListener {
                binding.containerSearch.visibility = View.VISIBLE
                binding.etSearch.requestFocus()
                showKeyboard(binding.etSearch)
            }

            setupLevelButtons()
        }
    }

    private fun setupLevelButtons() {
        val levelButtons = listOf(
            binding.levelContainer.btnLevel1 to 1,
            binding.levelContainer.btnLevel2 to 2,
            binding.levelContainer.btnLevel3 to 3,
        )

        levelButtons.forEach { (button, level) ->
            button.setOnClickListener {
                wordViewModel.onAction(WordAction.OnSortWords(SortType.LEVEL(level)))
            }
        }
    }

    private fun collectState() {
        /* shouldn't use that because it can cause memory leak (when fragment is destroyed, the job is not cancel)*/
//        lifecycleScope.launch(Dispatchers.Main) { ... }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                wordViewModel.state.collect { wordState ->
                    loadingUI(wordState)
                    wordAdapter.setItemList(wordState.words)
                    onSelectedLevel(wordState.selectedLevel)
                }
            }
        }
    }

    private fun onSelectedLevel(selectedLevel: Int?) {
        val levelButtons = mapOf(
            binding.levelContainer.btnLevel1 to 1,
            binding.levelContainer.btnLevel2 to 2,
            binding.levelContainer.btnLevel3 to 3,
        )
        levelButtons.forEach { (button, level) ->
            button.alpha = if (selectedLevel == level) 1f else 0.3f
        }
    }

    private fun loadingUI(wordState: WordState) {
        binding.viewNoData.visibility =
            if (wordState.words.isEmpty() && !wordState.isLoading) View.VISIBLE else View.GONE
        binding.progressBar.visibility = if (wordState.isLoading) View.VISIBLE else View.GONE
    }

    private fun collectUIEvent() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                wordViewModel.uiEvent.collect { event ->
                    when (event) {
                        is WordUIEvent.ShowAddWordDialog -> showAddWordDialog()
                        is WordUIEvent.ShowDetailWordDialog -> showDetailWordDialog(event.word)
                        is WordUIEvent.ShowToast -> showToast(event.message)
                        is WordUIEvent.HideLevelContainer -> hideLevelContainer()
                        is WordUIEvent.ScrollToExistWord -> scrollToExistWord(event.word)
                        is WordUIEvent.ShowFullStudyingWords -> showFullStudyingWordsBottomSheet()
                        is WordUIEvent.HideDeleteButton -> hideDeleteButton()
                        is WordUIEvent.ShowExistWordDialog -> showExistWordDialog(event.category)
                    }
                }
            }
        }
    }

    private fun showExistWordDialog(category: CategoryData) {
        val dialog = ExistWordDialog(
            category = category,
            onGoToThisList = {
                onChangeList(it)
            }
        )
        dialog.show(supportFragmentManager, "ExistWordDialog")
    }

    private fun onChangeList(category: CategoryData) {
        wordViewModel.setCategoryId(category.id!!)
        binding.tvNameCategory.text = category.name.replaceFirstChar { it.uppercase() }
    }

    private fun hideDeleteButton() {
        binding.btnDelete.animateDown()
    }

    private fun showDeleteButton() {
        binding.btnDelete.animateUp()
    }

    private fun showFullStudyingWordsBottomSheet() {
        val bottomSheet = FullWordsBottomSheet(
            onGoToSetting = {
                startActivity(Intent(this, NoteAlertSettingActivity::class.java))
            }
        )
        bottomSheet.show(supportFragmentManager, "FullWordsBottomSheet")
    }

    fun scrollToExistWord(word: String) {
        binding.recyclerView.post {
            val index =
                wordAdapter.itemList.indexOfFirst { it.word.equals(word, ignoreCase = true) }
            if (index != -1) {
                binding.recyclerView.smoothScrollToPosition(index)

                binding.recyclerView.postDelayed({
                    val holder = binding.recyclerView.findViewHolderForAdapterPosition(index)
                    holder?.itemView?.let { Utils.shakeView(it) }
                }, 400)
            }
        }
    }

    private fun hideLevelContainer() {
        binding.levelContainer.root.visibility = View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showAddWordDialog() {
        val dialog = AddWordDialog(
            onEnter = { word ->
                wordViewModel.onAction(WordAction.OnSaveWord(word))
            }
        )
        supportFragmentManager.commit(allowStateLoss = false) {
            add(dialog, "AddWordDialog")
        }
    }

    private fun showDetailWordDialog(word: WordData) {
        val dialog = DetailDefinitionDialog(word) { newWord ->
            wordViewModel.onAction(WordAction.OnUpdateNote(newWord))
        }
        supportFragmentManager.commit(allowStateLoss = false) {
            add(dialog, "DetailDefinitionDialog")
        }
    }

    private fun handleIntent() {
        val categoryId = intent.getIntExtra("CATEGORY_ID", -1)
        wordViewModel.setCategoryId(categoryId)
    }

    private fun setUpRecyclerView() {
        binding.recyclerView.apply {
            adapter = wordAdapter
            layoutManager = LinearLayoutManager(this@WordActivity)
        }
    }

    private fun showKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupHideKeyboardOnTouchOutside() {
        binding.root.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                hideKeyboard()
            }
            false
        }

        binding.containerSearch.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                return@setOnTouchListener true
            }
            false
        }
    }

    private fun hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            view.clearFocus()
        }
    }
}