package com.example.glucosetracker.model

import android.icu.text.SimpleDateFormat
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.Locale

@Entity(tableName = "readings")
data class Reading(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val value: Float,
    val unit: String,
    val readingTime: Date,
    val dateAdded: Long = System.currentTimeMillis(), // current time in miliseconds
    val notes: String? = null,
){

    // date formatting
    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

}