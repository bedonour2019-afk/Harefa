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

    suspend fun getUserById(id: Long): User? = dao.getUserByIdSync(id)

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

    // Seed realistic initial data if database is empty
    suspend fun seedInitialDataIfNeeded() {
        val existingUsers = dao.getAllUsers().firstOrNull()
        if (existingUsers.isNullOrEmpty()) {
            val admin = User(
                id = 1,
                phone = "01000000000",
                name = "كابتن أحمد (الادمن)",
                password = "admin",
                isAdmin = true,
                jerseyNumber = 7,
                position = "هجوم",
                matchesPlayed = 24,
                matchesWon = 18,
                mvpCount = 8,
                isCurrentMvp = true,
                teamAssignment = "TEAM_A",
                isWinningTeamMember = true,
                hasPaid = true,
                xp = 1450,
                level = 5,
                goals = 19,
                assists = 11,
                yellowCards = 2,
                redCards = 0,
                rating = 9.4
            )
            dao.insertUser(admin)

            val players = listOf(
                User(2, "01111111111", "مصطفى زيزو", "123456", isAdmin = false, jerseyNumber = 10, position = "خط وسط", matchesPlayed = 20, matchesWon = 14, mvpCount = 4, teamAssignment = "TEAM_A", isWinningTeamMember = true, hasPaid = true, xp = 980, level = 4, goals = 12, assists = 14, yellowCards = 1, redCards = 0, rating = 8.9),
                User(3, "01222222222", "عمر السولية", "123456", isAdmin = false, jerseyNumber = 14, position = "خط وسط", matchesPlayed = 19, matchesWon = 12, mvpCount = 3, teamAssignment = "TEAM_B", isWinningTeamMember = false, hasPaid = true, xp = 760, level = 3, goals = 7, assists = 9, yellowCards = 3, redCards = 0, rating = 8.2),
                User(4, "01033333333", "محمود الونش", "123456", isAdmin = false, jerseyNumber = 4, position = "دفاع", matchesPlayed = 22, matchesWon = 15, mvpCount = 5, teamAssignment = "TEAM_A", isWinningTeamMember = true, hasPaid = false, xp = 1120, level = 4, goals = 4, assists = 3, yellowCards = 5, redCards = 1, rating = 8.8),
                User(5, "01144444444", "كريم نيدفيد", "123456", isAdmin = false, jerseyNumber = 8, position = "خط وسط", matchesPlayed = 18, matchesWon = 10, mvpCount = 2, teamAssignment = "TEAM_B", isWinningTeamMember = false, hasPaid = true, xp = 540, level = 2, goals = 5, assists = 6, yellowCards = 2, redCards = 0, rating = 7.7),
                User(6, "01255555555", "محمد الشناوي", "123456", isAdmin = false, jerseyNumber = 1, position = "حراسة مرمى", matchesPlayed = 23, matchesWon = 16, mvpCount = 6, teamAssignment = "TEAM_A", isWinningTeamMember = true, hasPaid = true, xp = 1250, level = 5, goals = 0, assists = 2, yellowCards = 1, redCards = 0, rating = 9.1),
                User(7, "01066666666", "أحمد فتحي", "123456", isAdmin = false, jerseyNumber = 3, position = "دفاع", matchesPlayed = 21, matchesWon = 13, mvpCount = 2, teamAssignment = "TEAM_B", isWinningTeamMember = false, hasPaid = false, xp = 620, level = 3, goals = 3, assists = 5, yellowCards = 4, redCards = 0, rating = 8.0),
                User(8, "01177777777", "حسين الشحات", "123456", isAdmin = false, jerseyNumber = 11, position = "هجوم", matchesPlayed = 17, matchesWon = 11, mvpCount = 3, teamAssignment = "TEAM_B", isWinningTeamMember = false, hasPaid = true, xp = 690, level = 3, goals = 11, assists = 7, yellowCards = 2, redCards = 0, rating = 8.4),
                User(9, "01288888888", "إمام عاشور", "123456", isAdmin = false, jerseyNumber = 22, position = "خط وسط", matchesPlayed = 15, matchesWon = 9, mvpCount = 4, teamAssignment = "TEAM_A", isWinningTeamMember = true, hasPaid = true, xp = 850, level = 3, goals = 9, assists = 8, yellowCards = 3, redCards = 0, rating = 8.7),
                User(10, "01099999999", "أبو جبل", "123456", isAdmin = false, jerseyNumber = 16, position = "حراسة مرمى", matchesPlayed = 16, matchesWon = 8, mvpCount = 1, teamAssignment = "TEAM_B", isWinningTeamMember = false, hasPaid = false, xp = 420, level = 2, goals = 0, assists = 1, yellowCards = 1, redCards = 0, rating = 7.5)
            )
            players.forEach { dao.insertUser(it) }

            val match = MatchSession(
                id = 1,
                title = "كلاسيكو الأصدقاء الأسبوعي ⚽",
                stadiumName = "ملعب تريكة سبورت - نجيل ممتاز",
                dateText = "الجمعة القادمة",
                timeText = "09:00 م - 11:00 م",
                location = "مدينة نصر - خلف النادي الأهلي",
                totalPitchCost = 600.0,
                targetPlayersCount = 10,
                status = "OPEN",
                winningTeam = "TEAM_A",
                mvpUserId = 1,
                mvpUserName = "كابتن أحمد (الادمن)",
                teamAName = "الفريق الأبيض (الأبطال 🏆)",
                teamBName = "الفريق الأسود (المتحدون)",
                teamAScore = 5,
                teamBScore = 3
            )
            dao.insertOrUpdateMatch(match)

            // Seed detailed match stats for the current match
            dao.insertOrUpdateMatchStat(PlayerMatchStat(matchId = 1, userId = admin.id, playerName = admin.name, jerseyNumber = 7, team = "TEAM_A", goals = 3, assists = 1, yellowCards = 0, redCards = 0, rating = 9.8))
            dao.insertOrUpdateMatchStat(PlayerMatchStat(matchId = 1, userId = players[0].id, playerName = players[0].name, jerseyNumber = 10, team = "TEAM_A", goals = 2, assists = 2, yellowCards = 0, redCards = 0, rating = 9.2))
            dao.insertOrUpdateMatchStat(PlayerMatchStat(matchId = 1, userId = players[7].id, playerName = players[7].name, jerseyNumber = 22, team = "TEAM_A", goals = 0, assists = 2, yellowCards = 1, redCards = 0, rating = 8.5))
            dao.insertOrUpdateMatchStat(PlayerMatchStat(matchId = 1, userId = players[4].id, playerName = players[4].name, jerseyNumber = 1, team = "TEAM_A", goals = 0, assists = 0, yellowCards = 0, redCards = 0, rating = 9.0))
            dao.insertOrUpdateMatchStat(PlayerMatchStat(matchId = 1, userId = players[2].id, playerName = players[2].name, jerseyNumber = 4, team = "TEAM_A", goals = 0, assists = 0, yellowCards = 1, redCards = 0, rating = 8.4))

            dao.insertOrUpdateMatchStat(PlayerMatchStat(matchId = 1, userId = players[6].id, playerName = players[6].name, jerseyNumber = 11, team = "TEAM_B", goals = 2, assists = 1, yellowCards = 0, redCards = 0, rating = 8.7))
            dao.insertOrUpdateMatchStat(PlayerMatchStat(matchId = 1, userId = players[1].id, playerName = players[1].name, jerseyNumber = 14, team = "TEAM_B", goals = 1, assists = 1, yellowCards = 1, redCards = 0, rating = 8.1))
            dao.insertOrUpdateMatchStat(PlayerMatchStat(matchId = 1, userId = players[3].id, playerName = players[3].name, jerseyNumber = 8, team = "TEAM_B", goals = 0, assists = 1, yellowCards = 0, redCards = 0, rating = 7.6))

            // Seed attendances
            listOf(admin).plus(players.take(7)).forEach { p ->
                dao.upsertAttendance(
                    MatchAttendance(
                        matchId = 1,
                        userId = p.id,
                        userName = p.name,
                        userPhone = p.phone,
                        userPosition = p.position,
                        status = "COMING"
                    )
                )
            }
            dao.upsertAttendance(
                MatchAttendance(
                    matchId = 1,
                    userId = players[7].id,
                    userName = players[7].name,
                    userPhone = players[7].phone,
                    userPosition = players[7].position,
                    status = "MAYBE"
                )
            )
            dao.upsertAttendance(
                MatchAttendance(
                    matchId = 1,
                    userId = players[8].id,
                    userName = players[8].name,
                    userPhone = players[8].phone,
                    userPosition = players[8].position,
                    status = "NOT_COMING"
                )
            )

            // Seed posts (Admin post highlighted!)
            dao.insertPost(
                Post(
                    userId = admin.id,
                    authorName = admin.name,
                    authorPhone = admin.phone,
                    isAdminAuthor = true,
                    isPinned = true,
                    content = "🚨 تنبيه هام للجميع: حجز الماتش يوم الجمعة الساعة 9 بالدقيقة في ملعب تريكة سبورت. الحضور قبل الماتش بربع ساعة للتسخين وتقسيم التيشرتات، والقطية 60 جنيه لكل لاعب. الرجاء تأكيد الحضور فوراً!",
                    tag = "إعلان رسمي 📢",
                    likesCount = 14,
                    commentsCount = 3
                )
            )
            dao.insertPost(
                Post(
                    userId = players[0].id,
                    authorName = players[0].name,
                    authorPhone = players[0].phone,
                    isAdminAuthor = false,
                    isPinned = false,
                    content = "جاهزين لماتش الجمعة يا رجالة؟ الفريق الأبيض هيكتسح زي العادة وجايب حذاء جديد مخصوص للتهديف! 🔥⚽",
                    tag = "تحدي 🔥",
                    likesCount = 8,
                    commentsCount = 2
                )
            )
            dao.insertPost(
                Post(
                    userId = players[1].id,
                    authorName = players[1].name,
                    authorPhone = players[1].phone,
                    isAdminAuthor = false,
                    isPinned = false,
                    content = "اللي هيضيع انفرادات زي الماتش اللي فات هو اللي هيدفع حق حجز الملعب كلو 😂",
                    tag = "ميمز 😂",
                    likesCount = 12,
                    commentsCount = 1
                )
            )

            // Seed public chat messages
            dao.insertMessage(
                ChatMessage(
                    senderId = admin.id,
                    senderName = admin.name,
                    isAdminSender = true,
                    receiverId = null,
                    content = "مساء الخير يا شباب! مين جاي الجمعة؟"
                )
            )
            dao.insertMessage(
                ChatMessage(
                    senderId = players[0].id,
                    senderName = players[0].name,
                    isAdminSender = false,
                    receiverId = null,
                    content = "أنا أول الحاضرين إن شاء الله، التيشرت الأبيض جاهز ⚪"
                )
            )
            dao.insertMessage(
                ChatMessage(
                    senderId = players[1].id,
                    senderName = players[1].name,
                    isAdminSender = false,
                    receiverId = null,
                    content = "معاكم يا رجالة، الكابتن أحمد هيظبط التقسيمة على الفرازة"
                )
            )

            // Seed initial notifications
            dao.insertNotification(
                AppNotification(
                    title = "ماتش الجمعة متاح للحجز! ⚽",
                    message = "تم فتح باب تأكيد الحضور لكلاسيكو الأسبوع.",
                    type = "MATCH"
                )
            )
            dao.insertNotification(
                AppNotification(
                    title = "👑 تتويج أفضل لاعب",
                    message = "تم اختيار كابتن أحمد رجل المباراة السابقة وتتويجه بالتاج الذهبي.",
                    type = "MVP"
                )
            )
        }
    }
}
