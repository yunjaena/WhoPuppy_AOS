package com.yunjaena.whopuppy.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.orhanobut.logger.Logger

class SMSBroadCastReceiver : BroadcastReceiver() {
    var smsReceiveCallBack: ((String) -> Unit)? = null
    override fun onReceive(context: Context?, intent: Intent?) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action) {
            val extras = intent.extras
            val status = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val message = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
                    Logger.d("SMSBroadCastReceiver message : $message ")
                    try {
                        val code = message.split("[")[2].split("]")[0]
                        smsReceiveCallBack?.invoke(code)
                    } catch (e: Exception) {
                        Logger.e("SMSBroadCastReceiver error ${e.message}")
                    }
                }

                CommonStatusCodes.TIMEOUT -> {
                    return
                }
            }
        }
    }
}
