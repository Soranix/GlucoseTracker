package com.example.glucosetracker.nav

// navigation routes
sealed class Screen(val route: String){
    object HomeScreen: Screen("home_screen")
    object EditScreen: Screen("edit_screen/{id}"){
        fun withId(id:Int) = "edit_screen/$id"
    }
    object AddScreen: Screen("add_screen")
}
