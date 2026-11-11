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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.oims.smsread.presentation.ui.theme.SmsReadAppTheme

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

        setContent {
            SmsReadAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding),
                        smsAllowed = smsAllowed,
                        mustOpenSettings = mustOpenSettings,
                        onAllowClick = ::onAllowClick
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
    modifier: Modifier = Modifier,
    smsAllowed: Boolean = true,
    mustOpenSettings: Boolean = false,
    onAllowClick: () -> Unit = {}
) {

    Column(
        modifier = modifier.fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (smsAllowed) {
            Text(
                text = "This app will forward your access code",
                style = MaterialTheme.typography.labelMedium
            )
        } else {
            Text(
                text = if (mustOpenSettings) {
                    "SMS permission is off. Open Settings → Permissions → SMS and choose Allow, " +
                        "so this app can forward your access code."
                } else {
                    "Allow SMS access so this app can forward your access code."
                },
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center
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
    SmsReadAppTheme {
        MainScreen()
    }
}
