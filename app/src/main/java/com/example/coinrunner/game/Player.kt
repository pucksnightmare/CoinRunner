package com.example.coinrunner.game

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class Player(
    var x: Float,
    var y: Float,
    private val size: Float = 100f
) {
    private val paint = Paint().apply {
        color = Color.BLUE
    }

    fun draw(canvas: Canvas) {
        canvas.drawRect(
            x,
            y,
            x + size,
            y + size,
            paint
        )
    }
}
