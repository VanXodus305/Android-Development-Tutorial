package com.example.myapplication

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun myAppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.SCREEN_A, builder = {
        composable(Routes.SCREEN_A) { screenA(navController) }
        composable(Routes.SCREEN_B + "?name={name}") {
            val name = it.arguments?.getString("name")
            screenB(name = name)
        }
    })
}