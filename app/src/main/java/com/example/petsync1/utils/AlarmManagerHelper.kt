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

/**
 * Helper object to manage Android System Alarms for pet reminders.
 * Coordinates with AlarmManager to trigger notifications even when the app is closed.
 */
object AlarmManagerHelper {

    /**
     * Schedules a system alarm for a specific reminder.
     * @param context Application context.
     * @param reminder The reminder object containing date, time, and metadata.
     */
    fun scheduleReminder(context: Context, reminder: Reminder) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Android 12 (API 31) and above requires specific permission for exact alarms
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.e("AlarmManagerHelper", "Exact alarm permission missing.")
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                context.startActivity(intent)
                Toast.makeText(context, "Please allow exact alarms for reminders to work.", Toast.LENGTH_LONG).show()
                return
            }
        }

        // Use dueDate for General tasks, or endDate for medical schedules
        val dateToSchedule = if (reminder.category == "General") reminder.dueDate else reminder.endDate
        val timeInMillis = convertDateTimeStringToMillis(dateToSchedule)

        // Don't schedule if the time has already passed
        if (timeInMillis <= System.currentTimeMillis()) {
            Log.w("AlarmManagerHelper", "Skipping alarm for $dateToSchedule - past date.")
            return
        }

        // Create an intent to trigger our BroadcastReceiver
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("reminder_type", reminder.type)
            putExtra("reminder_dueDate", reminder.dueDate)
            putExtra("reminder_status", if (reminder.completed) "Completed" else "Upcoming")
            putExtra("id", reminder.id)
        }

        // Wrap the intent in a PendingIntent. Use hashcode of Firestore ID for unique notification slots.
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            // setExactAndAllowWhileIdle ensures the alarm fires even in Doze mode
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                timeInMillis,
                pendingIntent
            )
            Log.d("AlarmManagerHelper", "Scheduled alarm: ${reminder.type} at $timeInMillis")
        } catch (e: SecurityException) {
            Log.e("AlarmManagerHelper", "SecurityException: ${e.message}")
        }
    }

    /**
     * Cancels a previously scheduled alarm.
     * Used when a reminder is deleted or marked as completed.
     */
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
        Log.d("AlarmManagerHelper", "Cancelled alarm for: ${reminder.id}")
    }
}
