package com.example.android_homework.presentation.screen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Addchart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.android_homework.R
import com.example.android_homework.data.Data
import com.example.android_homework.domain.model.WeatherMainInfo
import com.example.android_homework.presentation.theme.AndroidhomeworkTheme
import com.example.android_homework.presentation.viewModel.MainViewModel
import com.example.android_homework.presentation.viewModel.UiState
import java.util.Locale

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onCityCardClick: (String) -> Unit,
    onGraphClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when(state){
        is UiState.Idle -> viewModel.fetchData()
        is UiState.Loading -> ShimmerEffectList(Data.citiesList.size)
        is UiState.Loaded -> {
            val weatherList = (state as UiState.Loaded).weatherList
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(weatherList) { weather ->
                        CityCard(weather = weather, onClick = { onCityCardClick(weather.city) })
                    }
                }

                FloatingActionButton(
                    onClick = { onGraphClick() },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Addchart,
                        contentDescription = "Добавить"
                    )
                }
            }

        }
        is UiState.Error -> {
            val error = (state as UiState.Error).message
            ErrorScreen(error) { viewModel.fetchData() }
        }
    }
}

@Composable
fun CityCard(weather: WeatherMainInfo, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {

            WeatherIcon(icon = weather.icon, modifier = Modifier.size(50.dp))

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = weather.city,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = weather.main.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = stringResource(R.string.temp_format, weather.temp),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ShimmerEffectList(itemsCount: Int) {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f)
    )

    val transition = rememberInfiniteTransition()
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 2000f,
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

    LazyColumn {
        items(itemsCount) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .height(80.dp)
                    .background(brush = brush)
            )
        }
    }
}

@Composable
fun WeatherIcon(icon: String, modifier: Modifier = Modifier) {
    AsyncImage(
        model = "https://openweathermap.org/img/wn/${icon}@2x.png",
        contentDescription = "Weather Icon",
        modifier = modifier,
        placeholder = painterResource(id = R.drawable.error_image),
        error = painterResource(id = R.drawable.error_image)
    )
}

@Preview
@Composable
private fun Preview() {
    AndroidhomeworkTheme {
        Surface(modifier = Modifier
            .fillMaxSize()
            .padding(32.dp, 16.dp)) {
            ShimmerEffectList(3)
        }
    }
}