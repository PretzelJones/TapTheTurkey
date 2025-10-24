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

    // NEW: Listener interface for external actions (like graphics updates/pauses)
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
            moveDelayMs = 800L, flashChancePercent = 15, missPenalty = 1,
            allowedAppearances = TurkeyAppearance.APPEARANCES.filter { it != TurkeyAppearance.TINY }, // Exclude TINY
            turkeyBaseResId = R.drawable.turkey, backgroundResId = R.drawable.background
        ),
        // Level 1 (II: Sharpshooter) - Score 5+ (using temporary test score)
        GameSettings(
            levelName = "Sharpshooter", scoreThreshold = 2,
            //levelName = "Sharpshooter", scoreThreshold = 30,
            moveDelayMs = 700L, flashChancePercent = 25, missPenalty = 1,
            allowedAppearances = TurkeyAppearance.APPEARANCES, // All 6 sizes are active
            turkeyBaseResId = R.drawable.turkey, backgroundResId = R.drawable.background // Use placeholder
        ),
        // Level 2 (III: Expert) - Score 10+ (using temporary test score)
        GameSettings(
            levelName = "Expert", scoreThreshold = 4,
            //levelName = "Expert", scoreThreshold = 75,
            moveDelayMs = 600L, flashChancePercent = 35, missPenalty = 2,
            allowedAppearances = TurkeyAppearance.APPEARANCES.filter { it != TurkeyAppearance.HUGE }, // Exclude HUGE
            turkeyBaseResId = R.drawable.turkey, backgroundResId = R.drawable.background // Use placeholder
        ),
        // Level 3 (IV: Master) - Score 15+ (using temporary test score)
        GameSettings(
            levelName = "Master", scoreThreshold = 6,
            //levelName = "Master", scoreThreshold = 150,
            moveDelayMs = 500L, flashChancePercent = 50, missPenalty = 3,
            allowedAppearances = listOf(TurkeyAppearance.TINY, TurkeyAppearance.SMALL, TurkeyAppearance.MEDIUM_SMALL, TurkeyAppearance.MEDIUM),
            turkeyBaseResId = R.drawable.turkey, backgroundResId = R.drawable.background // Use placeholder
        ),
        // Level 4 (V: Legendary) - Score 20+ (using temporary test score)
        GameSettings(
            levelName = "Legendary", scoreThreshold = 8,
            //levelName = "Legendary", scoreThreshold = 220,
            moveDelayMs = 400L, flashChancePercent = 60, missPenalty = 4,
            allowedAppearances = listOf(TurkeyAppearance.TINY, TurkeyAppearance.SMALL), // Only the two smallest targets
            turkeyBaseResId = R.drawable.turkey, backgroundResId = R.drawable.background // Use placeholder
        )
    )

    private val WIN_SCORE = 250 // Score to trigger the final win state

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
            // Only trigger if we are not already in the WINNER state
            if (currentLevelIndex < levelSettings.size) {
                levelChangeListener?.onLevelUp(getWinSettings())
                currentLevelIndex = WINNER_INDEX
            }
            return true
        }

        // 2. Determine the highest level index reached
        var newLevelIndex = currentLevelIndex
        for (i in levelSettings.indices.reversed()) {
            if (currentScore >= levelSettings[i].scoreThreshold) {
                newLevelIndex = i
                break
            }
        }

        // 3. CRITICAL FIX: Only trigger onLevelUp if the level index has genuinely INCREASED
        if (newLevelIndex > currentLevelIndex) {
            currentLevelIndex = newLevelIndex
            // Trigger the level up callback in MainActivity for graphics/pause/heart refill
            levelChangeListener?.onLevelUp(levelSettings[currentLevelIndex])
            return true
        }

        // Level was not progressed (score dipped or stayed the same)
        return false
    }

    /**
     * Applies the current level's settings to the game components.
     * Called both on start and after a level-up transition.
     */
    fun applyCurrentSettings(turkey: ImageView, gameBoundary: View, topOffset: Int) {
        val settings = getCurrentSettings()

        // Update TurkeyMover with new speed, penalty chance, and allowed appearances
        TurkeyMover.updateMovementParams(
            moveDelayMs = settings.moveDelayMs,
            flashChancePercent = settings.flashChancePercent,
            missPenalty = settings.missPenalty, // Pass the new miss penalty
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
        // NOTE: Replace R.drawable.turkey_cooked and R.drawable.background_win with your final assets
        return GameSettings(
            levelName = "WINNER", scoreThreshold = WIN_SCORE,
            moveDelayMs = 0L, flashChancePercent = 0, missPenalty = 0,
            allowedAppearances = emptyList(), // No movement/size changes in WIN state
            turkeyBaseResId = R.drawable.turkey, backgroundResId = R.drawable.background
        )
    }
}