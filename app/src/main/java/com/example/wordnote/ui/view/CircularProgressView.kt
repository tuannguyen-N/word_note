package com.example.wordnote.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.graphics.toColorInt
import kotlin.math.min

class CircularProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val strokeWidth = 10f
    private val startAngle = 180f
    private val sweepAngleMax = 405f

    var progress = 70f
        set(value) {
            field = value.coerceIn(0f, 100f)
            invalidate()
        }

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = this@CircularProgressView.strokeWidth
        color = Color.TRANSPARENT
        strokeCap = Paint.Cap.ROUND
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = this@CircularProgressView.strokeWidth
        color = "#0085FF".toColorInt()
        strokeCap = Paint.Cap.ROUND
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val size = min(width, height)
        val padding = strokeWidth / 2
        val rect = RectF(
            padding,
            padding,
            size - padding,
            size - padding
        )

        // background arc
        canvas.drawArc(rect, startAngle, sweepAngleMax, false, bgPaint)

        // progress arc
        val sweep = sweepAngleMax * (progress / 100f)
        canvas.drawArc(rect, startAngle, sweep, false, progressPaint)
    }
}
