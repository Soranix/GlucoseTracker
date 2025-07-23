package com.example.glucosetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.glucosetracker.model.Reading
import com.example.glucosetracker.model.DatabaseInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = DatabaseInstance.getDatabase(application).readingDao()

    private val _reading = MutableStateFlow<Reading?>(null)
    val reading: StateFlow<Reading?> = _reading

    fun loadReading(id: Int) {
        viewModelScope.launch {
            _reading.value = dao.getAllReadingsById(id)
        }
    }

    // update reading and navigate back
    fun updateReading(updatedReading: Reading, onSuccess: () -> Unit) {
        viewModelScope.launch {
            dao.updateReading(updatedReading)
            onSuccess()
        }
    }

    // delete reading and navigate back
    fun deleteReading(reading: Reading, onSuccess: () -> Unit) {
        viewModelScope.launch {
            dao.deleteReading(reading)
            onSuccess()
        }
    }
}