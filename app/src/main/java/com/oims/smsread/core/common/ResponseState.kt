package com.oims.smsread.core.common

/**
 *Created by SATYAA on 30/11/24.
 * Copyright (c) 2024 smsReadApp All rights reserved.
 */

//data class ResponseState<out T>(val status: Status, val data: T?, val message: String?) {
//    companion object {
//        fun <T> success(data: T?, message: String?): ResponseState<T> {
//            return ResponseState(Status.SUCCESS, data, message)
//        }
//
//        fun <T> error(data: T?, message: String?): ResponseState<T> {
//            return ResponseState(Status.ERROR, data, message)
//        }
//
//        fun <T> loading(): ResponseState<T> {
//            return ResponseState(Status.LOADING, null, null)
//        }
//    }
//}

sealed class ResponseState<out T> {
    data class Success<out T>(val data: T?, val message: String?) : ResponseState<T>()
    data class Error(val message: String?) : ResponseState<Nothing>()
    data object Loading : ResponseState<Nothing>()
}

enum class Status {
    SUCCESS, ERROR, LOADING
}