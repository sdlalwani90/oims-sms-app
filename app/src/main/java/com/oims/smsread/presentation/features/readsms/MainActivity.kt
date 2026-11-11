package com.oims.smsread.presentation.features.readsms

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.oims.smsread.data.local.PostStatus
import com.oims.smsread.data.local.RecentOtp
import com.oims.smsread.data.local.RecentOtpStore
import com.oims.smsread.presentation.ui.theme.SmsReadAppTheme
import com.oims.smsread.presentation.utils.SmsParser
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private val smsPermissions = arrayOf(
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.READ_SMS
    )

    private var smsAllowed by mutableStateOf(false)

    // True once the user has denied with "Don't ask again" (or denied twice):
    // Android will no longer show the popup, so we send them to Settings instead.
    private var mustOpenSettings by mutableStateOf(false)

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            smsAllowed = hasSmsPermission()
            mustOpenSettings = !smsAllowed &&
                !shouldShowRequestPermissionRationale(Manifest.permission.RECEIVE_SMS)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        smsAllowed = hasSmsPermission()
        if (!smsAllowed) permissionLauncher.launch(smsPermissions)

        val store = RecentOtpStore.get(this)

        setContent {
            SmsReadAppTheme {
                val otps by store.otps.collectAsState()
                // Ticks every second so countdowns move and expired OTPs drop off.
                val now by produceState(System.currentTimeMillis()) {
                    while (true) {
                        value = System.currentTimeMillis()
                        delay(1_000)
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        contentPadding = innerPadding,
                        smsAllowed = smsAllowed,
                        mustOpenSettings = mustOpenSettings,
                        onAllowClick = ::onAllowClick,
                        otps = otps,
                        now = now
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Pick up changes made in system Settings while the app was in the background.
        smsAllowed = hasSmsPermission()
        if (smsAllowed) mustOpenSettings = false
    }

    private fun hasSmsPermission(): Boolean =
        ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) ==
            PackageManager.PERMISSION_GRANTED

    private fun onAllowClick() {
        if (mustOpenSettings) {
            startActivity(
                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", packageName, null)
                )
            )
        } else {
            permissionLauncher.launch(smsPermissions)
        }
    }
}


@Composable
fun MainScreen(
    contentPadding: PaddingValues = PaddingValues(),
    smsAllowed: Boolean = true,
    mustOpenSettings: Boolean = false,
    onAllowClick: () -> Unit = {},
    otps: Map<String, RecentOtp> = emptyMap(),
    now: Long = System.currentTimeMillis()
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column {
                Text(text = "Recent OTPs", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "This phone forwards insurance OTPs to OIMS. Each one shows here for 10 minutes after it arrives.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (!smsAllowed) {
            item { SmsPermissionCard(mustOpenSettings, onAllowClick) }
        }

        items(SmsParser.companies, key = { it.serverName }) { company ->
            CompanyOtpCard(
                companyName = company.displayName,
                entry = otps[company.serverName],
                now = now,
                listening = smsAllowed
            )
        }
    }
}

@Composable
private fun SmsPermissionCard(mustOpenSettings: Boolean, onAllowClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (mustOpenSettings) {
                    "SMS permission is off. Open Settings → Permissions → SMS and choose Allow, " +
                        "so this app can forward your access code."
                } else {
                    "Allow SMS access so this app can forward your access code."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onAllowClick) {
                Text(text = if (mustOpenSettings) "Open Settings" else "Allow SMS")
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    val now = System.currentTimeMillis()
    SmsReadAppTheme {
        MainScreen(
            otps = mapOf(
                "Care Health Insurance" to RecentOtp("2229", now - 95_000, PostStatus.POSTED),
                "hdfc" to RecentOtp("481920", now - 30_000, PostStatus.SENDING)
            ),
            now = now
        )
    }
}
