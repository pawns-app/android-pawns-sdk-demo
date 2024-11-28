package com.pawns.sdkdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pawns.sdk.common.dto.ServiceState
import com.pawns.sdk.common.sdk.Pawns
import com.pawns.sdkdemo.ui.theme.PawnsSdkDemoTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow

class ComposeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PawnsSdkDemoTheme {
                val state = Pawns.getInstance().getServiceState().collectAsState(Dispatchers.Main.immediate)
                DemoScreen(state)
            }
        }
    }

}

@Composable
private fun DemoScreen(state: State<ServiceState>) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val text = when (val serviceState = state.value) {
                    is ServiceState.Launched.Error -> "Error: ${serviceState.error}"
                    ServiceState.Off -> "Off"
                    ServiceState.On -> "On"
                    is ServiceState.Launched.Running -> "Running ${serviceState.traffic}"
                    ServiceState.Launched.LowBattery -> "Low Battery"
                }
                Text(
                    modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                    text = text
                )
                val context = LocalContext.current
                Box(modifier = Modifier.height(20.dp))
                Button(onClick = { Pawns.getInstance().startSharing(context) }) {
                    Text("START")
                }
                Box(modifier = Modifier.height(10.dp))
                Button(onClick = { Pawns.getInstance().stopSharing(context) }) {
                    Text("STOP")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PawnsSdkDemoTheme {
        DemoScreen(MutableStateFlow(ServiceState.Launched.Running(null)).collectAsState())
    }
}