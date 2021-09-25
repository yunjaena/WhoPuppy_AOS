package com.dicelab.whopuppy.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.content.res.Configuration
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
import com.orhanobut.logger.Logger
import com.dicelab.whopuppy.R
import com.dicelab.whopuppy.adapter.CommentAdapter
import com.dicelab.whopuppy.base.getScreenHeight
import com.dicelab.whopuppy.base.getStatusBarHeight
import com.dicelab.whopuppy.databinding.FragmentArticleCommentBottomSheetBinding
import com.dicelab.whopuppy.util.UpdateEvent
import com.dicelab.whopuppy.util.showArticleCommentWriteBottomSheetDialog
import com.dicelab.whopuppy.util.showToast
import com.dicelab.whopuppy.viewmodel.CommentViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.androidx.viewmodel.ext.android.viewModel

class ArticleCommentBottomSheetFragment : BottomSheetDialogFragment() {
    lateinit var binding: FragmentArticleCommentBottomSheetBinding
    private lateinit var commentAdapter: CommentAdapter
    private val commentViewModel: CommentViewModel by viewModel()
    private var articleId: Long = 0

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshEvent(updateEvent: UpdateEvent) {
        if (updateEvent.tag != TAG) return
        commentViewModel.getComments(articleId, true)
    }

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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : BottomSheetDialog(requireContext(), theme) {
            override fun onBackPressed() {
                dismiss()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObserver()
    }

    private fun initView() {
        articleId = arguments?.getLong(EXTRA_ARTICLE_ID) ?: 0L
        dialog?.setOnShowListener {
            setDialogHeight(it)
            clearDim()
        }
        isCancelable = false
        initCloseButton()
        initRecyclerView()
        initSwipeRefreshLayout()
        commentViewModel.getComments(articleId)
    }

    private fun initSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            commentViewModel.getComments(articleId, true)
        }
    }

    private fun initCloseButton() {
        binding.closeButton.setOnClickListener {
            dismiss()
        }
    }

    private fun setDialogHeight(dialogInterface: DialogInterface) {
        val bottomSheet = dialog?.findViewById<View>(R.id.design_bottom_sheet)
        bottomSheet?.let { sheet ->
            val height = getDialogHeight()
            val dialog = dialogInterface as BottomSheetDialog
            dialog.behavior.peekHeight = height
            sheet.parent.parent.requestLayout()
            val behavior = BottomSheetBehavior.from(bottomSheet)
            val layoutParams = bottomSheet.layoutParams
            layoutParams.height = height
            bottomSheet.layoutParams = layoutParams
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        }
    }

    private fun getDialogHeight(): Int {
        val height = arguments?.getInt(EXTRA_HEIGHT, 0) ?: 0
        return if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            requireActivity().getScreenHeight() - requireActivity().getStatusBarHeight()
        } else {
            height
        }
    }

    private fun clearDim() {
        val window: Window = dialog?.window ?: return
        val windowParams: WindowManager.LayoutParams = window.attributes
        windowParams.dimAmount = 0f
        windowParams.flags = windowParams.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
        window.attributes = windowParams
    }

    private fun initRecyclerView() {
        commentAdapter = CommentAdapter()
        commentAdapter.commentWriteViewClickListener = {
            childFragmentManager.showArticleCommentWriteBottomSheetDialog(
                it.articleId,
                it.profileUrl
            )
        }
        binding.commentRecyclerView.adapter = commentAdapter
    }

    private fun initObserver() {
        with(commentViewModel) {
            commentViews.observe(viewLifecycleOwner) {
                commentAdapter.updateItem(it)
                binding.swipeRefreshLayout.isRefreshing = false
                setCommentCount(it.size - 1)
            }
            showErrorMessage.observe(viewLifecycleOwner) {
                if (it != null)
                    requireContext().showToast(it)
            }

            fetchFailEvent.observe(viewLifecycleOwner) {
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun setCommentCount(count: Int) {
        if (count < 0) {
            binding.commentCountTextView.visibility = View.GONE
            return
        }
        binding.commentCountTextView.visibility = View.VISIBLE
        binding.commentCountTextView.text = count.toString()
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    companion object {
        const val TAG = "ArticleCommentBottomSheetFragment"
        const val EXTRA_ARTICLE_ID = "ARTICLE_ID"
        const val EXTRA_HEIGHT = "EXTRA_HEIGHT"

        fun newInstance(articleId: Long, height: Int): ArticleCommentBottomSheetFragment {
            return ArticleCommentBottomSheetFragment().apply {
                arguments = bundleOf(EXTRA_ARTICLE_ID to articleId, EXTRA_HEIGHT to height)
            }
        }
    }
}
