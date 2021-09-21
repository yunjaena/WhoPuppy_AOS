package com.dicelab.whopuppy.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.core.net.toUri
import com.dicelab.whopuppy.R
import com.dicelab.whopuppy.adapter.ImageUploadAdapter
import com.dicelab.whopuppy.base.activity.ViewBindingActivity
import com.dicelab.whopuppy.data.Area
import com.dicelab.whopuppy.data.entity.ArticleItem
import com.dicelab.whopuppy.databinding.ActivityArticleEditBinding
import com.dicelab.whopuppy.dialog.AreaSelectorDialog
import com.dicelab.whopuppy.fragment.ArticleFragment
import com.dicelab.whopuppy.util.UpdateEvent
import com.dicelab.whopuppy.util.getEmojiFilter
import com.dicelab.whopuppy.util.showAreaSelectorDialog
import com.dicelab.whopuppy.util.showToast
import com.dicelab.whopuppy.viewmodel.ArticleViewModel
import com.dicelab.whopuppy.viewmodel.Content
import com.dicelab.whopuppy.viewmodel.Image
import com.dicelab.whopuppy.viewmodel.Region
import com.dicelab.whopuppy.viewmodel.Title
import gun0912.tedimagepicker.builder.TedImagePicker
import org.greenrobot.eventbus.EventBus
import org.koin.androidx.viewmodel.ext.android.viewModel

class ArticleEditActivity : ViewBindingActivity<ActivityArticleEditBinding>() {
    override val layoutId: Int = R.layout.activity_article_edit
    private val articleViewModel: ArticleViewModel by viewModel()
    private lateinit var imageUploadAdapter: ImageUploadAdapter
    private var boardId: Long = 0
    private var articleId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        articleId = intent.getLongExtra(EXTRA_ARTICLE_ID, 0)
        useDefaultLoading(articleViewModel)
        initAreaSelectFragmentResultListener()
        initView()
        initObserver()
        articleViewModel.getArticle(articleId)
    }

    private fun initView() {
        initUploadButton()
        initEditTextFilter()
        initAreaSelectTextView()
        initRecyclerView()
        initImageAddImageView()
    }

    private fun initUploadButton() {
        val boardName = articleViewModel.articleFetchEvent.value?.board
        setBaseAppBar(boardName ?: getString(R.string.edit))
        setBackKey()
        setCheckButton {
            val title = binding.titleEditText.text.toString().trim()
            val content = binding.contentEditText.text.toString().trim()
            articleViewModel.editArticle(
                articleId,
                ArticleItem(
                    boardId = boardId,
                    title = title,
                    content = content
                )
            )
        }
    }

    private fun initEditTextFilter() {
        binding.titleEditText.filters = arrayOf(getEmojiFilter())
        binding.contentEditText.filters = arrayOf(getEmojiFilter())
    }

    private fun initImageAddImageView() {
        binding.imageAddImageView.setOnClickListener {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            TedImagePicker.with(this)
                .startMultiImage { uriList ->
                    articleViewModel.addImageUri(uriList)
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER
                }
        }
    }

    private fun initAreaSelectFragmentResultListener() {
        supportFragmentManager.setFragmentResultListener(
            AreaSelectorDialog.TAG,
            this
        ) { _, bundle ->
            val selectArea = bundle.getSerializable(AreaSelectorDialog.SELECT_AREA) as Area
            articleViewModel.updateSelectArea(selectArea)
        }
    }

    private fun initAreaSelectTextView() {
        binding.areaTextView.setOnClickListener {
            supportFragmentManager.showAreaSelectorDialog(
                articleViewModel.selectArea.value ?: Area.ALL, false
            )
        }
    }

    private fun initRecyclerView() {
        imageUploadAdapter = ImageUploadAdapter {
            articleViewModel.removeImageUri(it)
        }
        binding.imageRecyclerView.adapter = imageUploadAdapter
    }

    private fun initObserver() {
        with(articleViewModel) {
            articleFetchEvent.observe(this@ArticleEditActivity) { articleItem ->
                if (articleItem == null) return@observe
                val area = Area.values().find { it.areaName == articleItem.region } ?: Area.ALL
                updateSelectArea(area)
                setAppBarTitle(articleItem.board ?: "")
                binding.titleEditText.setText(articleItem.title ?: "")
                binding.contentEditText.setText(articleItem.content ?: "")
                articleItem.images?.map { it -> it.toUri() }?.let {
                    addImageUri(it)
                }
            }

            imageUris.observe(this@ArticleEditActivity) {
                imageUploadAdapter.updateItem(it)
            }

            selectArea.observe(this@ArticleEditActivity) {
                if (it != null)
                    binding.areaTextView.text = it.areaName
            }

            articleEmptyCheck.observe(this@ArticleEditActivity) { it ->
                if (it == null) return@observe
                when (it) {
                    Region -> showToast(getString(R.string.select_area))
                    Content -> showToast(getString(R.string.input_content))
                    Image -> showToast(getString(R.string.please_upload_image))
                    Title -> showToast(getString(R.string.input_title))
                }
            }

            updateArticleSuccessEvent.observe(this@ArticleEditActivity) {
                EventBus.getDefault().post(UpdateEvent(ArticleFragment.TAG))
                EventBus.getDefault().post(UpdateEvent(ArticleDetailActivity.TAG))
                finish()
            }

            showErrorMessage.observe(this@ArticleEditActivity) {
                if (it != null)
                    showToast(it)
            }
        }
    }

    companion object {
        const val EXTRA_ARTICLE_ID = "ARTICLE_ID"
        const val EXTRA_BOARD_ID = "BOARD_ID"
    }
}
