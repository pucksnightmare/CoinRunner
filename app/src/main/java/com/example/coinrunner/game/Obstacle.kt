package com.example.coinrunner.game

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF

class Obstacle(
    var x: Float,
    var y: Float,
    var w: Float,
    var h: Float
) {
    private val paint = Paint().apply { color = Color.RED }
    val rect = RectF(x, y, x + w, y + h)

    fun updateRect() {
        rect.set(x, y, x + w, y + h)
    }

    fun draw(canvas: Canvas) {
        canvas.drawRect(rect, paint)
    }
}
