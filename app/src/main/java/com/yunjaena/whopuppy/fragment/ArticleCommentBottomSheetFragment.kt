package com.yunjaena.whopuppy.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yunjaena.whopuppy.R
import com.yunjaena.whopuppy.databinding.FragmentArticleCommentBottomSheetBinding


class ArticleCommentBottomSheetFragment : BottomSheetDialogFragment() {
    lateinit var binding: FragmentArticleCommentBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_article_comment_bottom_sheet,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        val height = arguments?.getInt(EXTRA_HEIGHT, 0) ?: 0
        dialog?.setOnShowListener {
            val dialog = it as BottomSheetDialog
            val bottomSheet = dialog.findViewById<View>(R.id.design_bottom_sheet)
            bottomSheet?.let { sheet ->
                dialog.behavior.peekHeight = height
                sheet.parent.parent.requestLayout()
                val behavior = BottomSheetBehavior.from(bottomSheet)
                val layoutParams = bottomSheet.layoutParams
                layoutParams.height = height
                bottomSheet.layoutParams = layoutParams
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
            }
            clearDim()
        }

        binding.commentRecyclerView.isNestedScrollingEnabled = false
    }

    private fun clearDim() {
        val window: Window = dialog?.window ?: return
        val windowParams: WindowManager.LayoutParams = window.attributes
        windowParams.dimAmount = 0f
        windowParams.flags = windowParams.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
        window.attributes = windowParams
    }

    companion object {
        const val TAG = "ArticleCommentBottomSheetFragment"
        const val EXTRA_ARTICLE_ID = "ARTICLE_ID"
        const val EXTRA_HEIGHT = "ARTICLE_ID"

        fun newInstance(articleId: Long, height: Int): ArticleCommentBottomSheetFragment {
            return ArticleCommentBottomSheetFragment().apply {
                arguments = bundleOf(EXTRA_ARTICLE_ID to articleId, EXTRA_HEIGHT to height)
            }
        }
    }
}
