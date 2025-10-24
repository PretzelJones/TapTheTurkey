package com.bossondesign.taptheturkey

import android.view.View
import android.view.animation.TranslateAnimation

object TurkeyAnimator {
    /**
     * Applies a quick shake animation to a view.
     * @param view The View (e.g., ImageView) to be shaken.
     */
    fun shake(view: View) {
        // Your existing shake logic: translate 20px over 10ms, repeated 20 times.
        val shakeAnimation = TranslateAnimation(0f, 20f, 0f, 20f).apply {
            duration = 10
            repeatCount = 20
        }
        view.startAnimation(shakeAnimation)
    }
}