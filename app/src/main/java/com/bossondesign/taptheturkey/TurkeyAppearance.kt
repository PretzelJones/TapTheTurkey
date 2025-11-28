package com.bossondesign.taptheturkey

data class TurkeyAppearance(val name: String, val width: Int, val height: Int) {
    companion object {
        val TINY = TurkeyAppearance("TINY", 190, 190)
        val SMALL = TurkeyAppearance("SMALL", 300, 300)
        val MEDIUM_SMALL = TurkeyAppearance("MEDIUM_SMALL", 410, 410)
        val MEDIUM = TurkeyAppearance("MEDIUM", 525, 525)
        val LARGE = TurkeyAppearance("LARGE", 640, 640)
        val HUGE = TurkeyAppearance("HUGE", 750, 750)

        val APPEARANCES = listOf(TINY, SMALL, MEDIUM_SMALL, MEDIUM, LARGE, HUGE)
    }
}