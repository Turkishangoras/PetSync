package com.example.petsync1.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.petsync1.R

/**
 * Helper object to manage and display system notifications for PetSync.
 * Handles channel creation for Android O+ and notification dispatching.
 */
object NotificationHelper {

    private const val CHANNEL_ID = "reminder_channel"

    /**
     * Initializes the notification channel.
     * Required for Android 8.0 (Oreo) and above to display notifications.
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "PetSync Reminders"
            val descriptionText = "Notifications for pet medical and daily reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Builds and displays a notification.
     * @param context Application context.
     * @param title Title of the notification.
     * @param message Content body of the notification.
     */
    fun sendNotification(context: Context, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.noticon) // Custom app notification icon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // Dismiss notification when tapped

        // Use current time as ID to ensure multiple notifications can be shown simultaneously
        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}
