package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.AppNotification
import com.example.data.models.User
import com.example.ui.theme.ChampionGold
import com.example.ui.theme.ChampionGoldBright
import com.example.ui.theme.ChampionGoldDark
import com.example.ui.theme.PitchAccentMint
import com.example.ui.theme.PitchGreenDark
import com.example.ui.theme.PitchGreenPrimary

@Composable
fun GoldenCrownBadge(modifier: Modifier = Modifier, size: Int = 18) {
    Text(
        text = "👑",
        fontSize = size.sp,
        modifier = modifier.padding(horizontal = 2.dp)
    )
}

@Composable
fun WinningTrophyBadge(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFFFFD54F), Color(0xFFFFB300))
                )
            )
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text("🏆", fontSize = 12.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "فريق بطل",
            color = Color(0xFF3E2723),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AdminBadge(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(PitchGreenPrimary, PitchAccentMint)
                )
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Security,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = "أدمن",
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppHeader(
    currentUserName: String,
    isAdminModeUnlocked: Boolean,
    unreadNotificationsCount: Int,
    onOpenAdminPanel: () -> Unit,
    onOpenNotifications: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier.shadow(4.dp),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(PitchGreenPrimary, PitchAccentMint)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsSoccer,
                        contentDescription = "شعار التطبيق",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "حجز كورة",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (isAdminModeUnlocked) {
                            Spacer(modifier = Modifier.width(6.dp))
                            AdminBadge()
                        }
                    }
                    Text(
                        text = "مرحباً، $currentUserName",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        actions = {
            // Admin Panel Action Button
            IconButton(
                onClick = onOpenAdminPanel,
                modifier = Modifier.testTag("admin_panel_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "لوحة تحكم الأدمن",
                    tint = if (isAdminModeUnlocked) ChampionGold else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(
                onClick = onOpenNotifications,
                modifier = Modifier.testTag("notifications_button")
            ) {
                BadgedBox(
                    badge = {
                        if (unreadNotificationsCount > 0) {
                            Badge(
                                containerColor = Color(0xFFE53935),
                                contentColor = Color.White
                            ) {
                                Text(
                                    text = if (unreadNotificationsCount > 9) "+9" else "$unreadNotificationsCount",
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "الإشعارات",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    )
}

/**
 * 2D Interactive Football Tactical Pitch visualizer
 */
@Composable
fun FootballPitchBoard(
    teamAPlayers: List<User>,
    teamBPlayers: List<User>,
    winningTeam: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E5B2A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Pitch grass and markings
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val lineColor = Color.White.copy(alpha = 0.85f)
                val lineWidth = 2.5f

                // Outer boundary
                drawRect(
                    color = lineColor,
                    topLeft = Offset(16f, 16f),
                    size = Size(w - 32f, h - 32f),
                    style = Stroke(width = lineWidth)
                )

                // Halfway line
                drawLine(
                    color = lineColor,
                    start = Offset(w / 2, 16f),
                    end = Offset(w / 2, h - 16f),
                    strokeWidth = lineWidth
                )

                // Center circle
                drawCircle(
                    color = lineColor,
                    radius = 38f,
                    center = Offset(w / 2, h / 2),
                    style = Stroke(width = lineWidth)
                )
                drawCircle(
                    color = lineColor,
                    radius = 3f,
                    center = Offset(w / 2, h / 2)
                )

                // Left penalty area (Team A)
                drawRect(
                    color = lineColor,
                    topLeft = Offset(16f, h / 4),
                    size = Size(50f, h / 2),
                    style = Stroke(width = lineWidth)
                )

                // Right penalty area (Team B)
                drawRect(
                    color = lineColor,
                    topLeft = Offset(w - 66f, h / 4),
                    size = Size(50f, h / 2),
                    style = Stroke(width = lineWidth)
                )
            }

            // Players layout on the pitch
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Team A Side (White / Gold)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val isTeamAWinner = winningTeam == "TEAM_A"
                    Text(
                        text = if (isTeamAWinner) "👑 الفريق الأبيض" else "الفريق الأبيض",
                        color = if (isTeamAWinner) ChampionGold else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    teamAPlayers.take(6).chunked(2).forEach { rowPlayers ->
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            rowPlayers.forEach { player ->
                                PlayerPitchChip(
                                    player = player,
                                    isWinner = isTeamAWinner,
                                    isTeamA = true
                                )
                            }
                        }
                    }
                }

                // Team B Side (Black / Gold)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val isTeamBWinner = winningTeam == "TEAM_B"
                    Text(
                        text = if (isTeamBWinner) "👑 الفريق الأسود" else "الفريق الأسود",
                        color = if (isTeamBWinner) ChampionGold else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    teamBPlayers.take(6).chunked(2).forEach { rowPlayers ->
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            rowPlayers.forEach { player ->
                                PlayerPitchChip(
                                    player = player,
                                    isWinner = isTeamBWinner,
                                    isTeamA = false
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerPitchChip(
    player: User,
    isWinner: Boolean,
    isTeamA: Boolean
) {
    val bg = when {
        isWinner -> Brush.linearGradient(listOf(ChampionGold, Color(0xFFFFA000)))
        isTeamA -> Brush.linearGradient(listOf(Color(0xFFEEEEEE), Color(0xFFBDBDBD)))
        else -> Brush.linearGradient(listOf(Color(0xFF263238), Color(0xFF102027)))
    }
    val textColor = when {
        isWinner -> Color(0xFF3E2723)
        isTeamA -> Color.Black
        else -> Color.White
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(bg)
                .border(
                    width = if (isWinner || player.isCurrentMvp) 2.dp else 1.dp,
                    color = if (isWinner || player.isCurrentMvp) ChampionGold else Color.White,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${player.jerseyNumber}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (player.isCurrentMvp) {
                Text("👑", fontSize = 9.sp)
            }
            Text(
                text = player.name.split(" ").firstOrNull() ?: player.name,
                color = if (isWinner) ChampionGold else Color.White,
                fontSize = 10.sp,
                fontWeight = if (isWinner) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1
            )
        }
    }
}

/**
 * In-app Notification list drawer / popup dialog
 */
@Composable
fun NotificationsDialog(
    notifications: List<AppNotification>,
    onDismiss: () -> Unit,
    onClearAll: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(480.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "الإشعارات الفورية",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (notifications.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "لا توجد إشعارات جديدة حالياً ⚽",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    androidx.compose.foundation.lazy.LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(notifications.size) { index ->
                            val notif = notifications[index]
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val icon = when (notif.type) {
                                        "MVP" -> "👑"
                                        "MATCH" -> "🏟️"
                                        "ATTENDANCE" -> "🏃‍♂️"
                                        "POST" -> "📢"
                                        "CHAT" -> "💬"
                                        else -> "⚽"
                                    }
                                    Text(text = icon, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = notif.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = notif.message,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                androidx.compose.material3.TextButton(
                    onClick = onClearAll,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("تحديد الكل كمقروء")
                }
            }
        }
    }
}

/**
 * Dialog to unlock Admin Control Panel using Bruce / 951753
 */
@Composable
fun AdminUnlockDialog(
    onDismiss: () -> Unit,
    onUnlock: (String, String) -> Unit
) {
    var adminUser by remember { mutableStateOf("") }
    var adminPass by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
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
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = ChampionGold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "لوحة تحكم الأدمن 🛡️",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إلغاء")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "أدخل بيانات الأدمن للتحكم الكامل في الماتش، تقسيم الفرق، إحصائيات اللاعبين وحظر الأرقام.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                androidx.compose.material3.OutlinedTextField(
                    value = adminUser,
                    onValueChange = { adminUser = it; errorMsg = null },
                    label = { Text("اسم المستخدم (Username)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                androidx.compose.material3.OutlinedTextField(
                    value = adminPass,
                    onValueChange = { adminPass = it; errorMsg = null },
                    label = { Text("كلمة المرور (Password)") },
                    singleLine = true,
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMsg != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = errorMsg ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                androidx.compose.material3.Button(
                    onClick = {
                        if (adminUser.isBlank() || adminPass.isBlank()) {
                            errorMsg = "يرجى إدخال اسم المستخدم وكلمة المرور!"
                            return@Button
                        }
                        if (adminUser.trim().equals("Bruce", ignoreCase = true) && adminPass.trim() == "951753") {
                            onUnlock(adminUser.trim(), adminPass.trim())
                        } else {
                            errorMsg = "اسم المستخدم أو كلمة المرور غير صحيحة!"
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("دخول وتفعيل لوحة التحكم 🚀", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
