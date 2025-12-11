package com.example.coinrunner.game

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class Player {

    private val paint = Paint().apply {
        color = Color.BLUE
    }

    // Posición inicial del jugador
    var x = 200f
    var y = 200f

    // Tamaño del jugador
    private val size = 80f

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
