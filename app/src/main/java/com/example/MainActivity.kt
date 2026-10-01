package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.DynamicFeed
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.FootballViewModel
import com.example.ui.components.AppHeader
import com.example.ui.components.NotificationsDialog
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.FeedScreen
import com.example.ui.screens.MatchScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.TeamsScreen
import com.example.ui.theme.ChampionGold
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PitchAccentMint
import com.example.ui.theme.PitchGreenPrimary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: FootballViewModel = viewModel()
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()

            MyApplicationTheme(darkTheme = isDarkTheme) {
                // Ensure RTL layout for authentic Arabic football experience
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    MainAppContent(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: FootballViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val uiToast by viewModel.uiToast.collectAsState()
    val context = LocalContext.current

    var selectedNavIndex by remember { mutableIntStateOf(0) }
    var showNotificationsDialog by remember { mutableStateOf(false) }

    // Toast listener
    LaunchedEffect(uiToast) {
        uiToast?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    if (currentUser == null) {
        AuthScreen(viewModel = viewModel)
    } else {
        val user = currentUser!!
        val unreadCount = notifications.count { !it.isRead }

        // BackHandler to return to home tab if on other tabs
        BackHandler(enabled = selectedNavIndex != 0) {
            selectedNavIndex = 0
        }

        if (showNotificationsDialog) {
            NotificationsDialog(
                notifications = notifications,
                onDismiss = { showNotificationsDialog = false },
                onClearAll = {
                    viewModel.markAllNotificationsRead()
                }
            )
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                AppHeader(
                    currentUserName = user.name,
                    isAdmin = user.isAdmin,
                    isDarkTheme = isDarkTheme,
                    unreadNotificationsCount = unreadCount,
                    onToggleTheme = { viewModel.toggleTheme() },
                    onOpenNotifications = { showNotificationsDialog = true }
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    // Match Tab
                    NavigationBarItem(
                        selected = selectedNavIndex == 0,
                        onClick = { selectedNavIndex = 0 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.SportsSoccer,
                                contentDescription = "الماتش"
                            )
                        },
                        label = { Text("الماتش", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PitchGreenPrimary,
                            indicatorColor = PitchAccentMint.copy(alpha = 0.25f)
                        ),
                        modifier = Modifier.testTag("nav_match")
                    )

                    // Teams Tab
                    NavigationBarItem(
                        selected = selectedNavIndex == 1,
                        onClick = { selectedNavIndex = 1 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Groups,
                                contentDescription = "الفرق"
                            )
                        },
                        label = { Text("الفرق", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ChampionGold,
                            indicatorColor = ChampionGold.copy(alpha = 0.25f)
                        ),
                        modifier = Modifier.testTag("nav_teams")
                    )

                    // Feed Tab
                    NavigationBarItem(
                        selected = selectedNavIndex == 2,
                        onClick = { selectedNavIndex = 2 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.DynamicFeed,
                                contentDescription = "المجتمع"
                            )
                        },
                        label = { Text("المجتمع", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PitchGreenPrimary,
                            indicatorColor = PitchAccentMint.copy(alpha = 0.25f)
                        ),
                        modifier = Modifier.testTag("nav_feed")
                    )

                    // Chat Tab
                    NavigationBarItem(
                        selected = selectedNavIndex == 3,
                        onClick = { selectedNavIndex = 3 },
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Chat,
                                contentDescription = "الشات"
                            )
                        },
                        label = { Text("الشات", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PitchGreenPrimary,
                            indicatorColor = PitchAccentMint.copy(alpha = 0.25f)
                        ),
                        modifier = Modifier.testTag("nav_chat")
                    )

                    // Profile Tab
                    NavigationBarItem(
                        selected = selectedNavIndex == 4,
                        onClick = { selectedNavIndex = 4 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "البروفايل"
                            )
                        },
                        label = { Text("البروفايل", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = if (user.isCurrentMvp) ChampionGold else PitchGreenPrimary,
                            indicatorColor = if (user.isCurrentMvp) ChampionGold.copy(alpha = 0.25f) else PitchAccentMint.copy(alpha = 0.25f)
                        ),
                        modifier = Modifier.testTag("nav_profile")
                    )

                    // Admin Tab (if user has admin role)
                    if (user.isAdmin) {
                        NavigationBarItem(
                            selected = selectedNavIndex == 5,
                            onClick = { selectedNavIndex = 5 },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = "الأدمن"
                                )
                            },
                            label = { Text("الأدمن 🛡️", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ChampionGold,
                                indicatorColor = ChampionGold.copy(alpha = 0.25f)
                            ),
                            modifier = Modifier.testTag("nav_admin")
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (selectedNavIndex) {
                    0 -> MatchScreen(viewModel = viewModel)
                    1 -> TeamsScreen(viewModel = viewModel)
                    2 -> FeedScreen(viewModel = viewModel)
                    3 -> ChatScreen(viewModel = viewModel)
                    4 -> ProfileScreen(viewModel = viewModel)
                    5 -> if (user.isAdmin) AdminScreen(viewModel = viewModel) else MatchScreen(viewModel = viewModel)
                }
            }
        }
    }
}
