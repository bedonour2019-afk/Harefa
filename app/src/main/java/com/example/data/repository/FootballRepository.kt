package com.example.data.repository

import com.example.data.local.AppDao
import com.example.data.models.AppNotification
import com.example.data.models.ChatMessage
import com.example.data.models.Comment
import com.example.data.models.MatchAttendance
import com.example.data.models.MatchSession
import com.example.data.models.PlayerMatchStat
import com.example.data.models.Post
import com.example.data.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlin.random.Random

class FootballRepository(private val dao: AppDao) {

    val allUsers: Flow<List<User>> = dao.getAllUsers()
    val currentMatch: Flow<MatchSession?> = dao.getMatch()
    val allPosts: Flow<List<Post>> = dao.getAllPosts()
    val publicMessages: Flow<List<ChatMessage>> = dao.getPublicMessages()
    val allNotifications: Flow<List<AppNotification>> = dao.getAllNotifications()
    val matchStats: Flow<List<PlayerMatchStat>> = dao.getStatsForMatch(1)

    fun getAttendances(matchId: Long): Flow<List<MatchAttendance>> = dao.getAttendances(matchId)

    fun getComments(postId: Long): Flow<List<Comment>> = dao.getCommentsForPost(postId)

    fun getPrivateConversation(userId1: Long, userId2: Long): Flow<List<ChatMessage>> =
        dao.getPrivateConversation(userId1, userId2)

    suspend fun getUserByPhone(phone: String): User? = dao.getUserByPhone(phone)

    suspend fun getUserByPhoneOrName(input: String): User? = dao.getUserByPhoneOrName(input.trim())

    suspend fun getUserById(id: Long): User? = dao.getUserByIdSync(id)

    suspend fun deleteUser(userId: Long) {
        dao.deleteUser(userId)
    }

    suspend fun deleteComment(commentId: Long) {
        dao.deleteComment(commentId)
    }

    suspend fun registerUser(user: User): Long {
        val id = dao.insertUser(user)
        dao.insertNotification(
            AppNotification(
                title = "عضو جديد انضم للفريق! ⚽",
                message = "انضم الكابتن ${user.name} إلى تطبيق حجز كورة.",
                type = "ADMIN"
            )
        )
        return id
    }

    suspend fun updateUser(user: User) {
        dao.updateUser(user)
    }

    suspend fun toggleBanUser(phone: String, isBanned: Boolean) {
        dao.setBanned(phone, isBanned)
        val actionText = if (isBanned) "حظر" else "إلغاء حظر"
        dao.insertNotification(
            AppNotification(
                title = "تحديث حالة مستخدم 🛡️",
                message = "قام الأدمن بـ $actionText الرقم: $phone",
                type = "ADMIN"
            )
        )
    }

    suspend fun togglePaidStatus(userId: Long, hasPaid: Boolean) {
        dao.updatePaidStatus(userId, hasPaid)
    }

    suspend fun setAttendance(
        matchId: Long,
        user: User,
        status: String // "COMING", "NOT_COMING", "MAYBE"
    ) {
        val attendance = MatchAttendance(
            matchId = matchId,
            userId = user.id,
            userName = user.name,
            userPhone = user.phone,
            userPosition = user.position,
            status = status
        )
        dao.upsertAttendance(attendance)

        if (status == "COMING") {
            // Reward 25 XP for confirming attendance!
            dao.addXpToUser(user.id, 25)
        }

        val statusArabic = when (status) {
            "COMING" -> "أكد حضوره للماتش القادم وحصل على +25 XP! 🔥"
            "NOT_COMING" -> "اعتذر عن الحضور ❌"
            else -> "متردد في الحضور ❓"
        }

        dao.insertNotification(
            AppNotification(
                title = "تحديث حضور الماتش 🏃‍♂️",
                message = "${user.name} $statusArabic",
                type = "ATTENDANCE"
            )
        )
    }

    suspend fun recordPlayerStat(stat: PlayerMatchStat) {
        dao.insertOrUpdateMatchStat(stat)
        // Award XP: 30 XP per goal, 20 XP per assist, 50 XP participation
        val xpBonus = (stat.goals * 30) + (stat.assists * 20) + 50
        dao.addXpToUser(stat.userId, xpBonus)
        dao.addCareerStats(stat.userId, stat.goals, stat.assists, stat.yellowCards, stat.redCards)
        dao.updatePlayerRating(stat.userId, stat.rating)

        dao.insertNotification(
            AppNotification(
                title = "إحصائية جديدة في الماتش 📊",
                message = "سجل الكابتن ${stat.playerName}: ${stat.goals} أهداف، ${stat.assists} أسيست (+${xpBonus} XP)",
                type = "MATCH"
            )
        )
    }

    suspend fun createOrUpdateMatch(match: MatchSession) {
        dao.insertOrUpdateMatch(match)
        dao.insertNotification(
            AppNotification(
                title = "تحديث تفاصيل الماتش 🏟️",
                message = "${match.title} - ${match.stadiumName} (${match.dateText})",
                type = "MATCH"
            )
        )
    }

    suspend fun createPost(
        user: User,
        content: String,
        tag: String,
        isPinned: Boolean = false
    ): Long {
        val post = Post(
            userId = user.id,
            authorName = user.name,
            authorPhone = user.phone,
            isAdminAuthor = user.isAdmin,
            isPinned = isPinned || (user.isAdmin && tag.contains("إعلان")),
            content = content,
            tag = tag
        )
        val id = dao.insertPost(post)
        // Reward 10 XP for community engagement
        dao.addXpToUser(user.id, 10)

        val title = if (user.isAdmin) "⭐ منشور رسمي من الأدمن!" else "منشور جديد في المجتمع 💬"
        dao.insertNotification(
            AppNotification(
                title = title,
                message = "${user.name}: ${content.take(50)}...",
                type = "POST"
            )
        )
        return id
    }

