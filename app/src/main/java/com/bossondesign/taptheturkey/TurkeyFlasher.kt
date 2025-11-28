package com.bossondesign.taptheturkey

import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.core.graphics.drawable.DrawableCompat

object TurkeyFlasher {

    private val handler = Handler(Looper.getMainLooper())
    var isFlashing = false
    private val flashInterval: Long = 100
    val flashColor = Color.argb(180, 255, 255, 255)
    val redFlash = Color.argb(180, 255, 0, 0)
    private var currentFlashColor = flashColor

    /**
     * Starts the rapid color flash effect on the turkey.
     */
    fun startFlashing(turkey: ImageView, durationMs: Long) {
        if (isFlashing) return

        currentFlashColor = flashColor
        isFlashing = true
        var isToggledOn = false

        val flashRunnable = object : Runnable {
            override fun run() {
                isToggledOn = !isToggledOn
                setTurkeyTint(turkey, isToggledOn)
                if (isFlashing) {
                    handler.postDelayed(this, flashInterval)
                }
            }
        }

        handler.post(flashRunnable)

        // FIX: Ensure stopFlashing is called with the required (optional) parameter.
        handler.postDelayed({
            stopFlashing(turkey, durationMs)
        }, durationMs)
    }

    /**
     * Stops the flashing and resets the turkey's color to normal.
     * The durationMs is optional but included to satisfy the internal call from startFlashing.
     */
    fun stopFlashing(turkey: ImageView, durationMs: Long = 0) {
        isFlashing = false
        handler.removeCallbacksAndMessages(null)
        setTurkeyTint(turkey, false)
    }

    fun flashRedOnTap(turkey: ImageView) {
        if (!isFlashing) return
        currentFlashColor = redFlash
    }

    private fun setTurkeyTint(turkey: ImageView, applyRedTint: Boolean) {
        val originalDrawable = turkey.drawable ?: return
        val wrappedDrawable = DrawableCompat.wrap(originalDrawable.mutate())

        if (applyRedTint) {
            DrawableCompat.setTint(wrappedDrawable, currentFlashColor)
        } else {
            DrawableCompat.setTintList(wrappedDrawable, null)
        }

        turkey.setImageDrawable(wrappedDrawable)
    }
}