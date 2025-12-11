package com.example.coinrunner

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.coinrunner.game.GameView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mostramos directamente nuestro SurfaceView del juego
        val gameView = GameView(this)
        setContentView(gameView)
    }
}
