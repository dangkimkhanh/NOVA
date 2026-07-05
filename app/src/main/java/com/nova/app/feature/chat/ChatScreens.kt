package com.nova.app.feature.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nova.app.core.ui.NovaTextField
import com.nova.app.core.ui.NovaTopBar
import com.nova.app.ui.theme.*

@Composable
fun ChatListScreen(onChatClick: (String) -> Unit) {
    val chats = listOf("Seraphina", "Elena", "Marcus", "Chloe")
    var searchQuery by remember { mutableStateOf("") }
    
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            NovaTopBar(
                title = "Messages",
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Search, contentDescription = "Search friends", tint = MaterialTheme.colorScheme.onBackground)
                    }
                }
            )
            
            NovaTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = "Search by name or ID...",
                leadingIcon = Icons.Default.Search,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(chats) { name ->
                    ChatListItem(name) { onChatClick(name) }
                }
            }
        }
    }
}

@Composable
fun ChatListItem(name: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(name, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
            Text("Hey! How is your day going?", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.weight(1f))
        Text("2m ago", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), fontSize = 10.sp)
    }
}

@Composable
fun ChatDetailScreen(
    name: String, 
    onBack: () -> Unit,
    onVoiceCall: () -> Unit,
    onVideoCall: () -> Unit
) {
    var message by remember { mutableStateOf("") }
    var showMoreActions by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            NovaTopBar(
                title = name,
                subtitle = "Online",
                onBack = onBack,
                actions = {
                    IconButton(onClick = onVoiceCall) {
                        Icon(Icons.Default.Call, contentDescription = "Voice Call", tint = MaterialTheme.colorScheme.onBackground)
                    }
                    IconButton(onClick = onVideoCall) {
                        Icon(Icons.Default.Videocam, contentDescription = "Video Call", tint = MaterialTheme.colorScheme.onBackground)
                    }
                    IconButton(onClick = { showSettings = true }) {
                        Icon(Icons.Default.Info, contentDescription = "Settings", tint = MaterialTheme.colorScheme.onBackground)
                    }
                }
            )
        },
        bottomBar = {
            Column(modifier = Modifier.navigationBarsPadding().imePadding()) {
                if (showMoreActions) {
                    ChatActionMenu()
                }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { showMoreActions = !showMoreActions },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (showMoreActions) PurpleMain else MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
                    ) { 
                        Icon(
                            if (showMoreActions) Icons.Default.Close else Icons.Default.Add, 
                            contentDescription = "More", 
                            tint = MaterialTheme.colorScheme.onBackground 
                        ) 
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    NovaTextField(
                        value = message,
                        onValueChange = { message = it },
                        placeholder = "Type...",
                        modifier = Modifier.weight(1f)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    IconButton(
                        onClick = {},
                        modifier = Modifier.clip(CircleShape).background(PurpleMain)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = MaterialTheme.colorScheme.onBackground)
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp)) {
            Column {
                MessageBubble("Hi! I saw you like photography too.", true, status = "Seen")
                MessageBubble("Yes! I love capturing landscapes. You?", false)
            }
        }
        
        if (showSettings) {
            ChatSettingsDialog(name = name, onDismiss = { showSettings = false })
        }
    }
}

@Composable
fun ChatActionMenu() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ActionIcon(Icons.Default.CameraAlt, "Camera", PurpleMain)
        ActionIcon(Icons.Default.Image, "Gallery", Color(0xFF4CAF50))
        ActionIcon(Icons.Default.Mic, "Voice", Color(0xFFFF9800))
        ActionIcon(Icons.Default.AttachFile, "File", Color(0xFF2196F3))
    }
}

@Composable
fun ActionIcon(icon: ImageVector, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
    }
}

@Composable
fun ChatSettingsDialog(name: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        title = { Text("Chat with $name", color = MaterialTheme.colorScheme.onBackground) },
        text = {
            Column {
                ChatSettingItem(Icons.Default.Edit, "Change Nickname")
                ChatSettingItem(Icons.Default.Palette, "Change Theme")
                ChatSettingItem(Icons.Default.Image, "View Media & Files")
                ChatSettingItem(Icons.Default.Block, "Block User", Color.Red)
                ChatSettingItem(Icons.Default.Report, "Report User", Color.Red)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close", color = PurpleMain) }
        }
    )
}

@Composable
fun ChatSettingItem(icon: ImageVector, label: String, color: Color = MaterialTheme.colorScheme.onBackground) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = color.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, color = color, fontSize = 14.sp)
    }
}

@Composable
fun MessageBubble(text: String, isMe: Boolean, status: String? = null) {
    val alignment = if (isMe) Alignment.End else Alignment.Start
    val bgColor = if (isMe) PurpleMain else MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
    val shape = if (isMe) {
        RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)
    }
    
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalAlignment = alignment) {
        Box(
            modifier = Modifier
                .clip(shape)
                .background(bgColor)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(text, color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp)
        }
        if (isMe && status != null) {
            Text(status, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), fontSize = 10.sp, modifier = Modifier.padding(top = 2.dp))
        }
    }
}
