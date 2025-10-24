package com.bossondesign.taptheturkey

data class TurkeyAppearance(val name: String, val width: Int, val height: Int) {
    companion object {
        // SCALED UP PIXEL VALUES (Multiplied by ~2.5 to guarantee visibility)
        // You can adjust these down once they are visible.
        val TINY = TurkeyAppearance("TINY", 300, 300)       // Should be easily visible
        val SMALL = TurkeyAppearance("SMALL", 400, 400)
        val MEDIUM_SMALL = TurkeyAppearance("MEDIUM_SMALL", 500, 500)
        val MEDIUM = TurkeyAppearance("MEDIUM", 600, 600)
        val LARGE = TurkeyAppearance("LARGE", 750, 750)
        val HUGE = TurkeyAppearance("HUGE", 900, 900)       // Should be very large

        val APPEARANCES = listOf(TINY, SMALL, MEDIUM_SMALL, MEDIUM, LARGE, HUGE)
    }
}