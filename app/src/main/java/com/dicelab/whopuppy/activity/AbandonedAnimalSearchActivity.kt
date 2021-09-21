package com.dicelab.whopuppy.activity

import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.paginate.Paginate
import com.dicelab.whopuppy.R
import com.dicelab.whopuppy.adapter.AbandonAnimalListAdapter
import com.dicelab.whopuppy.base.activity.ViewBindingActivity
import com.dicelab.whopuppy.base.dpToPx
import com.dicelab.whopuppy.base.hideKeyBoard
import com.dicelab.whopuppy.databinding.ActivityAbandonedAnimalSearchBinding
import com.dicelab.whopuppy.util.GridLayoutItemDecoration
import com.dicelab.whopuppy.util.showAbandonedDogSortDialog
import com.dicelab.whopuppy.viewmodel.AbandonedDogSearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AbandonedAnimalSearchActivity : ViewBindingActivity<ActivityAbandonedAnimalSearchBinding>() {
    override val layoutId: Int = R.layout.activity_abandoned_animal_search
    private val abandonedDogSearchViewModel: AbandonedDogSearchViewModel by viewModel()
    private lateinit var abandonAnimalAdapter: AbandonAnimalListAdapter

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
        abandonAnimalAdapter = AbandonAnimalListAdapter()
        binding.abandonedDogRecyclerView.adapter = abandonAnimalAdapter
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
            abandonAnimalAdapter.updateItem(it)
        }
    }
}
