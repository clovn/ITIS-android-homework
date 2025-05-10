package com.example.android_homework.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.android_homework.presentation.screen.DetailScreen
import com.example.android_homework.presentation.screen.MainScreen

@Composable
fun WeatherAppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "main_screen"
    ) {
        composable("main_screen") {
            MainScreen(
                onCityClick = { city ->
                    navController.navigate("detail_screen/$city")
                }
            )
        }

        composable(
            route = "detail_screen/{city}",
            arguments = listOf(navArgument("city") { type = NavType.StringType })
        ) { backStackEntry ->
            val city = backStackEntry.arguments?.getString("city") ?: ""
            DetailScreen(city = city) {
                navController.navigateUp()
            }
        }
    }
}