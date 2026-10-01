package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val authorName: String,
    val authorPhone: String,
    val isAdminAuthor: Boolean = false,
    val isPinned: Boolean = false,
    val content: String,
    val tag: String = "عام", // "إعلان رسمي 📢", "تكتيك ⚽", "تحدي 🔥", "ميمز 😂", "عام 💬"
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
