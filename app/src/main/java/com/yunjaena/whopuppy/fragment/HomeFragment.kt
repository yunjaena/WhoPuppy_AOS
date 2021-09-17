package com.yunjaena.whopuppy.fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.yunjaena.whopuppy.R
import com.yunjaena.whopuppy.activity.MainActivity
import com.yunjaena.whopuppy.adapter.AbandonAnimalAdapter
import com.yunjaena.whopuppy.base.dpToPx
import com.yunjaena.whopuppy.base.fragment.ViewBindingFragment
import com.yunjaena.whopuppy.databinding.FragmentHomeBinding
import com.yunjaena.whopuppy.util.LinearLayoutItemDecoration
import com.yunjaena.whopuppy.util.SelectTabEvent
import com.yunjaena.whopuppy.util.goToAbandonedAnimalActivity
import com.yunjaena.whopuppy.util.goToDogImageUploadActivity
import com.yunjaena.whopuppy.viewmodel.HomeViewModel
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
        abandonedAnimalListAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.abandonedAnimalRecyclerView.adapter = abandonedAnimalListAdapter
    }

    private fun initObserver() {
        with(homeViewModel) {
            abandonedAnimalList.observe(viewLifecycleOwner) {
                if (it == null) return@observe
                abandonedAnimalListAdapter.updateItem(it)
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
