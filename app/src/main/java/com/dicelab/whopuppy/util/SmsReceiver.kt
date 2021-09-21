package com.dicelab.whopuppy.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.orhanobut.logger.Logger
import java.lang.ref.WeakReference

class SmsReceiver private constructor() : LifecycleObserver {
    private var activity: WeakReference<FragmentActivity>? = null
    private var resultCallBack: ((Result<String>) -> Unit)? = null
    private var smsBroadCastReceiver: BroadcastReceiver? = null

    private fun register(activity: FragmentActivity, resultCallBack: (Result<String>) -> Unit) {
        this.activity = WeakReference(activity)
        this.resultCallBack = resultCallBack
        startSMSRetriever()
        activity.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun registerSMSBroadCastReceiver() {
        initBroadCastReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(SMS_RETRIEVED_ACTION)
        activity?.get()?.registerReceiver(
            smsBroadCastReceiver,
            intentFilter,
            SMS_PERMISSION,
            null
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun unRegisterSmsBroadCastReceiver() {
        if (smsBroadCastReceiver != null) {
            activity?.get()?.unregisterReceiver(smsBroadCastReceiver)
            smsBroadCastReceiver = null
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        activity = null
        resultCallBack = null
        smsBroadCastReceiver = null
    }

    private fun initBroadCastReceiver() {
        smsBroadCastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action) {
                    val extras = intent.extras
                    val status = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

                    when (status.statusCode) {
                        CommonStatusCodes.SUCCESS -> {
                            val message = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
                            Logger.d("SMSBroadCastReceiver message : $message ")
                            resultCallBack?.invoke(Result.success(message))
                        }

                        CommonStatusCodes.TIMEOUT -> {
                            sendError("SmsReceiver Time out")
                            return
                        }
                    }
                }
            }
        }
    }

    private fun startSMSRetriever() {
        if (!isActivityActive()) {
            return
        }
        val client = SmsRetriever.getClient(activity!!.get()!!)
        val task = client.startSmsRetriever()
        task.addOnSuccessListener {}
        task.addOnFailureListener {
            sendError("SmsRetriever initialize failed")
        }
    }

    private fun sendError(message: String) {
        if (!isActivityActive())
            resultCallBack?.invoke(Result.failure(IllegalStateException(message)))
    }

    private fun isActivityActive(): Boolean {
        return !(activity?.get() == null || activity!!.get()!!.lifecycle.currentState == Lifecycle.State.DESTROYED)
    }

    companion object {
        private const val TAG = "SmsReceiver"
        const val SMS_PERMISSION = "com.google.android.gms.auth.api.phone.permission.SEND"
        const val SMS_RETRIEVED_ACTION = "com.google.android.gms.auth.api.phone.SMS_RETRIEVED"

        @JvmStatic
        fun register(
            activity: FragmentActivity,
            resultCallBack: (Result<String>) -> Unit
        ): SmsReceiver {
            val smsReceiver = SmsReceiver()
            smsReceiver.register(activity, resultCallBack)
            return smsReceiver
        }

        @JvmStatic
        fun getAppSignatureCode(context: Context): String {
            val signatures = context.getAppSignatures()
            return when {
                signatures.size > 0 -> signatures[0]
                else -> ""
            }
        }
    }
}
