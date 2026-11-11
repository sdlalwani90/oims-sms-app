package com.oims.smsread.presentation.features.readsms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oims.smsread.data.local.PostStatus
import com.oims.smsread.data.local.RecentOtp
import com.oims.smsread.presentation.ui.theme.SmsReadAppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val PostedGreen = Color(0xFF2E7D32)

/** One authenticator-style card for an insurance company and its latest OTP, if still recent. */
@Composable
fun CompanyOtpCard(
    companyName: String,
    entry: RecentOtp?,
    now: Long,
    modifier: Modifier = Modifier,
    listening: Boolean = true
) {
    val visible = entry?.takeIf { it.isVisible(now) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (visible != null) MaterialTheme.colorScheme.surfaceContainerHigh
            else MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = companyName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (visible == null) {
                Spacer(modifier = Modifier.height(8.dp))
                StandbyRow(listening)
                return@Column
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = formatOtp(visible.otp),
                fontSize = 36.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 4.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Received ${formatTime(visible.receivedAt)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                PostStatusLabel(visible.status)
            }

            Spacer(modifier = Modifier.height(10.dp))
            val remaining = visible.remainingMillis(now)
            LinearProgressIndicator(
                progress = { remaining.toFloat() / RecentOtp.VISIBLE_FOR_MILLIS },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Disappears in ${formatRemaining(remaining)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** Shown when there is no recent OTP: a spinner while the app is ready for the next SMS. */
@Composable
private fun StandbyRow(listening: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (listening) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Waiting for OTP…",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Text(
                text = "Not listening: SMS permission is off",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun PostStatusLabel(status: PostStatus) {
    val (text, color) = when (status) {
        PostStatus.SENDING -> "Sending…" to MaterialTheme.colorScheme.onSurfaceVariant
        PostStatus.POSTED -> "✓ Posted" to PostedGreen
        PostStatus.FAILED -> "✗ Not posted" to MaterialTheme.colorScheme.error
    }
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = color
    )
}

/** "123456" → "123 456"; shorter codes are shown as they are. */
internal fun formatOtp(otp: String): String =
    if (otp.length == 6) "${otp.take(3)} ${otp.drop(3)}" else otp

/** Milliseconds → "m:ss", rounded up so the bar never reads 0:00 while still shown. */
internal fun formatRemaining(millis: Long): String {
    val totalSeconds = (millis + 999) / 1000
    return "%d:%02d".format(totalSeconds / 60, totalSeconds % 60)
}

private fun formatTime(epochMillis: Long): String =
    SimpleDateFormat("h:mm:ss a", Locale.getDefault()).format(Date(epochMillis))

@Preview(showBackground = true)
@Composable
private fun CompanyOtpCardPreview() {
    val now = System.currentTimeMillis()
    SmsReadAppTheme {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(16.dp)) {
            CompanyOtpCard("Care Health", RecentOtp("2229", now - 95_000, PostStatus.POSTED), now)
            CompanyOtpCard("HDFC ERGO", RecentOtp("481920", now - 400_000, PostStatus.FAILED), now)
            CompanyOtpCard("Tata AIG", null, now)
        }
    }
}
