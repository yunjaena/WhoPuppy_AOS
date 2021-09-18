package com.yunjaena.whopuppy.fragment

import android.net.Uri
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.yunjaena.whopuppy.R
import com.yunjaena.whopuppy.base.fragment.ViewBindingFragment
import com.yunjaena.whopuppy.databinding.FragmentMyInfoBinding
import com.yunjaena.whopuppy.util.UpdateEvent
import com.yunjaena.whopuppy.util.goToLoginActivity
import com.yunjaena.whopuppy.util.goToMyInfoEditActivity
import com.yunjaena.whopuppy.util.goToOssLibraryActivity
import com.yunjaena.whopuppy.viewmodel.MyInfoViewModel
import gun0912.tedimagepicker.builder.TedImagePicker
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyPageFragment : ViewBindingFragment<FragmentMyInfoBinding>() {
    override val layoutId: Int = R.layout.fragment_my_info
    private val myInfoViewModel: MyInfoViewModel by viewModel()

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updatePage(event: UpdateEvent) {
        if (event.tag != TAG) return
        myInfoViewModel.getUserInfo()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        EventBus.getDefault().register(this)
        initView()
        initObserver()
        getUserNickName()
    }

    private fun initView() {
        binding.myInfoEditLayout.setOnClickListener {
            requireContext().goToMyInfoEditActivity()
        }

        binding.profileImageContainer.setOnClickListener {
            TedImagePicker.with(requireContext())
                .start { uri -> updateProfileImage(uri) }
        }

        binding.openSourceLayout.setOnClickListener {
            requireContext().goToOssLibraryActivity()
        }

        binding.logoutLayout.setOnClickListener {
            myInfoViewModel.logout()
        }
    }

    private fun initObserver() {
        with(myInfoViewModel) {
            userInfo.observe(viewLifecycleOwner) {
                setUserWelcomeText(it.nickname)
                setUserProfileImage(it.profileImageUrl)
            }

            logoutSuccess.observe(viewLifecycleOwner) {
                requireContext().goToLoginActivity()
            }
        }
    }

    private fun updateProfileImage(uri: Uri) {
        myInfoViewModel.updateUserProfile(uri)
    }

    private fun setUserWelcomeText(nickname: String?) {
        binding.welcomeTextView.text = getString(R.string.my_info_welcome_format, nickname ?: "")
    }

    private fun setUserProfileImage(userProfileImage: String?) {
        Glide.with(this)
            .load(userProfileImage)
            .error(R.drawable.ic_brown_dog)
            .into(binding.profileImageView)
    }

    private fun getUserNickName() {
        myInfoViewModel.getUserInfo()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    companion object {
        const val TAG = "MyPageFragment"
        fun newInstance(): MyPageFragment = MyPageFragment()
    }
}
