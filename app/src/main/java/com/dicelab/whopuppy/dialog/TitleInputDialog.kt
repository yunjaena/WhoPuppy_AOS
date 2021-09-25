package com.dicelab.whopuppy.dialog

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.dicelab.whopuppy.R
import com.dicelab.whopuppy.base.fragment.ViewBindingDialogFragment
import com.dicelab.whopuppy.base.getScreenWidth
import com.dicelab.whopuppy.databinding.DialogTitleInputBinding
import com.dicelab.whopuppy.util.showToast

class TitleInputDialog : ViewBindingDialogFragment<DialogTitleInputBinding>() {
    override val layoutId: Int = R.layout.dialog_title_input


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        isCancelable = false
    }

    private fun initView() {
        initConfirmTextView()
    }


    private fun initConfirmTextView() {
        binding.confirmButton.setOnClickListener {
            val titleText = binding.chatRoomInputEditText.text.toString().trim()
            if (titleText.isNullOrEmpty()) {
                requireContext().showToast(getString(R.string.input_chat_room_title))
                return@setOnClickListener
            }
            setFragmentResult(TAG, bundleOf(TITLE to titleText, RESULT to RESULT_OK))
            dismiss()
        }

        binding.cancelButton.setOnClickListener {
            setFragmentResult(TAG, bundleOf(RESULT to RESULT_CANCEL))
            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.attributes = dialog?.window?.attributes?.apply {
            width = (requireActivity().getScreenWidth() * 0.9).toInt()
        }
    }

    companion object {
        const val TAG = "TitleInputDialog"
        const val TITLE = "TITLE"
        const val RESULT = "RESULT"
        const val RESULT_CANCEL = 0
        const val RESULT_OK = 1


        fun newInstance(): TitleInputDialog {
            return TitleInputDialog()
        }
    }
}

