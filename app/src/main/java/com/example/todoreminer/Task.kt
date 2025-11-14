package com.example.todoreminer

data class Task(
    val id: Long,
    var title: String,
    var description: String,
    var isCompleted: Boolean = false,
    var reminderTimestamp: Long? = null
)
