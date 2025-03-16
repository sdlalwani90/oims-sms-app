package com.oims.smsread.di

import com.oims.smsread.data.remote.network.ApiService
import com.oims.smsread.data.remote.network.httpClientAndroid
import io.ktor.client.HttpClient
import org.koin.dsl.module

/**
 *Created by SATYAA on 25/11/24.
 * Copyright (c) 2024 smsReadApp All rights reserved.
 */

val networkModule = module {
    single { provideApiService(get()) }
    single { provideHttpClient() }
}

fun provideHttpClient(): HttpClient {
    return httpClientAndroid
}

fun provideApiService(httpClient: HttpClient): ApiService {
    return ApiService(httpClient)
}