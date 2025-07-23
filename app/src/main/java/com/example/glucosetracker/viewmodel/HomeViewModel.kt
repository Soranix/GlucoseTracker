package com.example.glucosetracker.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.glucosetracker.model.AppDatabase
import com.example.glucosetracker.model.Reading
import com.example.glucosetracker.model.ReadingDao
import com.example.glucosetracker.model.DatabaseInstance
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Core = Implement basic crud functionality

class HomeViewModel (application: Application) : AndroidViewModel(application){

    /*
    - UI observes 'readings', read only
    - '_readings' can be altered by the viewmodel
    - stateflow will not retain data after the application closes.
    */

    private val _readings = MutableStateFlow<List<Reading>>(emptyList())
    val readings: StateFlow<List<Reading>> = _readings

    // database instantiated
    private val dao = DatabaseInstance.getDatabase(application).readingDao()


    // this function will help load and refresh the database that the user will see

    // read
    fun loadReadings() {
        viewModelScope.launch {
            _readings.value = dao.getAllReadings()
        }
    }
}
