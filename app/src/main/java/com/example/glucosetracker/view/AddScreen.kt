package com.example.glucosetracker.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.glucosetracker.viewmodel.AddViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    navController: NavController,
    viewModel: AddViewModel = viewModel()
){
    var value by remember { mutableFloatStateOf(0.0f)}
    //var unit by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var readingTime by remember { mutableStateOf(Date())
    }

    // this is everything I need for selecting a date via a calendar
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    // selecting time
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

    var selectedDate by remember { mutableStateOf("") } // Show to user
    var dateTimestamp by remember { mutableLongStateOf(System.currentTimeMillis()) } // Save to DB

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            dateTimestamp = calendar.timeInMillis
            val format = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            selectedDate = format.format(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val unitOptions = listOf("mmol/L", "mg/dL")
    var expanded by remember { mutableStateOf(false) }
    var unit by remember { mutableStateOf(unitOptions[0]) } // default selection


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(text = "Add Reading", fontWeight = FontWeight.Bold) },
                // back button
                navigationIcon = {
                    IconButton(onClick = {navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                        )
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
                OutlinedTextField(
                    value = value.toString(),
                    onValueChange = { value = it.toFloatOrNull()?: 0f },
                    label = { Text("Value") },
                    modifier = Modifier.fillMaxWidth()
                )

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

                Box(
                    modifier = Modifier
                        .fillMaxWidth()

                ) {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it},
                        label = { Text("Notes") },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }



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
                    // this button will not appear unless title and unit are not blank.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            viewModel.addReading(
                                value = value,
                                unit = unit,
                                notes = notes,
                                readingTime = readingTime,
                                dateAdded = dateTimestamp,
                            ) {
                                navController.popBackStack()
                            }
                        },
                        enabled = value.toString().isNotBlank() && unit.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    )
}
