package com.example.android_homework.presentation.customViews

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import com.example.android_homework.R
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class CircularDiagram @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var numberOfSectors = 4
    private var sectorColors = listOf(
        Color.BLUE,
        Color.YELLOW,
        Color.GREEN,
        Color.CYAN
    )
    private var activeSectorIndex = -1

    private var ringRadius = 0f
    private val startAngle = 180f
    private var ringThickness = 200f
    private val center = PointF(0f, 0f)
    private var sweepAngle = 360f / numberOfSectors

    private val sectorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 128f
        textAlign = Paint.Align.CENTER
    }

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.CircularDiagram, 0, 0).apply {
            try {
                numberOfSectors = getInt(R.styleable.CircularDiagram_sectorCount, 4)
                sweepAngle = 360f / numberOfSectors

                val colorResId = getResourceId(R.styleable.CircularDiagram_sectorColors, 0)
                if (colorResId != 0) {
                    val ta = resources.obtainTypedArray(colorResId)
                    sectorColors = generateColorListWithoutRepeats(numberOfSectors, List(ta.length()) { i -> ta.getColor(i, Color.GRAY) })
                    ta.recycle()
                }

            } finally {
                recycle()
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        ringThickness = minOf(w, h) * 0.2f
        sectorPaint.strokeWidth = ringThickness
        ringRadius = (minOf(w, h) / 2 - ringThickness / 2)
        center.set(w/2f, h/2f)
    }

    override fun onDraw(canvas: Canvas) {
        for (i in 0 until numberOfSectors) {
            val colorIndex = i % sectorColors.size
            val angle = i*sweepAngle + startAngle
            val currentColor = if (i == activeSectorIndex) lightenColor(sectorColors[colorIndex]) else sectorColors[colorIndex]
            drawSingleSector(canvas, angle, currentColor)
        }

        for (i in 0 until numberOfSectors){
            val angle = i*sweepAngle + startAngle
            val colorIndex = i % sectorColors.size
            val currentColor = if (i == activeSectorIndex) lightenColor(sectorColors[colorIndex]) else sectorColors[colorIndex]
            drawCircles(canvas, angle, currentColor)
        }

        canvas.drawText("$numberOfSectors", center.x, center.y + textPaint.textSize / 2, textPaint)
    }

    private fun drawCircles(canvas: Canvas, startAngle: Float, currentColor: Int){

        sectorPaint.apply {
            reset()
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.BUTT
            strokeWidth = ringThickness
            color = currentColor
            isAntiAlias = true
        }

        val endAngleRad = Math.toRadians((startAngle + sweepAngle).toDouble()).toFloat()

        val x = center.x + ringRadius * cos(endAngleRad.toDouble()).toFloat()
        val y = center.y + ringRadius * sin(endAngleRad.toDouble()).toFloat()

        canvas.drawCircle(x, y, 0.5f, sectorPaint)
    }

    private fun drawSingleSector(canvas: Canvas, startAngle: Float, currentColor: Int) {
        sectorPaint.apply {
            reset()
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.BUTT
            strokeWidth = ringThickness
            color = currentColor
            isAntiAlias = true
        }


        canvas.drawArc(
            width / 2f - ringRadius,
            height / 2f - ringRadius,
            width / 2f + ringRadius,
            height / 2f + ringRadius,
            startAngle,
            sweepAngle,
            false,
            sectorPaint
        )

    }

    fun lightenColor(@ColorInt color: Int, factor: Float = 0.6f): Int {
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)

        val newR = (r + ((255 - r) * factor)).toInt().coerceIn(0, 255)
        val newG = (g + ((255 - g) * factor)).toInt().coerceIn(0, 255)
        val newB = (b + ((255 - b) * factor)).toInt().coerceIn(0, 255)

        return Color.argb(Color.alpha(color), newR, newG, newB)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val touchX = event.x
                val touchY = event.y

                activeSectorIndex = getSectorIndex(touchX, touchY)

                invalidate()
            }
        }
        return super.onTouchEvent(event)
    }

    private fun getSectorIndex(x: Float, y: Float): Int {
        val dx = x - center.x
        val dy = y - center.y
        val distance = sqrt((dx * dx + dy * dy).toDouble())

        if ((distance >= ringRadius + ringThickness/2) or (distance <= ringRadius - ringThickness/2)) return -1

        var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
        if (angle < 0) angle += 360f
        angle = (angle - startAngle + 360) % 360
        val activeIndex = (angle / sweepAngle).toInt()

        return if (activeIndex < 0 || activeIndex >= numberOfSectors) {
            -1
        } else {
            activeIndex
        }
    }

    private fun generateColorListWithoutRepeats(count: Int, colors: List<Int>): List<Int> {
        if (colors.isEmpty()) return List(count) { Color.GRAY }

        val result = mutableListOf<Int>()
        var lastColor: Int? = null

        for (i in 0 until count - 1) {
            val filtered = if (lastColor == null) colors else colors.filter { it != lastColor }
            val nextColor = if (filtered.isNotEmpty()) {
                filtered.random()
            } else {
                colors.random()
            }
            result.add(nextColor)
            lastColor = nextColor
        }

        result.add(colors.filter { it != result.first() }.random())

        return result
    }

    fun setSectorNumbers(count: Int){
        numberOfSectors = count
        sweepAngle = 360f / numberOfSectors
        invalidate()
    }

    fun setColors(colors: List<Int>) {
        sectorColors = generateColorListWithoutRepeats(numberOfSectors, colors)
    }
}