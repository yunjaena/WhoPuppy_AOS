package com.yunjaena.whopuppy.fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.yunjaena.whopuppy.R
import com.yunjaena.whopuppy.adapter.AbandonDogAdapter
import com.yunjaena.whopuppy.base.dpToPx
import com.yunjaena.whopuppy.base.fragment.ViewBindingFragment
import com.yunjaena.whopuppy.databinding.FragmentHomeBinding
import com.yunjaena.whopuppy.util.LinearLayoutItemDecoration
import com.yunjaena.whopuppy.util.goToAbandonedDogActivity
import com.yunjaena.whopuppy.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : ViewBindingFragment<FragmentHomeBinding>() {
    override val layoutId: Int = R.layout.fragment_home
    private val homeViewModel: HomeViewModel by viewModel()
    private lateinit var abandonedDogListAdapter: AbandonDogAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        initView()
        initObserver()
    }

    private fun initView() {
        binding.showMoreAbandonedDogImageView.setOnClickListener {
            requireContext().goToAbandonedDogActivity()
        }
        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.abandonedDogRecyclerView.addItemDecoration(
            LinearLayoutItemDecoration(
                LinearLayout.HORIZONTAL,
                Rect(5.dpToPx(), 0.dpToPx(), 5.dpToPx(), 0.dpToPx())
            )
        )
        abandonedDogListAdapter = AbandonDogAdapter()
        abandonedDogListAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.abandonedDogRecyclerView.adapter = abandonedDogListAdapter
    }

    private fun initObserver() {
        with(homeViewModel) {
            abandonedAnimalList.observe(viewLifecycleOwner) {
                if (it == null) return@observe
                abandonedDogListAdapter.updateItem(it)
            }

            abandonedDogItemFetchFailMessage.observe(viewLifecycleOwner) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val TAG = "HomeFragment"
        fun newInstance(): HomeFragment = HomeFragment()
    }
}
