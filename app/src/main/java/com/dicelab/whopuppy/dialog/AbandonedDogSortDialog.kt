package com.dicelab.whopuppy.dialog

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.dicelab.whopuppy.R
import com.dicelab.whopuppy.base.fragment.ViewBindingDialogFragment
import com.dicelab.whopuppy.databinding.DialogAbandonedAnimalSortBinding
import com.dicelab.whopuppy.viewmodel.AbandonedDogSearchViewModel
import com.dicelab.whopuppy.viewmodel.Sex
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class  AbandonedDogSortDialog : ViewBindingDialogFragment<DialogAbandonedAnimalSortBinding>() {
    override val layoutId: Int = R.layout.dialog_abandoned_animal_sort
    private val abandonedDogSearchViewModel: AbandonedDogSearchViewModel by sharedViewModel()
    private var currentSelectSex: Sex = Sex.ALL

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        initSexRadioButton()
        initAreaEditText()
        initConfirmTextView()
    }

    private fun initSexRadioButton() {
        val sex = abandonedDogSearchViewModel.animalSearchQuery.sexCd
        binding.sexRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            currentSelectSex = when (checkedId) {
                binding.allSexRadioButton.id -> Sex.ALL
                binding.maleRadioButton.id -> Sex.MALE
                binding.femaleRadioButton.id -> Sex.FEMALE
                binding.neutralRadioButton.id -> Sex.NEUTRAL
                else -> Sex.ALL
            }
        }
        when (sex) {
            Sex.ALL -> binding.allSexRadioButton.isChecked = true
            Sex.MALE -> binding.maleRadioButton.isChecked = true
            Sex.FEMALE -> binding.femaleRadioButton.isChecked = true
            Sex.NEUTRAL -> binding.neutralRadioButton.isChecked = true
        }
    }

    private fun initAreaEditText() {
        val area = abandonedDogSearchViewModel.animalSearchQuery.noticeNo
        binding.areaEditText.setText(area ?: "")
    }

    private fun initConfirmTextView() {
        binding.confirmTextView.setOnClickListener {
            val area = binding.areaEditText.text.toString().trim()
            abandonedDogSearchViewModel.setArea(area)
            abandonedDogSearchViewModel.setSex(currentSelectSex)
            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    companion object {
        const val TAG = "AbandonedDogSortDialog"

        fun getInstance(): AbandonedDogSortDialog {
            return AbandonedDogSortDialog()
        }
    }
}
