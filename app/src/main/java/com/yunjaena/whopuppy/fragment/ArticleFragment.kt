package com.yunjaena.whopuppy.fragment

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.paginate.Paginate
import com.yunjaena.whopuppy.R
import com.yunjaena.whopuppy.adapter.ArticleListAdapter
import com.yunjaena.whopuppy.base.fragment.ViewBindingFragment
import com.yunjaena.whopuppy.databinding.FragmentArticleBinding
import com.yunjaena.whopuppy.util.UpdateEvent
import com.yunjaena.whopuppy.util.goToArticleWriteActivity
import com.yunjaena.whopuppy.viewmodel.ArticleSearchQuery
import com.yunjaena.whopuppy.viewmodel.ArticleViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.androidx.viewmodel.ext.android.viewModel

class ArticleFragment : ViewBindingFragment<FragmentArticleBinding>(), RefreshFragment {
    override val layoutId: Int = R.layout.fragment_article
    private val articleViewModel: ArticleViewModel by viewModel()
    private lateinit var articleListAdapter: ArticleListAdapter
    private var boardId = 0L
    var boardTitle: String = ""
    var title: String? = null
    var area: String? = null

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshEvent(updateEvent: UpdateEvent) {
        if (updateEvent.tag != TAG) return
        articleViewModel.refreshArticleList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)
        boardId = arguments?.getLong(BOARD_ID, 0) ?: 0
        boardTitle = arguments?.getString(BOARD_TITLE) ?: ""
        title = arguments?.getString(TITLE)
        area = arguments?.getString(AREA)
        init()
        if (savedInstanceState == null)
            updateList(boardId, title, area)
    }

    private fun init() {
        initView()
        initObserver()
    }

    private fun initView() {
        initFloatingButton()
        initSwipeLayout()
        initRecyclerView()
        initPaginate()
    }

    private fun initFloatingButton() {
        binding.floatingButton.setOnClickListener {
            requireContext().goToArticleWriteActivity(boardId, boardTitle)
        }
    }

    private fun initSwipeLayout() {
        binding.swipeLayout.setOnRefreshListener {
            articleViewModel.refreshArticleList()
        }
    }

    private fun initRecyclerView() {
        articleListAdapter = ArticleListAdapter()
        articleListAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.articleRecyclerView.adapter = articleListAdapter
    }

    private fun initPaginate() {
        val paginateCallback = object : Paginate.Callbacks {
            override fun onLoadMore() {
                articleViewModel.getNextPage()
            }

            override fun isLoading(): Boolean {
                return articleViewModel.isLoadingPage
            }

            override fun hasLoadedAllItems(): Boolean {
                return articleViewModel.isEndOfPage
            }
        }

        Paginate.with(binding.articleRecyclerView, paginateCallback)
            .setLoadingTriggerThreshold(5)
            .addLoadingListItem(false)
            .build()
    }

    private fun initObserver() {
        with(articleViewModel) {
            articleList.observe(viewLifecycleOwner) {
                articleListAdapter.updateItem(it)
                binding.swipeLayout.isRefreshing = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    override fun refresh(bundle: Bundle?) {
        if (bundle == null) return
        title = bundle.getString(TITLE)
        area = bundle.getString(AREA)
        updateList(boardId, title, area)
    }

    private fun updateList(boardId: Long, title: String?, area: String?) {
        articleViewModel.getArticles(ArticleSearchQuery(boardId, title, area))
    }

    companion object {
        const val TAG = "ArticleFragment"
        const val BOARD_ID = "BOARD_ID"
        const val BOARD_TITLE = "BOARD_TITLE"
        const val AREA = "AREA"
        const val TITLE = "TITLE"

        fun newInstance(
            boardId: Long,
            boarTile: String,
            title: String? = null,
            area: String? = null
        ): ArticleFragment {
            val fragment = ArticleFragment()
            fragment.arguments =
                bundleOf(BOARD_ID to boardId, BOARD_TITLE to boarTile, TITLE to title, AREA to area)
            return fragment
        }
    }
}
