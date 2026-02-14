package com.oims.smsread.presentation.features.readsms

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oims.smsread.presentation.ui.theme.SmsReadAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmsReadAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}



@Composable
fun MainScreen(modifier: Modifier = Modifier){

    Column(
        modifier = modifier.fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
//        var otpValue by remember { mutableStateOf("") }
        Text(text = "This app will forward your access code",
            style = MaterialTheme.typography.labelMedium)
//        TextField(
//            singleLine = true,
//            value = otpValue,
//            onValueChange = {
//                otpValue = it
//            },
//            label = { Text("Enter Access code") },
//            shape = ShapeDefaults.Medium
//        )
//        Spacer(modifier = Modifier.height(8.dp))
//        Button(
//            onClick = {},
//        ) {
//            Text(text = "Submit")
//        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    SmsReadAppTheme {
        MainScreen()
    }
}