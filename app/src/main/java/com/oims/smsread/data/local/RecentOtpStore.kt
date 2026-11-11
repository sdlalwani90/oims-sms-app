package com.oims.smsread.data.local

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class PostStatus { SENDING, POSTED, FAILED }

/** The latest OTP captured for one company. */
data class RecentOtp(
    val otp: String,
    val receivedAt: Long,
    val status: PostStatus
) {
    fun remainingMillis(now: Long): Long = (receivedAt + VISIBLE_FOR_MILLIS - now).coerceAtLeast(0)

    fun isVisible(now: Long): Boolean = remainingMillis(now) > 0

    /** "otp|receivedAt|STATUS", the form kept in SharedPreferences. */
    fun encode(): String = "$otp|$receivedAt|${status.name}"

    companion object {
        /** How long an OTP stays on screen after it arrives. */
        const val VISIBLE_FOR_MILLIS = 10 * 60 * 1000L

        fun decode(value: String?): RecentOtp? {
            val parts = value?.split('|') ?: return null
            if (parts.size != 3) return null
            val receivedAt = parts[1].toLongOrNull() ?: return null
            val status = PostStatus.entries.firstOrNull { it.name == parts[2] } ?: return null
            return RecentOtp(parts[0], receivedAt, status)
        }
    }
}

/**
 * Remembers the latest OTP per company (keyed by the company's server name) so the main screen
 * can show it for [RecentOtp.VISIBLE_FOR_MILLIS]. Written by the SMS receiver, read by the screen;
 * both run in the app process, so the [otps] flow updates the screen live, and SharedPreferences
 * keeps the entries when the app is closed.
 */
class RecentOtpStore private constructor(context: Context) {

    private val prefs = context.getSharedPreferences("recent_otps", Context.MODE_PRIVATE)

    private val _otps = MutableStateFlow(load())
    val otps: StateFlow<Map<String, RecentOtp>> = _otps.asStateFlow()

    fun record(company: String, otp: String, receivedAt: Long) =
        save(company, RecentOtp(otp, receivedAt, PostStatus.SENDING))

    /** Sets the post result, unless a newer OTP for the same company has replaced it meanwhile. */
    fun setStatus(company: String, receivedAt: Long, status: PostStatus) {
        val current = _otps.value[company] ?: return
        if (current.receivedAt == receivedAt) save(company, current.copy(status = status))
    }

    private fun save(company: String, entry: RecentOtp) {
        _otps.update { it + (company to entry) }
        prefs.edit().putString(company, entry.encode()).apply()
    }

    private fun load(): Map<String, RecentOtp> {
        val now = System.currentTimeMillis()
        val expired = mutableListOf<String>()
        val entries = prefs.all.mapNotNull { (company, value) ->
            val entry = RecentOtp.decode(value as? String)
            if (entry == null || !entry.isVisible(now)) {
                expired += company
                null
            } else {
                company to entry
            }
        }.toMap()
        if (expired.isNotEmpty()) prefs.edit().apply { expired.forEach(::remove) }.apply()
        return entries
    }

    companion object {
        @Volatile
        private var instance: RecentOtpStore? = null

        fun get(context: Context): RecentOtpStore =
            instance ?: synchronized(this) {
                instance ?: RecentOtpStore(context.applicationContext).also { instance = it }
            }
    }
}
