package com.dicelab.whopuppy.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import com.dicelab.whopuppy.R
import com.dicelab.whopuppy.base.activity.ViewBindingActivity
import com.dicelab.whopuppy.base.handleBackPressedListener
import com.dicelab.whopuppy.databinding.ActivityMainBinding
import com.dicelab.whopuppy.fragment.BoardListFragment
import com.dicelab.whopuppy.fragment.ChatListFragment
import com.dicelab.whopuppy.fragment.HomeFragment
import com.dicelab.whopuppy.fragment.MyPageFragment
import com.dicelab.whopuppy.util.showToast

class MainActivity : ViewBindingActivity<ActivityMainBinding>() {
    override val layoutId: Int = R.layout.activity_main
    private var isBackButtonClicked = false
    private val fragmentMap = mutableMapOf<Int, Pair<Fragment, String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initBackPressed()
        if (savedInstanceState == null) {
            selectHomeFragment()
        }
    }

    private fun initBackPressed() {
        handleBackPressedListener(
            onBackPressed = { goToHomeFragment() },
            singleClickAction = { showToast(getString(R.string.press_back_button_to_exit)) },
            doubleClickAction = { finish() }
        )
    }


    private fun goToHomeFragment(): Boolean {
        return if (binding.bottomNavigation.selectedItemId != R.id.action_home) {
            selectHomeFragment()
            true
        } else {
            false
        }
    }

    private fun initView() {
        fragmentMap[R.id.action_home] = HomeFragment.newInstance() to HomeFragment.TAG
        fragmentMap[R.id.action_board] = BoardListFragment.newInstance() to BoardListFragment.TAG
        fragmentMap[R.id.action_chat] = ChatListFragment.newInstance() to ChatListFragment.TAG
        fragmentMap[R.id.action_my_page] = MyPageFragment.newInstance() to MyPageFragment.TAG

        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            fragmentMap[it.itemId]?.let { fragmentPair ->
                val fragment = fragmentPair.first
                val fragmentTag = fragmentPair.second
                showFragment(fragment, fragmentTag)
                true
            } ?: false
        }
    }

    private fun selectHomeFragment() {
        binding.bottomNavigation.selectedItemId = R.id.action_home
    }

    private fun showFragment(fragment: Fragment, tag: String) {
        val transaction = supportFragmentManager.beginTransaction()
        val currentFragment = supportFragmentManager.primaryNavigationFragment
        val savedFragment = supportFragmentManager.findFragmentByTag(tag)

        if (currentFragment != null) {
            transaction.hide(currentFragment)
        }

        val primaryNavigationFragment: Fragment = when (savedFragment) {
            null -> {
                transaction.add(R.id.container, fragment, tag)
                fragment
            }
            else -> {
                transaction.show(savedFragment)
                savedFragment
            }
        }

        transaction.setPrimaryNavigationFragment(primaryNavigationFragment)
        transaction.setReorderingAllowed(true)
        transaction.commitNowAllowingStateLoss()
    }

    override fun onBackPressed() {
        if (binding.bottomNavigation.selectedItemId != R.id.action_home) {
            selectHomeFragment()
            return
        }
        handleBackPressed()
    }

    private fun handleBackPressed() {
        if (isBackButtonClicked) {
            finish()
            return
        }
        this.isBackButtonClicked = true
        showToast(getString(R.string.press_back_button_to_exit))
        Handler(Looper.getMainLooper()).postDelayed({ isBackButtonClicked = false }, 2000)
    }
}
