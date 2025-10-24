package com.bossondesign.taptheturkey

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import kotlin.random.Random

@SuppressLint("StaticFieldLeak")
object TurkeyMover {

    private var handler = Handler(Looper.getMainLooper())
    private var moveRunnable: Runnable? = null

    // Movement parameters
    private var currentMoveDelayMs: Long = 800L
    private var currentFlashChancePercent: Int = 15
    private var currentMissPenalty: Int = 1
    private var currentAllowedAppearances: List<TurkeyAppearance> = TurkeyAppearance.APPEARANCES

    // FIX: Made views nullable to prevent memory leaks
    private var currentTurkey: ImageView? = null
    private var currentBoundaryView: View? = null
    private var currentTopOffset: Int = 0

    var isRed: Boolean = false

    /**
     * Initializes and starts the turkey's movement.
     * This signature matches the call in MainActivity.
     */
    fun startMovement(
        moveDelayMs: Long,
        flashChancePercent: Int,
        missPenalty: Int,
        allowedAppearances: List<TurkeyAppearance>,
        turkey: ImageView,
        boundaryView: View,
        topOffset: Int
    ) {
        stopMovement()

        // Update current parameters and store references
        currentMoveDelayMs = moveDelayMs
        currentFlashChancePercent = flashChancePercent
        currentMissPenalty = missPenalty
        currentAllowedAppearances = allowedAppearances
        currentTurkey = turkey
        currentBoundaryView = boundaryView
        currentTopOffset = topOffset

        // Correct call to stopFlashing (durationMs is optional now)
        TurkeyFlasher.stopFlashing(turkey)
        isRed = false

        // Start the movement loop
        moveRunnable = object : Runnable {
            override fun run() {
                moveTurkey()
                handler.postDelayed(this, currentMoveDelayMs)
            }
        }
        handler.post(moveRunnable!!)
    }

    /**
     * Stops the turkey's movement and CLEARS VIEW REFERENCES.
     */
    fun stopMovement() {
        moveRunnable?.let { handler.removeCallbacks(it) }

        currentTurkey?.let { TurkeyFlasher.stopFlashing(it) }

        // FIX: CLEAR REFERENCES TO PREVENT MEMORY LEAKS
        currentTurkey = null
        currentBoundaryView = null

        isRed = false
        moveRunnable = null
    }

    /**
     * Updates movement parameters when difficulty changes.
     */
    fun updateMovementParams(
        moveDelayMs: Long,
        flashChancePercent: Int,
        missPenalty: Int,
        allowedAppearances: List<TurkeyAppearance>,
        turkey: ImageView,
        boundaryView: View,
        topOffset: Int
    ) {
        // Update all parameters, including UI references
        currentMoveDelayMs = moveDelayMs
        currentFlashChancePercent = flashChancePercent
        currentMissPenalty = missPenalty
        currentAllowedAppearances = allowedAppearances
        currentTurkey = turkey
        currentBoundaryView = boundaryView
        currentTopOffset = topOffset

        // If movement is currently active, stop and restart with new delay.
        if (moveRunnable != null) {
            startMovement(
                currentMoveDelayMs,
                currentFlashChancePercent,
                currentMissPenalty,
                currentAllowedAppearances,
                currentTurkey!!,
                currentBoundaryView!!,
                currentTopOffset
            )
        }
    }

    private fun moveTurkey() {
        if (currentTurkey == null || currentBoundaryView == null) {
            return
        }

        val turkey = currentTurkey!!
        val boundaryView = currentBoundaryView!!

        // Apply appearance (size)
        val selectedAppearance = currentAllowedAppearances.random(Random)
        turkey.layoutParams.width = selectedAppearance.width
        turkey.layoutParams.height = selectedAppearance.height
        turkey.requestLayout()

        // Calculate boundaries and position turkey
        val screenWidth = boundaryView.width
        val screenHeight = boundaryView.height

        val targetWidth = if (turkey.width > 0) turkey.width else selectedAppearance.width
        val targetHeight = if (turkey.height > 0) turkey.height else selectedAppearance.height

        val randomX = Random.nextFloat() * (screenWidth - targetWidth)
        val randomY = Random.nextFloat() * (screenHeight - targetHeight - currentTopOffset) + currentTopOffset
        turkey.x = randomX
        turkey.y = randomY

        // Randomly decide if the turkey should flash red
        if (Random.nextInt(100) < currentFlashChancePercent) {
            // Change this line:
            TurkeyFlasher.startFlashing(turkey, 1000)
            isRed = true
        } else {
            TurkeyFlasher.stopFlashing(turkey)
            isRed = false
        }
    }
}