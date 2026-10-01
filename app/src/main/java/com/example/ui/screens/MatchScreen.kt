package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.models.MatchAttendance
import com.example.data.models.PlayerMatchStat
import com.example.data.models.User
import com.example.ui.FootballViewModel
import com.example.ui.components.GoldenCrownBadge
import com.example.ui.theme.ChampionGold
import com.example.ui.theme.PitchAccentMint
import com.example.ui.theme.PitchGreenDark
import com.example.ui.theme.PitchGreenPrimary

@Composable
fun MatchScreen(
    viewModel: FootballViewModel,
    modifier: Modifier = Modifier
) {
    val currentMatch by viewModel.currentMatch.collectAsState()
    val attendances by viewModel.attendances.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val matchStats by viewModel.matchStats.collectAsState()
    val isAdminModeUnlocked by viewModel.isAdminModeUnlocked.collectAsState()

    val isUserAdmin = (currentUser?.isAdmin == true) || isAdminModeUnlocked

    val myAttendance = attendances.find { it.userId == currentUser?.id }
    val confirmedCount = attendances.count { it.status == "COMING" }
    val apologizedCount = attendances.count { it.status == "NOT_COMING" }
    val maybeCount = attendances.count { it.status == "MAYBE" }

    // Pitch cost split calculation
    val totalCost = currentMatch?.totalPitchCost ?: 600.0
    val targetCount = currentMatch?.targetPlayersCount ?: 12
    val activeCountForSplit = if (confirmedCount > 0) confirmedCount else targetCount
    val costPerPlayer = totalCost / activeCountForSplit
    val paidCount = allUsers.count { it.hasPaid }

    var showFeeCalculator by remember { mutableStateOf(false) }
    var showRecordStatDialog by remember { mutableStateOf(false) }

    // Dialog for Admin to record detailed match stats (Goals, Assists, Cards, Rating)
    if (showRecordStatDialog) {
        RecordPlayerStatDialog(
            allUsers = allUsers,
            onDismiss = { showRecordStatDialog = false },
            onSaveStat = { stat ->
                viewModel.recordPlayerMatchStat(stat)
                showRecordStatDialog = false
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))

            // Hero Stadium Banner Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.hero_pitch_1790856102146),
                        contentDescription = "ملعب كرة القدم",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Dark Gradient Overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color(0xCC0D2818), Color(0xF00D2818))
                                )
                            )
                    )

                    // Hero Content
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PitchAccentMint)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "الحجز القادم ⚽",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${currentMatch?.dateText} - ${currentMatch?.timeText}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentMatch?.title ?: "كلاسيكو الأصدقاء",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "📍 ${currentMatch?.stadiumName} - ${currentMatch?.location}",
                            color = Color(0xFFC8E6C9),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Attendance Action Card: "اختار انا جاي ولا لا"
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
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
                        Text(
                            text = "حالة حضورك للماتش 🎯 (+25 XP)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // Badge indicating current status
                        val currentStatusText = when (myAttendance?.status) {
                            "COMING" -> "أنت مسجل: جاي 🔥"
                            "NOT_COMING" -> "أنت مسجل: معتذر ❌"
                            "MAYBE" -> "أنت مسجل: متردد ❓"
                            else -> "لم تحدد موقفك بعد"
                        }
                        val currentStatusColor = when (myAttendance?.status) {
                            "COMING" -> PitchAccentMint
                            "NOT_COMING" -> Color(0xFFE57373)
                            "MAYBE" -> Color(0xFFFFB74D)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }

                        Text(
                            text = currentStatusText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = currentStatusColor
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Three Attendance Selection Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // "أنا جاي" Button
                        val isComing = myAttendance?.status == "COMING"
                        Button(
                            onClick = { viewModel.setAttendanceStatus("COMING") },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("btn_coming"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isComing) PitchGreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (isComing) Color.White else MaterialTheme.colorScheme.onSurface
                            ),
                            border = if (isComing) androidx.compose.foundation.BorderStroke(2.dp, PitchAccentMint) else null
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "أنا جاي ⚽",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // "مش جاي" Button
                        val isNotComing = myAttendance?.status == "NOT_COMING"
                        Button(
                            onClick = { viewModel.setAttendanceStatus("NOT_COMING") },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("btn_not_coming"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isNotComing) Color(0xFFC62828) else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (isNotComing) Color.White else MaterialTheme.colorScheme.onSurface
                            ),
                            border = if (isNotComing) androidx.compose.foundation.BorderStroke(2.dp, Color.Red) else null
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "مش جاي ❌",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // "متردد" Button
                        val isMaybe = myAttendance?.status == "MAYBE"
                        Button(
                            onClick = { viewModel.setAttendanceStatus("MAYBE") },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("btn_maybe"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isMaybe) Color(0xFFEF6C00) else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (isMaybe) Color.White else MaterialTheme.colorScheme.onSurface
                            ),
                            border = if (isMaybe) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFFB74D)) else null
                        ) {
                            Icon(
                                imageVector = Icons.Default.HelpOutline,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "متردد ❓",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quota progress indicator
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "اكتمل الحضور: $confirmedCount من $targetCount لاعب",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${((confirmedCount.toFloat() / targetCount.toFloat()) * 100).toInt()}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PitchAccentMint
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { (confirmedCount.toFloat() / targetCount.toFloat()).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = PitchAccentMint,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }
        }

        // Detailed Match Statistics & Top Scorers Section
        item {
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
                                imageVector = Icons.Default.Leaderboard,
                                contentDescription = null,
                                tint = PitchAccentMint,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "إحصائيات الماتش والأداء 📊",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (isUserAdmin) {
                            Button(
                                onClick = { showRecordStatDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.testTag("record_stat_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("تسجيل إحصائية ⚽", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Top Scorers summary
                    val topScorers = matchStats.filter { it.goals > 0 }.sortedByDescending { it.goals }
                    if (topScorers.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "هداف الماتش: ${topScorers.first().playerName} (${topScorers.first().goals} أهداف ⚽)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ChampionGold
                            )
                            val topAssister = matchStats.filter { it.assists > 0 }.maxByOrNull { it.assists }
                            if (topAssister != null) {
                                Text(
                                    text = "الأسيست: ${topAssister.playerName} (${topAssister.assists} 👟)",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Table headers
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("اللاعب", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(2f))
                        Text("أهداف ⚽", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        Text("صناعة 👟", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        Text("كروت 🟨🟥", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1.2f), textAlign = TextAlign.Center)
                        Text("التقييم ⭐", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    }

                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    if (matchStats.isEmpty()) {
                        Text(
                            text = "لم يتم تسجيل إحصائيات للماتش الحالي بعد.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        matchStats.forEach { stat ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(2f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${stat.jerseyNumber}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = stat.playerName.split(" ").firstOrNull() ?: stat.playerName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1
                                    )
                                }
                                Text("${stat.goals}", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, color = if (stat.goals > 0) ChampionGold else MaterialTheme.colorScheme.onSurface)
                                Text("${stat.assists}", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                                Row(
                                    modifier = Modifier.weight(1.2f),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (stat.yellowCards > 0) Text("🟨 ${stat.yellowCards}", fontSize = 10.sp)
                                    if (stat.redCards > 0) Text(" 🟥 ${stat.redCards}", fontSize = 10.sp)
                                    if (stat.yellowCards == 0 && stat.redCards == 0) Text("-", fontSize = 11.sp, color = Color.Gray)
                                }
                                Text(
                                    text = String.format("%.1f", stat.rating),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (stat.rating >= 8.5) ChampionGold else PitchAccentMint,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        // Surprise Feature: "حاسبة قطية الملعب ومصاريف الحجز"
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
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
                                imageVector = Icons.Default.Paid,
                                contentDescription = null,
                                tint = ChampionGold,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "حاسبة قطية الملعب 💰",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        OutlinedButton(
                            onClick = { showFeeCalculator = !showFeeCalculator },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(if (showFeeCalculator) "إخفاء التفاصيل" else "عرض القطية")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("تكلفة الملعب", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${totalCost.toInt()} ج.م", fontWeight = FontWeight.Black, fontSize = 16.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("قطية الفرد", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${costPerPlayer.toInt()} ج.م", fontWeight = FontWeight.Black, fontSize = 16.sp, color = PitchGreenPrimary)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("تم التحصيل", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$paidCount لاعب", fontWeight = FontWeight.Black, fontSize = 16.sp, color = ChampionGold)
                        }
                    }

                    AnimatedVisibility(visible = showFeeCalculator) {
                        Column(modifier = Modifier.padding(top = 14.dp)) {
                            Text(
                                text = "قائمة سداد قطية الملعب للأعضاء الحاضرين:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            val confirmedAttendees = attendances.filter { it.status == "COMING" }
                            confirmedAttendees.forEach { att ->
                                val user = allUsers.find { it.id == att.userId }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "⚽ ${att.userName}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (user?.hasPaid == true) "دفع ✅" else "لم يدفع ⏳",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (user?.hasPaid == true) PitchAccentMint else Color(0xFFE57373)
                                        )
                                        if (isUserAdmin) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Switch(
                                                checked = user?.hasPaid == true,
                                                onCheckedChange = { isPaid ->
                                                    viewModel.togglePaidStatus(att.userId, isPaid)
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Confirmed Attendees Roster Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "كشف أسماء الحاضرين ($confirmedCount)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "معتذر: $apologizedCount",
                        fontSize = 11.sp,
                        color = Color(0xFFE57373),
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "متردد: $maybeCount",
                        fontSize = 11.sp,
                        color = Color(0xFFFFB74D),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Attendees List
        val sortedAttendances = attendances.sortedWith(
            compareBy(
                { when (it.status) { "COMING" -> 0; "MAYBE" -> 1; else -> 2 } },
                { it.timestamp }
            )
        )

        items(sortedAttendances) { att ->
            val user = allUsers.find { it.id == att.userId }
            val statusColor = when (att.status) {
                "COMING" -> PitchGreenPrimary
                "NOT_COMING" -> Color(0xFFD32F2F)
                else -> Color(0xFFF57C00)
            }
            val statusLabel = when (att.status) {
                "COMING" -> "مؤكد الحضور ⚽"
                "NOT_COMING" -> "معتذر ❌"
                else -> "متردد ❓"
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(statusColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${user?.jerseyNumber ?: 10}",
                                fontWeight = FontWeight.Bold,
                                color = statusColor,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (user?.isCurrentMvp == true) {
                                    GoldenCrownBadge(size = 14)
                                }
                                Text(
                                    text = att.userName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (user?.isCurrentMvp == true) ChampionGold else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "مركز: ${att.userPosition} • ${user?.getLevelTitle() ?: ""}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(statusColor.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = statusLabel,
                            color = statusColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

/**
 * Dialog for recording/editing match events (Goals, Assists, Cards, Ratings)
 */
@Composable
fun RecordPlayerStatDialog(
    allUsers: List<User>,
    onDismiss: () -> Unit,
    onSaveStat: (PlayerMatchStat) -> Unit
) {
    var selectedUser by remember { mutableStateOf(allUsers.firstOrNull()) }
    var goals by remember { mutableIntStateOf(1) }
    var assists by remember { mutableIntStateOf(0) }
    var yellowCards by remember { mutableIntStateOf(0) }
    var redCards by remember { mutableIntStateOf(0) }
    var rating by remember { mutableDoubleStateOf(8.0) }
    var showPlayerDropdown by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
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
                        text = "تسجيل إحصائيات الماتش ⚽📊",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إلغاء")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Select Player
                Text("اختر اللاعب:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { showPlayerDropdown = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(selectedUser?.let { "${it.name} (#${it.jerseyNumber})" } ?: "اختر لاعباً")
                    }
                    DropdownMenu(
                        expanded = showPlayerDropdown,
                        onDismissRequest = { showPlayerDropdown = false }
                    ) {
                        allUsers.forEach { u ->
                            DropdownMenuItem(
                                text = { Text("${u.name} (#${u.jerseyNumber})") },
                                onClick = {
                                    selectedUser = u
                                    showPlayerDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Goals & Assists counters
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("الأهداف ⚽ (+30 XP)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedButton(onClick = { if (goals > 0) goals-- }, shape = RoundedCornerShape(8.dp)) { Text("-") }
                            Text("$goals", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                            OutlinedButton(onClick = { goals++ }, shape = RoundedCornerShape(8.dp)) { Text("+") }
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("صناعة 👟 (+20 XP)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedButton(onClick = { if (assists > 0) assists-- }, shape = RoundedCornerShape(8.dp)) { Text("-") }
                            Text("$assists", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                            OutlinedButton(onClick = { assists++ }, shape = RoundedCornerShape(8.dp)) { Text("+") }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Cards counters
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("كارت أصفر 🟨", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedButton(onClick = { if (yellowCards > 0) yellowCards-- }, shape = RoundedCornerShape(8.dp)) { Text("-") }
                            Text("$yellowCards", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                            OutlinedButton(onClick = { yellowCards++ }, shape = RoundedCornerShape(8.dp)) { Text("+") }
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("كارت أحمر 🟥", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedButton(onClick = { if (redCards > 0) redCards-- }, shape = RoundedCornerShape(8.dp)) { Text("-") }
                            Text("$redCards", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                            OutlinedButton(onClick = { redCards++ }, shape = RoundedCornerShape(8.dp)) { Text("+") }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Rating
                Text("تقييم الأداء ⭐ (${String.format("%.1f", rating)} من 10)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                androidx.compose.material3.Slider(
                    value = rating.toFloat(),
                    onValueChange = { rating = it.toDouble() },
                    valueRange = 1f..10f,
                    steps = 17
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val u = selectedUser ?: return@Button
                        val stat = PlayerMatchStat(
                            matchId = 1,
                            userId = u.id,
                            playerName = u.name,
                            jerseyNumber = u.jerseyNumber,
                            team = u.teamAssignment.ifEmpty { "TEAM_A" },
                            goals = goals,
                            assists = assists,
                            yellowCards = yellowCards,
                            redCards = redCards,
                            rating = rating
                        )
                        onSaveStat(stat)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("حفظ الإحصائية وإضافة نقاط XP 🚀", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
