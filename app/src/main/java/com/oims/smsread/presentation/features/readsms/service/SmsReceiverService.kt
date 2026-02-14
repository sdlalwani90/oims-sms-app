package com.oims.smsread.presentation.features.readsms.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.oims.smsread.R
import com.oims.smsread.presentation.features.readsms.receiver.MessageReceiver
import com.oims.smsread.presentation.features.readsms.viewmodel.MainViewModel
import com.oims.smsread.presentation.utils.SmsParser
import org.koin.android.ext.android.inject

/**
 *Created by SATYAA on 24/11/24.
 * Copyright (c) 2024 smsReadApp All rights reserved.
 */
class SmsReceiverService : Service() {
    private val messageReceiver = MessageReceiver()
    private val viewModel :MainViewModel by inject()

    companion object {
        private const val NOTIFICATION_ID = "SMSChannel"
        private const val NOTIFICATION_NAME = "SMS Service Channel"
        private const val NOTIFICATION_TITLE = "Read incoming SMS"
//        private const val HDFC = "hdfc"
//        private const val MEDI_ASSIST = "MediAssist"
//        private const val TATA_AIG = "Tata AIG"
//        private const val STAR_HEALTH = "STAR Health Insurance"
    }

    override fun onCreate() {
        super.onCreate()
        // Create the notification channel for Android 8.0 (Oreo) and above
        createNotificationChannel()
        startForegroundService()

        val intentFilter = IntentFilter("android.provider.Telephony.SMS_RECEIVED")
        registerReceiver(messageReceiver, intentFilter)

//        messageReceiver.bindListener(object : MessageListener {
//            override fun messageReceived(message: String?) {
//                callSubmitOTPAPI(message)
//            }
//        })
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(messageReceiver)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                NOTIFICATION_ID,
                NOTIFICATION_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }
    private fun startForegroundService() {
        val notification = NotificationCompat.Builder(this, NOTIFICATION_ID)
            .setContentTitle(NOTIFICATION_TITLE)
            .setContentText("Running in background")
            .setSmallIcon(R.mipmap.ic_launcher) // Use your own small icon
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                1,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(1, notification)
        }
    }

    private fun callSubmitOTPAPI(msg: String?){
        msg?.let {
            val (companyName, code) = SmsParser.parse(msg)
            if(companyName.isNotEmpty() && code.isNotEmpty()){
                Log.d("SMSReceived","values cName: $companyName , Code: $code")
                viewModel.submitOTP(companyName,code)
            }
        }
    }

//    private fun extractAccessCodeAndCompanyName(inputString: String): Pair<String, String> {
//        val regex = Regex("(?i)otp\\D*(\\b\\d{4}\\b|\\b\\d{6}\\b)")
//        val match = regex.find(inputString)
//        val code = match?.groupValues?.get(1) ?: ""
//        var companyName = ""
//        if(code.isNotEmpty()) {
//            val companyMap = mapOf(
//                "HDFC ERGO" to HDFC,
//                "MediAssist" to MEDI_ASSIST,
//                "Tata AIG" to TATA_AIG,
//                "STAR Health Insurance" to STAR_HEALTH
//            )
//            companyName = companyMap.entries
//                .firstOrNull {inputString.contains(it.key, ignoreCase = true)}?.value ?: ""
//        }
//        return Pair(companyName, code)
//    }

}