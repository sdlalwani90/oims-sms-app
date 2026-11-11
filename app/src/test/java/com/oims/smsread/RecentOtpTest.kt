package com.oims.smsread

import com.oims.smsread.data.local.PostStatus
import com.oims.smsread.data.local.RecentOtp
import com.oims.smsread.presentation.features.readsms.formatOtp
import com.oims.smsread.presentation.features.readsms.formatRemaining
import com.oims.smsread.presentation.utils.SmsParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RecentOtpTest {

    private val receivedAt = 1_700_000_000_000L
    private val entry = RecentOtp("2229", receivedAt, PostStatus.POSTED)

    @Test
    fun otp_isVisibleForTenMinutes_thenDisappears() {
        assertTrue(entry.isVisible(receivedAt))
        assertTrue(entry.isVisible(receivedAt + 9 * 60_000 + 59_999))
        assertFalse(entry.isVisible(receivedAt + 10 * 60_000))
        assertFalse(entry.isVisible(receivedAt + 60 * 60_000))
    }

    @Test
    fun remainingTime_countsDownAndStopsAtZero() {
        assertEquals(10 * 60_000L, entry.remainingMillis(receivedAt))
        assertEquals(4 * 60_000L, entry.remainingMillis(receivedAt + 6 * 60_000))
        assertEquals(0L, entry.remainingMillis(receivedAt + 11 * 60_000))
    }

    @Test
    fun encodeDecode_roundTrips() {
        PostStatus.entries.forEach { status ->
            val original = entry.copy(status = status)
            assertEquals(original, RecentOtp.decode(original.encode()))
        }
    }

    @Test
    fun decode_rejectsBrokenValues() {
        assertNull(RecentOtp.decode(null))
        assertNull(RecentOtp.decode(""))
        assertNull(RecentOtp.decode("2229|notANumber|POSTED"))
        assertNull(RecentOtp.decode("2229|1700000000000|UNKNOWN"))
    }

    @Test
    fun otpFormatting_groupsSixDigitCodes() {
        assertEquals("123 456", formatOtp("123456"))
        assertEquals("2229", formatOtp("2229"))
    }

    @Test
    fun remainingFormatting_isMinutesAndSeconds() {
        assertEquals("10:00", formatRemaining(10 * 60_000L))
        assertEquals("4:05", formatRemaining(4 * 60_000L + 5_000))
        assertEquals("0:01", formatRemaining(1))
    }

    @Test
    fun everyCompany_hasUniqueServerName() {
        val names = SmsParser.companies.map { it.serverName }
        assertEquals(names.size, names.toSet().size)
        assertTrue("Care Health Insurance" in names)
    }
}
