package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.models.AppNotification
import com.example.data.models.ChatMessage
import com.example.data.models.Comment
import com.example.data.models.MatchAttendance
import com.example.data.models.MatchSession
import com.example.data.models.PlayerMatchStat
import com.example.data.models.Post
import com.example.data.models.User
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // --- Users ---
    @Query("SELECT * FROM users ORDER BY name ASC")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserById(id: Long): Flow<User?>

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): User?

    @Query("SELECT * FROM users WHERE phone = :input OR name = :input LIMIT 1")
    suspend fun getUserByPhoneOrName(input: String): User?

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: Long)

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserByIdSync(id: Long): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE users SET isBanned = :isBanned WHERE phone = :phone")
    suspend fun setBanned(phone: String, isBanned: Boolean)

    @Query("UPDATE users SET hasPaid = :hasPaid WHERE id = :userId")
    suspend fun updatePaidStatus(userId: Long, hasPaid: Boolean)

    @Query("UPDATE users SET teamAssignment = :team WHERE id = :userId")
    suspend fun updateTeamAssignment(userId: Long, team: String)

    @Query("UPDATE users SET teamAssignment = '', isWinningTeamMember = 0")
    suspend fun resetTeams()

    @Query("UPDATE users SET isWinningTeamMember = CASE WHEN teamAssignment = :team THEN 1 ELSE 0 END")
    suspend fun setWinningTeamMembers(team: String)

    @Query("UPDATE users SET isCurrentMvp = CASE WHEN id = :userId THEN 1 ELSE 0 END")
    suspend fun setMvpUser(userId: Long)

    @Query("UPDATE users SET mvpCount = mvpCount + 1 WHERE id = :userId")
    suspend fun incrementMvpCount(userId: Long)

    @Query("UPDATE users SET xp = xp + :xpGained, level = 1 + ((xp + :xpGained) / 300) WHERE id = :userId")
    suspend fun addXpToUser(userId: Long, xpGained: Int)

    @Query("UPDATE users SET goals = goals + :goals, assists = assists + :assists, yellowCards = yellowCards + :yellow, redCards = redCards + :red WHERE id = :userId")
    suspend fun addCareerStats(userId: Long, goals: Int, assists: Int, yellow: Int, red: Int)

    @Query("UPDATE users SET rating = :rating WHERE id = :userId")
    suspend fun updatePlayerRating(userId: Long, rating: Double)

    // --- Matches ---
    @Query("SELECT * FROM matches WHERE id = 1 LIMIT 1")
    fun getMatch(): Flow<MatchSession?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateMatch(match: MatchSession)

    // --- Attendances ---
    @Query("SELECT * FROM attendances WHERE matchId = :matchId ORDER BY timestamp ASC")
    fun getAttendances(matchId: Long): Flow<List<MatchAttendance>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAttendance(attendance: MatchAttendance)

    @Query("DELETE FROM attendances WHERE matchId = :matchId AND userId = :userId")
    suspend fun deleteAttendance(matchId: Long, userId: Long)

    // --- Player Match Stats ---
    @Query("SELECT * FROM player_match_stats WHERE matchId = :matchId ORDER BY goals DESC, assists DESC")
    fun getStatsForMatch(matchId: Long): Flow<List<PlayerMatchStat>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateMatchStat(stat: PlayerMatchStat)

    @Query("SELECT * FROM player_match_stats WHERE matchId = :matchId AND userId = :userId LIMIT 1")
    suspend fun getStatForPlayer(matchId: Long, userId: Long): PlayerMatchStat?

    // --- Posts ---
    @Query("SELECT * FROM posts ORDER BY isPinned DESC, createdAt DESC")
    fun getAllPosts(): Flow<List<Post>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: Post): Long

    @Query("UPDATE posts SET likesCount = likesCount + 1 WHERE id = :postId")
    suspend fun likePost(postId: Long)

    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePost(postId: Long)

    // --- Comments ---
    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY createdAt ASC")
    fun getCommentsForPost(postId: Long): Flow<List<Comment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: Comment): Long

    @Query("DELETE FROM comments WHERE id = :commentId")
    suspend fun deleteComment(commentId: Long)

    @Query("UPDATE posts SET commentsCount = commentsCount + 1 WHERE id = :postId")
    suspend fun incrementCommentCount(postId: Long)

    // --- Chat Messages ---
    @Query("SELECT * FROM chat_messages WHERE receiverId IS NULL ORDER BY timestamp ASC")
    fun getPublicMessages(): Flow<List<ChatMessage>>

    @Query("SELECT * FROM chat_messages WHERE (senderId = :userId1 AND receiverId = :userId2) OR (senderId = :userId2 AND receiverId = :userId1) ORDER BY timestamp ASC")
    fun getPrivateConversation(userId1: Long, userId2: Long): Flow<List<ChatMessage>>

    @Query("SELECT * FROM chat_messages WHERE receiverId IS NOT NULL AND (senderId = :userId OR receiverId = :userId) ORDER BY timestamp DESC")
    fun getPrivateChatsForUser(userId: Long): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage): Long

    // --- Notifications ---
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<AppNotification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AppNotification)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllNotificationsRead()

    // --- Cleanup & Purge Dummy Data ---
    @Query("DELETE FROM users WHERE phone IN ('01000000000', '01011111111', '01022222222', '01033333333', '01044444444', '01055555555', '01066666666', '01077777777', '01099999999') OR name LIKE '%(الأدمن)%'")
    suspend fun purgeDummyUsers()

    @Query("DELETE FROM posts WHERE authorPhone IN ('01000000000', '01011111111', '01022222222', '01033333333')")
    suspend fun purgeDummyPosts()

    @Query("DELETE FROM chat_messages WHERE senderName LIKE '%(الأدمن)%' OR senderName IN ('عمر الكردي', 'مصطفى شلبي', 'كريم عبد العزيز')")
    suspend fun purgeDummyMessages()

    @Query("DELETE FROM attendances WHERE userPhone IN ('01000000000', '01011111111', '01022222222', '01033333333', '01044444444', '01055555555', '01066666666', '01077777777')")
    suspend fun purgeDummyAttendances()

    @Query("DELETE FROM notifications WHERE message LIKE '%كابتن أحمد%' OR message LIKE '%عمر الكردي%'")
    suspend fun purgeDummyNotifications()

    @Query("DELETE FROM chat_messages WHERE receiverId IS NULL")
    suspend fun clearPublicChat()

    @Query("DELETE FROM posts")
    suspend fun clearAllPosts()
}
