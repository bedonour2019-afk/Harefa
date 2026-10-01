package com.example.data.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "attendances",
    indices = [Index(value = ["matchId", "userId"], unique = true)]
)
data class MatchAttendance(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val matchId: Long,
    val userId: Long,
    val userName: String,
    val userPhone: String,
    val userPosition: String,
    val status: String, // "COMING", "NOT_COMING", "MAYBE"
    val timestamp: Long = System.currentTimeMillis()
)
