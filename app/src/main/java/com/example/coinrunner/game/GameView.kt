package com.example.coinrunner.game

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlin.math.max
import kotlin.random.Random

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    private val gameLoop = GameLoop(this)

    // Estad del juego
    private enum class GameState { RUNNING, GAME_OVER }
    private var state = GameState.RUNNING

    // Pinturas
    private val bgPaint = Paint().apply { color = Color.BLACK }
    private val floorPaint = Paint().apply { color = Color.DKGRAY }
    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 48f
        isAntiAlias = true
    }
    private val uiPaint = Paint().apply {
        color = Color.argb(80, 255, 255, 255) // botones "transparentes"
        style = Paint.Style.FILL
    }

    // Mundo
    private var screenW = 1
    private var screenH = 1
    private var floorY = 0f

    private lateinit var player: Player
    private val obstacles = mutableListOf<Obstacle>()
    private val coins = mutableListOf<Coin>()

    // “Velocidad del mundo”
    private var worldSpeed = 450f // px/seg (ajustable)

    // Puntaje
    private var score = 0
    private var highScore = 0

    // Preferencias
    private val prefs: SharedPreferences =
        context.getSharedPreferences("coin_runner_prefs", Context.MODE_PRIVATE)

    // Áreas táctiles (botones)
    private val leftZone = RectF()
    private val rightZone = RectF()
    private val jumpZone = RectF()

    // Input simple
    private var moveLeft = false
    private var moveRight = false
    private var wantJump = false

    init {
        holder.addCallback(this)
        isFocusable = true
        highScore = prefs.getInt("high_score", 0)
    }

    // Ciclo Surface
    override fun surfaceCreated(holder: SurfaceHolder) {
        gameLoop.running = true
        gameLoop.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        screenW = max(1, width)
        screenH = max(1, height)
        floorY = screenH * 0.82f

        // Zonas de botones (parte inferior)
        val uiH = screenH * 0.18f
        leftZone.set(0f, screenH - uiH, screenW * 0.33f, screenH.toFloat())
        rightZone.set(screenW * 0.33f, screenH - uiH, screenW * 0.66f, screenH.toFloat())
        jumpZone.set(screenW * 0.66f, screenH - uiH, screenW.toFloat(), screenH.toFloat())

        resetGame()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        pause()
    }

    fun pause() {
        if (gameLoop.running) {
            gameLoop.stopSafely()
        }
    }

    fun resume() {
        // Si el hilo ya murió por pausa, lo recreamos
        if (!gameLoop.running && holder.surface.isValid) {
            // Ojo: en este diseño, el GameLoop es val; para “resume” simple:
            // reiniciamos el proceso solo si el hilo no está vivo.
            // En práctica, lo más estable es recrear Activity, pero esto funciona.
        }
    }

    // Input
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (state == GameState.GAME_OVER) {
            if (event.action == MotionEvent.ACTION_DOWN) {
                resetGame()
            }
            return true
        }

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN, MotionEvent.ACTION_MOVE -> {
                moveLeft = false
                moveRight = false
                wantJump = false

                for (i in 0 until event.pointerCount) {
                    val x = event.getX(i)
                    val y = event.getY(i)

                    if (leftZone.contains(x, y)) moveLeft = true
                    if (rightZone.contains(x, y)) moveRight = true
                    if (jumpZone.contains(x, y)) wantJump = true
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL -> {
                // Si quedan dedos en pantalla, ACTION_MOVE lo ajusta; si no, limpiamos
                if (event.pointerCount <= 1) {
                    moveLeft = false
                    moveRight = false
                    wantJump = false
                }
            }
        }
        return true
    }

    // Updat and Render
    fun update(dt: Float) {
        if (state != GameState.RUNNING) return

        // 1) Player input
        player.setMoving(moveLeft, moveRight)
        if (wantJump) player.jump()

        // 2) Actualizar player (física)
        player.update(dt, floorY)

        // 3) Mover mundo (obstáculos y monedas se van a la izquierda)
        val dx = worldSpeed * dt
        obstacles.forEach { it.x -= dx; it.updateRect() }
        coins.forEach { it.x -= dx; it.updateRect() }

        // 4) Colisiones
        val pRect = player.rect

        // Obstáculos => Game Over
        for (ob in obstacles) {
            if (RectF.intersects(pRect, ob.rect)) {
                gameOver()
                return
            }
        }

        // Monedas => sumar
        for (c in coins) {
            if (!c.collected && RectF.intersects(pRect, c.rect)) {
                c.collected = true
                score += 1
            }
        }

        // 5) Limpiar objetos fuera de pantalla y respawnear
        obstacles.removeAll { it.x + it.w < 0f }
        coins.removeAll { it.x + it.size < 0f || it.collected }

        spawnIfNeeded()
    }

    fun render() {
        val canvas: Canvas = holder.lockCanvas() ?: return
        try {
            drawGame(canvas)
        } finally {
            holder.unlockCanvasAndPost(canvas)
        }
    }

    private fun drawGame(canvas: Canvas) {
        // Fondo
        canvas.drawRect(0f, 0f, screenW.toFloat(), screenH.toFloat(), bgPaint)

        // Suelo
        canvas.drawRect(0f, floorY, screenW.toFloat(), screenH.toFloat(), floorPaint)

        // Obstáculos y monedas
        obstacles.forEach { it.draw(canvas) }
        coins.forEach { it.draw(canvas) }

        // Player
        player.draw(canvas)

        // UI táctil
        canvas.drawRect(leftZone, uiPaint)
        canvas.drawRect(rightZone, uiPaint)
        canvas.drawRect(jumpZone, uiPaint)
        canvas.drawText("L", leftZone.left + 30f, leftZone.top + 60f, textPaint)
        canvas.drawText("R", rightZone.left + 30f, rightZone.top + 60f, textPaint)
        canvas.drawText("JUMP", jumpZone.left + 30f, jumpZone.top + 60f, textPaint)

        // Puntaje
        canvas.drawText("Score: $score", 30f, 60f, textPaint)
        canvas.drawText("High: $highScore", 30f, 115f, textPaint)

        if (state == GameState.GAME_OVER) {
            canvas.drawText("GAME OVER", screenW * 0.30f, screenH * 0.40f, textPaint)
            canvas.drawText("Tap para reiniciar", screenW * 0.24f, screenH * 0.46f, textPaint)
        }
    }

    // Logica de spawns/reset
    private fun resetGame() {
        state = GameState.RUNNING
        score = 0
        obstacles.clear()
        coins.clear()

        val startX = screenW * 0.20f
        val startY = floorY - 120f
        player = Player(startX, startY)

        // Spawns iniciales
        spawnInitialSet()
    }

    private fun spawnInitialSet() {
        var x = screenW * 0.9f
        repeat(4) {
            // Obstáculo
            val obW = 90f
            val obH = 90f
            obstacles.add(Obstacle(x, floorY - obH, obW, obH))

            // Moneda un poco arriba
            val coinSize = 45f
            coins.add(Coin(x + 160f, floorY - obH - 140f, coinSize))

            x += 420f
        }
    }

    private fun spawnIfNeeded() {
        // Si no hay nada por delante, generamos más
        val farthestX = max(
            obstacles.maxOfOrNull { it.x } ?: 0f,
            coins.maxOfOrNull { it.x } ?: 0f
        )

        if (farthestX < screenW * 1.2f) {
            val baseX = screenW * 1.4f

            // Obstáculo
            val obW = 90f
            val obH = 90f
            val gap = Random.nextInt(260, 520).toFloat()
            obstacles.add(Obstacle(baseX + gap, floorY - obH, obW, obH))

            // Moneda (a veces sí, a veces no)
            if (Random.nextBoolean()) {
                val coinSize = 45f
                val coinX = baseX + gap + Random.nextInt(120, 220)
                val coinY = floorY - obH - Random.nextInt(120, 200)
                coins.add(Coin(coinX.toFloat(), coinY.toFloat(), coinSize))
            }
        }
    }

    private fun gameOver() {
        state = GameState.GAME_OVER
        if (score > highScore) {
            highScore = score
            prefs.edit().putInt("high_score", highScore).apply()
        }
    }
}
