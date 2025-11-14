package com.example.todoreminer

data class PlayerProfile(
    var level: Int = 1,
    var experiencePoints: Int = 0,
    val badges: MutableSet<Badge> = mutableSetOf()
)

data class Badge(
    val name: String,
    val description: String,
    val icon: Int
)
