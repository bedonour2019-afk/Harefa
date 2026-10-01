package com.example.data.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "player_match_stats",
    indices = [Index(value = ["matchId", "userId"], unique = true)]
)
data class PlayerMatchStat(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val matchId: Long = 1,
    val userId: Long,
    val playerName: String,
    val jerseyNumber: Int,
    val team: String = "TEAM_A", // "TEAM_A" or "TEAM_B"
    val goals: Int = 0,
    val assists: Int = 0,
    val yellowCards: Int = 0,
    val redCards: Int = 0,
    val rating: Double = 7.0
)
