package com.oims.smsread.presentation.features.readsms.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.util.Log

/**
 *Created by SATYAA on 24/11/24.
 * Copyright (c) 2024 smsReadApp All rights reserved.
 */
class MessageReceiver : BroadcastReceiver() {

    private var mListener: MessageListener? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("SMSReceived", "onReceive")
        if (intent?.action == "android.provider.Telephony.SMS_RECEIVED") {
            val pdus = intent.extras?.get("pdus") as? Array<*>
            val messages = pdus?.map { SmsMessage.createFromPdu(it as ByteArray) }

            messages?.forEach { message ->
                // Process the received SMS message
                Log.i("SMSReceived", "msg body ${message.messageBody}")
                Log.i("SMSReceived", "msg display ${message.displayMessageBody}")
//                val senderNum = message.displayOriginatingAddress
                val messageBody = message.displayMessageBody
                // Update your UI or database with the received message
                mListener?.messageReceived(messageBody)
            }
        }
    }

    fun bindListener(listener: MessageListener) {
        mListener = listener
    }
}