package com.pawns.sdkdemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.pawns.sdk.common.sdk.Pawns

class BootStartupReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent?.action.equals(Intent.ACTION_BOOT_COMPLETED, true)) {
            Pawns.getInstance().startSharing(context)
        }
    }

}