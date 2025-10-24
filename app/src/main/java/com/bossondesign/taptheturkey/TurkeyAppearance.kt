package com.bossondesign.taptheturkey

// NOTE: You will need to create the drawable files (e.g., R.drawable.turkey_level1, R.drawable.turkey_level2)
// For this code to compile, we'll use R.drawable.turkey as a placeholder.

data class TurkeyAppearance(
    val width: Int, // Size in dp
    val height: Int, // Size in dp
    val drawableResId: Int // Image resource to use for the turkey's appearance
) {
    companion object {
        // --- 6 Appearance Variations (maintaining 307:283 aspect ratio) ---

        // New smallest size (90x83)
        val TINY = TurkeyAppearance(90, 83, R.drawable.turkey)

        // Previously smallest (120x111)
        val SMALL = TurkeyAppearance(120, 111, R.drawable.turkey)

        // Medium-Small size (167x154)
        val MEDIUM_SMALL = TurkeyAppearance(167, 154, R.drawable.turkey)

        // Balanced size (214x197)
        val MEDIUM = TurkeyAppearance(214, 197, R.drawable.turkey)

        // Large size (261x241)
        val LARGE = TurkeyAppearance(261, 241, R.drawable.turkey)

        // Original largest size (307x283)
        val HUGE = TurkeyAppearance(307, 283, R.drawable.turkey)

        // List of all six possible appearances for random size selection
        val APPEARANCES = listOf(TINY, SMALL, MEDIUM_SMALL, MEDIUM, LARGE, HUGE)
    }
}