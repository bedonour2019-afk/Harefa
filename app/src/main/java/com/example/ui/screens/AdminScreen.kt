package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.User
import com.example.ui.FootballViewModel
import com.example.ui.components.AdminBadge
import com.example.ui.theme.ChampionGold
import com.example.ui.theme.PitchAccentMint
import com.example.ui.theme.PitchGreenPrimary

@Composable
fun AdminScreen(
    viewModel: FootballViewModel,
    modifier: Modifier = Modifier
) {
    var adminTab by remember { mutableIntStateOf(0) } // 0: Member Credentials & Ban, 1: Match Settings

    val allUsers by viewModel.allUsers.collectAsState()
    val currentMatch by viewModel.currentMatch.collectAsState()

    // Match editor states
    var matchTitle by remember(currentMatch) { mutableStateOf(currentMatch?.title ?: "") }
    var stadiumName by remember(currentMatch) { mutableStateOf(currentMatch?.stadiumName ?: "") }
    var matchDate by remember(currentMatch) { mutableStateOf(currentMatch?.dateText ?: "") }
    var matchTime by remember(currentMatch) { mutableStateOf(currentMatch?.timeText ?: "") }
    var totalCost by remember(currentMatch) { mutableStateOf((currentMatch?.totalPitchCost ?: 600.0).toString()) }
    var location by remember(currentMatch) { mutableStateOf(currentMatch?.location ?: "") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))

            // Admin Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(ChampionGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = Color(0xFF3E2723),
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "لوحة تحكم الأدمن 🛡️",
                                fontWeight = FontWeight.Black,
                                fontSize = 17.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            AdminBadge()
                        }
                        Text(
                            text = "عرض بيانات الأعضاء (الاسم، الرقم، الباسورد) وخاصية الحظر",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            TabRow(
                selectedTabIndex = adminTab,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = adminTab == 0,
                    onClick = { adminTab = 0 },
                    text = { Text("بيانات الأعضاء والحظر 👥", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("admin_tab_members")
                )
                Tab(
                    selected = adminTab == 1,
                    onClick = { adminTab = 1 },
                    text = { Text("إعدادات الماتش 🏟️", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("admin_tab_match_settings")
                )
            }
        }

        if (adminTab == 0) {
            // User Credentials & Ban Management
            item {
                Text(
                    text = "الأعضاء المسجلون (${allUsers.size}):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            items(allUsers) { user ->
                AdminUserRowCard(
                    user = user,
                    onToggleBan = { isBanned ->
                        viewModel.toggleBan(user.phone, isBanned)
                    }
                )
            }
        } else {
            // Match Settings Editor
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
                            .padding(18.dp)
                    ) {
                        Text(
                            text = "تعديل تفاصيل حجز الماتش 🏟️",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = matchTitle,
                            onValueChange = { matchTitle = it },
                            label = { Text("عنوان الماتش") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = stadiumName,
                            onValueChange = { stadiumName = it },
                            label = { Text("اسم الملعب ونوعه (مثلاً: 7 ضد 7)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = matchDate,
                                onValueChange = { matchDate = it },
                                label = { Text("اليوم والتاريخ") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = matchTime,
                                onValueChange = { matchTime = it },
                                label = { Text("التوقيت") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = totalCost,
                            onValueChange = { totalCost = it },
                            label = { Text("تكلفة حجز الملعب الإجمالية (ج.م)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text("العنوان ومكان الملعب") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                val cost = totalCost.toDoubleOrNull() ?: 600.0
                                viewModel.updateMatchDetails(
                                    matchTitle,
                                    stadiumName,
                                    matchDate,
                                    matchTime,
                                    cost,
                                    location
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("save_match_details_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("حفظ ونشر التعديلات للجميع 🚀", fontWeight = FontWeight.Bold)
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
 * Member Credentials & Ban Control Card (Full Name, Phone, Password)
 */
@Composable
fun AdminUserRowCard(
    user: User,
    onToggleBan: (Boolean) -> Unit
) {
    var showPassword by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (user.isBanned) Color(0xFF3E1F1F) else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (user.isBanned) Color.Red
                                else if (user.isAdmin) ChampionGold
                                else MaterialTheme.colorScheme.primary
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${user.jerseyNumber}",
                            fontWeight = FontWeight.Bold,
                            color = if (user.isAdmin) Color(0xFF3E2723) else Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = user.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (user.isBanned) Color(0xFFFFCDD2) else MaterialTheme.colorScheme.onSurface
                            )
                            if (user.isAdmin) {
                                Spacer(modifier = Modifier.width(4.dp))
                                AdminBadge()
                            }
                        }
                        Text(
                            text = "📱 ${user.phone}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Ban / Unban Button
                if (!user.isAdmin) {
                    Button(
                        onClick = { onToggleBan(!user.isBanned) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (user.isBanned) PitchGreenPrimary else Color(0xFFC62828),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = if (user.isBanned) Icons.Default.CheckCircle else Icons.Default.Block,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (user.isBanned) "إلغاء الحظر" else "حظر (بان) 🚫",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(6.dp))

            // Audit row for Password (as explicitly requested by user)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = ChampionGold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "الباسورد: ",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if (showPassword) user.password else "••••••••",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(
                    onClick = { showPassword = !showPassword },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (showPassword) "إخفاء" else "إظهار",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
