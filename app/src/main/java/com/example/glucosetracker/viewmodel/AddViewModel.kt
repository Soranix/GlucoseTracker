package com.example.glucosetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.glucosetracker.model.DatabaseInstance
import com.example.glucosetracker.model.Reading
import com.example.glucosetracker.model.ReadingDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date

class AddViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = DatabaseInstance.getDatabase(application).readingDao()
    private val _readings = MutableStateFlow<List<Reading>>(emptyList())
    val readings: StateFlow<List<Reading>> = _readings


    fun addReading(
        value: Float,
        unit: String,
        readingTime: Date,
        dateAdded: Long,
        notes: String?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {

            val reading = Reading(
                value = value,
                unit = unit,
                notes = notes.takeIf { !it.isNullOrBlank() },
                readingTime = readingTime,
                dateAdded = dateAdded
            )
            dao.insertReading(reading)

            onSuccess() // pop back stack

        }
    }
}
