package com.oims.smsread.data.repository

import android.util.Log
import com.oims.smsread.data.remote.network.ApiService
import io.ktor.http.isSuccess


/**
 *Created by SATYAA on 30/11/24.
 * Copyright (c) 2024 smsReadApp All rights reserved.
 */
class SubmitOTPRepositoryImpl(
    private val apiService: ApiService
) : SubmitOTPRepository {
    override suspend fun submitOtp(cName: String, code: String): Boolean =
        try {
            val response = apiService.submitOTP(cName, code)
            if (response.status.isSuccess()) {
                Log.d("SMSReceived", "Submit OTP successful")
                true
            } else {
                Log.w("SMSReceived", "Submit OTP Failed ${response.status.value} ")
                false
            }
        } catch (e: Exception) {
            Log.e("SMSReceived", "Submit Error: ${e.message} ")
            false
        }
}
