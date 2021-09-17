package com.yunjaena.whopuppy.util

import androidx.fragment.app.FragmentManager
import com.yunjaena.whopuppy.data.Area
import com.yunjaena.whopuppy.dialog.AbandonedDogSortDialog
import com.yunjaena.whopuppy.dialog.AreaSelectorDialog

fun FragmentManager.showAbandonedDogSortDialog() {
    val tag = AbandonedDogSortDialog.TAG
    val dialog = AbandonedDogSortDialog.getInstance()
    if (findFragmentByTag(tag) != null || isDestroyed) return
    dialog.showNow(this, tag)
}

fun FragmentManager.showAreaSelectorDialog(
    selectArea: Area = Area.ALL,
    isAllAreaSelectPossible: Boolean = true
) {
    val tag = AreaSelectorDialog.TAG
    val dialog = AreaSelectorDialog.newInstance(selectArea, isAllAreaSelectPossible)
    if (findFragmentByTag(tag) != null || isDestroyed) return
    dialog.showNow(this, tag)
}
