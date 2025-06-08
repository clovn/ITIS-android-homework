package com.example.android_homework.presentation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.android_homework.presentation.screen.DetailScreen
import com.example.android_homework.presentation.screen.GraphScreen
import com.example.android_homework.presentation.screen.MainScreen
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig

@Composable
fun WeatherAppNavHost(
    navController: NavHostController = rememberNavController()
) {
    val navigationManager = remember { NavigationManager.instance }
    var isOpenChartFeature by remember { mutableStateOf(true) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        navigationManager.navigationEvents.collect { route ->
            if(navController.currentBackStackEntry?.destination?.route == route){
                Toast.makeText(context, "Вы уже находитесь на этом экране", Toast.LENGTH_LONG).show()
            }
            navController.navigate(route)
        }
    }

    LaunchedEffect(Unit) {
        val remoteConfig = Firebase.remoteConfig
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val chartFeature = remoteConfig.getBoolean("openChartFeature")
                isOpenChartFeature = chartFeature
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "main_screen"
    ) {
        composable("main_screen") {
            MainScreen(
                onCityCardClick = {city ->
                    navController.navigate("detail_screen/$city")
                },
                onGraphClick = {
                    if (isOpenChartFeature){
                        navController.navigate("graph_screen")
                    } else {
                        Toast.makeText(context, "Доступ к экрану закрыт", Toast.LENGTH_LONG).show()
                    }
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


        composable("graph_screen") {
            GraphScreen()
        }
    }
}