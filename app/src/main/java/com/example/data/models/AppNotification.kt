package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class AppNotification(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val message: String,
    val type: String, // "ATTENDANCE", "MATCH", "POST", "CHAT", "ADMIN", "MVP"
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
