package com.dicelab.whopuppy.customview

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable.INFINITE
import com.dicelab.whopuppy.R

class StatusAnimationView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {
    lateinit var lottieAnimationView: LottieAnimationView
    var status: ProgressStatus = ProgressStatus.STOP
        set(value) {
            when (value) {
                ProgressStatus.STOP -> setAnimationStop()
                ProgressStatus.LOADING -> setLoadingAnimation()
                ProgressStatus.FAIL -> setFailAnimation()
                ProgressStatus.SUCCESS -> setSuccessAnimation()
            }
            field = value
        }

    init {
        initView()
        attributeSet?.let {
            getAttrs(it, defStyleAttr)
        }
    }

    private fun initView() {
        val view = LayoutInflater.from(context).inflate(R.layout.status_animation_view, null)
        addView(view)
        lottieAnimationView = view.findViewById(R.id.lottie_animation_view)
    }

    private fun getAttrs(attrs: AttributeSet, defStyle: Int) {
        val typedArray: TypedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.StatusAnimationView,
            defStyle,
            0
        )
        setTypeArray(typedArray)
    }

    private fun setTypeArray(typedArray: TypedArray) {
        try {
            val statusType = typedArray.getInt(
                R.styleable.StatusAnimationView_status,
                ProgressStatus.STOP.status
            )
            status = ProgressStatus.values()[statusType]
        } finally {
            typedArray.recycle()
        }
    }

    private fun setAnimationStop() {
        with(lottieAnimationView) {
            pauseAnimation()
            visibility = View.INVISIBLE
        }
    }

    private fun setLoadingAnimation() {
        with(lottieAnimationView) {
            setAnimation(LOADING_ANIMATION_FILE_NAME)
            repeatCount = INFINITE
            playAnimation()
            visibility = View.VISIBLE
        }
    }

    private fun setFailAnimation() {
        with(lottieAnimationView) {
            setAnimation(FAIL_ANIMATION_FILE_NAME)
            repeatCount = 0
            playAnimation()
            visibility = View.VISIBLE
        }
    }

    private fun setSuccessAnimation() {
        with(lottieAnimationView) {
            setAnimation(SUCCESS_ANIMATION_FILE_NAME)
            repeatCount = 0
            playAnimation()
            visibility = View.VISIBLE
        }
    }

    companion object {
        private const val LOADING_ANIMATION_FILE_NAME = "status_loading.json"
        private const val FAIL_ANIMATION_FILE_NAME = "status_fail.json"
        private const val SUCCESS_ANIMATION_FILE_NAME = "status_success.json"
    }
}

@BindingAdapter("status")
fun setStatus(view: StatusAnimationView, status: ProgressStatus) {
    view.status = status
}
