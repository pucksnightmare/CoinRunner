package com.example.coinrunner.game

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF

class Coin(
    var x: Float,
    var y: Float,
    var size: Float
) {
    private val paint = Paint().apply { color = Color.YELLOW }
    var collected: Boolean = false
    val rect = RectF(x, y, x + size, y + size)

    fun updateRect() {
        rect.set(x, y, x + size, y + size)
    }

    fun draw(canvas: Canvas) {
        if (!collected) {
            // coin simple: círculo
            val cx = x + size / 2f
            val cy = y + size / 2f
            canvas.drawCircle(cx, cy, size / 2f, paint)
        }
    }
}
