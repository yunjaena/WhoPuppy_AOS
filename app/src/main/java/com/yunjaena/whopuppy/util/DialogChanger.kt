package com.yunjaena.whopuppy.util

import androidx.fragment.app.FragmentManager
import com.yunjaena.whopuppy.dialog.AbandonedDogSortDialog

fun FragmentManager.showAbandonedDogSortDialog(){
    val tag = AbandonedDogSortDialog.TAG
    val dialog = AbandonedDogSortDialog.getInstance()
    if(findFragmentByTag(tag) != null || isDestroyed) return
    dialog.showNow(this, tag)
}
