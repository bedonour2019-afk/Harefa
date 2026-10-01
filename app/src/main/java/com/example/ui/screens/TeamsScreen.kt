package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.User
import com.example.ui.FootballViewModel
import com.example.ui.components.FootballPitchBoard
import com.example.ui.components.GoldenCrownBadge
import com.example.ui.components.WinningTrophyBadge
import com.example.ui.theme.ChampionGold
import com.example.ui.theme.ChampionGoldBright
import com.example.ui.theme.ChampionGoldDark
import com.example.ui.theme.PitchAccentMint
import com.example.ui.theme.PitchGreenPrimary

@Composable
fun TeamsScreen(
    viewModel: FootballViewModel,
    modifier: Modifier = Modifier
) {
    val allUsers by viewModel.allUsers.collectAsState()
    val currentMatch by viewModel.currentMatch.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val isAdminModeUnlocked by viewModel.isAdminModeUnlocked.collectAsState()

    val isUserAdmin = (currentUser?.isAdmin == true) || isAdminModeUnlocked

    val teamAPlayers = allUsers.filter { it.teamAssignment == "TEAM_A" }
    val teamBPlayers = allUsers.filter { it.teamAssignment == "TEAM_B" }
    val unassignedPlayers = allUsers.filter { it.teamAssignment != "TEAM_A" && it.teamAssignment != "TEAM_B" }

    val winningTeam = currentMatch?.winningTeam ?: ""
    val isTeamAWinner = winningTeam == "TEAM_A"
    val isTeamBWinner = winningTeam == "TEAM_B"

    var showMvpDropdown by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))

            // Screen Header & Auto-split button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "تقسيم الفرق وتحديد البطل ⚽",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "الفريق الفائز وأفضل لاعب يتوجان باللون الذهبي والتاج 👑",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (isUserAdmin) {
                    Button(
                        onClick = { viewModel.autoDivideTeams() },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.testTag("auto_split_teams_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("تقسيم عادل", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 2D Tactical Football Pitch
        item {
            FootballPitchBoard(
                teamAPlayers = teamAPlayers,
                teamBPlayers = teamBPlayers,
                winningTeam = winningTeam
            )
        }

        // Best Player (Man of the Match) Card & Crown showcase
        item {
            val mvpPlayer = allUsers.find { it.id == currentMatch?.mvpUserId || it.isCurrentMvp }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        brush = Brush.horizontalGradient(
                            listOf(ChampionGold, Color(0xFFFFA000), ChampionGold)
                        ),
                        shape = RoundedCornerShape(18.dp)
                    ),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (MaterialTheme.colorScheme.background == Color(0xFF0F1711)) {
                        Color(0xFF231C04)
                    } else {
                        Color(0xFFFFF9E6)
                    }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(ChampionGold, Color(0xFFFF8F00))
                                    )
                                )
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👑", fontSize = 24.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "رجل المباراة (أفضل لاعب)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ChampionGoldDark
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("👑", fontSize = 14.sp)
                            }
                            Text(
                                text = mvpPlayer?.name ?: "لم يتم الاختيار بعد",
                                fontWeight = FontWeight.Black,
                                fontSize = 17.sp,
                                color = if (mvpPlayer != null) ChampionGold else MaterialTheme.colorScheme.onSurface
                            )
                            if (mvpPlayer != null) {
                                Text(
                                    text = "رقم: ${mvpPlayer.jerseyNumber} - مركز: ${mvpPlayer.position} (فاز باللقب ${mvpPlayer.mvpCount} مرات)",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Admin MVP Picker dropdown button
                    if (isUserAdmin) {
                        Box {
                            OutlinedButton(
                                onClick = { showMvpDropdown = true },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("admin_pick_mvp_button")
                            ) {
                                Text("تغيير 👑", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            DropdownMenu(
                                expanded = showMvpDropdown,
                                onDismissRequest = { showMvpDropdown = false }
                            ) {
                                allUsers.forEach { user ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(user.name)
                                                if (user.id == mvpPlayer?.id) {
                                                    Text(" (الحالي)")
                                                }
                                            }
                                        },
                                        onClick = {
                                            viewModel.setManOfTheMatch(user)
                                            showMvpDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Team A Card (الفريق الأبيض)
        item {
            TeamCard(
                teamName = "الفريق الأبيض (الأبطال)",
                isWinner = isTeamAWinner,
                players = teamAPlayers,
                isTeamA = true,
                isAdmin = isUserAdmin,
                onSelectWinner = { viewModel.setWinningTeam("TEAM_A") },
                onSwitchPlayer = { userId ->
                    viewModel.assignPlayerTeam(userId, "TEAM_B")
                }
            )
        }

        // Team B Card (الفريق الأسود)
        item {
            TeamCard(
                teamName = "الفريق الأسود (التحدي)",
                isWinner = isTeamBWinner,
                players = teamBPlayers,
                isTeamA = false,
                isAdmin = isUserAdmin,
                onSelectWinner = { viewModel.setWinningTeam("TEAM_B") },
                onSwitchPlayer = { userId ->
                    viewModel.assignPlayerTeam(userId, "TEAM_A")
                }
            )
        }

        // Unassigned Players Pool (if any)
        if (unassignedPlayers.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "لاعبون بحاجة للتوزيع (${unassignedPlayers.size}):",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        unassignedPlayers.forEach { player ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "⚽ ${player.name} (${player.position})",
                                    fontSize = 13.sp
                                )
                                if (isUserAdmin) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        OutlinedButton(
                                            onClick = { viewModel.assignPlayerTeam(player.id, "TEAM_A") },
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("للأبيض ⚪", fontSize = 11.sp)
                                        }
                                        OutlinedButton(
                                            onClick = { viewModel.assignPlayerTeam(player.id, "TEAM_B") },
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("للأسود ⚫", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
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
 * Team Squad Card - Glowing GOLDEN when marked as Winner!
 */
@Composable
fun TeamCard(
    teamName: String,
    isWinner: Boolean,
    players: List<User>,
    isTeamA: Boolean,
    isAdmin: Boolean,
    onSelectWinner: () -> Unit,
    onSwitchPlayer: (Long) -> Unit
) {
    // When the team wins, apply a rich golden border and gold-accented background
    val cardBorder = if (isWinner) {
        androidx.compose.foundation.BorderStroke(
            2.5.dp,
            Brush.horizontalGradient(listOf(ChampionGold, Color(0xFFFFA000), ChampionGold))
        )
    } else {
        null
    }

    val cardBackground = when {
        isWinner && MaterialTheme.colorScheme.background == Color(0xFF0F1711) -> Color(0xFF261D03)
        isWinner -> Color(0xFFFFFBEA)
        else -> MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (cardBorder != null) Modifier.border(cardBorder.width, cardBorder.brush, RoundedCornerShape(20.dp))
                else Modifier
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isWinner) 8.dp else 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Team Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (isTeamA) Color.White else Color(0xFF212121))
                            .border(1.dp, Color.Gray, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isTeamA) "⚪" else "⚫",
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = teamName,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = if (isWinner) ChampionGold else MaterialTheme.colorScheme.onSurface
                            )
                            if (isWinner) {
                                Spacer(modifier = Modifier.width(6.dp))
                                WinningTrophyBadge()
                            }
                        }
                        Text(
                            text = "${players.size} لاعبين مسجلين",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Admin button to crown this team as the winner!
                if (isAdmin) {
                    Button(
                        onClick = onSelectWinner,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isWinner) ChampionGold else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isWinner) Color(0xFF3E2723) else MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag(if (isTeamA) "select_winner_team_a" else "select_winner_team_b")
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (isWinner) Color(0xFF3E2723) else ChampionGold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isWinner) "🏆 الفريق الفائز" else "تحديد كفائز",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(8.dp))

            // Members List: Names become GOLDEN if this is the winning team!
            if (players.isEmpty()) {
                Text(
                    text = "لا يوجد لاعبين في هذا الفريق حتى الآن",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                players.forEach { player ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Jersey chip
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isWinner) Brush.linearGradient(listOf(ChampionGold, Color(0xFFFFA000)))
                                        else Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, PitchAccentMint))
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${player.jerseyNumber}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isWinner) Color(0xFF3E2723) else Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            // Member Name: GOLDEN if winning team!
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (player.isCurrentMvp) {
                                        GoldenCrownBadge(size = 14)
                                    }
                                    Text(
                                        text = player.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        // The user explicitly stated: "واعضائه اسمهم بللون الذهبي"
                                        color = if (isWinner) ChampionGold else MaterialTheme.colorScheme.onSurface
                                    )
                                    if (isWinner) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("🏆", fontSize = 11.sp)
                                    }
                                }
                                Text(
                                    text = "مركز: ${player.position}",
                                    fontSize = 11.sp,
                                    color = if (isWinner) ChampionGoldDark else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Admin switch team action
                        if (isAdmin) {
                            IconButton(
                                onClick = { onSwitchPlayer(player.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SwapHoriz,
                                    contentDescription = "نقل للفريق الآخر",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
