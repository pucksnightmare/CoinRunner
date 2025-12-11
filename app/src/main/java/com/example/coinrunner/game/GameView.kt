package com.example.coinrunner.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.SurfaceHolder
import android.view.SurfaceView

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    private lateinit var gameLoop: GameLoop
    private val paint = Paint()

    init {
        holder.addCallback(this)
        gameLoop = GameLoop(this, holder)
        isFocusable = true
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        gameLoop.running = true
        gameLoop.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        gameLoop.running = false
        gameLoop.joinSafely()
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        // Fondo del juego
        canvas.drawColor(Color.BLACK)

        // Dibujar un rectángulo de prueba
        paint.color = Color.RED
        canvas.drawRect(100f, 100f, 300f, 300f, paint)
    }
}
