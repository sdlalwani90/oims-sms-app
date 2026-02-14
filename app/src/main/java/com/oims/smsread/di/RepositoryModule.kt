package com.oims.smsread.di

import com.oims.smsread.data.repository.SubmitOTPRepository
import com.oims.smsread.data.repository.SubmitOTPRepositoryImpl
import org.koin.dsl.module

/**
 *Created by SATYAA on 25/11/24.
 * Copyright (c) 2024 smsReadApp All rights reserved.
 */

val repositoryModule = module {
    single<SubmitOTPRepository> {
        SubmitOTPRepositoryImpl(apiService = get())
    }
    single {
        SubmitOTPRepositoryImpl(apiService = get())
    }
}