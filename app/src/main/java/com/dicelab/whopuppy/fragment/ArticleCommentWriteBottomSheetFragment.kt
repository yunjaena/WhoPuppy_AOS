package com.dicelab.whopuppy.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.dicelab.whopuppy.R
import com.dicelab.whopuppy.databinding.FragmentArticleCommentWriteBottomSheetBinding
import com.dicelab.whopuppy.util.UpdateEvent
import com.dicelab.whopuppy.util.setOnSingleClickListener
import com.dicelab.whopuppy.util.showToast
import com.dicelab.whopuppy.viewmodel.CommentViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.greenrobot.eventbus.EventBus
import org.koin.androidx.viewmodel.ext.android.viewModel

class ArticleCommentWriteBottomSheetFragment : BottomSheetDialogFragment() {
    lateinit var binding: FragmentArticleCommentWriteBottomSheetBinding
    private val commentViewModel: CommentViewModel by viewModel()
    private var articleId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_article_comment_write_bottom_sheet,
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
        isCancelable = false
        val imageUrl = arguments?.getString(EXTRA_PROFILE_URL)
        setProfileImage(imageUrl)
        setVisibilitySendButton()
        setCommentEditTextListener()
        setAddCommentButton()
        binding.addCommentEditText.requestFocus()
    }

    private fun setAddCommentButton() {
        binding.addCommentButton.setOnSingleClickListener {
            val comment = binding.addCommentEditText.text.toString().trim()
            commentViewModel.addComment(articleId, comment)
        }
    }

    private fun setVisibilitySendButton() {
        val textCount = binding.addCommentEditText.text.toString().trim().count()
        if (textCount > 0) {
            binding.addCommentButton.visibility = View.VISIBLE
        } else {
            binding.addCommentButton.visibility = View.GONE
        }
    }

    private fun setCommentEditTextListener() {
        binding.addCommentEditText.addTextChangedListener {
            setVisibilitySendButton()
        }
    }

    private fun setProfileImage(imageUrl: String?) {
        Glide.with(this)
            .load(imageUrl)
            .error(R.drawable.ic_brown_dog)
            .into(binding.profileImageView)
    }

    private fun initObserver() {
        with(commentViewModel) {
            showErrorMessage.observe(viewLifecycleOwner) {
                if (it != null)
                    requireContext().showToast(it)
            }

            commentWriteSuccessEvent.observe(viewLifecycleOwner) {
                EventBus.getDefault().post(UpdateEvent(ArticleCommentBottomSheetFragment.TAG))
                requireContext().showToast(getString(R.string.write_complete))
                dismiss()
            }
        }
    }

    companion object {
        const val TAG = "ArticleCommentWriteBottomSheetFragment"
        const val EXTRA_PROFILE_URL = "EXTRA_PROFILE_URL"
        const val EXTRA_ARTICLE_ID = "ARTICLE_ID"

        fun newInstance(
            articleId: Long,
            profileUrl: String?
        ): ArticleCommentWriteBottomSheetFragment {
            return ArticleCommentWriteBottomSheetFragment().apply {
                arguments = bundleOf(EXTRA_ARTICLE_ID to articleId, EXTRA_PROFILE_URL to profileUrl)
            }
        }
    }
}
