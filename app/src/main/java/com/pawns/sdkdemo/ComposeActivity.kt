package com.pawns.sdkdemo

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    val context = LocalContext.current
    val pawns = Pawns.getInstance()

    var isConsentGiven by remember {
        mutableStateOf(pawns.isConsentGiven())
    }

    val consentLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val given = result.resultCode == Activity.RESULT_OK
        isConsentGiven = given
        if (given) {
            pawns.startSharing(context)
        }
    }

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
                Box(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        if (!pawns.isConsentGiven()) {
                            consentLauncher.launch(pawns.getConsentIntent())
                            return@Button
                        }
                        pawns.startSharing(context)
                    }
                ) {
                    Text("START")
                }
                Box(modifier = Modifier.height(10.dp))
                Button(onClick = { pawns.stopSharing(context) }) {
                    Text("STOP")
                }
                Box(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Consent given")
                    Box(modifier = Modifier.width(20.dp))
                    Switch(
                        checked = isConsentGiven,
                        onCheckedChange = { checked ->
                            isConsentGiven = checked
                            pawns.setConsentGiven(checked)
                        }
                    )
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