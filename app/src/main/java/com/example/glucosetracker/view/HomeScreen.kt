package com.example.glucosetracker.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.glucosetracker.model.Reading
import com.example.glucosetracker.nav.Screen
import com.example.glucosetracker.viewmodel.HomeViewModel
import androidx.compose.material.icons.filled.List
import java.text.SimpleDateFormat
import java.util.Formatter
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val readings by viewModel.readings.collectAsState() // all readings
    viewModel.loadReadings()

    // to filter to see only dangerous readings
    var showOnlyDangers by remember { mutableStateOf(false) }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(
                    text = "My Readings",
                    fontWeight = FontWeight.Bold
                )
            },
                actions = {},
            )
        },

        // FAB '+' button. Navigates to the Add screen
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.AddScreen.route)},
                containerColor = Color.Black,
                contentColor = Color.White
                ) {
                Text("+")
            }
        }
    ) { padding ->
        if (readings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No readings yet. Tap + to add one.")
            }
        } else { // auto scrolling
            LazyColumn(
                contentPadding = padding,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(readings) { reading ->
                    ReadingItem(
                        reading = reading,
                        onClick = { navController.navigate(Screen.EditScreen.withId(reading.id)) }
                        )
                }
            }
        }
    }
}
@Composable
fun ReadingItem(reading: Reading, onClick: () -> Unit) {

    // using this to colour in the number red or blue depending on the value
    val readingValue = reading.value
    val valueColor = when {
        readingValue < 4.0 -> Color.Blue
        readingValue > 13.9 -> Color.Red
        else -> MaterialTheme.colorScheme.onSurface // or Color.Black
    }
    // time format
    val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = reading.value.toString() + " " + reading.unit,
                style = MaterialTheme.typography.titleMedium,
                color = valueColor
                )
            Text(
                text = "Added: ${reading.formatDate(reading.dateAdded)}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Time: ${timeFormatter.format(reading.readingTime)}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

