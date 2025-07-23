package com.example.glucosetracker.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.glucosetracker.view.AddScreen
import com.example.glucosetracker.view.HomeScreen
import com.example.glucosetracker.view.EditScreen

// handles navigation
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route// Default screen
    ) {
        composable(Screen.HomeScreen.route) {
            HomeScreen(navController)
        }
        composable(Screen.AddScreen.route) {
            AddScreen(navController)
        }
        composable(
            route = Screen.EditScreen.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: return@composable
            EditScreen(id = id, navController = navController)
            }
        }
    }