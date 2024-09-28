package com.rohnsha.medbuddyai.domain.modules

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.rohnsha.medbuddyai.domain.notifications.dbUpdateService

class AppContxt: Application(){
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel= NotificationChannel(
            dbUpdateService.dbUPDATEchannelName,
            "Database Updates",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager= getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}