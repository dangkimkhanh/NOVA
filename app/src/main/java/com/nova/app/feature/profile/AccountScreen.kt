package com.nova.app.feature.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nova.app.core.ui.NovaCard
import com.nova.app.core.ui.NovaChip
import com.nova.app.core.ui.NovaGlassBox
import com.nova.app.core.ui.NovaTopBar
import com.nova.app.core.ui.formatCount
import com.nova.app.ui.theme.*

@Composable
fun AccountScreen(onSettingsClick: () -> Unit, onEditProfile: () -> Unit, onNewPostClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            NovaTopBar(
                title = "Profile",
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Surface(
                            color = WarningOrange.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, WarningOrange.copy(alpha = 0.5f))
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = WarningOrange, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(2.dp))
                                Text("V3", color = WarningOrange, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        
                        Spacer(Modifier.width(8.dp))
                        
                        Surface(
                            color = Color(0xFF00E5FF).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f))
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Diamond, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(2.dp))
                                Text(formatCount(17859), color = Color(0xFF00E5FF), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.onBackground)
                    }
                }
            )
            
            ProfileHeader(onEditProfile)
            Spacer(modifier = Modifier.height(16.dp))
            
            BioSection()
            Spacer(modifier = Modifier.height(16.dp))
            
            StatsSection()
            Spacer(modifier = Modifier.height(16.dp))
            
            InterestsSection()
            Spacer(modifier = Modifier.height(24.dp))
            
            UserPostsSection(onNewPostClick)
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun ProfileHeader(onEdit: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                    .border(2.dp, Brush.linearGradient(listOf(PurpleMain, PurplePink)), CircleShape)
            )
            IconButton(
                onClick = onEdit,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(PurpleMain)
                    .border(2.dp, MaterialTheme.colorScheme.background, CircleShape)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(16.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Alex Rivera, 26", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.Male, contentDescription = null, tint = Color(0xFF42A5F5), modifier = Modifier.size(20.dp))
        }
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("ID: nova_alex_98", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                Icons.Default.ContentCopy, 
                contentDescription = "Copy ID", 
                tint = PurpleMain, 
                modifier = Modifier.size(14.dp).clickable { /* Copy logic */ }
            )
        }
    }
}

@Composable
fun BioSection() {
    Text(
        text = "Lover of art, coffee, and urban exploration. Always looking for the next adventure. ✨",
        modifier = Modifier.padding(horizontal = 24.dp),
        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        color = Color.LightGray,
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
fun InterestsSection() {
    @OptIn(ExperimentalLayoutApi::class)
    FlowRow(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf("Photography", "Travel", "Jazz", "Tech").forEach {
            NovaChip(text = it, selected = true)
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
fun StatsSection() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem("Followers", "1.2k")
        StatItem("Following", "450")
        StatItem("Views", "8.9k")
        StatItem("Hotness", "98%")
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
    }
}

@Composable
fun UserPostsSection(onNewPostClick: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("My Posts", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
            TextButton(onClick = onNewPostClick) {
                Text("+ New Post", color = PurpleMain)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        FeedPostItem(imageCount = 3)
        Spacer(modifier = Modifier.height(16.dp))
        FeedPostItem(imageCount = 0)
    }
}

@Composable
fun FeedPostItem(imageCount: Int) {
    NovaCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Text(
                "Exploring the hidden gems of the city today. The vibe is just perfect!",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 14.sp
            )
            
            if (imageCount > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                ImageGrid(imageCount)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("128", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), fontSize = 12.sp)
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("24", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), fontSize = 12.sp)
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Icon(Icons.Default.Share, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
                
                Spacer(modifier = Modifier.weight(1f))
                
                IconButton(onClick = {}) {
                    Icon(Icons.Default.MoreHoriz, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                }
            }
        }
    }
}

@Composable
fun ImageGrid(count: Int) {
    val shape = RoundedCornerShape(16.dp)
    when (count) {
        1 -> Box(modifier = Modifier.fillMaxWidth().height(240.dp).clip(shape).background(Color.DarkGray))
        2 -> Row(modifier = Modifier.fillMaxWidth().height(200.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.weight(1f).fillMaxHeight().clip(shape).background(Color.DarkGray))
            Box(modifier = Modifier.weight(1f).fillMaxHeight().clip(shape).background(Color.DarkGray))
        }
        else -> {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth().height(150.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().clip(shape).background(Color.DarkGray))
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().clip(shape).background(Color.DarkGray))
                }
                Row(modifier = Modifier.fillMaxWidth().height(150.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().clip(shape).background(Color.DarkGray))
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().clip(shape).background(Color.DarkGray), contentAlignment = Alignment.Center) {
                        if (count > 4) {
                            Text("+${count - 4}", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.headlineMedium)
                        }
                    }
                }
            }
        }
    }
}
