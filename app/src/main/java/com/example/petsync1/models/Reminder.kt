package com.example.petsync1.models

import java.text.SimpleDateFormat
import java.util.*

data class Reminder(
    val id: String = "",
    val ownerId: String = "",
    val petId: String = "",
    val petName: String = "",
    val type: String = "",
    val dueDate: String = "", // Used for General reminders
    val perDay: String = "",   // Used for Medication reminders
    val endDate: String = "",   // Used for Medication reminders
    val category: String = "",
    val completed: Boolean = false // Renamed from isCompleted for better Firestore compatibility
) {
    // Utility method to determine if a reminder is overdue
    fun isOverdue(): Boolean {
        if (completed) return false
        val currentTime = System.currentTimeMillis()
        val dateToCheck = if (category == "General") dueDate else endDate
        val timeMillis = convertDateTimeStringToMillis(dateToCheck)
        
        return timeMillis in 1L until currentTime
    }

    fun getDisplayStatus(): String {
        return if (completed) "Completed" else if (isOverdue()) "Overdue" else "Upcoming"
    }

    private fun convertDateTimeStringToMillis(dateTimeString: String, pattern: String = "M/d/yyyy"): Long {
        return try {
            val sdf = SimpleDateFormat(pattern, Locale.getDefault())
            val date = sdf.parse(dateTimeString)
            date?.time ?: 0L
        } catch (_: Exception) {
            0L
        }
    }
}