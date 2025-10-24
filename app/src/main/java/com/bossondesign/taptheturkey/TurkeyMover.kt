package com.bossondesign.taptheturkey

import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import kotlin.random.Random

object TurkeyMover {

    private val handler = Handler(Looper.getMainLooper())
    private var isMoving = false
    private var isFlashingAllowed = false
    private const val FLASH_GRACE_PERIOD_MS = 6000L

    // NEW: Dynamic parameters
    private var flashChancePercent: Int = 15
    private var allowedAppearances: List<TurkeyAppearance> = TurkeyAppearance.APPEARANCES
    private var currentMoveDelayMs: Long = 800L

    val isRed: Boolean
        get() = TurkeyFlasher.isFlashing

    /**
     * Starts the turkey moving to a random location at a specified interval.
     */
    fun startMovement(turkey: ImageView, moveDelayMs: Long, boundaryView: View, topOffset: Int = 0) {
        if (isMoving) return

        // Set initial speed for the loop
        this.currentMoveDelayMs = moveDelayMs

        // Reset Flashing Grace Period and schedule activation
        isFlashingAllowed = false
        handler.postDelayed({
            isFlashingAllowed = true
        }, FLASH_GRACE_PERIOD_MS)

        TurkeyFlasher.stopFlashing(turkey)

        val runnable = object : Runnable {
            override fun run() {
                moveTurkeyRandomly(turkey, boundaryView, topOffset)
                handler.postDelayed(this, currentMoveDelayMs) // Use dynamic speed

                // Only check for red state if the grace period has passed
                if (isFlashingAllowed) {
                    // Check chance based on dynamic flashChancePercent
                    if (Random.nextInt(1, 101) <= flashChancePercent) {
                        flashRed(turkey)
                    }
                }
            }
        }

        isMoving = true
        handler.post(runnable)
    }

    /**
     * Stops the scheduled turkey movement and flashing.
     */
    fun stopMovement() {
        handler.removeCallbacksAndMessages(null)
        isMoving = false
        isFlashingAllowed = false
    }

    /**
     * UPDATED: Moves the turkey, picks a random appearance, and applies the size.
     */
    private fun moveTurkeyRandomly(turkey: ImageView, boundaryView: View, topOffset: Int) {

        // 1. Randomly select a new appearance from the allowed list
        val newAppearance = allowedAppearances.random()

        // 2. Apply the new size (in pixels, converted from dp)
        val density = turkey.resources.displayMetrics.density

        turkey.layoutParams.width = (newAppearance.width * density).toInt()
        turkey.layoutParams.height = (newAppearance.height * density).toInt()
        turkey.requestLayout() // Crucial to redraw the view with the new size

        // 3. Calculate movement boundaries based on the turkey's new size
        val maxX = boundaryView.width - turkey.layoutParams.width
        val maxY = boundaryView.height - turkey.layoutParams.height

        if (maxX <= 0 || maxY <= 0) return

        val randomX = Random.nextInt(0, maxX).toFloat()
        val randomY = Random.nextInt(topOffset, maxY).toFloat()

        // 4. Move the turkey
        turkey.animate()
            .x(randomX)
            .y(randomY)
            .setDuration(200)
            .start()
    }

    private fun flashRed(turkey: ImageView) {
        if (!isFlashingAllowed || isRed) return
        val redDuration = Random.nextLong(1000, 2001)
        TurkeyFlasher.startFlashing(turkey, redDuration)
    }

    /**
     * Updates parameters and restarts the movement loop.
     */
    fun updateMovementParams(moveDelayMs: Long, flashChancePercent: Int, missPenalty: Int, allowedAppearances: List<TurkeyAppearance>, turkey: ImageView, boundaryView: View, topOffset: Int) {
        // Stop current loop
        handler.removeCallbacksAndMessages(null)

        // Update stored parameters
        this.flashChancePercent = flashChancePercent
        this.allowedAppearances = allowedAppearances
        // We no longer need to store missPenalty here, but it's passed back to MainActivity for use

        // Restart the loop with new speed
        startMovement(turkey, moveDelayMs, boundaryView, topOffset)
    }
}