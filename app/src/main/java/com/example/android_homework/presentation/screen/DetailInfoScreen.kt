package com.example.android_homework.presentation.screen

import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.example.android_homework.R
import com.example.android_homework.domain.model.WeatherDetailed
import com.example.android_homework.domain.model.ForecastItem
import com.example.android_homework.presentation.theme.AndroidhomeworkTheme
import com.example.android_homework.presentation.viewModel.WeatherInfoState
import com.example.android_homework.presentation.viewModel.DetailInfoViewModel
import com.example.android_homework.presentation.viewModel.ForecastInfoState
import java.util.Locale

@Composable
fun DetailScreen(
    city: String,
    viewModel: DetailInfoViewModel = hiltViewModel(),
    navController: NavController
) {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f)
    )
    val transition = rememberInfiniteTransition()
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val brush by remember {
        derivedStateOf {
            Brush.linearGradient(
                colors = shimmerColors,
                start = Offset.Zero,
                end = Offset(x = translateAnim.value, y = 0f)
            )
        }
    }

    LaunchedEffect(city){
        viewModel.fetchWeather(city)
        viewModel.fetchForecast(city)
    }

    val weatherState by viewModel.weatherState.collectAsStateWithLifecycle()
    val forecastState by viewModel.forecastState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = city,
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            when(weatherState){
                is WeatherInfoState.Loading -> WeatherShimmerEffect(brush)
                is WeatherInfoState.Loaded -> {
                    val weather = (weatherState as WeatherInfoState.Loaded).weatherDetailed
                    CurrentWeatherSection(weather)

                }
                is WeatherInfoState.Error -> {
                    val error = (weatherState as WeatherInfoState.Error).message
                    ErrorScreen(error, Modifier.height(300.dp)) {
                        viewModel.fetchWeather(city)
                    }
                }
            }

            Text(
                text = stringResource(R.string.predict_5_day),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            when(forecastState) {
                is ForecastInfoState.Loading -> ForecastShimmerEffect(brush)
                is ForecastInfoState.Loaded -> {
                    LazyColumn {
                        val forecast = (forecastState as ForecastInfoState.Loaded).forecast
                        items(forecast) { item ->
                            ForecastListItem(item = item)
                        }
                    }
                }
                is ForecastInfoState.Error -> {
                    val error = (forecastState as ForecastInfoState.Error).message
                    ErrorScreen(error, Modifier.height(300.dp)) {
                        viewModel.fetchForecast(city)
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { navController.navigateUp() },
            containerColor = Color.Gray,
            shape = FloatingActionButtonDefaults.extendedFabShape,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .size(56.dp)
        ) {
            Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Назад")
        }
    }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.toastFlow.collect { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
fun AdditionalInfoSection(feelsLike: Int, windSpeed: Double, humidity: Int, pressure: Int) {
    Card(
        modifier = Modifier
            .padding(vertical = 16.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            InfoRow(icon = Icons.Default.Thermostat, label = stringResource(R.string.feels_like), value = stringResource(
                R.string.temp_format, feelsLike
            )
            )
            InfoRow(icon = Icons.Default.Air, label = stringResource(R.string.wind), value = stringResource(
                R.string.speed_format, windSpeed
            )
            )
            InfoRow(icon = Icons.Default.WaterDrop, label = stringResource(R.string.humidity), value = stringResource(
                R.string.humidity_format, humidity
            )
            )
            InfoRow(icon = Icons.Default.Speed, label = stringResource(R.string.pressure), value = stringResource(
                R.string.pressure_format, pressure
            )
            )
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(32.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = stringResource(R.string.label_format, label), style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.weight(1f))
        Text(text = value, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun ForecastListItem(item: ForecastItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp)
        ) {
            Text(
                text = item.date,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = stringResource(R.string.temp_format, item.temp),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            WeatherIcon(
                icon = item.icon,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(48.dp)
            )
        }
    }
}

@Composable
fun CurrentWeatherSection(weatherDetailed: WeatherDetailed) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WeatherIcon(icon = weatherDetailed.weather.icon, modifier = Modifier.size(96.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.temp_format, weatherDetailed.weather.temp),
                style = MaterialTheme.typography.displayMedium
            )
        }

        Text(
            text = weatherDetailed.weather.main.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale("ru")
                ) else it.toString()
            },
            style = MaterialTheme.typography.titleLarge,
            color = Color.LightGray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        AdditionalInfoSection(weatherDetailed.feelsLike, weatherDetailed.windSpeed, weatherDetailed.humidity, weatherDetailed.pressure)
    }
}

@Composable
fun WeatherShimmerEffect(
    brush: Brush,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Spacer(
            modifier = Modifier
                .width(186.dp)
                .height(160.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(brush = brush)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .height(192.dp)
                .background(brush = brush)
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun ForecastShimmerEffect(brush: Brush, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        repeat(5) {
            Spacer(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(brush = brush)
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    AndroidhomeworkTheme {
    }
}