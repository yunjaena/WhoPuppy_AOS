package com.dicelab.whopuppy.activity

import android.os.Bundle
import com.dicelab.whopuppy.R
import com.dicelab.whopuppy.base.activity.ViewBindingActivity
import com.dicelab.whopuppy.databinding.ActivityMyInfoEditBinding
import com.dicelab.whopuppy.fragment.MyPageFragment
import com.dicelab.whopuppy.util.UpdateEvent
import com.dicelab.whopuppy.util.debounce
import com.dicelab.whopuppy.util.showToast
import com.dicelab.whopuppy.viewmodel.MyInfoEditViewModel
import io.reactivex.rxjava3.kotlin.addTo
import org.greenrobot.eventbus.EventBus
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyInfoEditActivity : ViewBindingActivity<ActivityMyInfoEditBinding>() {
    override val layoutId: Int = R.layout.activity_my_info_edit
    private val myInfoEditViewModel: MyInfoEditViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        initView()
        setBaseAppBar(getString(R.string.my_info_edit))
        setBackKey()
        setCheckButton {
            myInfoEditViewModel.changeNickName()
        }
        initObserver()
    }

    private fun initView() {
        binding.vm = myInfoEditViewModel
        binding.userNickNameEditText.debounce()
            .subscribe { text ->
                myInfoEditViewModel.checkNickNameValid(text.toString())
            }.addTo(compositeDisposable)
    }

    private fun initObserver() {
        with(myInfoEditViewModel) {
            nickNameChangeSuccessEvent.observe(this@MyInfoEditActivity) {
                showToast(getString(R.string.update_success))
                EventBus.getDefault().post((UpdateEvent(MyPageFragment.TAG)))
                finish()
                return@observe
            }

            nickNameChangeFailMessage.observe(this@MyInfoEditActivity) {
                if (it != null)
                    showToast(it)
            }
        }
    }
}
