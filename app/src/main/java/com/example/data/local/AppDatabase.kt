package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.models.AppNotification
import com.example.data.models.ChatMessage
import com.example.data.models.Comment
import com.example.data.models.MatchAttendance
import com.example.data.models.MatchSession
import com.example.data.models.PlayerMatchStat
import com.example.data.models.Post
import com.example.data.models.User

@Database(
    entities = [
        User::class,
        MatchSession::class,
        MatchAttendance::class,
        Post::class,
        Comment::class,
        ChatMessage::class,
        AppNotification::class,
        PlayerMatchStat::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "football_match_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
