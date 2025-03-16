package com.oims.smsread.presentation.features.readsms.receiver

/**
 *Created by SATYAA on 24/11/24.
 * Copyright (c) 2024 smsReadApp All rights reserved.
 */
interface MessageListener {
    /**
     * To call this method when new message received and send back
     * @param message Message
     */
    fun messageReceived(message: String?)
}