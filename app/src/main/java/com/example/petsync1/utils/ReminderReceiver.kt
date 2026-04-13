package com.example.petsync1.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra("reminder_type") ?: "Reminder"
        val dueDate = intent.getStringExtra("reminder_dueDate") ?: "N/A"
        val status = intent.getStringExtra("reminder_status") ?: "Upcoming"
        val id = intent.getStringExtra("id") ?: "N/A"

        Log.d("ReminderReceiver", "Alarm received for: $type (ID: $id, Due: $dueDate)")

        // Send a notification using our helper
        NotificationHelper.sendNotification(
            context = context,
            title = "PetSync Reminder: $type",
            message = "Scheduled for: $dueDate (Status: $status)"
        )
    }
}
