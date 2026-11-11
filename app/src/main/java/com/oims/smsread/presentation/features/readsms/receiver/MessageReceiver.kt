package com.oims.smsread.presentation.features.readsms.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.SmsMessage
import android.util.Log
import com.oims.smsread.data.local.PostStatus
import com.oims.smsread.data.local.RecentOtpStore
import com.oims.smsread.data.repository.SubmitOTPRepositoryImpl
import com.oims.smsread.presentation.utils.SmsParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

/**
 *Created by SATYAA on 24/11/24.
 * Copyright (c) 2024 smsReadApp All rights reserved.
 */
class MessageReceiver : BroadcastReceiver() {

//    private var mListener: MessageListener? = null

    override fun onReceive(context: Context?, intent: Intent) {

        if (intent.action != "android.provider.Telephony.SMS_RECEIVED") return
        Log.i("SMSReceived", "onReceive")
        val bundle = intent.extras ?: return
        val pdus = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getSerializable("pdus", Array<ByteArray>::class.java)
        } else {
            @Suppress("DEPRECATION")
            bundle.get("pdus") as? Array<*>
        } ?: return


        val repository: SubmitOTPRepositoryImpl =
            KoinJavaComponent.get(SubmitOTPRepositoryImpl::class.java)

        for (pdu in pdus) {
            val format = bundle.getString("format")
            val sms = SmsMessage.createFromPdu(pdu as ByteArray, format)
            val message = sms.messageBody
            val timestamp = sms.timestampMillis

            Log.i("SMSReceived", "msg body $message")
            Log.i("SMSReceived", "msg timeStamp $timestamp")

            // Ignore old SMS (VERY IMPORTANT)
            if (System.currentTimeMillis() - timestamp > 2 * 60 * 1000) return

            val (company, code) = SmsParser.parse(message)
            Log.i("SMSReceived", "company: $company , code: $code")
            if (company.isNotEmpty() && code.isNotEmpty()) {
                // Shown on the main screen for 10 minutes, with whether the post succeeded.
                val store = context?.let { RecentOtpStore.get(it) }
                val capturedAt = System.currentTimeMillis()
                store?.record(company, code, capturedAt)

                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        Log.i("SMSReceived", "sending otp....")
                        val posted = repository.submitOtp(company, code)
                        store?.setStatus(company, capturedAt, if (posted) PostStatus.POSTED else PostStatus.FAILED)
                    } catch (e: Exception) {
                        Log.e("SMSReceived", "OTP API failed: ${e.message}")
                        store?.setStatus(company, capturedAt, PostStatus.FAILED)
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
        }
//        if (intent.action == "android.provider.Telephony.SMS_RECEIVED") {
//            val pdus = intent.extras?.get("pdus") as? Array<*>
//            val messages = pdus?.map { SmsMessage.createFromPdu(it as ByteArray) }
//
//            messages?.forEach { message ->
//                // Process the received SMS message
//                Log.i("SMSReceived", "msg body ${message.messageBody}")
//                Log.i("SMSReceived", "msg display ${message.displayMessageBody}")
////                val senderNum = message.displayOriginatingAddress
//                val messageBody = message.displayMessageBody
//                // Update your UI or database with the received message
//                mListener?.messageReceived(messageBody)
//            }
//        }
    }

//    fun bindListener(listener: MessageListener) {
//        mListener = listener
//    }
}