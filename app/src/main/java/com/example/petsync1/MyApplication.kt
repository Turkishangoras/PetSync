package com.example.petsync1

import android.app.Application
import com.example.petsync1.utils.NotificationHelper

/**
 * Custom Application class for PetSync.
 * Used for one-time initialization of app-wide components like notification channels.
 */
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Create the notification channel when the app starts to ensure reminders work correctly
        NotificationHelper.createNotificationChannel(this)
    }
}