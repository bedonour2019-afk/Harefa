package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class Comment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val postId: Long,
    val userId: Long,
    val authorName: String,
    val isAdminAuthor: Boolean = false,
    val text: String,
    val createdAt: Long = System.currentTimeMillis()
)
