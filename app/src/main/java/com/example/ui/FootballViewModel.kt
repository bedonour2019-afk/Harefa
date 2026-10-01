package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.models.AppNotification
import com.example.data.models.ChatMessage
import com.example.data.models.Comment
import com.example.data.models.MatchAttendance
import com.example.data.models.MatchSession
import com.example.data.models.Post
import com.example.data.models.User
import com.example.data.repository.FootballRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

class FootballViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FootballRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = FootballRepository(db.appDao())
        viewModelScope.launch {
            repository.seedInitialDataIfNeeded()
        }
    }

    // App Preferences (Always Dark Stadium Theme)
    val isDarkTheme: StateFlow<Boolean> = MutableStateFlow(true)

    // Admin Control Panel Unlock State (Bruce / 951753)
    private val _isAdminModeUnlocked = MutableStateFlow(false)
    val isAdminModeUnlocked: StateFlow<Boolean> = _isAdminModeUnlocked.asStateFlow()

    fun unlockAdminMode(username: String, pass: String, onResult: (Boolean, String) -> Unit) {
        if (username.trim().equals("Bruce", ignoreCase = true) && pass.trim() == "951753") {
            _isAdminModeUnlocked.value = true
            showToast("تم فتح لوحة تحكم الأدمن بنجاح 🛡️")
            onResult(true, "تم الدخول كأدمن")
        } else {
            onResult(false, "اسم مستخدم الأدمن أو الباسورد غير صحيح!")
        }
    }

    fun loginAsAdminGateway(username: String, pass: String, onResult: (Boolean, String) -> Unit) {
        if (username.trim().equals("Bruce", ignoreCase = true) && pass.trim() == "951753") {
            _isAdminModeUnlocked.value = true
            val adminUser = User(
                id = 999999,
                phone = "0000000000",
                name = "Bruce (الأدمن)",
                password = pass.trim(),
                isAdmin = true,
                position = "مدرب الفريق",
                jerseyNumber = 1,
                matchesPlayed = 0,
                matchesWon = 0,
                mvpCount = 0
            )
            _currentUser.value = adminUser
            showToast("تم فتح لوحة تحكم الأدمن (Bruce) بنجاح 🛡️")
            onResult(true, "تم الدخول بنجاح")
        } else {
            onResult(false, "اسم مستخدم الأدمن أو الباسورد غير صحيح!")
        }
    }

    fun purgeAllDummyData() {
        viewModelScope.launch {
            repository.purgeAllDummyData()
            showToast("تم تصفير ومسح كافة البيانات التجريبية والوهمية 🧹")
        }
    }

    fun clearPublicChat() {
        viewModelScope.launch {
            repository.clearPublicChat()
            showToast("تم تفريغ رسائل الشات العام 🧹")
        }
    }

    fun clearAllPosts() {
        viewModelScope.launch {
            repository.clearAllPosts()
            showToast("تم مسح كافة المنشورات 🧹")
        }
    }

    fun lockAdminMode() {
        _isAdminModeUnlocked.value = false
        showToast("تم قفل لوحة تحكم الأدمن 🔒")
    }

    // Auth State
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // SMS OTP Flow
    private val _pendingSmsCode = MutableStateFlow<String?>(null)
    val pendingSmsCode: StateFlow<String?> = _pendingSmsCode.asStateFlow()

    private val _pendingRegistration = MutableStateFlow<Triple<String, String, String>?>(null) // Name, Phone, Password
    val pendingRegistration = _pendingRegistration.asStateFlow()

    private val _pendingPasswordReset = MutableStateFlow<Pair<String, String>?>(null) // Phone, NewPassword
    val pendingPasswordReset = _pendingPasswordReset.asStateFlow()

    val currentMatch: StateFlow<MatchSession?> = repository.currentMatch
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allUsers: StateFlow<List<User>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPosts: StateFlow<List<Post>> = repository.allPosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val publicMessages: StateFlow<List<ChatMessage>> = repository.publicMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<AppNotification>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val attendances: StateFlow<List<MatchAttendance>> = repository.getAttendances(1)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val matchStats: StateFlow<List<com.example.data.models.PlayerMatchStat>> = repository.matchStats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Private Chat
    private val _selectedPrivateUser = MutableStateFlow<User?>(null)
    val selectedPrivateUser: StateFlow<User?> = _selectedPrivateUser.asStateFlow()

    private val _privateChatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val privateChatMessages: StateFlow<List<ChatMessage>> = _privateChatMessages.asStateFlow()

    // UI Feedback Banner
    private val _uiToast = MutableStateFlow<String?>(null)
    val uiToast: StateFlow<String?> = _uiToast.asStateFlow()

    fun clearToast() {
        _uiToast.value = null
    }

    fun showToast(message: String) {
        _uiToast.value = message
    }

    // Auth Operations
    fun login(phone: String, pass: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val user = repository.getUserByPhone(phone.trim())
            if (user == null) {
                onResult(false, "رقم الهاتف غير مسجل في التطبيق!")
                return@launch
            }
            if (user.isBanned) {
                onResult(false, "تم حظر هذا الحساب من قبل الأدمن!")
                return@launch
            }
            if (user.password != pass.trim()) {
                onResult(false, "كلمة المرور غير صحيحة!")
                return@launch
            }
            _currentUser.value = user
            showToast("أهلاً بك يا كابتن ${user.name} ⚽")
            onResult(true, "تم تسجيل الدخول بنجاح")
        }
    }

    fun initiateRegister(name: String, phone: String, pass: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            if (name.isBlank() || phone.isBlank() || pass.isBlank()) {
                onResult(false, "يرجى ملء جميع الحقول!")
                return@launch
            }
            val existing = repository.getUserByPhone(phone.trim())
            if (existing != null) {
                onResult(false, "رقم الهاتف مسجل بالفعل مسبقاً!")
                return@launch
            }

            // Generate 6 digit SMS verification code
            val code = (100000 + Random.nextInt(900000)).toString()
            _pendingSmsCode.value = code
            _pendingRegistration.value = Triple(name.trim(), phone.trim(), pass.trim())
            onResult(true, "تم إرسال كود التحقق عبر SMS")
        }
    }

    fun completeRegistrationAfterOtp() {
        val reg = _pendingRegistration.value ?: return
        viewModelScope.launch {
            val newUser = User(
                phone = reg.second,
                name = reg.first,
                password = reg.third,
                isAdmin = false,
                jerseyNumber = Random.nextInt(1, 99),
                position = "خط وسط",
                matchesPlayed = 0,
                matchesWon = 0,
                mvpCount = 0
            )
            val id = repository.registerUser(newUser)
            val created = newUser.copy(id = id)
            _currentUser.value = created
            _pendingRegistration.value = null
            _pendingSmsCode.value = null
            showToast("تم التحقق بنجاح! مرحباً بك يا ${created.name} ⚽")
        }
    }

    fun resendSmsCode() {
        val newCode = (100000 + Random.nextInt(900000)).toString()
        _pendingSmsCode.value = newCode
        showToast("تم إرسال كود جديد: $newCode")
    }

    fun cancelSmsVerification() {
        _pendingSmsCode.value = null
        _pendingRegistration.value = null
        _pendingPasswordReset.value = null
    }

    fun changePassword(oldPass: String, newPass: String, onResult: (Boolean, String) -> Unit) {
        val user = _currentUser.value ?: return
        if (newPass.trim().length < 4) {
            onResult(false, "كلمة المرور يجب أن لا تقل عن 4 خانات!")
            return
        }
        if (user.password != oldPass.trim()) {
            onResult(false, "كلمة المرور الحالية غير صحيحة!")
            return
        }
        viewModelScope.launch {
            val updated = user.copy(password = newPass.trim())
            repository.updateUser(updated)
            _currentUser.value = updated
            showToast("تم تحديث كلمة المرور بنجاح 🔒")
            onResult(true, "تم تحديث كلمة المرور بنجاح")
        }
    }

    fun initiatePasswordReset(phone: String, newPass: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val user = repository.getUserByPhone(phone.trim())
            if (user == null) {
                onResult(false, "رقم الهاتف غير مسجل في التطبيق!")
                return@launch
            }
            if (newPass.trim().length < 4) {
                onResult(false, "كلمة المرور الجديدة يجب أن لا تقل عن 4 خانات!")
                return@launch
            }
            val code = (100000 + Random.nextInt(900000)).toString()
            _pendingSmsCode.value = code
            _pendingPasswordReset.value = Pair(phone.trim(), newPass.trim())
            onResult(true, "تم إرسال كود التحقق لإعادة تعيين كلمة المرور عبر SMS")
        }
    }

    fun completePasswordResetAfterOtp() {
        val reset = _pendingPasswordReset.value ?: return
        viewModelScope.launch {
            val user = repository.getUserByPhone(reset.first)
            if (user != null) {
                val updated = user.copy(password = reset.second)
                repository.updateUser(updated)
                _pendingPasswordReset.value = null
                _pendingSmsCode.value = null
                showToast("تم تغيير كلمة المرور بنجاح! يمكنك الدخول الآن 🔑")
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        showToast("تم تسجيل الخروج")
    }

    // Attendance
    fun setAttendanceStatus(status: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.setAttendance(1, user, status)
            val text = when (status) {
                "COMING" -> "تم تأكيد حضورك للماتش! ⚽🔥"
                "NOT_COMING" -> "تم تسجيل اعتذارك عن الحضور."
                else -> "تم تسجيل حالتك (متردد)."
            }
            showToast(text)
        }
    }

    // Posts & Feed
    fun publishPost(content: String, tag: String, isPinned: Boolean = false) {
        val user = _currentUser.value ?: return
        if (content.isBlank()) return
        viewModelScope.launch {
            repository.createPost(user, content.trim(), tag, isPinned)
            showToast("تم نشر البوست بنجاح 📣")
        }
    }

    fun likePost(postId: Long) {
        viewModelScope.launch {
            repository.likePost(postId)
        }
    }

    fun deletePost(postId: Long) {
        viewModelScope.launch {
            repository.deletePost(postId)
            showToast("تم حذف المنشور بنجاح 🗑️")
        }
    }

    fun deleteComment(commentId: Long) {
        viewModelScope.launch {
            repository.deleteComment(commentId)
            showToast("تم حذف التعليق 🗑️")
        }
    }

    fun deleteUserAccount(userId: Long) {
        viewModelScope.launch {
            repository.deleteUser(userId)
            showToast("تم حذف حساب اللاعب نهائياً 🗑️")
        }
    }

    fun addComment(postId: Long, text: String) {
        val user = _currentUser.value ?: return
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.addComment(postId, user, text.trim())
            showToast("تم إضافة تعليقك ✅")
        }
    }

    fun getCommentsForPost(postId: Long) = repository.getComments(postId)

    // Chat
    fun sendPublicMessage(content: String) {
        val user = _currentUser.value ?: return
        if (content.isBlank()) return
        viewModelScope.launch {
            repository.sendPublicChatMessage(user, content.trim())
        }
    }

    fun selectPrivateUser(user: User?) {
        _selectedPrivateUser.value = user
        if (user != null && _currentUser.value != null) {
            viewModelScope.launch {
                repository.getPrivateConversation(_currentUser.value!!.id, user.id)
                    .collect { msgs ->
                        _privateChatMessages.value = msgs
                    }
            }
        }
    }

    fun sendPrivateMessage(content: String) {
        val sender = _currentUser.value ?: return
        val receiver = _selectedPrivateUser.value ?: return
        if (content.isBlank()) return
        viewModelScope.launch {
            repository.sendPrivateChatMessage(sender, receiver, content.trim())
        }
    }

    // Team Division & Admin controls
    fun autoDivideTeams() {
        viewModelScope.launch {
            val confirmedUserIds = attendances.value
                .filter { it.status == "COMING" }
                .map { it.userId }
            val attendingUsers = allUsers.value.filter { it.id in confirmedUserIds }
            val usersToSplit = if (attendingUsers.isNotEmpty()) attendingUsers else allUsers.value.take(12)
            repository.autoDivideTeams(usersToSplit)
            showToast("تم تقسيم الفرق تلقائياً بشكل عادل ⚖️⚽")
        }
    }

    fun assignPlayerTeam(userId: Long, team: String) {
        viewModelScope.launch {
            repository.assignPlayerTeam(userId, team)
        }
    }

    fun setWinningTeam(team: String) {
        viewModelScope.launch {
            repository.setWinningTeam(team)
            val name = if (team == "TEAM_A") "الفريق الأبيض" else "الفريق الأسود"
            showToast("🏆 تم تتويج $name كفريق فائز باللون الذهبي!")
        }
    }

    fun setManOfTheMatch(user: User) {
        viewModelScope.launch {
            repository.setManOfTheMatch(user)
            showToast("👑 تم اختيار الكابتن ${user.name} كرجل المباراة!")
        }
    }

    // Admin Member Management
    fun toggleBan(phone: String, isBanned: Boolean) {
        viewModelScope.launch {
            repository.toggleBanUser(phone, isBanned)
            val msg = if (isBanned) "تم حظر الرقم $phone" else "تم إلغاء حظر الرقم $phone"
            showToast(msg)
        }
    }

    fun togglePaidStatus(userId: Long, hasPaid: Boolean) {
        viewModelScope.launch {
            repository.togglePaidStatus(userId, hasPaid)
        }
    }

    fun updateMatchDetails(
        title: String,
        stadium: String,
        date: String,
        time: String,
        cost: Double,
        location: String
    ) {
        viewModelScope.launch {
            val current = currentMatch.value ?: MatchSession()
            val updated = current.copy(
                title = title,
                stadiumName = stadium,
                dateText = date,
                timeText = time,
                totalPitchCost = cost,
                location = location
            )
            repository.createOrUpdateMatch(updated)
            showToast("تم تحديث بيانات الماتش وحفظها بنجاح 🏟️")
        }
    }

    fun updateProfile(name: String, jersey: Int, position: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val updated = user.copy(
                name = name.trim(),
                jerseyNumber = jersey,
                position = position
            )
            repository.updateUser(updated)
            _currentUser.value = updated
            showToast("تم حفظ تعديلات البروفايل ✅")
        }
    }

    fun recordPlayerMatchStat(stat: com.example.data.models.PlayerMatchStat) {
        viewModelScope.launch {
            repository.recordPlayerStat(stat)
            showToast("تم تسجيل إحصائيات ${stat.playerName} وحساب نقاط الخبرة XP! ⚽📊")
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead()
        }
    }
}
