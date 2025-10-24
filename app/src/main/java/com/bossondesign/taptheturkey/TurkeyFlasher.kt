package com.bossondesign.taptheturkey

import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.core.graphics.drawable.DrawableCompat

object TurkeyFlasher {

    private val handler = Handler(Looper.getMainLooper())
    var isFlashing = false
    private val flashInterval: Long = 100 // Flash every 100 milliseconds (fast!)

    /**
     * Starts the rapid color flash effect on the turkey.
     */
    fun startFlashing(turkey: ImageView, durationMs: Long) {
        if (isFlashing) return

        isFlashing = true
        var isToggledOn = false

        // 1. Define the Runnable for rapid toggling
        val flashRunnable = object : Runnable {
            override fun run() {
                isToggledOn = !isToggledOn
                setTurkeyTint(turkey, isToggledOn)
                if (isFlashing) {
                    handler.postDelayed(this, flashInterval)
                }
            }
        }

        // 2. Start the flashing immediately
        handler.post(flashRunnable)

        // 3. Schedule the stop based on the total duration
        handler.postDelayed({
            stopFlashing(turkey)
        }, durationMs)
    }

    /**
     * Stops the flashing and resets the turkey's color to normal.
     */
    fun stopFlashing(turkey: ImageView) {
        isFlashing = false
        handler.removeCallbacksAndMessages(null)
        // Ensure the turkey returns to its original color (no tint)
        setTurkeyTint(turkey, false)
    }

    // CRITICAL: Mutate and setImageDrawable are used here for reliable tinting/clearing
    private fun setTurkeyTint(turkey: ImageView, applyRedTint: Boolean) {
        val originalDrawable = turkey.drawable ?: return

        // Mutate ensures we modify only this drawable instance.
        val wrappedDrawable = DrawableCompat.wrap(originalDrawable.mutate())

        if (applyRedTint) {
            DrawableCompat.setTint(wrappedDrawable, Color.RED)
        } else {
            // Clear the tint by setting the tint list to null
            DrawableCompat.setTintList(wrappedDrawable, null)
        }

        // Re-assigning the drawable forces the ImageView to refresh and apply the change.
        turkey.setImageDrawable(wrappedDrawable)
    }
}