package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val phone: String,
    val name: String,
    val password: String,
    val isAdmin: Boolean = false,
    val isBanned: Boolean = false,
    val jerseyNumber: Int = 10,
    val position: String = "هجوم", // "حراسة مرمى", "دفاع", "خط وسط", "هجوم"
    val matchesPlayed: Int = 0,
    val matchesWon: Int = 0,
    val mvpCount: Int = 0,
    val isCurrentMvp: Boolean = false,
    val teamAssignment: String = "", // "TEAM_A", "TEAM_B", or ""
    val isWinningTeamMember: Boolean = false,
    val hasPaid: Boolean = false,
    val avatarColorHex: String = "#2E7D32",
    // Level & XP System
    val xp: Int = 150,
    val level: Int = 1,
    // Detailed Statistics
    val goals: Int = 0,
    val assists: Int = 0,
    val yellowCards: Int = 0,
    val redCards: Int = 0,
    val rating: Double = 7.5
) {
    // XP Calculation: 300 XP per level
    fun getLevelTitle(): String {
        return when (level) {
            1 -> "هاوي 🥉"
            2 -> "أساسي 🥈"
            3 -> "نجم الفريق 🥇"
            4 -> "محترف كلاسيكو 💎"
            else -> "أسطورة الملاعب 👑"
        }
    }

    fun getXpCurrentLevelProgress(): Pair<Int, Int> {
        val xpNeededForNext = level * 300
        val currentLevelBase = (level - 1) * 300
        val currentProgress = (xp - currentLevelBase).coerceAtLeast(0)
        return Pair(currentProgress, 300)
    }

    fun getBadges(): List<String> {
        val badges = mutableListOf<String>()
        if (goals >= 10) badges.add("هداف البطولة ⚽🔥")
        if (assists >= 5) badges.add("صانع ألعاب ماهر 👟✨")
        if (matchesPlayed >= 10) badges.add("حاضر دايماً 🏃‍♂️")
        if (mvpCount >= 3) badges.add("صائد التيجان 👑")
        if (matchesWon >= 8) badges.add("تميمة الحظ 🏆")
        if (badges.isEmpty()) badges.add("موهبة صاعدة 🌟")
        return badges
    }
}
