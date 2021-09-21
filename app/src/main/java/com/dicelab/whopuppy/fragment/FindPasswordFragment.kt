package com.dicelab.whopuppy.fragment

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.dicelab.whopuppy.R
import com.dicelab.whopuppy.base.fragment.ViewBindingFragment
import com.dicelab.whopuppy.base.showAlertDialog
import com.dicelab.whopuppy.databinding.FragmentFindPasswordBinding
import com.dicelab.whopuppy.util.RequestPhoneNumberUtil
import com.dicelab.whopuppy.util.SmsReceiver
import com.dicelab.whopuppy.util.getEmojiFilter
import com.dicelab.whopuppy.util.setOnSingleClickListener
import com.dicelab.whopuppy.viewmodel.FindPasswordViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FindPasswordFragment : ViewBindingFragment<FragmentFindPasswordBinding>() {
    override val layoutId: Int = R.layout.fragment_find_password
    private val findPasswordViewModel: FindPasswordViewModel by viewModel()
    private lateinit var requestPhoneNumberUtil: RequestPhoneNumberUtil
    private val account
        get() = binding.userIdEditText.text.toString()
    private val phoneNumber
        get() = binding.userPhoneNumberEditText.text.toString()
    private val secretCode
        get() = binding.userPhoneNumberSecretEditText.text.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initRequestPhoneNumber()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        initView()
        registerSMSBroadCastReceiver()
        initObserver()
    }

    private fun initView() {
        useDefaultLoading(findPasswordViewModel)
        with(binding) {
            binding.backButton.setOnClickListener {
                requireActivity().finish()
            }

            userPhoneNumberEditText.filters = arrayOf(getEmojiFilter())
            userPhoneNumberSecretEditText.filters = arrayOf(getEmojiFilter())
            userIdEditText.filters = arrayOf(getEmojiFilter())

            userPhoneNumberEditText.isFocusable = false

            userPhoneNumberEditText.setOnClickListener {
                requestPhoneNumber()
            }

            phoneNumberValidCheckButton.setOnSingleClickListener {
                findPasswordViewModel.phoneNumberValidCheckWithSms(
                    phoneNumber,
                    SmsReceiver.getAppSignatureCode(requireContext())
                )
            }

            userPhoneNumberSecretCheckButton.setOnSingleClickListener {
                setInputArea(false)
                findPasswordViewModel.checkSecretCode(
                    phoneNumber,
                    secretCode
                )
            }

            findPasswordButton.setOnClickListener {
                if (findPasswordViewModel.isSecretCodeCheckSuccess.value != true) {
                    requireContext().showAlertDialog(
                        getString(R.string.alert),
                        getString(R.string.check_secret_code),
                        getString(R.string.confirm)
                    ) { dialog ->
                        dialog.dismiss()
                    }
                    return@setOnClickListener
                }

                goToChangePasswordFragment()
            }
        }
    }

    private fun initObserver() {
        findPasswordViewModel.isSecretCodeCheckSuccess.observe(this) {
            when (it) {
                true -> setInputArea(false)
                else -> setInputArea(true)
            }
        }

        findPasswordViewModel.alertMessage.observe(this) {
            if (it == null) return@observe

            requireContext().showAlertDialog(
                getString(R.string.alert),
                it,
                getString(R.string.confirm)
            ) { dialog ->
                dialog.dismiss()
            }
        }
    }

    private fun initRequestPhoneNumber() {
        requestPhoneNumberUtil = RequestPhoneNumberUtil.register(requireActivity())
    }

    private fun setInputArea(isEnable: Boolean) {
        with(binding) {
            userIdEditText.isEnabled = isEnable
            userPhoneNumberEditText.isEnabled = isEnable
            userPhoneNumberSecretEditText.isEnabled = isEnable
        }
    }

    private fun goToChangePasswordFragment() {
        val action =
            FindPasswordFragmentDirections.actionFindPasswordFragmentToChangePasswordFragment(
                account,
                phoneNumber,
                secretCode
            )
        findNavController().navigate(action)
    }

    private fun registerSMSBroadCastReceiver() {
        SmsReceiver.register(requireActivity()) {
            if (it.isSuccess) {
                val code = (it.getOrNull() ?: "").split("[")[2].split("]")[0]
                binding.userPhoneNumberSecretEditText.setText(code)
            }
        }
    }

    private fun requestPhoneNumber() {
        requestPhoneNumberUtil.requestHint { result: Result<String> ->
            when {
                result.isSuccess -> {
                    val phoneNumber = (result.getOrNull() ?: "").replace("+82", "0")
                    binding.userPhoneNumberEditText.setText(phoneNumber)
                }
                result.isFailure -> {
                    binding.userPhoneNumberEditText.isFocusable = true
                    binding.userPhoneNumberEditText.isFocusableInTouchMode = true
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = FindPasswordFragment()
    }
}
