package com.oims.smsread.data.repository

/**
 *Created by SATYAA on 30/11/24.
 * Copyright (c) 2024 smsReadApp All rights reserved.
 */
 interface SubmitOTPRepository  {
    /** Posts the OTP; true when the server accepted it. */
    suspend fun submitOtp(cName: String, code: String): Boolean
}