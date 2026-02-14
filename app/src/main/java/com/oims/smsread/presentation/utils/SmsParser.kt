package com.oims.smsread.presentation.utils

/**
 *Created by SATYAA on 14/02/26.
 * Copyright (c) 2026 smsReadApp All rights reserved.
 */
object SmsParser {

    private val otpRegex =
        Regex("""(?<!\d)(\d{4}|\d{6})(?!\d)""")

    //        private const val HDFC = "hdfc"
//        private const val MEDI_ASSIST = "MediAssist"
//        private const val TATA_AIG = "Tata AIG"
//        private const val STAR_HEALTH = "STAR Health Insurance"
    private val companyPatterns = mapOf(
        Regex("hdfc\\s*ergo", RegexOption.IGNORE_CASE) to "hdfc",
        Regex("medi\\s*assist", RegexOption.IGNORE_CASE) to "MediAssist",
        Regex("tata\\s*aig", RegexOption.IGNORE_CASE) to "Tata AIG",
        Regex("star\\s*health", RegexOption.IGNORE_CASE) to "STAR Health Insurance"
    )

    fun parse(input: String): Pair<String, String> {

        // Extract OTP
        val code = otpRegex.find(input)?.value.orEmpty()
        if (code.isEmpty()) return "" to ""

        // Detect company
        val company = companyPatterns.entries
            .firstOrNull { it.key.containsMatchIn(input) }
            ?.value
            .orEmpty()

        return company to code
    }
}