package com.dicelab.whopuppy.util

import androidx.fragment.app.FragmentManager
import com.dicelab.whopuppy.data.Area
import com.dicelab.whopuppy.dialog.AbandonedDogSortDialog
import com.dicelab.whopuppy.dialog.AreaSelectorDialog
import com.dicelab.whopuppy.dialog.TitleInputDialog
import com.dicelab.whopuppy.fragment.ArticleCommentBottomSheetFragment
import com.dicelab.whopuppy.fragment.ArticleCommentWriteBottomSheetFragment

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


fun FragmentManager.showArticleCommentBottomSheetDialog(
    height: Int,
    articleId: Long
) {
    val tag = ArticleCommentBottomSheetFragment.TAG
    val dialog = ArticleCommentBottomSheetFragment.newInstance(articleId, height)
    if (findFragmentByTag(tag) != null || isDestroyed) return
    dialog.showNow(this, tag)
}

fun FragmentManager.showArticleCommentWriteBottomSheetDialog(
    articleId: Long,
    profileUrl: String?
) {
    val tag = ArticleCommentWriteBottomSheetFragment.TAG
    val dialog = ArticleCommentWriteBottomSheetFragment.newInstance(articleId, profileUrl)
    if (findFragmentByTag(tag) != null || isDestroyed) return
    dialog.showNow(this, tag)
}

fun FragmentManager.showTitleInputDialog() {
    val tag = TitleInputDialog.TAG
    val dialog = TitleInputDialog.newInstance()
    if (findFragmentByTag(tag) != null || isDestroyed) return
    dialog.showNow(this, tag)
}
