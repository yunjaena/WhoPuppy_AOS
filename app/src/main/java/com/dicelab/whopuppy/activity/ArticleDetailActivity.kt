package com.dicelab.whopuppy.activity

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.dicelab.whopuppy.R
import com.dicelab.whopuppy.adapter.ImageSliderAdapter
import com.dicelab.whopuppy.base.activity.ViewBindingActivity
import com.dicelab.whopuppy.base.dpToPx
import com.dicelab.whopuppy.base.fadeInAnimation
import com.dicelab.whopuppy.base.fadeOutAnimation
import com.dicelab.whopuppy.base.showAlertDialog
import com.dicelab.whopuppy.base.slideVisibility
import com.dicelab.whopuppy.databinding.ActivityArticleDetailBinding
import com.dicelab.whopuppy.fragment.ArticleFragment
import com.dicelab.whopuppy.util.UpdateEvent
import com.dicelab.whopuppy.util.goToArticleEditActivity
import com.dicelab.whopuppy.util.goToChattingActivity
import com.dicelab.whopuppy.util.showArticleCommentBottomSheetDialog
import com.dicelab.whopuppy.util.showToast
import com.dicelab.whopuppy.viewmodel.ArticleViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.max

class ArticleDetailActivity : ViewBindingActivity<ActivityArticleDetailBinding>() {
    override val layoutId: Int = R.layout.activity_article_detail
    private val articleViewModel: ArticleViewModel by viewModel()
    private lateinit var imageSliderAdapter: ImageSliderAdapter
    private var articleId: Long = 0

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshEvent(updateEvent: UpdateEvent) {
        if (updateEvent.tag != ArticleFragment.TAG) return
        articleViewModel.refreshArticle(articleId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        EventBus.getDefault().register(this)
        articleId = intent.getLongExtra(EXTRA_ARTICLE_ID, 0)
        useDefaultLoading(articleViewModel)
        initView()
        initObserver()
        articleViewModel.getArticle(articleId)
    }

    private fun initView() {
        initFloatingActionButton()
        initRecyclerView()
    }

    private fun initFloatingActionButton() {
        setFloatingActionButtonAnimation()
        binding.messageFloatingActionButton.setOnClickListener {
            if (articleViewModel.isArticleOwner) {
                showToast(getString(R.string.write_owner))
                return@setOnClickListener
            }
            goToChattingActivity(articleId = articleId)
        }

        binding.commentFloatingActionButton.setOnClickListener {
            val parentLayoutHeight = max(binding.parentLayout.height, binding.parentLayout.width)
            val viewPagerHeight = 200.dpToPx()
            val height = parentLayoutHeight - viewPagerHeight
            supportFragmentManager.showArticleCommentBottomSheetDialog(height, articleId)
        }

        binding.editFloatingActionButton.setOnClickListener {
            goToArticleEditActivity(articleId)
        }

        binding.deleteFloatingActionButton.setOnClickListener {
            showAlertDialog(
                getString(R.string.alert),
                getString(R.string.delete_check_message),
                getString(R.string.confirm),
                getString(R.string.cancel),
                { dialog ->
                    dialog.dismiss()
                    articleViewModel.deleteArticle(articleId)
                }
            )
        }
    }

    private fun setFloatingActionButtonAnimation() {
        binding.floatingActionButton.setOnClickListener {
            if (isFloatingButtonIsOpened()) {
                binding.floatingActionButton.setImageResource(R.drawable.ic_add)
                binding.commentFloatingActionButton.slideVisibility(false)
                binding.messageFloatingActionButton.slideVisibility(false)
                binding.messageTextView.fadeOutAnimation(0)
                binding.commentTextView.fadeOutAnimation(0)
                if (articleViewModel.isArticleOwner) {
                    binding.deleteFloatingActionButton.slideVisibility(false)
                    binding.editFloatingActionButton.slideVisibility(false)
                    binding.deleteTextView.fadeOutAnimation(0)
                    binding.editTextView.fadeOutAnimation(0)
                }
            } else {
                binding.floatingActionButton.setImageResource(R.drawable.ic_remove)
                if (articleViewModel.isArticleOwner) {
                    binding.editFloatingActionButton.slideVisibility(true)
                    binding.deleteFloatingActionButton.slideVisibility(true)
                    binding.deleteTextView.fadeInAnimation()
                    binding.editTextView.fadeInAnimation()
                }
                binding.messageFloatingActionButton.slideVisibility(true)
                binding.commentFloatingActionButton.slideVisibility(true)
                binding.messageTextView.fadeInAnimation()
                binding.commentTextView.fadeInAnimation()
            }
        }
    }

    private fun initRecyclerView() {
        imageSliderAdapter = ImageSliderAdapter()
        binding.imageViewPager.adapter = imageSliderAdapter
    }

    private fun initObserver() {
        with(articleViewModel) {
            articleDetailItem.observe(this@ArticleDetailActivity) {
                if (!it.images.isNullOrEmpty()) {
                    setupIndicators(it.images.size)
                    setViewPager(it.images)
                }
                setTitle(it.title)
                setProfileImage(it.profileImageUrl)
                setNickName(it.nickname)
                setDate(it.updatedAt)
                setContent(it.content)
                checkUserWriteArticle(it)
            }

            deleteArticleSuccessEvent.observe(this@ArticleDetailActivity) {
                EventBus.getDefault().post(UpdateEvent(ArticleFragment.TAG))
                finish()
            }
        }
    }

    private fun setupIndicators(count: Int) {
        if (count <= 1) {
            binding.indicatorLinearLayout.visibility = View.GONE
            return
        }

        binding.indicatorLinearLayout.visibility = View.VISIBLE
        val indicators: Array<ImageView> = Array(count) { ImageView(this) }
        binding.indicatorLinearLayout.removeAllViews()
        for (i in indicators.indices) {
            binding.indicatorLinearLayout.addView(indicators[i])
        }
    }

    private fun setCurrentIndicator(position: Int) {
        val childCount: Int = binding.indicatorLinearLayout.childCount
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(16, 8, 16, 8)
        for (i in 0 until childCount) {
            val imageView: ImageView = binding.indicatorLinearLayout.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.bg_indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.bg_indicator_in_active
                    )
                )
            }
            imageView.layoutParams = params
        }
    }

    private fun setViewPager(imageUrls: List<String>) {
        imageSliderAdapter.setImageUrls(imageUrls)
        setCurrentIndicator(binding.imageViewPager.currentItem)
        binding.imageViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })
    }

    private fun setTitle(title: String?) {
        binding.titleTextView.text = title ?: ""
    }

    private fun setProfileImage(imageUrl: String?) {
        if (imageUrl.isNullOrEmpty()) return
        Glide.with(this)
            .load(imageUrl)
            .into(binding.profileImageView)
    }

    private fun setNickName(nickName: String?) {
        binding.nickNameTextView.text = nickName ?: ""
    }

    private fun setDate(date: String?) {
        binding.dateTextView.text = date ?: ""
    }

    private fun setContent(content: String?) {
        binding.contentTextView.text = content ?: ""
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onBackPressed() {
        if (isFloatingButtonIsOpened()) {
            binding.floatingActionButton.performClick()
            return
        }
        super.onBackPressed()
    }

    private fun isFloatingButtonIsOpened(): Boolean {
        return binding.messageFloatingActionButton.visibility == View.VISIBLE
    }

    companion object {
        const val EXTRA_ARTICLE_ID = "EXTRA_ARTICLE_ID"
        const val TAG = "ArticleDetailActivity"
    }
}
