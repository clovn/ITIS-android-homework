package com.example.android_homework.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.android_homework.presentation.screen.DetailScreen
import com.example.android_homework.presentation.screen.GraphScreen
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
                onCityCardClick = {city ->
                    navController.navigate("detail_screen/$city")
                },
                onGraphClick = { navController.navigate("graph_screen") }
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

        composable("graph_screen") {
            GraphScreen()
        }
    }
}