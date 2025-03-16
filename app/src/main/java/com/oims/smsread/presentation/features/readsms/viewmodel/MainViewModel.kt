package com.oims.smsread.presentation.features.readsms.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oims.smsread.data.repository.SubmitOTPRepositoryImpl
import kotlinx.coroutines.launch

/**
 *Created by SATYAA on 30/11/24.
 * Copyright (c) 2024 smsReadApp All rights reserved.
 */
class MainViewModel(private val submitOTPRepositoryImpl: SubmitOTPRepositoryImpl) : ViewModel() {

    fun submitOTP(cName: String,code:String){
        viewModelScope.launch {
            submitOTPRepositoryImpl.submitOtp(
                cName,code
            )
        }
    }

}