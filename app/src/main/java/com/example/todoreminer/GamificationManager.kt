package com.example.todoreminer

object GamificationManager {
    private const val XP_PER_TASK = 10
    private const val XP_TO_LEVEL_UP = 100

    fun completeTask(profile: PlayerProfile) {
        profile.experiencePoints += XP_PER_TASK
        if (profile.experiencePoints >= XP_TO_LEVEL_UP) {
            profile.level++
            profile.experiencePoints -= XP_TO_LEVEL_UP
            // In a real app, we would award a badge here
        }
    }
}
