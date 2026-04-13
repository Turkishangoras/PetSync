package com.example.petsync1

import android.app.Application
import com.example.petsync1.utils.NotificationHelper

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Create the notification channel when the app starts
        NotificationHelper.createNotificationChannel(this)
    }
}