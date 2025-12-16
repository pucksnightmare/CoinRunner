package com.example.coinrunner.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.view.SurfaceHolder
import android.view.SurfaceView

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    private lateinit var gameLoop: GameLoop
    private lateinit var player: Player

    init {
        holder.addCallback(this)
        gameLoop = GameLoop(this, holder)
        isFocusable = true
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        player = Player(width, height)
        gameLoop.running = true
        gameLoop.start()
    }

    override fun surfaceChanged(
        holder: SurfaceHolder,
        format: Int,
        width: Int,
        height: Int
    ) {
        // obligatorio aunque esté vacío
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        gameLoop.running = false
        gameLoop.joinSafely()
    }

    fun update() {
        player.update()
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        // Fondo negro
        canvas.drawColor(Color.BLACK)

        player.draw(canvas)
    }
}
