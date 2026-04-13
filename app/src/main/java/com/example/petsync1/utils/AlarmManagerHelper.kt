package com.example.petsync1.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import com.example.petsync1.models.Reminder

object AlarmManagerHelper {

    // Schedule a reminder using AlarmManager
    fun scheduleReminder(context: Context, reminder: Reminder) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Check for exact alarm permission on Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.e("AlarmManagerHelper", "Cannot schedule exact alarms. Requesting permission.")
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                context.startActivity(intent)
                Toast.makeText(context, "Please allow exact alarms for reminders to work.", Toast.LENGTH_LONG).show()
                return
            }
        }

        val dateToSchedule = if (reminder.category == "General") reminder.dueDate else reminder.endDate
        val timeInMillis = convertDateTimeStringToMillis(dateToSchedule)

        if (timeInMillis <= System.currentTimeMillis()) {
            Log.w("AlarmManagerHelper", "Skipping alarm for $dateToSchedule as it is in the past.")
            return
        }

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("reminder_type", reminder.type)
            putExtra("reminder_dueDate", reminder.dueDate)
            putExtra("reminder_status", if (reminder.completed) "Completed" else "Upcoming")
            putExtra("id", reminder.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.hashCode(), // Use stable ID from Firestore
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                timeInMillis,
                pendingIntent
            )
            Log.d("AlarmManagerHelper", "Scheduled alarm for ${reminder.type} at $timeInMillis")
        } catch (e: SecurityException) {
            Log.e("AlarmManagerHelper", "SecurityException scheduling exact alarm: ${e.message}")
        }
    }

    // Cancel a scheduled reminder
    fun cancelReminder(context: Context, reminder: Reminder) {
        val intent = Intent(context, ReminderReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
        Log.d("AlarmManagerHelper", "Cancelled alarm for reminder ID: ${reminder.id}")
    }
}
