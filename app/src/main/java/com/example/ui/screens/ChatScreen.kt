package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.ChatMessage
import com.example.data.models.User
import com.example.ui.FootballViewModel
import com.example.ui.components.AdminBadge
import com.example.ui.components.GoldenCrownBadge
import com.example.ui.theme.ChampionGold
import com.example.ui.theme.PitchAccentMint
import com.example.ui.theme.PitchGreenPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatScreen(
    viewModel: FootballViewModel,
    modifier: Modifier = Modifier
) {
    var selectedChatTab by remember { mutableIntStateOf(0) } // 0: Public Group Chat, 1: Private 1-on-1 Chat

    val publicMessages by viewModel.publicMessages.collectAsState()
    val privateMessages by viewModel.privateChatMessages.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val selectedPrivateUser by viewModel.selectedPrivateUser.collectAsState()

    var textInput by remember { mutableStateOf("") }
    val publicListState = rememberLazyListState()
    val privateListState = rememberLazyListState()

    // Auto-scroll to bottom on new message
    LaunchedEffect(publicMessages.size) {
        if (publicMessages.isNotEmpty()) {
            publicListState.animateScrollToItem(publicMessages.size - 1)
        }
    }
    LaunchedEffect(privateMessages.size) {
        if (privateMessages.isNotEmpty()) {
            privateListState.animateScrollToItem(privateMessages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Tab Selector between Public Chat and Private Chat
        TabRow(
            selectedTabIndex = selectedChatTab,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Tab(
                selected = selectedChatTab == 0,
                onClick = { selectedChatTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Public, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("شات الماتش العام ⚽", fontWeight = FontWeight.Bold)
                    }
                },
                modifier = Modifier.testTag("tab_public_chat")
            )
            Tab(
                selected = selectedChatTab == 1,
                onClick = { selectedChatTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("شات خاص 🔒", fontWeight = FontWeight.Bold)
                    }
                },
                modifier = Modifier.testTag("tab_private_chat")
            )
        }

        if (selectedChatTab == 0) {
            // Public Group Chat
            LazyColumn(
                state = publicListState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(publicMessages) { msg ->
                    val isMe = msg.senderId == currentUser?.id
                    val senderUser = allUsers.find { it.id == msg.senderId }
                    ChatMessageBubble(
                        message = msg,
                        isMe = isMe,
                        senderUser = senderUser
                    )
                }
            }

            // Quick football emoji bar
            QuickEmojiBar(onEmojiSelected = { textInput += it })

            // Public Input Bar
            MessageInputBar(
                text = textInput,
                onTextChanged = { textInput = it },
                onSend = {
                    if (textInput.isNotBlank()) {
                        viewModel.sendPublicMessage(textInput)
                        textInput = ""
                    }
                },
                placeholder = "اكتب رسالة لشات الماتش العام..."
            )
        } else {
            // Private 1-on-1 Chat
            if (selectedPrivateUser == null) {
                // Show list of players to chat with
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "اختر لاعب لبدء محادثة خاصة وسرية 🔒:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    val otherUsers = allUsers.filter { it.id != currentUser?.id }
                    if (otherUsers.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("👥", fontSize = 40.sp)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "لا يوجد لاعبون آخرون مسجلون بعد",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "عندما يسجل أصحابك بأرقامهم الحقيقية ستظهر أسماؤهم هنا لبدء الشات الخاص معهم 🔒",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(otherUsers) { user ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.selectPrivateUser(user)
                                    },
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
                                                .size(42.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (user.isAdmin) ChampionGold
                                                    else MaterialTheme.colorScheme.primary
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${user.jerseyNumber}",
                                                color = if (user.isAdmin) Color(0xFF3E2723) else Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                if (user.isCurrentMvp) {
                                                    GoldenCrownBadge(size = 14)
                                                }
                                                Text(
                                                    text = user.name,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp
                                                )
                                                if (user.isAdmin) {
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    AdminBadge()
                                                }
                                            }
                                            Text(
                                                text = "مركز: ${user.position}",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.primaryContainer)
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "محادثة 💬",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                }
            } else {
                // Active 1-on-1 private chat thread with selected user
                val partner = selectedPrivateUser!!
                Column(modifier = Modifier.weight(1f)) {
                    // Chat header with back button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.selectPrivateUser(null) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع لقائمة الأصدقاء"
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${partner.jerseyNumber}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (partner.isCurrentMvp) {
                                    GoldenCrownBadge(size = 14)
                                }
                                Text(
                                    text = partner.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                            Text(
                                text = "محادثة خاصة ومشفرة 🔒",
                                fontSize = 11.sp,
                                color = PitchAccentMint
                            )
                        }
                    }

                    // Private Messages list
                    LazyColumn(
                        state = privateListState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(privateMessages) { msg ->
                            val isMe = msg.senderId == currentUser?.id
                            ChatMessageBubble(
                                message = msg,
                                isMe = isMe,
                                senderUser = if (isMe) currentUser else partner
                            )
                        }
                    }

                    // Private Input Bar
                    MessageInputBar(
                        text = textInput,
                        onTextChanged = { textInput = it },
                        onSend = {
                            if (textInput.isNotBlank()) {
                                viewModel.sendPrivateMessage(textInput)
                                textInput = ""
                            }
                        },
                        placeholder = "اكتب رسالة خاصة إلى ${partner.name}..."
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ChatMessageBubble(
    message: ChatMessage,
    isMe: Boolean,
    senderUser: User?
) {
    val bubbleBg = when {
        isMe -> PitchGreenPrimary
        message.isAdminSender -> Color(0xFF2C3E50)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = when {
        isMe -> Color.White
        message.isAdminSender -> Color.White
        else -> MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            if (!isMe) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 2.dp, start = 4.dp)
                ) {
                    if (senderUser?.isCurrentMvp == true) {
                        GoldenCrownBadge(size = 12)
                    }
                    Text(
                        text = message.senderName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (senderUser?.isCurrentMvp == true) ChampionGold else MaterialTheme.colorScheme.primary
                    )
                    if (message.isAdminSender) {
                        Spacer(modifier = Modifier.width(4.dp))
                        AdminBadge()
                    }
                }
            }

            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isMe) 16.dp else 4.dp,
                            bottomEnd = if (isMe) 4.dp else 16.dp
                        )
                    )
                    .background(bubbleBg)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = message.content,
                    color = textColor,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun QuickEmojiBar(onEmojiSelected: (String) -> Unit) {
    val emojis = listOf("⚽", "🔥", "🏆", "👑", "👏", "🧤", "👟", "😂")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        emojis.forEach { emoji ->
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onEmojiSelected(emoji) }
                    .padding(4.dp)
            ) {
                Text(text = emoji, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun MessageInputBar(
    text: String,
    onTextChanged: (String) -> Unit,
    onSend: () -> Unit,
    placeholder: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 60.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChanged,
            placeholder = { Text(placeholder, fontSize = 12.sp) },
            modifier = Modifier
                .weight(1f)
                .testTag("chat_input_field"),
            shape = RoundedCornerShape(24.dp),
            singleLine = true
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = onSend,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(PitchGreenPrimary)
                .testTag("send_chat_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "إرسال",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
