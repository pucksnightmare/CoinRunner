package com.example.coinrunner.game

import android.graphics.Canvas
import android.view.SurfaceHolder

class GameLoop(
    private val gameView: GameView,
    private val surfaceHolder: SurfaceHolder
) : Thread() {

    var running = false
    private val targetFPS = 60
    private val frameTime = 1000L / targetFPS

    override fun run() {
        while (running) {
            val startTime = System.currentTimeMillis()
            var canvas: Canvas? = null

            try {
                canvas = surfaceHolder.lockCanvas()
                if (canvas != null) {
                    synchronized(surfaceHolder) {
                        gameView.draw(canvas)
                    }
                }
            } finally {
                canvas?.let {
                    surfaceHolder.unlockCanvasAndPost(it)
                }
            }

            // Control de FPS
            val timeTaken = System.currentTimeMillis() - startTime
            val sleepTime = frameTime - timeTaken
            if (sleepTime > 0) {
                sleep(sleepTime)
            }
        }
    }

    fun joinSafely() {
        try {
            join()
        } catch (_: InterruptedException) {}
    }
}
