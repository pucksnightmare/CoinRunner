package com.example.coinrunner.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.SurfaceHolder
import android.view.SurfaceView

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    private val gameLoop: GameLoop = GameLoop(this, holder)

    // Pintura para probar el dibujo (cuadro rojo)
    private val paint = Paint().apply {
        color = Color.RED
    }

    init {
        holder.addCallback(this)
        isFocusable = true
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        gameLoop.running = true
        gameLoop.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // De momento no hacemos nada aquí
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        gameLoop.running = false
        gameLoop.joinSafely()
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        // Fondo negro
        canvas.drawColor(Color.BLACK)

        // Cuadro rojo de prueba (más adelante aquí irá el jugador, etc.)
        canvas.drawRect(
            100f,
            100f,
            300f,
            300f,
            paint
        )
    }
}
