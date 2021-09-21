package com.dicelab.whopuppy.dialog

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.dicelab.whopuppy.R
import com.dicelab.whopuppy.adapter.AreaSelectorAdapter
import com.dicelab.whopuppy.base.fragment.ViewBindingDialogFragment
import com.dicelab.whopuppy.base.getScreenHeight
import com.dicelab.whopuppy.base.getScreenWidth
import com.dicelab.whopuppy.data.Area
import com.dicelab.whopuppy.databinding.DialogAreaSelectorBinding
import com.dicelab.whopuppy.util.showToast

class AreaSelectorDialog : ViewBindingDialogFragment<DialogAreaSelectorBinding>() {
    override val layoutId: Int = R.layout.dialog_area_selector
    private lateinit var areaSelectAdapter: AreaSelectorAdapter
    private var firstSelectArea = Area.ALL
    private var isAllAreaSelectAreaPossible: Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getSerializable(SELECT_AREA)?.let {
            firstSelectArea = it as Area
        }

        arguments?.getBoolean(IS_ALL_AREA_SELECT_POSSIBLE)?.let {
            isAllAreaSelectAreaPossible = it
        }
        savedInstanceState?.getSerializable(SAVED_AREA)?.let {
            firstSelectArea = it as Area
        }
        initView()
    }

    private fun initView() {
        initConfirmTextView()
        initRecyclerView()
    }


    private fun initConfirmTextView() {
        binding.confirmTextView.setOnClickListener {
            if (isAreaSelectAllIsInPossible()) {
                requireContext().showToast(getString(R.string.select_specific_area))
                return@setOnClickListener
            }
            setFragmentResult(TAG, bundleOf(SELECT_AREA to areaSelectAdapter.selectArea))
            dismiss()
        }
    }

    private fun isAreaSelectAllIsInPossible(): Boolean {
        return !isAllAreaSelectAreaPossible && areaSelectAdapter.selectArea == Area.ALL
    }

    private fun initRecyclerView() {
        areaSelectAdapter = AreaSelectorAdapter(firstSelectArea)
        binding.areaRecyclerView.adapter = areaSelectAdapter
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.attributes = dialog?.window?.attributes?.apply {
            width = (requireActivity().getScreenWidth() * 0.9).toInt()
            height = (requireActivity().getScreenHeight() * 0.9).toInt()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val selectArea = areaSelectAdapter.selectArea
        outState.putSerializable(SAVED_AREA, selectArea)
    }

    companion object {
        const val TAG = "AreaSelectorDialog"
        const val SELECT_AREA = "SELECT_AREA"
        const val SAVED_AREA = "SAVED_AREA"
        const val IS_ALL_AREA_SELECT_POSSIBLE = "IS_ALL_AREA_SELECT_POSSIBLE"

        fun newInstance(
            selectArea: Area = Area.ALL,
            isAllAreaSelectPossible: Boolean = true
        ): AreaSelectorDialog {
            return AreaSelectorDialog().apply {
                arguments = bundleOf(
                    SELECT_AREA to selectArea,
                    IS_ALL_AREA_SELECT_POSSIBLE to isAllAreaSelectPossible
                )
            }
        }
    }
}

