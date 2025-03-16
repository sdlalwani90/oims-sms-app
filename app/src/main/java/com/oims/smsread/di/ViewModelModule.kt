package com.oims.smsread.di

import com.oims.smsread.presentation.features.readsms.viewmodel.MainViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.scope.get
import org.koin.dsl.module

/**
 *Created by SATYAA on 25/11/24.
 * Copyright (c) 2024 smsReadApp All rights reserved.
 */

val viewModelModule = module {
    viewModel { MainViewModel(get()) }
}