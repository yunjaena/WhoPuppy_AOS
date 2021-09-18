package com.yunjaena.whopuppy.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import com.yunjaena.whopuppy.R
import com.yunjaena.whopuppy.adapter.ImageUploadAdapter
import com.yunjaena.whopuppy.base.activity.ViewBindingActivity
import com.yunjaena.whopuppy.data.Area
import com.yunjaena.whopuppy.data.entity.ArticleItem
import com.yunjaena.whopuppy.databinding.ActivityArticleWriteBinding
import com.yunjaena.whopuppy.dialog.AreaSelectorDialog
import com.yunjaena.whopuppy.fragment.ArticleFragment
import com.yunjaena.whopuppy.util.UpdateEvent
import com.yunjaena.whopuppy.util.getEmojiFilter
import com.yunjaena.whopuppy.util.goToArticleDetailActivity
import com.yunjaena.whopuppy.util.showAreaSelectorDialog
import com.yunjaena.whopuppy.util.showToast
import com.yunjaena.whopuppy.viewmodel.ArticleViewModel
import com.yunjaena.whopuppy.viewmodel.Content
import com.yunjaena.whopuppy.viewmodel.Image
import com.yunjaena.whopuppy.viewmodel.Region
import com.yunjaena.whopuppy.viewmodel.Title
import gun0912.tedimagepicker.builder.TedImagePicker
import org.greenrobot.eventbus.EventBus
import org.koin.androidx.viewmodel.ext.android.viewModel

class ArticleWriteActivity : ViewBindingActivity<ActivityArticleWriteBinding>() {
    override val layoutId: Int = R.layout.activity_article_write
    private val articleViewModel: ArticleViewModel by viewModel()
    private lateinit var imageUploadAdapter: ImageUploadAdapter
    private var boardId: Long = 0
    private var boardTitle = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        boardId = intent.getLongExtra(EXTRA_BOARD_ID, 0)
        boardTitle = intent.getStringExtra(EXTRA_BOARD_TITLE) ?: ""
        useDefaultLoading(articleViewModel)
        initAreaSelectFragmentResultListener()
        initView()
        initObserver()
    }

    private fun initView() {
        initUploadButton()
        initEditTextFilter()
        initAreaSelectTextView()
        initRecyclerView()
        initImageAddImageView()
    }

    private fun initUploadButton() {
        setBaseAppBar(boardTitle)
        setBackKey()
        setCheckButton {
            val title = binding.titleEditText.text.toString().trim()
            val content = binding.contentEditText.text.toString().trim()
            articleViewModel.writeArticle(
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
            imageUris.observe(this@ArticleWriteActivity) {
                imageUploadAdapter.updateItem(it)
            }

            selectArea.observe(this@ArticleWriteActivity) {
                if (it != null)
                    binding.areaTextView.text = it.areaName
            }

            articleEmptyCheck.observe(this@ArticleWriteActivity) { it ->
                if (it == null) return@observe
                when (it) {
                    Region -> showToast(getString(R.string.select_area))
                    Content -> showToast(getString(R.string.input_content))
                    Image -> showToast(getString(R.string.please_upload_image))
                    Title -> showToast(getString(R.string.input_title))
                }
            }

            uploadArticleSuccessEvent.observe(this@ArticleWriteActivity) {
                if (it == null) return@observe
                EventBus.getDefault().post(UpdateEvent(ArticleFragment.TAG))
                goToArticleDetailActivity(it)
                finish()
            }

            showErrorMessage.observe(this@ArticleWriteActivity) {
                if (it != null)
                    showToast(it)
            }
        }
    }

    companion object {
        const val EXTRA_BOARD_ID = "BOARD_ID"
        const val EXTRA_BOARD_TITLE = "EXTRA_BOARD_TITLE"
    }
}
