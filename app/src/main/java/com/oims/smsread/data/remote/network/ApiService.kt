package com.oims.smsread.data.remote.network

import com.oims.smsread.core.common.Constant
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody

/**
 *Created by SATYAA on 25/11/24.
 * Copyright (c) 2024 smsReadApp All rights reserved.
 */
class ApiService(private val client: HttpClient)  {

     suspend fun submitOTP(companyName: String, otp: String) =
          client.post(Constant.APIEndPoint.SUBMIT_OTP) {
               val body = mapOf(
                    Constant.APIKeys.COMPANY_NAME to companyName,
                    Constant.APIKeys.OTP to otp
               )
               setBody(body)
          }

}