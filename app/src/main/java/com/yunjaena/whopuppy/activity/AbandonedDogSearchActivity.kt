package com.yunjaena.whopuppy.activity

import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.paginate.Paginate
import com.yunjaena.whopuppy.R
import com.yunjaena.whopuppy.adapter.AbandonDogListAdapter
import com.yunjaena.whopuppy.base.activity.ViewBindingActivity
import com.yunjaena.whopuppy.base.dpToPx
import com.yunjaena.whopuppy.base.hideKeyBoard
import com.yunjaena.whopuppy.databinding.ActivityAbandonedDogSearchBinding
import com.yunjaena.whopuppy.util.GridLayoutItemDecoration
import com.yunjaena.whopuppy.util.showAbandonedDogSortDialog
import com.yunjaena.whopuppy.viewmodel.AbandonedDogSearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AbandonedDogSearchActivity : ViewBindingActivity<ActivityAbandonedDogSearchBinding>() {
    override val layoutId: Int = R.layout.activity_abandoned_dog_search
    private val abandonedDogSearchViewModel: AbandonedDogSearchViewModel by viewModel()
    private lateinit var abandonDogAdapter: AbandonDogListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        initView()
        initObserver()
    }

    private fun initView() {
        initSearchView()
        initRecyclerView()
        initPaginate()
        initFloatingButton()
        initSortButton()
    }

    private fun initSearchView() {
        with(binding.searchView) {
            isSubmitButtonEnabled = true
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    hideKeyBoard()
                    binding.searchView.clearFocus()
                    searchDogsKind(query)
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText.equals("")) {
                        searchDogsKind(null)
                    }
                    return true
                }
            })
        }
    }

    private fun initRecyclerView() {
        abandonDogAdapter = AbandonDogListAdapter()
        abandonDogAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.abandonedDogRecyclerView.adapter = abandonDogAdapter
        binding.abandonedDogRecyclerView.addItemDecoration(GridLayoutItemDecoration(2, 10.dpToPx()))
        binding.abandonedDogRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    binding.floatingButton.show()
                } else {
                    binding.floatingButton.hide()
                }
            }
        })
    }

    private fun initPaginate() {
        val paginateCallback = object : Paginate.Callbacks {
            override fun onLoadMore() {
                abandonedDogSearchViewModel.getNextPage()
            }

            override fun isLoading(): Boolean {
                return abandonedDogSearchViewModel.isLoadingPage
            }

            override fun hasLoadedAllItems(): Boolean {
                return abandonedDogSearchViewModel.isEndOfPage
            }
        }

        Paginate.with(binding.abandonedDogRecyclerView, paginateCallback)
            .setLoadingTriggerThreshold(5)
            .addLoadingListItem(false)
            .build()
    }

    private fun searchDogsKind(kind: String?) {
        abandonedDogSearchViewModel.setKind(kind)
    }

    private fun initFloatingButton() {
        binding.floatingButton.setOnClickListener {
            binding.abandonedDogRecyclerView.smoothScrollToPosition(0)
        }
    }

    private fun initSortButton() {
        binding.filterButton.setOnClickListener {
            supportFragmentManager.showAbandonedDogSortDialog()
        }
    }

    private fun initObserver() {
        abandonedDogSearchViewModel.abandonedAnimalList.observe(this) {
            abandonDogAdapter.updateItem(it)
        }
    }
}