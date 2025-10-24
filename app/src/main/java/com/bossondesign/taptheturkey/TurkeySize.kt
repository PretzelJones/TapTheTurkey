package com.bossondesign.taptheturkey

data class TurkeySize(val width: Int, val height: Int) {
    companion object {
        // Defined Turkey Sizes (maintaining 307:283 aspect ratio)

        // 1. New smallest size
        val TINY = TurkeySize(90, 83)

        // 2. Previously smallest (120/111)
        val SMALL = TurkeySize(120, 111)

        // 3. Medium-Small size
        val MEDIUM_SMALL = TurkeySize(167, 154)

        // 4. Balanced size
        val MEDIUM = TurkeySize(214, 197)

        // 5. Large size
        val LARGE = TurkeySize(261, 241)

        // 6. Original largest size
        val HUGE = TurkeySize(307, 283)

        // List of all six possible sizes for random selection
        val SIZES = listOf(TINY, SMALL, MEDIUM_SMALL, MEDIUM, LARGE, HUGE)
    }
}