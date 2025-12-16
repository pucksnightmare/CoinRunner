package com.example.coinrunner.game

class GameLoop(
    private val gameView: GameView
) : Thread() {

    @Volatile
    var running: Boolean = false

    override fun run() {
        // 60 FPS aprox
        val targetFps = 60
        val targetFrameTimeMs = 1000L / targetFps

        var lastTime = System.nanoTime()

        while (running) {
            val now = System.nanoTime()
            val deltaSec = (now - lastTime) / 1_000_000_000f
            lastTime = now

            gameView.update(deltaSec)
            gameView.render()

            // Sleep simple para no quemar CPU
            val frameTimeMs = (System.nanoTime() - now) / 1_000_000L
            val sleepMs = targetFrameTimeMs - frameTimeMs
            if (sleepMs > 0) {
                try {
                    sleep(sleepMs)
                } catch (_: InterruptedException) {}
            }
        }
    }

    fun stopSafely() {
        running = false
        try {
            join()
        } catch (_: InterruptedException) {}
    }
}
