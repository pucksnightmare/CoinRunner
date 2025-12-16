package com.example.coinrunner.game

import android.graphics.Canvas
import android.view.SurfaceHolder

class GameLoop(
    private val gameView: GameView,
    private val surfaceHolder: SurfaceHolder
) : Thread() {

    var running = false

    override fun run() {
        while (running) {
            val canvas: Canvas? = surfaceHolder.lockCanvas()

            if (canvas != null) {
                synchronized(surfaceHolder) {
                    gameView.update()   // 🔹 ahora sí existe
                    gameView.draw(canvas)
                }
                surfaceHolder.unlockCanvasAndPost(canvas)
            }
        }
    }

    fun stopLoop() {
        running = false
        try {
            join()
        } catch (_: InterruptedException) {}
    }
}
