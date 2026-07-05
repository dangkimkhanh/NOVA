package com.nova.app

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nova.app.feature.auth.LoginScreen
import com.nova.app.feature.call.VideoCallScreen
import com.nova.app.feature.call.VoiceCallScreen
import com.nova.app.feature.chat.ChatDetailScreen
import com.nova.app.feature.chat.ChatListScreen
import com.nova.app.feature.community.CommunityScreen
import com.nova.app.feature.discover.DiscoverScreen
import com.nova.app.feature.post.CreatePostScreen
import com.nova.app.feature.onboarding.OnboardingScreen
import com.nova.app.feature.onboarding.SplashScreen
import com.nova.app.feature.profile.AccountScreen
import com.nova.app.feature.profile.ProfileSetupScreen
import com.nova.app.feature.settings.SettingsScreen
import com.nova.app.ui.theme.*

@Composable
fun NovaApp() {
    val systemInDarkTheme = isSystemInDarkTheme()
    var isDarkMode by remember { mutableStateOf(true) } // Default to true as per spec
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
    var selectedTab by remember { mutableStateOf(0) }
    var selectedChatUser by remember { mutableStateOf<String?>(null) }

    NOVATheme(darkTheme = isDarkMode) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Crossfade(targetState = currentScreen, label = "screen_fade") { screen ->
                when (screen) {
                    is Screen.Splash -> SplashScreen { currentScreen = Screen.Onboarding }
                    is Screen.Onboarding -> OnboardingScreen { currentScreen = Screen.Login }
                    is Screen.Login -> LoginScreen { currentScreen = Screen.ProfileSetup }
                    is Screen.ProfileSetup -> ProfileSetupScreen { currentScreen = Screen.Home }
                    is Screen.Home -> {
                        MainNavigation(
                            selectedTab = selectedTab,
                            onTabSelected = { selectedTab = it },
                            onChatClick = { 
                                selectedChatUser = it
                                currentScreen = Screen.ChatDetail
                            },
                            onSettingsClick = { currentScreen = Screen.Settings },
                            onEditProfile = { currentScreen = Screen.ProfileSetup }
                        )
                    }
                    is Screen.ChatDetail -> {
                        ChatDetailScreen(
                            name = selectedChatUser ?: "Chat", 
                            onBack = { currentScreen = Screen.Home },
                            onVoiceCall = { currentScreen = Screen.VoiceCall },
                            onVideoCall = { currentScreen = Screen.VideoCall }
                        )
                    }
                    is Screen.Settings -> {
                        SettingsScreen(
                            isDarkMode = isDarkMode,
                            onDarkModeToggle = { isDarkMode = it },
                            onBack = { currentScreen = Screen.Home }
                        )
                    }
                    is Screen.VoiceCall -> {
                        VoiceCallScreen(name = selectedChatUser ?: "User", onHangUp = { currentScreen = Screen.ChatDetail })
                    }
                    is Screen.VideoCall -> {
                        VideoCallScreen(name = selectedChatUser ?: "User", onHangUp = { currentScreen = Screen.ChatDetail })
                    }
                }
            }
        }
    }
}

@Composable
fun MainNavigation(
    selectedTab: Int, 
    onTabSelected: (Int) -> Unit,
    onChatClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onEditProfile: () -> Unit
) {
    var viewerMedia by remember { mutableStateOf<Pair<String, Boolean>?>(null) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        when (selectedTab) {
            0 -> DiscoverScreen()
            1 -> CommunityScreen(
                onNotificationClick = { /* Show Notifications */ },
                onMediaClick = { url, isVideo -> viewerMedia = url to isVideo }
            )
            2 -> CreatePostScreen(onBack = { onTabSelected(0) })
            3 -> ChatListScreen(onChatClick = onChatClick)
            4 -> AccountScreen(
                onSettingsClick = onSettingsClick, 
                onEditProfile = onEditProfile, 
                onNewPostClick = { onTabSelected(2) }
            )
        }
        
        if (selectedTab != 2 && viewerMedia == null) {
            NovaBottomNav(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        if (viewerMedia != null) {
            com.nova.app.core.ui.MediaViewer(
                mediaUrl = viewerMedia!!.first,
                isVideo = viewerMedia!!.second,
                onDismiss = { viewerMedia = null }
            )
        }
    }
}

@Composable
fun NovaBottomNav(selectedTab: Int, onTabSelected: (Int) -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .navigationBarsPadding()
            .padding(24.dp)
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(40.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(40.dp))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavIcon(Icons.Default.Explore, selectedTab == 0) { onTabSelected(0) }
            NavIcon(Icons.Default.Public, selectedTab == 1) { onTabSelected(1) }
            NavIcon(Icons.Default.Add, selectedTab == 2, isMain = true) { onTabSelected(2) }
            NavIcon(Icons.Default.ChatBubble, selectedTab == 3, badgeCount = 5) { onTabSelected(3) }
            NavIcon(Icons.Default.Person, selectedTab == 4) { onTabSelected(4) }
        }
    }
}

@Composable
fun NavIcon(
    icon: ImageVector, 
    isSelected: Boolean, 
    isMain: Boolean = false, 
    badgeCount: Int = 0,
    onClick: () -> Unit
) {
    val tint = if (isSelected) PurpleMain else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
    val containerSize = if (isMain) 56.dp else 48.dp
    val iconSize = if (isMain) 32.dp else 24.dp
    
    val modifier = if (isMain) {
        Modifier
            .size(containerSize)
            .clip(CircleShape)
            .background(androidx.compose.ui.graphics.Brush.linearGradient(listOf(PurpleMain, PurplePink)))
    } else {
        Modifier.size(containerSize)
    }
    
    IconButton(onClick = onClick, modifier = modifier) {
        Box {
            Icon(
                icon, 
                contentDescription = null, 
                tint = if (isMain) MaterialTheme.colorScheme.onBackground else tint,
                modifier = Modifier.size(iconSize)
            )
            if (badgeCount > 0) {
                com.nova.app.core.ui.NovaBadge(
                    count = badgeCount,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 6.dp, y = (-6).dp)
                )
            }
        }
    }
}

sealed class Screen {
    object Splash : Screen()
    object Onboarding : Screen()
    object Login : Screen()
    object ProfileSetup : Screen()
    object Home : Screen()
    object ChatDetail : Screen()
    object Settings : Screen()
    object VoiceCall : Screen()
    object VideoCall : Screen()
}
