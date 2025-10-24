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

// Implement the LevelChangeListener interface to handle milestones
class MainActivity : AppCompatActivity(), DifficultyManager.LevelChangeListener {

    // Global UI components
    private lateinit var turkey: ImageView
    private lateinit var scoreTextView: TextView
    private lateinit var gameBoundary: ConstraintLayout
    private lateinit var heartViews: List<ImageView>
    private lateinit var gameOverOverlay: View // The overlay for Game Over/Win screen
    private lateinit var gameOverText: TextView
    private lateinit var restartButton: Button

    // Game State variables
    private var score: Int = 0
    private var health: Int = 3
    private var isGameOver: Boolean = false // Set to true for Game Over or Win State
    private lateinit var mp: MediaPlayer

    // Calculated offset for movement boundary
    private var topAreaHeight: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Initialize UI components
        turkey = findViewById(R.id.turkey)
        scoreTextView = findViewById(R.id.textScore)
        gameBoundary = findViewById(R.id.rootConstraintLayout)
        val privacyTextView = findViewById<TextView>(R.id.textPrivacy)

        // Initialize Game Over UI (assuming you add these IDs to your XML)
        gameOverOverlay = findViewById(R.id.gameOverOverlay)
        gameOverText = findViewById(R.id.gameOverText)
        restartButton = findViewById(R.id.restartButton)

        // Initialize heart ImageViews (IDs from activity_main.xml)
        heartViews = listOf(
            findViewById(R.id.heart1),
            findViewById(R.id.heart2),
            findViewById(R.id.heart3)
        )

        // 2. Setup systems
        mp = MediaPlayer.create(this, R.raw.gobble)
        privacyTextView.movementMethod = LinkMovementMethod.getInstance()

        // Set the MainActivity as the listener for level-up events
        DifficultyManager.setLevelChangeListener(this)

        // Set up restart button listener
        restartButton.setOnClickListener {
            resetGame()
        }

        // 3. Set up listeners (Game Logic)
        setupTurkeyClickListener()
        setupMissClickListener()

        // 4. Start the game flow
        resetGame() // Call resetGame() to initialize the state and start movement
    }

    // --- LISTENER SETUP ---

    private fun setupTurkeyClickListener() {
        turkey.setOnClickListener {
            if (isGameOver) return@setOnClickListener

            // Check if the tap was a RED PENALTY
            if (TurkeyMover.isRed) {
                // RED PENALTY: Lose a heart
                health--
                updateHearts()
                checkGameOverOrWin()
            } else {
                // NORMAL HIT: Gain 1 point
                score++
                updateScoreText()
                checkGameOverOrWin()
            }

            TurkeyAnimator.shake(turkey)
            if (mp.isPlaying) { mp.stop(); mp.prepare() }
            mp.start()
        }
    }

    private fun setupMissClickListener() {
        gameBoundary.setOnClickListener {
            if (isGameOver) return@setOnClickListener

            // MISS PENALTY: Deduct points based on the current level's setting
            val missPenalty = DifficultyManager.getCurrentSettings().missPenalty
            score -= missPenalty

            // Prevent score from dropping below zero
            if (score < 0) score = 0

            updateScoreText()
            checkGameOverOrWin()
        }
    }

    // --- UI/STATE MANAGEMENT ---

    private fun updateScoreText() {
        scoreTextView.text = "Score: $score"
    }

    private fun updateHearts() {
        // You would use R.drawable.heart_full and R.drawable.heart_empty here
        val fullHeartDrawable = ContextCompat.getDrawable(this, R.drawable.heart_full)
        val emptyHeartDrawable = ContextCompat.getDrawable(this, R.drawable.heart_empty)

        heartViews.forEachIndexed { index, imageView ->
            imageView.setImageDrawable(if (index < health) fullHeartDrawable else emptyHeartDrawable)
        }
    }

    private fun checkGameOverOrWin() {
        // Check for Win Condition
        if (score >= 250) {
            isGameOver = true
            TurkeyMover.stopMovement()
            gameOverText.text = "YOU WIN! Final Score: $score"
            gameOverOverlay.visibility = View.VISIBLE
            return
        }

        // Check for Game Over Condition
        if (health <= 0) {
            isGameOver = true
            TurkeyMover.stopMovement()
            gameOverText.text = "GAME OVER! Final Score: $score"
            gameOverOverlay.visibility = View.VISIBLE
            return
        }

        // Check difficulty after every state change (hit or miss)
        val scoreOffset = scoreTextView.bottom + scoreTextView.paddingBottom
        DifficultyManager.checkAndUpdateDifficulty(score, turkey, gameBoundary, scoreOffset)
    }

    /**
     * Implements the level change logic (Graphics/Pause).
     */
    override fun onLevelUp(settings: GameSettings) {
        // 1. Stop the game flow
        TurkeyMover.stopMovement()

        // 2. APPLY HEART REFILL LOGIC
        if (settings.levelName != "WINNER") {
            health = 3 // Refill health to max
            updateHearts() // Update the UI to show full hearts
        }

        // 3. Apply New Graphics/Assets
        gameBoundary.setBackgroundResource(settings.backgroundResId)
        turkey.setImageResource(settings.turkeyBaseResId)

        // 4. PAUSE/TRANSITION LOGIC (Currently immediate resume)

        // 5. Resume the game
        val scoreOffset = scoreTextView.bottom + scoreTextView.paddingBottom
        if (settings.levelName == "WINNER") {
            // The Win state is handled separately and doesn't resume movement
        } else {
            // Apply new difficulty settings and restart movement
            DifficultyManager.applyCurrentSettings(turkey, gameBoundary, scoreOffset)
            Toast.makeText(this, "LEVEL UP! Now ${settings.levelName}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resetGame() {
        // Reset state variables
        score = 0
        health = 3
        isGameOver = false

        // Reset difficulty manager
        DifficultyManager.resetGame()

        // Reset UI
        updateScoreText()
        updateHearts()
        gameOverOverlay.visibility = View.GONE

        // Calculate boundary and start movement using initial settings (Level 0)
        gameBoundary.post {
            topAreaHeight = scoreTextView.bottom + scoreTextView.paddingBottom
            // The initial state is applied via applyCurrentSettings
            DifficultyManager.applyCurrentSettings(turkey, gameBoundary, topAreaHeight)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        TurkeyMover.stopMovement()
        TurkeyFlasher.stopFlashing(turkey)
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