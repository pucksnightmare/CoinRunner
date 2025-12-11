package com.example.coinrunner

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.coinrunner.game.GameView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // En lugar de inflar un layout XML, mostramos directamente el GameView
        val gameView = GameView(this)
        setContentView(gameView)
    }
}
