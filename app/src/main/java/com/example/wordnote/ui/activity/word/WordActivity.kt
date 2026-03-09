package com.example.wordnote.ui.activity.word

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wordnote.R
import com.example.wordnote.adapter.WordAdapter
import com.example.wordnote.alarm.AlarmScheduler
import com.example.wordnote.data.AppDatabase
import com.example.wordnote.data.AppPreferences
import com.example.wordnote.data.api.RetrofitInstance
import com.example.wordnote.data.repository.CategoryRepository
import com.example.wordnote.data.repository.WordRepository
import com.example.wordnote.databinding.ActivityWordBinding
import com.example.wordnote.domain.model.CategoryData
import com.example.wordnote.domain.model.WordData
import com.example.wordnote.domain.model.state.WordState
import com.example.wordnote.domain.usecase.LocalWordUseCase
import com.example.wordnote.manager.SpeakingManager
import com.example.wordnote.ui.activity.BaseActivity
import com.example.wordnote.ui.activity.setting.note_alerts.NoteAlertSettingActivity
import com.example.wordnote.ui.dialog.AddWordDialog
import com.example.wordnote.ui.dialog.DetailDefinitionDialog
import com.example.wordnote.ui.dialog.ExistWordDialog
import com.example.wordnote.ui.dialog.FullWordsBottomSheet
import com.example.wordnote.utils.NotificationPermissionLauncher
import com.example.wordnote.utils.PermissionResult
import com.example.wordnote.domain.model.SortType
import com.example.wordnote.domain.usecase.LocalCategoryUseCase
import com.example.wordnote.ui.dialog.CurrentCategoryBottomSheet
import com.example.wordnote.utils.Utils
import com.example.wordnote.utils.followKeyboardAndEdge
import com.example.wordnote.utils.formatDate
import com.example.wordnote.utils.onTextChanged
import com.example.wordnote.utils.setSafeOnClickListener
import com.example.wordnote.utils.showKeyboard
import com.example.wordnote.utils.upperFirstChar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WordActivity : BaseActivity<ActivityWordBinding>(ActivityWordBinding::inflate) {
    companion object {
        fun goToActivity(context: Context, category: CategoryData) {
            val intent = Intent(context, WordActivity::class.java)
            intent.putExtra("CATEGORY_ID", category.id)
            context.startActivity(intent)
        }
    }

    private lateinit var notificationPermissionLauncher: NotificationPermissionLauncher
    private lateinit var backCallback: OnBackPressedCallback

    private val wordViewModel: WordViewModel by viewModels {
        WordViewModelFactory(
            LocalWordUseCase(
                WordRepository(
                    AppDatabase.getInstance(this).wordDao,
                    AppDatabase.getInstance(this).wordCategoryDao,
                    RetrofitInstance.api
                ),
                CategoryRepository(AppDatabase.getInstance(this).categoryDao),
                AlarmScheduler(this)
            ),
            LocalCategoryUseCase(
                CategoryRepository(
                    AppDatabase.getInstance(this).categoryDao
                )
            ),
            SpeakingManager(this),
        )
    }

    private val categories = mutableListOf<CategoryData>()

    private val wordAdapter = WordAdapter(
        onAction = {
            wordViewModel.onAction(WordAction.OnOpenDetailWordDialog(it))
        },
        onSpeaking = { word ->
            wordViewModel.onAction(WordAction.OnSpeakingWord(word))
        },
        onStartStudying = { word ->
            if (!notificationPermissionLauncher.isPermissionGranted()) {
                notificationPermissionLauncher.requestPermission()
            } else {
                wordViewModel.onAction(WordAction.OnStartStudying(word))
            }
        },
        onStopStudying = { wordId ->
            wordViewModel.onAction(WordAction.OnStopStudying(wordId))
        },
        onDeleteWord = { wordId ->
            wordViewModel.onAction(WordAction.OnDeleteWord(wordId))
        },
        onChangeCategory = { wordId ->
            showBottomSheetCurrentCategory(wordId)
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initNotificationPermissionLauncher()
        setUpView()
        handleIntent()
        collectState()
        setUpRecyclerView()
        setOnClick()
        collectUIEvent()
        setUpBottomSheet()
        setUpMotionLayout()
        setUpIme()
        setupBackCallback()
    }

    private fun setUpBottomSheet() {
//        val behavior = BottomSheetBehavior.from(binding.bottomSheet)
//
//        behavior.isFitToContents = false
//        behavior.halfExpandedRatio = 0.75f
//        behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
//
//        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
//            override fun onStateChanged(bottomSheet: View, newState: Int) {
//                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
//                    behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
//                }
//            }
//
//            override fun onSlide(bottomSheet: View, slideOffset: Float) {
//            }
//        })
    }

    private fun setUpView() {
//        binding.root.followKeyboardAndEdge(
//            target = binding.containerSearch,
//            topCover = mBaseBinding.topBarCoverView
//        )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnClick() {
        binding.apply {
            btnBack.setOnClickListener { handleClickBtnBack() }

            btnAddWord.setSafeOnClickListener {
                wordViewModel.onAction(WordAction.OnShowAddWordDialog)
            }

            recyclerView.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    etSearch.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(etSearch.windowToken, 0)
                }
                false
            }

            etSearch.addTextChangedListener { editable ->
                val query = editable.toString().trim()
                wordViewModel.onAction(WordAction.OnSearchWord(query))
            }

            etCategoryName.setSafeOnClickListener {
                if (motionLayout.currentState == R.id.start) {
                    motionLayout.transitionToEnd()
                }
            }

            btnFavorite.setSafeOnClickListener {
                wordViewModel.onAction(WordAction.ToggleFavorite)
                btnFavorite.isSelected = !btnFavorite.isSelected
            }
        }
    }

    private fun ActivityWordBinding.handleClickBtnBack() {
        if (motionLayout.currentState == R.id.end) {
            motionLayout.transitionToStart()
            updateCategoryName()
        } else
            finish()
    }

    private fun updateCategoryName() {
        val categoryName = binding.etCategoryName.text.toString().trim()
        val description = binding.etCategoryNote.text.toString().trim()
        if (categoryName.isNotEmpty()) {
            wordViewModel.onAction(WordAction.OnUpdateCategoryName(categoryName, description))
        }
    }

    private fun collectState() {
        /* shouldn't use that because it can cause memory leak (when fragment is destroyed, the job is not cancel)*/
//        lifecycleScope.launch(Dispatchers.Main) { ... }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    wordViewModel.state.collect { wordState ->
                        loadingUI(wordState)
                        wordAdapter.setItemList(wordState.words)
                    }
                }

                launch {
                    wordViewModel.currentCategory.collect { category ->
                        updateUICategory(category ?: CategoryData())
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUICategory(category: CategoryData) {
        binding.btnFavorite.isSelected = category.isFavorite
        binding.etCategoryName.setText(category.name.upperFirstChar())
        binding.etCategoryNote.setText(category.description.upperFirstChar())
        binding.layoutDetailCategoryInformation.apply {
            tvWordNumber.text = "Word number: ${category.wordNumber} words"
            tvTimeCreated.text = "Created: ${category.createAt.formatDate()}"
        }
    }

    private fun loadingUI(wordState: WordState) {
        binding.progressBar.visibility = if (wordState.isLoading) View.VISIBLE else View.GONE
        if (wordState.words.isEmpty() && !wordState.isLoading) {
            binding.viewNoData.visibility = View.VISIBLE
        } else {
            binding.viewNoData.visibility = View.GONE
        }
    }

    private fun collectUIEvent() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                wordViewModel.uiEvent.collect { event ->
                    when (event) {
                        is WordUIEvent.ShowAddWordDialog -> showAddWordDialog()
                        is WordUIEvent.ShowDetailWordDialog -> showDetailWordDialog(event.word)
                        is WordUIEvent.ShowToast -> showToast(event.message)
                        is WordUIEvent.HideLevelContainer -> Unit //hideLevelContainer()
                        is WordUIEvent.ScrollToExistWord -> scrollToExistWord(event.word)
                        is WordUIEvent.ShowFullStudyingWords -> showFullStudyingWordsBottomSheet()
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
        wordViewModel.onAction(WordAction.InitCategory(category.id!!))

        binding.etCategoryName.setText(category.name.replaceFirstChar { it.uppercase() })
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
        wordViewModel.onAction(WordAction.InitCategory(categoryId))
    }

    private fun setUpRecyclerView() {
        binding.recyclerView.apply {
            adapter = wordAdapter
            layoutManager = LinearLayoutManager(this@WordActivity)
        }
//
//        binding.rvCategory.apply {
//            categoryAdapter = CategoryInWordsAdapter(
//                currentCategoryId = intent.getIntExtra("CATEGORY_ID", -1)
//            )
//            adapter = categoryAdapter
//            layoutManager =
//                LinearLayoutManager(this@WordActivity, LinearLayoutManager.HORIZONTAL, false)
//        }
    }

    private fun initNotificationPermissionLauncher() {
        notificationPermissionLauncher = NotificationPermissionLauncher(
            caller = this,
            activityProvider = { this },
            onResult = { result ->
                when (result) {
                    PermissionResult.Denied -> showToast("Was not Granted")

                    PermissionResult.Granted -> {
                        showToast("Granted")
                        AppPreferences.canPostNotifications = true
                    }

                    PermissionResult.NeedOpenSettings -> showToast("Need Open Settings to show notification")

                    PermissionResult.ShowRationaleDialog -> showToast("Show Rationale Dialog")
                }
            }
        )
    }

    private fun showBottomSheetCurrentCategory(wordId: Int) {
        val bottomSheet = CurrentCategoryBottomSheet(
            currentCategory = intent.getIntExtra("CATEGORY_ID", -1),
            categories = categories,
            onClickItem = { categoryId ->
                wordViewModel.onAction(WordAction.OnChangeCategory(wordId, categoryId))
            }
        )
        bottomSheet.show(supportFragmentManager, "CurrentCategoryBottomSheet")
    }

    private fun setUpMotionLayout() {
        binding.apply {
            motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
                override fun onTransitionCompleted(
                    motionLayout: MotionLayout,
                    currentId: Int
                ) {
                    when (currentId) {
                        R.id.end -> {
                            onBlockClick()
                            focusEditText()
                            backCallback.isEnabled = true
                        }

                        R.id.start -> {
                            onUnClockClick()
                            unfocusEditText()
                            backCallback.isEnabled = false
                        }
                    }
                }

                override fun onTransitionStarted(
                    motionLayout: MotionLayout, startId: Int, endId: Int
                ) {
                }

                override fun onTransitionChange(
                    motionLayout: MotionLayout,
                    startId: Int,
                    endId: Int,
                    progress: Float
                ) {
                }

                override fun onTransitionTrigger(
                    motionLayout: MotionLayout,
                    triggerId: Int,
                    positive: Boolean,
                    progress: Float
                ) {
                }
            })
        }
    }

    private fun focusEditText() {
        binding.etCategoryName.apply {
            isFocusableInTouchMode = true
            isFocusable = true
            setSingleLine(false)
            maxLines = 2
            ellipsize = null
            requestFocus()

            post {
                showKeyboard(this@WordActivity, this)
            }
        }
    }


    private fun unfocusEditText() {
        binding.etCategoryName.apply {
            clearFocus()
            isFocusable = false
            isFocusableInTouchMode = false
            setSingleLine(true)
            maxLines = 1
            ellipsize = TextUtils.TruncateAt.END
        }
        hideKeyboard()
    }

    private fun onBlockClick() {
        binding.hideView.apply {
            isClickable = true
            isFocusable = true
        }
    }

    private fun onUnClockClick() {
        binding.hideView.apply {
            isClickable = false
            isFocusable = false
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun setUpIme() {
        binding.etCategoryName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.etCategoryName.clearFocus()
                hideKeyboard()
                true
            } else false
        }
    }

    private fun setupBackCallback() {
        backCallback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                binding.motionLayout.transitionToStart()
                updateCategoryName()
            }
        }

        this
            .onBackPressedDispatcher
            .addCallback(this, backCallback)
    }
}
