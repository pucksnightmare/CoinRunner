package com.example.coinrunner.game

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import kotlin.math.max
import kotlin.math.min

class Player(
    startX: Float,
    startY: Float
) {
    private val paint = Paint().apply { color = Color.CYAN }

    var x = startX
    var y = startY

    private val w = 100f
    private val h = 120f

    private var vx = 0f
    private var vy = 0f

    private val speed = 520f
    private val gravity = 2200f
    private val jumpImpulse = -950f

    private var onGround = false

    val rect = RectF(x, y, x + w, y + h)

    fun setMoving(left: Boolean, right: Boolean) {
        vx = when {
            left && !right -> -speed
            right && !left -> speed
            else -> 0f
        }
    }

    fun jump() {
        if (onGround) {
            vy = jumpImpulse
            onGround = false
        }
    }

    fun update(dt: Float, floorY: Float) {
        // Aplicar gravedad
        vy += gravity * dt

        // Mover
        x += vx * dt
        y += vy * dt

        // Limitar X un poco
        x = max(0f, min(x, 9000f)) // el límite real lo puedes ajustar si quieres

        // Colisión con suelo
        val bottom = y + h
        if (bottom >= floorY) {
            y = floorY - h
            vy = 0f
            onGround = true
        }

        rect.set(x, y, x + w, y + h)
    }

    fun draw(canvas: Canvas) {
        canvas.drawRect(rect, paint)
    }
}
