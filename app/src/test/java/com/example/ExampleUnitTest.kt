package com.example

import com.example.data.models.User
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testUserLevelAndXpSystem() {
        val beginner = User(
            phone = "01000000000",
            name = "لاعب جديد",
            password = "pass",
            level = 1,
            xp = 100
        )
        assertEquals("هاوي 🥉", beginner.getLevelTitle())
        val (currentProgress, maxProgress) = beginner.getXpCurrentLevelProgress()
        assertEquals(100, currentProgress)
        assertEquals(300, maxProgress)

        val veteran = User(
            phone = "01111111111",
            name = "كابتن أسطوري",
            password = "pass",
            level = 5,
            xp = 1500,
            goals = 15,
            mvpCount = 4
        )
        assertEquals("أسطورة الملاعب 👑", veteran.getLevelTitle())
        assertTrue(veteran.getBadges().contains("هداف البطولة ⚽🔥"))
        assertTrue(veteran.getBadges().contains("صائد التيجان 👑"))
    }
}
