package com.example.petsync1.utils

import java.text.SimpleDateFormat
import java.util.*

fun convertDateTimeStringToMillis(dateTimeString: String, pattern: String = "MM/dd/yyyy"): Long {
    return try {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        val date = sdf.parse(dateTimeString)
        date?.time ?: 0L
    } catch (e: Exception) {
        e.printStackTrace()
        0L
    }
}