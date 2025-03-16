package com.oims.smsread.di

import org.koin.dsl.module

/**
 *Created by SATYAA on 25/11/24.
 * Copyright (c) 2024 smsReadApp All rights reserved.
 */
//https://github.com/mende273/KtorAndroidClientExample?tab=readme-ov-file

val appModule = module {
    includes(networkModule, repositoryModule, viewModelModule)
}