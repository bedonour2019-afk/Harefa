package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val senderId: Long,
    val senderName: String,
    val isAdminSender: Boolean = false,
    val receiverId: Long? = null, // null = Public Match Chat, otherwise Private 1-on-1
    val receiverName: String? = null,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)
