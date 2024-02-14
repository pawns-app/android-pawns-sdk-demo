package com.iproyal.sdkdemo

import android.app.Application
import com.iproyal.sdk.common.dto.ServiceConfig
import com.iproyal.sdk.common.dto.ServiceNotificationPriority
import com.iproyal.sdk.common.dto.ServiceType
import com.iproyal.sdk.common.sdk.Pawns

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Pawns.Builder(this)
            .apiKey("Your api key here")
            .serviceConfig(
                ServiceConfig(
                    title = R.string.service_name,
                    body = R.string.service_body,
                    smallIcon = R.drawable.ic_demo_icon,
                    notificationPriority = ServiceNotificationPriority.HIGH
                )
            )
            .loggerEnabled(true)
            .serviceType(ServiceType.FOREGROUND)
            .build()
    }

}