    suspend fun likePost(postId: Long) {
        dao.likePost(postId)
    }

    suspend fun deletePost(postId: Long) {
        dao.deletePost(postId)
    }

    suspend fun addComment(postId: Long, user: User, text: String) {
        val comment = Comment(
            postId = postId,
            userId = user.id,
            authorName = user.name,
            isAdminAuthor = user.isAdmin,
            text = text
        )
        dao.insertComment(comment)
        dao.incrementCommentCount(postId)
    }

    suspend fun sendPublicChatMessage(user: User, content: String) {
        val msg = ChatMessage(
            senderId = user.id,
            senderName = user.name,
            isAdminSender = user.isAdmin,
            receiverId = null,
            content = content
        )
        dao.insertMessage(msg)
    }

    suspend fun sendPrivateChatMessage(sender: User, receiver: User, content: String) {
        val msg = ChatMessage(
            senderId = sender.id,
            senderName = sender.name,
            isAdminSender = sender.isAdmin,
            receiverId = receiver.id,
            receiverName = receiver.name,
            content = content
        )
        dao.insertMessage(msg)
        dao.insertNotification(
            AppNotification(
                title = "رسالة خاصة جديدة 📩",
                message = "رسالة من ${sender.name}: ${content.take(40)}",
                type = "CHAT"
            )
        )
    }

    suspend fun assignPlayerTeam(userId: Long, team: String) {
        dao.updateTeamAssignment(userId, team)
    }

    suspend fun autoDivideTeams(attendingUsers: List<User>) {
        dao.resetTeams()
        val shuffled = attendingUsers.shuffled()
        shuffled.forEachIndexed { index, user ->
            val team = if (index % 2 == 0) "TEAM_A" else "TEAM_B"
            dao.updateTeamAssignment(user.id, team)
        }
    }

    suspend fun setWinningTeam(team: String) {
        val match = dao.getMatch().firstOrNull() ?: MatchSession()
        val updated = match.copy(winningTeam = team)
        dao.insertOrUpdateMatch(updated)
        dao.setWinningTeamMembers(team)

        // Award +100 XP to winning team members
        val allUsersList = dao.getAllUsers().firstOrNull() ?: emptyList()
        allUsersList.filter { it.teamAssignment == team }.forEach { winner ->
            dao.addXpToUser(winner.id, 100)
        }

        val teamName = if (team == "TEAM_A") match.teamAName else match.teamBName
        dao.insertNotification(
            AppNotification(
                title = "🏆 إعلان الفريق الفائز!",
                message = "توج $teamName بطلاً للمباراة وأصبح أعضاؤه باللون الذهبي وحصل كل لاعب على +100 XP!",
                type = "MVP"
            )
        )
    }

    suspend fun setManOfTheMatch(user: User) {
        val match = dao.getMatch().firstOrNull() ?: MatchSession()
        val updated = match.copy(mvpUserId = user.id, mvpUserName = user.name)
        dao.insertOrUpdateMatch(updated)
        dao.setMvpUser(user.id)
        dao.incrementMvpCount(user.id)
        // Award +150 XP for MVP
        dao.addXpToUser(user.id, 150)

        dao.insertNotification(
            AppNotification(
                title = "👑 رجل المباراة (أفضل لاعب)!",
                message = "اختار الأدمن الكابتن ${user.name} كأفضل لاعب وتوج بتاج المجد والبروفايل الذهبي (+150 XP)! 👑",
                type = "MVP"
            )
        )
    }

    suspend fun markAllNotificationsRead() {
        dao.markAllNotificationsRead()
    }

    // Clean initial data (No fake accounts at all; Admin is a control panel unlocked by Bruce/951753)
    suspend fun seedInitialDataIfNeeded() {
        // Purge any dummy users, fake posts, fake messages, or placeholder admin accounts
        dao.purgeDummyUsers()
        dao.purgeDummyPosts()
        dao.purgeDummyMessages()
        dao.purgeDummyAttendances()
        dao.purgeDummyNotifications()

        val oldBruce = dao.getUserByPhoneOrName("Bruce")
        if (oldBruce != null) {
            dao.deleteUser(oldBruce.id)
        }
        val oldDefault = dao.getUserByPhone("01000000000")
        if (oldDefault != null) {
            dao.deleteUser(oldDefault.id)
        }

        val existingMatch = dao.getMatch().firstOrNull()
        if (existingMatch == null) {
            val match = MatchSession(
                id = 1,
                title = "مباراة الأصدقاء القادمة ⚽",
                stadiumName = "ملعب كرة القدم",
                dateText = "الجمعة القادمة",
                timeText = "09:00 م",
                location = "الملعب الرئيسي",
                totalPitchCost = 600.0,
                targetPlayersCount = 10,
                status = "OPEN",
                winningTeam = "",
                mvpUserId = null,
                mvpUserName = null,
                teamAName = "الفريق الأبيض",
                teamBName = "الفريق الأسود",
                teamAScore = 0,
                teamBScore = 0
            )
            dao.insertOrUpdateMatch(match)
        }
    }

    suspend fun purgeAllDummyData() {
        dao.purgeDummyUsers()
        dao.purgeDummyPosts()
        dao.purgeDummyMessages()
        dao.purgeDummyAttendances()
        dao.purgeDummyNotifications()
    }

    suspend fun clearPublicChat() {
        dao.clearPublicChat()
    }

    suspend fun clearAllPosts() {
        dao.clearAllPosts()
    }
}
