package com.yunjaena.whopuppy.activity

import android.content.Intent
import android.os.Bundle
import com.yunjaena.whopuppy.R
import com.yunjaena.whopuppy.base.activity.ViewBindingActivity
import com.yunjaena.whopuppy.databinding.ActivityLoginBinding

class LoginActivity : ViewBindingActivity<ActivityLoginBinding>() {
    override val layoutId: Int = R.layout.activity_login

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.signUpButton.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
        }
    }
}
