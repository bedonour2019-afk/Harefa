package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.FootballViewModel
import com.example.ui.components.AdminBadge
import com.example.ui.components.AdminUnlockDialog
import com.example.ui.components.GoldenCrownBadge
import com.example.ui.theme.ChampionGold
import com.example.ui.theme.ChampionGoldBright
import com.example.ui.theme.ChampionGoldDark
import com.example.ui.theme.PitchAccentMint
import com.example.ui.theme.PitchGreenDark
import com.example.ui.theme.PitchGreenPrimary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    viewModel: FootballViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val isAdminModeUnlocked by viewModel.isAdminModeUnlocked.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showAdminUnlockDialog by remember { mutableStateOf(false) }

    if (currentUser == null) return
    val user = currentUser!!
    val isMvp = user.isCurrentMvp

    // Admin Unlock Dialog
    if (showAdminUnlockDialog) {
        AdminUnlockDialog(
            onDismiss = { showAdminUnlockDialog = false },
            onUnlock = { username, pass ->
                viewModel.unlockAdminMode(username, pass) { success, _ ->
                    if (success) {
                        showAdminUnlockDialog = false
                    }
                }
            }
        )
    }

    // Change Password Modal Dialog
    if (showPasswordDialog) {
        var oldPassword by remember { mutableStateOf("") }
        var newPassword by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        var passwordError by remember { mutableStateOf<String?>(null) }

        Dialog(onDismissRequest = { showPasswordDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🔑", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "تغيير كلمة المرور",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        IconButton(onClick = { showPasswordDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "إلغاء")
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = oldPassword,
                        onValueChange = { oldPassword = it; passwordError = null },
                        label = { Text("كلمة المرور الحالية") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it; passwordError = null },
                        label = { Text("كلمة المرور الجديدة") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it; passwordError = null },
                        label = { Text("تأكيد كلمة المرور الجديدة") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (passwordError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = passwordError ?: "",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (oldPassword.isBlank() || newPassword.isBlank()) {
                                passwordError = "يرجى ملء جميع الحقول المطلوبة!"
                                return@Button
                            }
                            if (newPassword != confirmPassword) {
                                passwordError = "كلمة المرور الجديدة غير متطابقة!"
                                return@Button
                            }
                            viewModel.changePassword(oldPassword, newPassword) { success, msg ->
                                if (success) {
                                    showPasswordDialog = false
                                } else {
                                    passwordError = msg
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("حفظ كلمة المرور الجديدة 🔒", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Edit Profile Modal
    if (showEditDialog) {
        var editName by remember { mutableStateOf(user.name) }
        var editJersey by remember { mutableIntStateOf(user.jerseyNumber) }
        var editPosition by remember { mutableStateOf(user.position) }

        Dialog(onDismissRequest = { showEditDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "تعديل بيانات اللاعب ✏️",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        IconButton(onClick = { showEditDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "إلغاء")
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("الاسم") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = editJersey.toString(),
                        onValueChange = {
                            val num = it.toIntOrNull()
                            if (num != null && num in 1..99) editJersey = num
                        },
                        label = { Text("رقم القميص (1 - 99)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("المركز المفضل في الملعب:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    val positions = listOf("حراسة مرمى", "دفاع", "خط وسط", "هجوم")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        positions.forEach { pos ->
                            OutlinedButton(
                                onClick = { editPosition = pos },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (editPosition == pos) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = pos,
                                    fontSize = 10.sp,
                                    maxLines = 1,
                                    color = if (editPosition == pos) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            viewModel.updateProfile(editName, editJersey, editPosition)
                            showEditDialog = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("حفظ التغييرات ✅", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        // Profile Hero Card - Turns completely GOLDEN if user is the MVP!
        val cardBorder = if (isMvp) {
            androidx.compose.foundation.BorderStroke(
                3.dp,
                Brush.horizontalGradient(listOf(ChampionGold, Color(0xFFFFA000), ChampionGold))
            )
        } else {
            null
        }

        val cardBackground = when {
            isMvp && isDarkTheme -> Color(0xFF281F03)
            isMvp -> Color(0xFFFFF9E6)
            else -> MaterialTheme.colorScheme.surface
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (cardBorder != null) Modifier.border(cardBorder.width, cardBorder.brush, RoundedCornerShape(24.dp))
                    else Modifier
                ),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isMvp) 10.dp else 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // If MVP, show sparkling crown banner!
                if (isMvp) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(ChampionGold, Color(0xFFFFB300))
                                )
                            )
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("👑", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "رجل المباراة الأفضل (البروفايل الذهبي)",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = Color(0xFF3E2723)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("👑", fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Avatar / Jersey Circle
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(
                            if (isMvp) Brush.linearGradient(listOf(ChampionGold, Color(0xFFFFA000)))
                            else Brush.linearGradient(listOf(PitchGreenPrimary, PitchAccentMint))
                        )
                        .border(
                            width = if (isMvp) 3.dp else 2.dp,
                            color = if (isMvp) ChampionGold else Color.White,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${user.jerseyNumber}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isMvp) Color(0xFF3E2723) else Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isMvp) {
                        GoldenCrownBadge(size = 20)
                    }
                    Text(
                        text = user.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isMvp) ChampionGold else MaterialTheme.colorScheme.onSurface
                    )
                    if (user.isAdmin) {
                        Spacer(modifier = Modifier.width(6.dp))
                        AdminBadge()
                    }
                }

                Text(
                    text = "رقم الهاتف: ${user.phone}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isMvp) ChampionGold.copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.primaryContainer
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "مركز اللعب: ${user.position}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isMvp) ChampionGoldDark else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ChampionGold.copy(alpha = 0.2f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "المستوى ${user.level} (${user.getLevelTitle()})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChampionGold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showEditDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("edit_profile_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("تعديل البيانات", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = { showPasswordDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("change_password_button")
                    ) {
                        Text("🔑 تغيير الباسورد", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Level & Experience Points (XP) System Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MilitaryTech,
                            contentDescription = null,
                            tint = ChampionGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "نظام المستوى ونقاط الخبرة (XP) ⚡",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "${user.xp} XP",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = ChampionGold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                val (currentXp, maxXp) = user.getXpCurrentLevelProgress()
                val progressFraction = (currentXp.toFloat() / maxXp.toFloat()).coerceIn(0f, 1f)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "المستوى الحالي: ${user.level} (${user.getLevelTitle()})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$currentXp / $maxXp XP للوصول للمستوى التالي",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(CircleShape),
                    color = ChampionGold,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // XP Gain rules footnote
                Text(
                    text = "💡 كيف تكسب نقاط XP؟\n• تأكيد الحضور: +25 XP  • المشاركة في الماتش: +50 XP\n• الفوز بالمباراة: +100 XP  • رجل المباراة (MVP): +150 XP\n• تسجيل هدف: +30 XP  • صناعة أسيست: +20 XP",
                    fontSize = 11.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Badges & Distinctions (الشارات والتمييزات)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = ChampionGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "شارات وتمييزات اللاعب 🎖️",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    user.getBadges().forEach { badge ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(1.dp, ChampionGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = badge,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Detailed Career Statistics Card (إحصائيات المسيرة الكروية)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "إحصائيات المسيرة الكروية التفصيلية 📊",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatBox(title = "الأهداف", value = "${user.goals}", icon = "⚽")
                    StatBox(title = "الأسيست", value = "${user.assists}", icon = "👟")
                    StatBox(title = "تقييم الأداء", value = String.format("%.1f", user.rating), icon = "⭐", isHighlight = true)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatBox(title = "ماتشات خاضها", value = "${user.matchesPlayed}", icon = "🏟️")
                    StatBox(title = "مرات الفوز", value = "${user.matchesWon}", icon = "🏆")
                    StatBox(title = "كروت 🟨 / 🟥", value = "${user.yellowCards} / ${user.redCards}", icon = "🎴")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Admin Control Gateway in Profile
        OutlinedButton(
            onClick = {
                if (isAdminModeUnlocked) {
                    viewModel.lockAdminMode()
                } else {
                    showAdminUnlockDialog = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = ChampionGold,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isAdminModeUnlocked) "لوحة تحكم الأدمن مفعلة 🛡️ (اضغط للقفل)" else "دخول لوحة تحكم الأدمن (Bruce) 🛡️",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (isAdminModeUnlocked) ChampionGold else MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Logout Button
        Button(
            onClick = { viewModel.logout() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("logout_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFC62828),
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("تسجيل الخروج", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun StatBox(
    title: String,
    value: String,
    icon: String,
    isHighlight: Boolean = false
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isHighlight) ChampionGold.copy(alpha = 0.15f)
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.width(96.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = icon, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Black,
                fontSize = 17.sp,
                color = if (isHighlight) ChampionGold else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
