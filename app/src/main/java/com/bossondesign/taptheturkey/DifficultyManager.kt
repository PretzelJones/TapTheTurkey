package com.bossondesign.taptheturkey

import android.view.View
import android.widget.ImageView

// Data class to hold all game settings for a specific level
data class GameSettings(
    val levelName: String,
    val scoreThreshold: Int, // Score required to reach this level
    val moveDelayMs: Long,
    val flashChancePercent: Int,
    val missPenalty: Int, // The number of points deducted for a miss
    val allowedAppearances: List<TurkeyAppearance>, // Sizes allowed for random selection
    val turkeyBaseResId: Int, // Base turkey image asset for this level
    val backgroundResId: Int // Background image asset for this level
)

object DifficultyManager {

    private var currentLevelIndex = 0
    private const val WINNER_INDEX = 5 // Index for the special WINNER state (one beyond the last normal level)

    // Listener interface for external actions (like graphics updates/pauses)
    interface LevelChangeListener {
        fun onLevelUp(settings: GameSettings)
    }
    private var levelChangeListener: LevelChangeListener? = null

    fun setLevelChangeListener(listener: LevelChangeListener) {
        this.levelChangeListener = listener
    }

    // --- LEVEL CONFIGURATION ---
    private val levelSettings = listOf(
        // Level 0 (I: Rookie) - Score 0+
        GameSettings(
            levelName = "Rookie", scoreThreshold = 0,
            moveDelayMs = 1200L, flashChancePercent = 15, missPenalty = 1,
            //allowedAppearances = TurkeyAppearance.APPEARANCES.filter { it != TurkeyAppearance.TINY }, // Exclude TINY
            allowedAppearances = TurkeyAppearance.APPEARANCES.filter { it != TurkeyAppearance.TINY && it != TurkeyAppearance.SMALL && it != TurkeyAppearance.MEDIUM_SMALL },
            turkeyBaseResId = R.drawable.turkey_1,
            backgroundResId = R.drawable.background // Placeholder
        ),
        // Level 1 (II: Sharpshooter) - Score 30+ (using temporary test score 5)
        GameSettings(
            levelName = "Sharpshooter", scoreThreshold = 5,
            moveDelayMs = 1000L, flashChancePercent = 25, missPenalty = 1,
            //allowedAppearances = TurkeyAppearance.APPEARANCES, // All 6 sizes are active
            allowedAppearances = TurkeyAppearance.APPEARANCES.filter { it != TurkeyAppearance.TINY && it != TurkeyAppearance.SMALL },
            turkeyBaseResId = R.drawable.turkey_2,
            backgroundResId = R.drawable.background // Placeholder
        ),
        // Level 2 (III: Expert) - Score 75+ (using temporary test score 10)
        GameSettings(
            levelName = "Expert", scoreThreshold = 10,
            moveDelayMs = 800L, flashChancePercent = 35, missPenalty = 2,
            allowedAppearances = TurkeyAppearance.APPEARANCES.filter { it != TurkeyAppearance.HUGE }, // Exclude HUGE
            turkeyBaseResId = R.drawable.turkey_3,
            backgroundResId = R.drawable.background // Placeholder
        ),
        // Level 3 (IV: Master) - Score 150+ (using temporary test score 15)
        GameSettings(
            levelName = "Master", scoreThreshold = 15,
            moveDelayMs = 700L, flashChancePercent = 50, missPenalty = 2,
            allowedAppearances = listOf(TurkeyAppearance.TINY, TurkeyAppearance.SMALL, TurkeyAppearance.MEDIUM_SMALL, TurkeyAppearance.MEDIUM),
            turkeyBaseResId = R.drawable.turkey_4,
            backgroundResId = R.drawable.background // Placeholder
        ),
        // Level 4 (V: Legendary) - Score 220+ (using temporary test score 20)
        GameSettings(
            levelName = "Legendary", scoreThreshold = 20,
            moveDelayMs = 600L, flashChancePercent = 60, missPenalty = 3,
            allowedAppearances = listOf(TurkeyAppearance.TINY, TurkeyAppearance.SMALL), // Only the two smallest targets
            turkeyBaseResId = R.drawable.turkey_5,
            backgroundResId = R.drawable.background // Placeholder
        )
    )

    //private val WIN_SCORE = 250 // Score to trigger the final win state
    private val WIN_SCORE = 25
    // Helper function to safely get settings, handles WINNER state
    fun getCurrentSettings(): GameSettings {
        return if (currentLevelIndex >= levelSettings.size) getWinSettings() else levelSettings[currentLevelIndex]
    }

    /**
     * Checks score, updates level, and applies new settings if a milestone is reached.
     * @return true if the level changed, false otherwise.
     */
    fun checkAndUpdateDifficulty(currentScore: Int, turkey: ImageView, gameBoundary: View, topOffset: Int): Boolean {

        // 1. Handle WINNER state first
        if (currentScore >= WIN_SCORE) {
            if (currentLevelIndex < levelSettings.size) { // Only trigger if not already in WINNER state
                levelChangeListener?.onLevelUp(getWinSettings())
                currentLevelIndex = WINNER_INDEX
                return true
            }
            return false
        }

        // 2. Determine the highest level index reached
        var newLevelIndex = 0
        for (i in levelSettings.indices.reversed()) {
            if (currentScore >= levelSettings[i].scoreThreshold) {
                newLevelIndex = i
                break
            }
        }

        // 3. Only trigger onLevelUp if the level index has genuinely INCREASED
        if (newLevelIndex > currentLevelIndex) {
            currentLevelIndex = newLevelIndex
            // Trigger the level up callback in MainActivity for graphics/pause/heart refill
            levelChangeListener?.onLevelUp(levelSettings[currentLevelIndex])
            return true
        }

        // Level was not progressed
        return false
    }

    /**
     * Applies the current level's settings to the game components.
     * This primarily updates TurkeyMover's parameters.
     */
    fun applyCurrentSettings(turkey: ImageView, gameBoundary: View, topOffset: Int) {
        val settings = getCurrentSettings()

        // Update TurkeyMover with new speed, penalty chance, and allowed appearances
        TurkeyMover.updateMovementParams(
            moveDelayMs = settings.moveDelayMs,
            flashChancePercent = settings.flashChancePercent,
            missPenalty = settings.missPenalty,
            allowedAppearances = settings.allowedAppearances,
            turkey = turkey,
            boundaryView = gameBoundary,
            topOffset = topOffset
        )
    }

    /**
     * Resets the game to the initial state (Level 0).
     */
    fun resetGame() {
        currentLevelIndex = 0
    }

    /**
     * Defines the special settings for the Win State.
     */
    private fun getWinSettings(): GameSettings {
        return GameSettings(
            levelName = "WINNER", scoreThreshold = WIN_SCORE,
            moveDelayMs = 0L, flashChancePercent = 0, missPenalty = 0,
            allowedAppearances = emptyList(),
            //turkeyBaseResId = R.drawable.turkey_6, // Cooked Turkey
            turkeyBaseResId = R.drawable.win,
            backgroundResId = R.drawable.background // Placeholder
        )
    }
}