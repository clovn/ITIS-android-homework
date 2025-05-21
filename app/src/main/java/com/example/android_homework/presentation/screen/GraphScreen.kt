package com.example.android_homework.presentation.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android_homework.R
import com.example.android_homework.presentation.theme.AndroidhomeworkTheme
import kotlin.math.absoluteValue

@Composable
fun GraphScreen() {
    var pointCount by remember { mutableStateOf("") }
    var valuesInput by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var graphData by remember { mutableStateOf<List<Float>?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = pointCount,
            onValueChange = { newText ->
                pointCount = newText
                error = null
            },
            label = { Text(stringResource(R.string.dots_count)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = valuesInput,
            onValueChange = { newText ->
                valuesInput = newText
                error = null
            },
            label = { Text(stringResource(R.string.numbers_semicolon)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val count = pointCount.toIntOrNull()
                if (count == null || count <= 0) {
                    error = "Введите корректное количество точек"
                    return@Button
                }

                val values = valuesInput.split(",").mapNotNull { it.trim().toFloatOrNull() }
                if (values.size != count) {
                    error = "Количество значений не совпадает с количеством точек"
                    return@Button
                }

                if (values.any { it < 0 }) {
                    error = "Все значения должны быть неотрицательными"
                    return@Button
                }

                graphData = values
                error = null
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.build_graph))
        }

        Spacer(modifier = Modifier.height(8.dp))

        error?.let{
            Text(text = error!!, color = Color.Red)
        }

        Spacer(modifier = Modifier.height(16.dp))


        graphData?.let {
            GraphView(graphData!!)
        }
    }
}

@Composable
fun GraphView(points: List<Float>) {
    val sizeCanvas = 300
    val padding = 32f

    var tooltipText by remember { mutableStateOf<String?>(null) }
    var tooltipPosition by remember { mutableStateOf<Offset?>(null) }

    Box(
        modifier = Modifier
            .size(sizeCanvas.dp)
            .background(Color.White)
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize(),
            onDraw = {
                val width = size.width
                val height = size.height

                drawLine(
                    color = Color.Black,
                    start = Offset(padding, height - padding),
                    end = Offset(width - padding, height - padding),
                    strokeWidth = 2f
                )

                drawLine(
                    color = Color.Black,
                    start = Offset(padding, padding),
                    end = Offset(padding, height - padding),
                    strokeWidth = 2f
                )

                val maxPoint = points.maxOrNull() ?: 0f
                val normalizedPoints = points.map { it / maxPoint }

                for (i in normalizedPoints.indices) {
                    val x = padding + i * ((width - 2 * padding) / (points.size - 1))
                    val y = height - padding - normalizedPoints[i] * (height - 2 * padding)

                    drawCircle(
                        color = Color.Black,
                        center = Offset(x, y),
                        radius = 8f
                    )
                }

                for (i in 1 until normalizedPoints.size) {
                    val prevX = padding + (i - 1) * ((width - 2 * padding) / (points.size - 1))
                    val prevY = height - padding - normalizedPoints[i - 1] * (height - 2 * padding)
                    val currX = padding + i * ((width - 2 * padding) / (points.size - 1))
                    val currY = height - padding - normalizedPoints[i] * (height - 2 * padding)

                    drawLine(
                        color = Color.Blue,
                        start = Offset(prevX, prevY),
                        end = Offset(currX, currY),
                        strokeWidth = 2f
                    )
                }

                val gradient = Brush.verticalGradient(
                    colors = listOf(Color.Blue, Color.White)
                )
                val path = Path().apply {
                    moveTo(padding, height - padding)

                    for (i in normalizedPoints.indices) {
                        val x = padding + i * ((width - 2 * padding) / (points.size - 1))
                        val y = height - padding - normalizedPoints[i] * (height - 2 * padding)
                        lineTo(x, y)
                    }
                    
                    lineTo(width - padding, height - padding)
                    close()
                }

                drawPath(
                    path = path,
                    brush = gradient
                )
            }
        )

        val density = LocalDensity.current
        val width = with(density) { sizeCanvas.dp.toPx() }
        val height = width

        Box(
            modifier = Modifier
                .matchParentSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { tapOffset ->
                            val chartWidth = width - 2 * padding
                            val chartHeight = height - 2 * padding

                            val maxPoint = points.maxOrNull() ?: 0f
                            val normalizedPoints = points.map { it / maxPoint }

                            for (i in normalizedPoints.indices) {
                                val pointX = padding + i * (chartWidth / (points.size - 1))
                                val pointY = height - padding - normalizedPoints[i] * chartHeight

                                if ((tapOffset.x - pointX).absoluteValue <= 16 &&
                                    (tapOffset.y - pointY).absoluteValue <= 16
                                ) {
                                    tooltipText = "Значение: ${points[i]}"
                                    tooltipPosition = Offset(tapOffset.x, tapOffset.y - 40)
                                    break
                                }
                            }
                        }
                    )
                }
        )

        if (tooltipText != null && tooltipPosition != null) {
            Text(
                text = tooltipText!!,
                modifier = Modifier
                    .offset {
                        IntOffset(
                            tooltipPosition!!.x.toInt(),
                            tooltipPosition!!.y.toInt()
                        )
                    }
                    .background(Color.LightGray.copy(alpha = 0.9f), shape = RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                fontSize = 12.sp
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    AndroidhomeworkTheme {
        GraphScreen()
    }
}
