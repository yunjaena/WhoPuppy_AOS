package com.dicelab.whopuppy.fragment

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dicelab.whopuppy.R
import com.dicelab.whopuppy.base.fragment.ViewBindingFragment
import com.dicelab.whopuppy.base.showAlertDialog
import com.dicelab.whopuppy.databinding.FragmentChangePasswordBinding
import com.dicelab.whopuppy.util.setOnSingleClickListener
import com.dicelab.whopuppy.viewmodel.FindPasswordViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChangePasswordFragment : ViewBindingFragment<FragmentChangePasswordBinding>() {
    override val layoutId: Int = R.layout.fragment_change_password
    private val findPasswordViewModel: FindPasswordViewModel by viewModel()
    private val args: ChangePasswordFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        initView()
        initObserver()
    }

    private fun initView() {
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.changePasswordButton.setOnSingleClickListener {
            findPasswordViewModel.changePassword(
                args.account,
                args.phoneNumber,
                binding.passwordEditText.text.toString(),
                binding.passwordCheckEditText.text.toString(),
                args.secret
            )
        }
    }

    private fun initObserver() {
        with(findPasswordViewModel) {
            alertMessage.observe(viewLifecycleOwner) {
                if (it == null) return@observe

                requireContext().showAlertDialog(
                    getString(R.string.alert),
                    it,
                    getString(R.string.confirm)
                ) { dialog ->
                    dialog.dismiss()
                }
            }

            alertMessageRes.observe(viewLifecycleOwner) {
                if (it == null) return@observe

                requireContext().showAlertDialog(
                    getString(R.string.alert),
                    getString(it),
                    getString(R.string.confirm)
                ) { dialog ->
                    dialog.dismiss()
                }
            }

            findPasswordSuccessEvent.observe(viewLifecycleOwner) {
                requireContext().showAlertDialog(
                    getString(R.string.alert),
                    getString(R.string.password_changed),
                    getString(R.string.confirm)
                ) { dialog ->
                    dialog.dismiss()
                    requireActivity().finish()
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ChangePasswordFragment()
    }
}
