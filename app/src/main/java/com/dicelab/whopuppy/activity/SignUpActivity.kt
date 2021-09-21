package com.dicelab.whopuppy.activity

import android.os.Bundle
import android.widget.Toast
import com.dicelab.whopuppy.R
import com.dicelab.whopuppy.base.activity.ViewBindingActivity
import com.dicelab.whopuppy.base.showAlertDialog
import com.dicelab.whopuppy.customview.ProgressStatus
import com.dicelab.whopuppy.data.entity.User
import com.dicelab.whopuppy.databinding.ActivitySignUpBinding
import com.dicelab.whopuppy.util.RequestPhoneNumberUtil
import com.dicelab.whopuppy.util.SmsReceiver
import com.dicelab.whopuppy.util.debounce
import com.dicelab.whopuppy.util.getEmojiFilter
import com.dicelab.whopuppy.util.setOnSingleClickListener
import com.dicelab.whopuppy.viewmodel.SignUpViewModel
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.kotlin.addTo
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignUpActivity : ViewBindingActivity<ActivitySignUpBinding>() {
    override val layoutId: Int = R.layout.activity_sign_up
    private val signUpViewModel: SignUpViewModel by viewModel()
    private lateinit var requestPhoneNumberUtil: RequestPhoneNumberUtil
    private val account
        get() = binding.userIdEditText.text.toString()
    private val phoneNumber
        get() = binding.userPhoneNumberEditText.text.toString()
    private val password
        get() = binding.userPasswordEditText.text.toString()
    private val nickname
        get() = binding.userNickNameEditText.text.toString()
    private val code
        get() = binding.userPhoneNumberSecretEditText.text.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        registerSMSBroadCastReceiver()
        initView()
        useDefaultLoading(signUpViewModel)
        initRequestPhoneNumber()
        initObserver()
    }

    private fun registerSMSBroadCastReceiver() {
        SmsReceiver.register(this) {
            if (it.isSuccess) {
                val code = (it.getOrNull() ?: "").split("[")[2].split("]")[0]
                binding.userPhoneNumberSecretEditText.setText(code)
            }
        }
    }

    private fun initObserver() {
        with(signUpViewModel) {
            phoneNumberStatus.observe(this@SignUpActivity) {
                when (it) {
                    ProgressStatus.SUCCESS, ProgressStatus.LOADING -> {
                        binding.userPhoneNumberSecretCheckButton.isEnabled = true
                        binding.userPhoneNumberSecret.isEnabled = true
                    }
                }
            }

            secretCodeStatus.observe(this@SignUpActivity) {
                when (it) {
                    ProgressStatus.SUCCESS, ProgressStatus.LOADING -> {
                        binding.userPhoneNumberSecretCheckButton.isEnabled = false
                        binding.userPhoneNumberSecret.isEnabled = false
                    }
                    else -> {
                        binding.userPhoneNumberSecretCheckButton.isEnabled = true
                        binding.userPhoneNumberSecret.isEnabled = true
                    }
                }
            }

            secretCodeStatusMessage.observe(this@SignUpActivity) { messageRes ->
                if (messageRes == null) return@observe
                Toast.makeText(applicationContext, messageRes, Toast.LENGTH_SHORT).show()
            }

            phoneNumberStatusMessage.observe(this@SignUpActivity) { messageRes ->
                if (messageRes == null) return@observe
                Toast.makeText(applicationContext, messageRes, Toast.LENGTH_SHORT).show()
            }

            signUpErrorMessage.observe(this@SignUpActivity) { message ->
                if (message == null) return@observe
                showAlertDialog(
                    getString(R.string.sign_up),
                    message,
                    getString(R.string.confirm)
                ) { dialog ->
                    dialog.dismiss()
                }
            }

            signUpResult.observe(this@SignUpActivity) { isSuccess ->
                when (isSuccess) {
                    true -> {
                        showSignUpSuccessDialog()
                    }
                    false -> {
                        showSignUpFailedDialog()
                    }
                }
            }
        }
    }

    private fun initRequestPhoneNumber() {
        requestPhoneNumberUtil = RequestPhoneNumberUtil.register(this)
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

    private fun initView() {
        with(binding) {
            vm = signUpViewModel

            userPhoneNumberEditText.isFocusable = false

            backButton.setOnClickListener {
                showBackPressAlert()
            }

            userIdEditText.filters = arrayOf(getEmojiFilter())
            userIdEditText.debounce()
                .subscribe { text ->
                    signUpViewModel.checkAccountValid(text.toString())
                }.addTo(compositeDisposable)

            userPasswordEditText.filters = arrayOf(getEmojiFilter())
            userPasswordEditText.debounce()
                .subscribe { text ->
                    signUpViewModel.checkPasswordValid(text.toString())
                }.addTo(compositeDisposable)
            userNickNameEditText.filters = arrayOf(getEmojiFilter())

            userNickNameEditText.debounce()
                .subscribe { text ->
                    signUpViewModel.checkNickNameValid(text.toString())
                }.addTo(compositeDisposable)

            userPhoneNumberEditText.setOnClickListener {
                requestPhoneNumber()
            }

            phoneNumberValidCheckButton.setOnClickListener {
                signUpViewModel.phoneNumberValidCheckWithSms(
                    phoneNumber,
                    SmsReceiver.getAppSignatureCode(this@SignUpActivity)
                )
            }

            userPasswordEditText.filters = arrayOf(getEmojiFilter())
            userPhoneNumberSecretCheckButton.setOnClickListener {
                signUpViewModel.checkSecretCode(phoneNumber, code)
            }

            signUpButton.setOnSingleClickListener {
                signUpViewModel.signUp(
                    User(
                        account = account,
                        nickname = nickname,
                        password = password,
                        phoneNumber = phoneNumber,
                        secret = code
                    )
                )
            }
        }
    }

    override fun onBackPressed() {
        showBackPressAlert()
    }

    private fun showBackPressAlert() {
        Snackbar.make(binding.container, R.string.sign_up_reset_alert, Snackbar.LENGTH_LONG).apply {
            setAction(R.string.yes) { finish() }
            show()
        }
    }

    private fun showSignUpSuccessDialog() {
        showAlertDialog(
            getString(R.string.sign_up),
            getString(R.string.sign_up_success),
            getString(R.string.confirm)
        ) { dialog ->
            dialog.dismiss()
            finish()
        }
    }

    private fun showSignUpFailedDialog() {
        showAlertDialog(
            getString(R.string.sign_up),
            getString(R.string.sign_up_failed),
            getString(R.string.confirm)
        ) { dialog ->
            dialog.dismiss()
        }
    }
}
