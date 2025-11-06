package com.bossondesign.taptheturkey

import android.media.MediaPlayer
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity(), DifficultyManager.LevelChangeListener {

    private lateinit var turkey: ImageView
    private lateinit var scoreTextView: TextView
    private lateinit var gameBoundary: ConstraintLayout
    private lateinit var heartViews: List<ImageView>
    private lateinit var gameOverOverlay: View
    // REMOVED: private lateinit var gameOverText: TextView // This line is gone
    private lateinit var restartButton: Button
    private lateinit var gameOverImage: ImageView

    private var score: Int = 0
    private var health: Int = 3
    private var isGameOver: Boolean = false
    private lateinit var mp: MediaPlayer

    private var topAreaHeight: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Initialize UI components
        turkey = findViewById(R.id.turkey)
        scoreTextView = findViewById(R.id.textScore)
        gameBoundary = findViewById(R.id.rootConstraintLayout)

        val privacyTextView = findViewById<TextView>(R.id.textPrivacy)

        gameOverOverlay = findViewById(R.id.gameOverOverlay)
        // REMOVED: gameOverText = findViewById(R.id.gameOverText) // This line is gone
        restartButton = findViewById(R.id.restartButton)
        gameOverImage = findViewById(R.id.gameOverImage)

        heartViews = listOf(
            findViewById(R.id.heart1),
            findViewById(R.id.heart2),
            findViewById(R.id.heart3)
        )

        // 2. Setup systems
        mp = MediaPlayer.create(this, R.raw.gobble)
        privacyTextView.movementMethod = LinkMovementMethod.getInstance()
        DifficultyManager.setLevelChangeListener(this)
        restartButton.setOnClickListener { resetGame() }

        // 3. Set up listeners (Game Logic)
        setupTurkeyClickListener()
        setupMissClickListener()

        // 4. Calculate boundary and start the game flow
        gameBoundary.post {
            topAreaHeight = scoreTextView.bottom + scoreTextView.paddingBottom
            resetGame()
        }
    }

    // --- LISTENER SETUP ---

    private fun setupTurkeyClickListener() {
        turkey.setOnClickListener {
            if (isGameOver) return@setOnClickListener

            if (TurkeyMover.isRed) {
                health--
                updateHearts()
            } else {
                score++
                updateScoreText()
            }

            TurkeyAnimator.shake(turkey)
            if (mp.isPlaying) { mp.stop(); mp.prepare() }
            mp.start()

            checkGameOverAndWinConditions()
        }
    }

    private fun setupMissClickListener() {
        gameBoundary.setOnClickListener {
            if (isGameOver) return@setOnClickListener

            val missPenalty = DifficultyManager.getCurrentSettings().missPenalty
            score -= missPenalty
            if (score < 0) score = 0
            updateScoreText()

            checkGameOverAndWinConditions()
        }
    }

    // --- UI/STATE MANAGEMENT ---

    private fun updateScoreText() {
        scoreTextView.text = "Score: $score"
    }

    private fun updateHearts() {
        val fullHeartDrawable = ContextCompat.getDrawable(this, R.drawable.heart_full)
        val emptyHeartDrawable = ContextCompat.getDrawable(this, R.drawable.heart_empty)

        heartViews.forEachIndexed { index, imageView ->
            imageView.setImageDrawable(if (index < health) fullHeartDrawable else emptyHeartDrawable)
        }
    }

    private fun checkGameOverAndWinConditions() {
        if (health <= 0) {
            isGameOver = true
            TurkeyMover.stopMovement()
            // REMOVED: gameOverText.text = "GAME OVER! Final Score: $score" // This line is gone
            gameOverImage.setImageResource(R.drawable.turkey_sad)
            gameOverOverlay.visibility = View.VISIBLE
            return
        }

        DifficultyManager.checkAndUpdateDifficulty(score, turkey, gameBoundary, topAreaHeight)
    }

    /**
     * Called by DifficultyManager when a new level is achieved (or game is won).
     */
    override fun onLevelUp(settings: GameSettings) {
        TurkeyMover.stopMovement()

        gameBoundary.setBackgroundResource(settings.backgroundResId)

        if (settings.levelName == "WINNER") {
            isGameOver = true

            // 1. Assign the win graphic (e.g., 'win' image) to the OVERLAY view
            gameOverImage.setImageResource(settings.turkeyBaseResId)

            // 2. Hide the tappable turkey
            turkey.visibility = View.INVISIBLE // Correctly hides the active turkey

            // 3. Show the final screen
            gameOverImage.visibility = View.VISIBLE // Ensure the new image is visible
            gameOverOverlay.visibility = View.VISIBLE

        } else {
            // --- NORMAL LEVEL UP LOGIC ---
            // ONLY set the image source when we are NOT winning
            turkey.setImageResource(settings.turkeyBaseResId) // <-- MOVED HERE

            health = 3
            updateHearts()

            TurkeyMover.startMovement(
                settings.moveDelayMs,
                settings.flashChancePercent,
                settings.missPenalty,
                settings.allowedAppearances,
                turkey,
                gameBoundary,
                topAreaHeight
            )
            Toast.makeText(this, "LEVEL UP! Now ${settings.levelName}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resetGame() {
        score = 0
        health = 3
        isGameOver = false

        DifficultyManager.resetGame()

        updateScoreText()
        updateHearts()
        gameOverOverlay.visibility = View.GONE
        turkey.visibility = View.VISIBLE

        val initialSettings = DifficultyManager.getCurrentSettings()
        turkey.setImageResource(initialSettings.turkeyBaseResId)

        TurkeyMover.startMovement(
            initialSettings.moveDelayMs,
            initialSettings.flashChancePercent,
            initialSettings.missPenalty,
            initialSettings.allowedAppearances,
            turkey,
            gameBoundary,
            topAreaHeight
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        TurkeyMover.stopMovement()
        mp.release()
    }
}

/*
package com.bossondesign.taptheturkey

import android.media.MediaPlayer
import android.os.Bundle
//import android.support.v7.app.AppCompatActivity
import android.text.method.LinkMovementMethod
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val turkey = findViewById<ImageView>(R.id.turkey) as ImageView
        val mp = MediaPlayer.create(this, R.raw.gobble)

        //privacy policy text link
        val textView = findViewById<TextView>(R.id.textPrivacy)
        textView.movementMethod = LinkMovementMethod.getInstance()

        turkey.setOnClickListener {

            //handles animation
            turkey.startAnimation(TranslateAnimation(0f, 20f, 0f, 20f).apply {
                duration = 10
                repeatCount = 20

                //plays sound
                onPause()
                mp!!.start()

            })

        }

    }
}
*/