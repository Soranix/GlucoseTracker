package com.example.glucosetracker.view

import android.app.DatePickerDialog
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
import androidx.compose.material3.ExperimentalMaterial3Api
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
    var unit by remember { mutableStateOf(reading!!.unit) }
    var notes by remember { mutableStateOf(reading!!.notes ?: "") }
    var dateAdded by remember { mutableLongStateOf(reading!!.dateAdded) }
    var readingTime by remember { mutableStateOf(reading!!.readingTime) }
    var valueText by remember { mutableStateOf(value.toString())}

    // edit date variables
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }

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


    /*LaunchedEffect(reading){
        reading?.let{
            calendar.timeInMillis = it.dateAdded
            selectedDate = format.format(calendar.time)
            dateTimestamp = it.dateAdded
        }
    }
     */


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
                    value = valueText,
                    onValueChange = {
                        valueText = it
                        value = it.toFloatOrNull() ?: 0f  // safe conversion
                    },
                    label = { Text("Value") },
                    modifier = Modifier.fillMaxWidth()
                )
                // unit
                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it.filter { c -> c.isDigit()} },
                    label = { Text("Author") },
                    modifier = Modifier.fillMaxWidth()
                )
                // notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Genre (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                /* total
                OutlinedTextField(
                    value = total,
                    onValueChange = { total = it.filter { c -> c.isDigit() } },
                    label = { Text("Total Pages") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                 */

                Spacer(modifier = Modifier.height(16.dp))

                // Date picker button
                Button(onClick = { datePickerDialog.show() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = buttonColors(containerColor = Color.Black, contentColor = Color.White)) {
                    Text(text = if (selectedDate.isNotEmpty()) selectedDate else "Select Date")
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
                                value = valueText.toFloat(),
                                unit = unit.trim(),
                                notes = notes.trim().ifEmpty { null },
                                readingTime = readingTime,

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
