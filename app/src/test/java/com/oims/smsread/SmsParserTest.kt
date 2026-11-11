package com.oims.smsread

import com.oims.smsread.presentation.utils.SmsParser
import org.junit.Assert.assertEquals
import org.junit.Test

class SmsParserTest {

    @Test
    fun careHealthOtp_isDetected() {
        val sms = "9798 is your OTP to verify your password. Please do not share this with anyone. " +
            "Kindly note this is valid for the next 5 minutes. Care Health"

        assertEquals("Care Health Insurance" to "9798", SmsParser.parse(sms))
    }

    @Test
    fun careHealthLoginMessage_withoutOtp_isIgnored() {
        val sms = "Dear ORACLE, you have successfully logged in Provider Portal. Care Health"

        assertEquals("" to "", SmsParser.parse(sms))
    }
}
