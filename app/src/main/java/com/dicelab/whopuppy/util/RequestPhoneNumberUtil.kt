package com.dicelab.whopuppy.util

import android.content.IntentSender
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest
import com.orhanobut.logger.Logger
import java.lang.ref.WeakReference

class RequestPhoneNumberUtil private constructor() : LifecycleObserver {
    private var activity: WeakReference<FragmentActivity>? = null
    private var requestPhoneNumberHint: ActivityResultLauncher<IntentSenderRequest>? = null
    private var resultCallBack: ((Result<String>) -> Unit)? = null

    private fun register(activity: FragmentActivity) {
        this.activity = WeakReference(activity)
        activity.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun onCreate() {
        requestPhoneNumberHint =
            activity?.get()
                ?.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                    when (result.resultCode) {
                        AppCompatActivity.RESULT_OK -> {
                            val credential: Credential? =
                                result.data?.getParcelableExtra(Credential.EXTRA_KEY)
                            val phoneNumber = (credential?.id ?: "")
                            this.resultCallBack?.invoke(Result.success(phoneNumber))
                        }
                        else -> {
                            this.resultCallBack?.invoke(Result.failure(IllegalStateException("Fail Request")))
                        }
                    }
                }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        activity = null
        requestPhoneNumberHint = null
        resultCallBack = null
    }

    fun requestHint(resultCallBack: (Result<String>) -> Unit) {
        this.resultCallBack = resultCallBack

        if (activity?.get() == null || activity?.get()?.lifecycle!!.currentState == Lifecycle.State.DESTROYED) {
            this.resultCallBack?.invoke(Result.failure(IllegalStateException("Activity is Empty")))
            return
        }

        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()

        val intent = Credentials.getClient(activity!!.get()!!).getHintPickerIntent(hintRequest)

        try {
            requestPhoneNumberHint?.launch(IntentSenderRequest.Builder(intent.intentSender).build())
        } catch (e: IntentSender.SendIntentException) {
            this.resultCallBack?.invoke(Result.failure(e))
            Logger.e("${e.message}")
        }
    }

    companion object {
        private const val TAG = "RequestPhoneNumber"

        @JvmStatic
        fun register(activity: FragmentActivity): RequestPhoneNumberUtil {
            if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                throw IllegalStateException(
                    "$TAG is attempting to register while current state is ${activity.lifecycle.currentState}. $TAG must call register before they are STARTED."
                )
            }

            val requestPhoneNumberUtil = RequestPhoneNumberUtil()
            requestPhoneNumberUtil.register(activity)
            return requestPhoneNumberUtil
        }
    }
}
