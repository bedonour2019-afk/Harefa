package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "matches")
data class MatchSession(
    @PrimaryKey
    val id: Long = 1,
    val title: String = "كلاسيكو الأصدقاء الأسبوعي ⚽",
    val stadiumName: String = "ملعب النادي الرياضي - 7 ضد 7",
    val dateText: String = "الجمعة القادمة",
    val timeText: String = "09:00 م - 11:00 م",
    val location: String = "ملعب سبورت - القاهرة",
    val totalPitchCost: Double = 600.0,
    val targetPlayersCount: Int = 12,
    val status: String = "OPEN", // "OPEN", "PLAYING", "FINISHED"
    val winningTeam: String = "", // "TEAM_A", "TEAM_B", or ""
    val mvpUserId: Long? = null,
    val mvpUserName: String? = null,
    val teamAName: String = "الفريق الأبيض (الأبطال)",
    val teamBName: String = "الفريق الأسود (التحدي)",
    val teamAScore: Int = 0,
    val teamBScore: Int = 0
)
