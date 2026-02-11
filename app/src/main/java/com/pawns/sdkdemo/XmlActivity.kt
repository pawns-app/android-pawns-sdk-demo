package com.pawns.sdkdemo

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.pawns.sdk.common.dto.ServiceState
import com.pawns.sdk.common.listener.PawnsServiceListener
import com.pawns.sdk.common.sdk.Pawns

class XmlActivity : AppCompatActivity(), PawnsServiceListener {

    private lateinit var infoText: TextView
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var consentSwitch: SwitchCompat

    private val consentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val pawns = Pawns.getInstance()
        val isGiven = result.resultCode == Activity.RESULT_OK

        consentSwitch.isChecked = isGiven
        pawns.setConsentGiven(isGiven)

        if (isGiven) {
            pawns.startSharing(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_xml)

        infoText = findViewById(R.id.info_text)
        startButton = findViewById(R.id.start_button)
        stopButton = findViewById(R.id.stop_button)
        consentSwitch = findViewById(R.id.consent_switch)

        val pawns = Pawns.getInstance()

        consentSwitch.isChecked = pawns.isConsentGiven()
        consentSwitch.setOnCheckedChangeListener { _, checked ->
            pawns.setConsentGiven(checked)
        }

        pawns.registerListener(this)

        startButton.setOnClickListener {
            if (!pawns.isConsentGiven()) {
                consentLauncher.launch(pawns.getConsentIntent())
                return@setOnClickListener
            }
            pawns.startSharing(this)
        }

        stopButton.setOnClickListener {
            pawns.stopSharing(this)
        }
    }

    override fun onDestroy() {
        Pawns.getInstance().unregisterListener()
        super.onDestroy()
    }

    override fun onStateChange(state: ServiceState) {
        val text = when (state) {
            is ServiceState.Launched.Error -> "Error: ${state.error}"
            ServiceState.Off -> "Off"
            ServiceState.On -> "On"
            is ServiceState.Launched.Running -> "Running ${state.traffic}"
            ServiceState.Launched.LowBattery -> "Low Battery"
        }
        infoText.text = text
    }
}