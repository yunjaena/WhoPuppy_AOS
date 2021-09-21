package com.dicelab.whopuppy.fragment

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.dicelab.whopuppy.R
import com.dicelab.whopuppy.adapter.ArticlePagerAdapter
import com.dicelab.whopuppy.base.fragment.ViewBindingFragment
import com.dicelab.whopuppy.base.hideKeyBoard
import com.dicelab.whopuppy.data.Area
import com.dicelab.whopuppy.data.entity.Boards
import com.dicelab.whopuppy.databinding.FragmentBoardListBinding
import com.dicelab.whopuppy.dialog.AreaSelectorDialog
import com.dicelab.whopuppy.util.SelectTabEvent
import com.dicelab.whopuppy.util.showAreaSelectorDialog
import com.dicelab.whopuppy.viewmodel.BoardListViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.androidx.viewmodel.ext.android.viewModel


class BoardListFragment : ViewBindingFragment<FragmentBoardListBinding>() {
    override val layoutId: Int = R.layout.fragment_board_list
    private val boardListViewModel: BoardListViewModel by viewModel()

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun tabSelectEvent(selectTabEvent: SelectTabEvent) {
        boardListViewModel.boardSelectPosition = selectTabEvent.tabIndex
        binding.tabLayout.getTabAt(selectTabEvent.tabIndex)?.select()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        initView()
        initObserver()
        boardListViewModel.getBoards()
    }

    private fun initView() {
        binding.vm = boardListViewModel
        initReloadView()
        initSearchView()
        initAreaFilterButton()
        initAreaSelectFragmentResultListener()
    }

    private fun initReloadView() {
        binding.reloadImageView.setOnClickListener {
            boardListViewModel.getBoards()
        }
    }

    private fun initSearchView() {
        with(binding.searchView) {
            isSubmitButtonEnabled = true
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    requireActivity().hideKeyBoard()
                    binding.searchView.clearFocus()
                    boardListViewModel.updateSearchTitle(query)
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText.equals("")) {
                        boardListViewModel.updateSearchTitle(null)
                    }
                    return true
                }
            })
        }
    }

    private fun initAreaFilterButton() {
        binding.filterButton.setOnClickListener {
            childFragmentManager.showAreaSelectorDialog(boardListViewModel.selectArea)
        }
    }

    private fun initAreaSelectFragmentResultListener() {
        childFragmentManager.setFragmentResultListener(
            AreaSelectorDialog.TAG,
            viewLifecycleOwner
        ) { _, bundle ->
            val selectArea = bundle.getSerializable(AreaSelectorDialog.SELECT_AREA) as Area
            boardListViewModel.updateSelectArea(selectArea)
        }
    }

    private fun initObserver() {
        with(boardListViewModel) {
            boardList.observe(viewLifecycleOwner) {
                initViewPager(it)
                TabLayoutMediator(binding.tabLayout, binding.articleViewPager) { tab, position ->
                    tab.text = it[position].board
                }.attach()
                initTabLayout()
            }

            articleSearchEvent.observe(viewLifecycleOwner) {
                if (it == null) return@observe
                (binding.articleViewPager.adapter as? ArticlePagerAdapter)?.updateFragment(it)
            }
        }
    }

    private fun initTabLayout() {
        val selectPosition = boardListViewModel.boardSelectPosition
        binding.tabLayout.getTabAt(selectPosition)?.select()
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (binding.tabLayout.tabCount > 0) {
                    boardListViewModel.boardSelectPosition = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) = Unit

            override fun onTabReselected(tab: TabLayout.Tab?) = Unit
        })
    }

    private fun initViewPager(boards: Boards) {
        with(binding.articleViewPager) {
            adapter = ArticlePagerAdapter(childFragmentManager, lifecycle, boards)
            offscreenPageLimit = boards.size
            isUserInputEnabled = false
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    companion object {
        const val TAG = "BoardListFragment"
        fun newInstance(): BoardListFragment = BoardListFragment()
    }
}
