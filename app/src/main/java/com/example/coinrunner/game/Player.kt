package com.example.coinrunner.game

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class Player {

    private var x = 100f
    private var y = 300f
    private val speed = 5f

    private val paint = Paint().apply {
        color = Color.BLUE
    }

    fun update() {
        // Movimiento automático simple (temporal)
        x += speed
    }

    fun draw(canvas: Canvas) {
        canvas.drawRect(
            x,
            y,
            x + 100,
            y + 100,
            paint
        )
    }
}
