package com.pawns.sdkdemo

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.pawns.sdk.common.dto.ServiceState
import com.pawns.sdk.common.listener.PawnsServiceListener
import com.pawns.sdk.common.sdk.Pawns

class XmlActivity : AppCompatActivity(), PawnsServiceListener {

    private lateinit var infoText: TextView
    private lateinit var startButton: Button
    private lateinit var stopButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_xml)
        Pawns.getInstance().registerListener(this)
        infoText = findViewById(R.id.info_text)
        startButton = findViewById(R.id.start_button)
        stopButton = findViewById(R.id.stop_button)

        startButton.setOnClickListener {
            Pawns.getInstance().startSharing(this)
        }
        stopButton.setOnClickListener {
            Pawns.getInstance().stopSharing(this)
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