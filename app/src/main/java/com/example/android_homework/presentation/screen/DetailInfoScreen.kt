package com.example.android_homework.presentation.screen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.android_homework.R
import com.example.android_homework.domain.model.Forecast
import com.example.android_homework.domain.model.ForecastItem
import com.example.android_homework.presentation.theme.AndroidhomeworkTheme
import com.example.android_homework.presentation.viewModel.DetailInfoState
import com.example.android_homework.presentation.viewModel.DetailInfoViewModel
import java.util.Locale

@Composable
fun DetailScreen(
    city: String,
    viewModel: DetailInfoViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when(state){
        is DetailInfoState.Idle -> viewModel.fetchData(city)
        is DetailInfoState.Loading -> ShimmerEffect(city)
        is DetailInfoState.Loaded -> {
            val forecast = (state as DetailInfoState.Loaded).forecast
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                item {
                    CurrentWeatherSection(forecast)
                }

                item {
                    Text(
                        text = stringResource(R.string.predict_5_day),
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(forecast.list) { item ->
                    ForecastListItem(item = item)
                }
            }
        }
        is DetailInfoState.Error -> {
            val error = (state as DetailInfoState.Error).message
            ErrorScreen(error) {
                viewModel.fetchData(city)
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
fun CurrentWeatherSection(forecast: Forecast) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = forecast.weather.city,
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WeatherIcon(icon = forecast.weather.icon, modifier = Modifier.size(96.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.temp_format, forecast.weather.temp),
                style = MaterialTheme.typography.displayMedium
            )
        }

        Text(
            text = forecast.weather.main.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale("ru")
                ) else it.toString()
            },
            style = MaterialTheme.typography.titleLarge,
            color = Color.LightGray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        AdditionalInfoSection(forecast.feelsLike, forecast.windSpeed, forecast.humidity, forecast.pressure)
    }
}

@Composable
fun ShimmerEffect(
    city: String,
    modifier: Modifier = Modifier,
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

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = 0f)
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = city,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Spacer(
            modifier = Modifier
                .width(160.dp)
                .height(128.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(brush = brush)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .height(128.dp)
                .background(brush = brush)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.predict_5_day),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.fillMaxWidth()
        )
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