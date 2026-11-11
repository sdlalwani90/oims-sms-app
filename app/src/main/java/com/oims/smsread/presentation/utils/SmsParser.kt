package com.oims.smsread.presentation.utils

/**
 *Created by SATYAA on 14/02/26.
 * Copyright (c) 2026 smsReadApp All rights reserved.
 */

/**
 * An insurance company whose OTP SMS the app forwards.
 * [serverName] is the company_name the backend stores it under; [displayName] is shown on screen.
 */
data class InsuranceCompany(
    val displayName: String,
    val serverName: String,
    val pattern: Regex
)

object SmsParser {

    private val otpRegex =
        Regex("""(?<!\d)(\d{4}|\d{6})(?!\d)""")

    val companies = listOf(
        InsuranceCompany("HDFC ERGO", "hdfc", Regex("hdfc\\s*ergo", RegexOption.IGNORE_CASE)),
        InsuranceCompany("Medi Assist", "MediAssist", Regex("medi\\s*assist", RegexOption.IGNORE_CASE)),
        InsuranceCompany("Tata AIG", "Tata AIG", Regex("tata\\s*aig", RegexOption.IGNORE_CASE)),
        InsuranceCompany("Star Health", "STAR Health Insurance", Regex("star\\s*health", RegexOption.IGNORE_CASE)),
        InsuranceCompany("Care Health", "Care Health Insurance", Regex("care\\s*health", RegexOption.IGNORE_CASE))
    )

    /** Returns (company serverName, OTP), or ("", "") when the SMS has no OTP or no known company. */
    fun parse(input: String): Pair<String, String> {

        // Extract OTP
        val code = otpRegex.find(input)?.value.orEmpty()
        if (code.isEmpty()) return "" to ""

        // Detect company
        val company = companies
            .firstOrNull { it.pattern.containsMatchIn(input) }
            ?.serverName
            .orEmpty()

        return company to code
    }
}
