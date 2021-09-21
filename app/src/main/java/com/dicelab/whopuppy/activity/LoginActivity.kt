package com.dicelab.whopuppy.activity

import android.content.Intent
import android.os.Bundle
import com.dicelab.whopuppy.R
import com.dicelab.whopuppy.base.activity.ViewBindingActivity
import com.dicelab.whopuppy.base.showAlertDialog
import com.dicelab.whopuppy.databinding.ActivityLoginBinding
import com.dicelab.whopuppy.util.setOnSingleClickListener
import com.dicelab.whopuppy.util.showToast
import com.dicelab.whopuppy.viewmodel.LoginViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : ViewBindingActivity<ActivityLoginBinding>() {
    override val layoutId: Int = R.layout.activity_login
    private val loginViewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        initView()
        initObserver()
    }

    private fun initView() {
        useDefaultLoading(loginViewModel)
        with(binding) {
            login.setOnSingleClickListener {
                login()
            }
            signUpButton.setOnClickListener {
                startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
            }

            findPassword.setOnClickListener {
                startActivity(Intent(this@LoginActivity, FindPasswordActivity::class.java))
            }
        }
    }

    private fun initObserver() {
        with(loginViewModel) {
            loginSuccessEvent.observe(this@LoginActivity) {
                showToast(getString(R.string.login_success))
                Intent(this@LoginActivity, MainActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            }
            loginFailMessage.observe(this@LoginActivity) { message ->
                if (message == null) return@observe
                showAlertDialog(
                    getString(R.string.alert),
                    message,
                    getString(R.string.confirm)
                ) { dialog ->
                    dialog.dismiss()
                }
            }
        }
    }

    private fun login() {
        val id = binding.userIdEditText.text.toString()
        val password = binding.passwordEditText.text.toString()
        loginViewModel.login(id, password)
    }
}
