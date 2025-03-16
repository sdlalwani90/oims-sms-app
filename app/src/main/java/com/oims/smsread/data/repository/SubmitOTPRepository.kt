package com.oims.smsread.data.repository

/**
 *Created by SATYAA on 30/11/24.
 * Copyright (c) 2024 smsReadApp All rights reserved.
 */
 interface SubmitOTPRepository  {
    suspend fun submitOtp(cName:String,code:String)
}