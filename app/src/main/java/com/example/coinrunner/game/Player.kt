package com.example.coinrunner.game

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class Player(
    private val screenWidth: Int,
    private val screenHeight: Int
) {

    private val paint = Paint()

    var x = 50f
    var y = screenHeight - 200f

    private val width = 100f
    private val height = 100f

    private var speedX = 8f

    init {
        paint.color = Color.BLUE
    }

    fun update() {
        x += speedX

        // LÍMITE DERECHO
        if (x + width > screenWidth) {
            x = screenWidth - width
            speedX = 0f
        }

        // LÍMITE IZQUIERDO (por si luego movemos a la izquierda)
        if (x < 0) {
            x = 0f
        }
    }

    fun draw(canvas: Canvas) {
        canvas.drawRect(
            x,
            y,
            x + width,
            y + height,
            paint
        )
    }
}
