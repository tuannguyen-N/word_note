package com.example.wordnote.ui.activity.word

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
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
import com.example.wordnote.domain.model.WordData
import com.example.wordnote.domain.model.WordState
import com.example.wordnote.domain.usecase.LocalWordUseCase
import com.example.wordnote.ui.activity.BaseActivity
import com.example.wordnote.ui.activity.setting.note_alerts.NoteAlertSettingActivity
import com.example.wordnote.ui.dialog.AddWordDialog
import com.example.wordnote.ui.dialog.CatDialog
import com.example.wordnote.ui.dialog.DetailDefinitionDialog
import com.example.wordnote.ui.dialog.FullWordsBottomSheet
import com.example.wordnote.utils.SortType
import com.example.wordnote.utils.SpeakingManager
import com.example.wordnote.utils.shakeView
import kotlinx.coroutines.launch

class WordActivity : BaseActivity<ActivityWordBinding>(ActivityWordBinding::inflate) {
    companion object {
        fun goToActivity(context: Context, categoryId: Int) {
            val intent = Intent(context, WordActivity::class.java)
            intent.putExtra("CATEGORY_ID", categoryId)
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
        onDelete = {
            wordViewModel.onAction(WordAction.OnDeleteWord(it))
        },
        onClickTvWord = { word ->
            wordViewModel.onAction(WordAction.OnSpeakingWord(word))
        },
        onStartStudying = { word ->
            wordViewModel.onAction(WordAction.OnStartStudying(word))
        },
        onStopStudying = { wordId ->
            wordViewModel.onAction(WordAction.OnStopStudying(wordId))
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent()
        setUpRecyclerView()
        setOnClick()
        collectState()
        collectUIEvent()
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
                    }
                }
            }
        }
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
                    holder?.itemView?.let { shakeView(it) }
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
            onEnter = { word, level ->
                wordViewModel.onAction(WordAction.OnSaveWord(word, level))
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
}