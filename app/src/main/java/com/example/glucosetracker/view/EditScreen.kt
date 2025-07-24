package com.example.glucosetracker.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.glucosetracker.nav.Screen
import com.example.glucosetracker.viewmodel.EditViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    id: Int,
    navController: NavController,
    viewModel: EditViewModel = viewModel()
) {
    // Load the reading once
    LaunchedEffect(id) {
        viewModel.loadReading(id)
    }

    // need these for editing the date/time
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }

    val reading by viewModel.reading.collectAsState()
    // Show a loading placeholder otherwise the app would crash. viewModel needs to load and update the stateflow
    if (reading == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // Editable fields.
    // !! will throw NullPointerException
    var value by remember { mutableFloatStateOf(reading!!.value) }
    //var unit by remember { mutableStateOf(reading!!.unit) }
    var notes by remember { mutableStateOf(reading!!.notes ?: "") }
    var dateAdded by remember { mutableLongStateOf(reading!!.dateAdded) }
    var readingTime by remember { mutableStateOf(reading!!.readingTime) }
    //var valueText by remember { mutableStateOf(value.toString())}

    // edit time state
    var selectedTime by remember { mutableStateOf(calendar.time) }

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour: Int, minute: Int ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            selectedTime = calendar.time
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true // 24-hour format
    )

    val format = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    var selectedDate by remember { mutableStateOf("") }
    var dateTimestamp by remember { mutableLongStateOf(System.currentTimeMillis()) }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            dateTimestamp = calendar.timeInMillis
            selectedDate = format.format(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    // for dropdown menu
    val unitOptions = listOf("mmol/L", "mg/dL")
    var expanded by remember { mutableStateOf(false) }
    var unit by remember { mutableStateOf(unitOptions[0]) } // default selection

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Edit Reading", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // value
                OutlinedTextField(
                    value = value.toString(),
                    onValueChange = { value = it.toFloatOrNull()?: 0f },
                    label = { Text("Value") },
                    modifier = Modifier.fillMaxWidth()
                )
                // unit
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = unit,
                        onValueChange = {}, // read-only
                        readOnly = true,
                        label = { Text("Unit") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        unitOptions.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    unit = selectionOption
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                // notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("notes (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )


                Spacer(modifier = Modifier.height(16.dp))

                // Date picker button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { datePickerDialog.show() },
                        modifier = Modifier.weight(1f), // Equal width
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = if (selectedDate.isEmpty()) "Select Date" else selectedDate)
                    }

                    Button(
                        onClick = { timePickerDialog.show() },
                        modifier = Modifier.weight(1f), // Equal width
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Select Time")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Delete button in red
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            viewModel.deleteReading(reading!!) {
                                navController.popBackStack()
                            }
                        },
                        colors = buttonColors(containerColor = Color.Red, contentColor = Color.White)
                    ) {
                        Text(text = "Delete")
                    }

                    // Save data, trim whitespace, update the database and return to home screen
                    Button(
                        onClick = {
                            val updatedReading = reading!!.copy(
                                value = value,
                                unit = unit.trim(),
                                notes = notes.trim().ifEmpty { null },
                                readingTime = selectedTime,
                                dateAdded = dateTimestamp
                            )
                            viewModel.updateReading(updatedReading) {

                                navController.popBackStack()
                            }
                        },
                        colors = buttonColors(containerColor = Color.Black, contentColor = Color.White)
                    ) {
                        Text("Save")
                    }
                }

            }
        }
    )
}
