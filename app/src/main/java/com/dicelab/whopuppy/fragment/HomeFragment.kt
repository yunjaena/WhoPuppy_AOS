package com.dicelab.whopuppy.fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import com.dicelab.whopuppy.R
import com.dicelab.whopuppy.activity.MainActivity
import com.dicelab.whopuppy.adapter.AbandonAnimalAdapter
import com.dicelab.whopuppy.base.dpToPx
import com.dicelab.whopuppy.base.fragment.ViewBindingFragment
import com.dicelab.whopuppy.databinding.FragmentHomeBinding
import com.dicelab.whopuppy.util.LinearLayoutItemDecoration
import com.dicelab.whopuppy.util.SelectTabEvent
import com.dicelab.whopuppy.util.goToAbandonedAnimalActivity
import com.dicelab.whopuppy.util.goToDogImageUploadActivity
import com.dicelab.whopuppy.viewmodel.HomeViewModel
import org.greenrobot.eventbus.EventBus
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : ViewBindingFragment<FragmentHomeBinding>() {
    override val layoutId: Int = R.layout.fragment_home
    private val homeViewModel: HomeViewModel by viewModel()
    private lateinit var abandonedAnimalListAdapter: AbandonAnimalAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        initView()
        initObserver()
    }

    private fun initView() {
        binding.reloadImageView.setOnClickListener {
            homeViewModel.getAnimalList(
                HomeViewModel.ANIMAL_LIST_LIMIT_SIZE,
                HomeViewModel.ANIMAL_LIST_PAGE
            )
        }

        binding.rehomeDogSearchImageView.setOnClickListener {
            goToBardFragment()
            EventBus.getDefault().post(SelectTabEvent(0))
        }

        binding.adoptDogSearchImageView.setOnClickListener {
            goToBardFragment()
            EventBus.getDefault().post(SelectTabEvent(1))
        }
        binding.adoptDogReviewSearchImageView.setOnClickListener {
            goToBardFragment()
            EventBus.getDefault().post(SelectTabEvent(2))
        }

        binding.showMoreAbandonedAnimalImageView.setOnClickListener {
            requireContext().goToAbandonedAnimalActivity()
        }
        binding.findDogBreedButton.setOnClickListener {
            requireContext().goToDogImageUploadActivity()
        }
        initRecyclerView()
    }

    private fun goToBardFragment() {
        (requireActivity() as? MainActivity)?.binding?.bottomNavigation?.selectedItemId =
            R.id.action_board
    }

    private fun initRecyclerView() {
        binding.abandonedAnimalRecyclerView.addItemDecoration(
            LinearLayoutItemDecoration(
                LinearLayout.HORIZONTAL,
                Rect(5.dpToPx(), 0.dpToPx(), 5.dpToPx(), 0.dpToPx())
            )
        )
        abandonedAnimalListAdapter = AbandonAnimalAdapter()
        binding.abandonedAnimalRecyclerView.adapter = abandonedAnimalListAdapter
    }

    private fun initObserver() {
        with(homeViewModel) {
            abandonedAnimalList.observe(viewLifecycleOwner) {
                if (it == null) return@observe
                abandonedAnimalListAdapter.updateItem(it)
                hideReloadImageView()
                showAbandonedAnimalRecyclerView()
            }

            abandonedDogItemFetchFailMessage.observe(viewLifecycleOwner) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAbandonedAnimalRecyclerView() {
        binding.abandonedAnimalRecyclerView.visibility = View.VISIBLE
    }

    private fun hideReloadImageView() {
        binding.reloadImageView.visibility = View.GONE
    }

    companion object {
        const val TAG = "HomeFragment"
        fun newInstance(): HomeFragment = HomeFragment()
    }
}
