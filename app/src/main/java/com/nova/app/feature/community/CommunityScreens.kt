package com.nova.app.feature.community

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nova.app.core.ui.*
import com.nova.app.ui.theme.*

@Composable
fun CommunityScreen(onNotificationClick: () -> Unit, onMediaClick: (String, Boolean) -> Unit) {
    var selectedTab by remember { mutableIntStateOf(2) } // Default to "For You"
    var searchQuery by remember { mutableStateOf("") }
    val tabs = listOf("Friends", "Following", "For You")
    
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            NovaTopBar(
                title = "Community",
                actions = {
                    Box(contentAlignment = Alignment.TopEnd) {
                        IconButton(onClick = onNotificationClick) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = MaterialTheme.colorScheme.onBackground)
                        }
                        NovaBadge(count = 3, modifier = Modifier.padding(top = 8.dp, end = 8.dp))
                    }
                }
            )
            
            // Search Bar
            NovaTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = "Search by name or ID...",
                leadingIcon = Icons.Default.Search,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Tab Row
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = PurpleMain,
                divider = {},
                edgePadding = 24.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = PurpleMain
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { 
                            Text(
                                title, 
                                color = if (selectedTab == index) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            ) 
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            CommunityContent(selectedTab, onMediaClick = onMediaClick)
        }
    }
}

@Composable
fun CommunityContent(selectedTab: Int, onMediaClick: (String, Boolean) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            StoryRow()
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        items(5) { index ->
            FeedPostItem(
                imageCount = when(index) {
                    0 -> 0 // Will show video
                    1 -> 1
                    2 -> 2
                    3 -> 3
                    else -> 4
                },
                onMediaClick = onMediaClick
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun StoryRow() {
    val users = listOf("You", "Seraphina", "Elena", "Marcus", "Chloe")
    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(users) { user ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .then(
                            if (user == "You") Modifier.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
                            else Modifier.background(Brush.linearGradient(listOf(PurpleMain, PurplePink)))
                        )
                        .padding(2.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize().clip(CircleShape).background(MaterialTheme.colorScheme.background)) {
                        Box(modifier = Modifier.fillMaxSize().padding(2.dp).clip(CircleShape).background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)))
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(user, color = Color.LightGray, fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun FeedPostItem(imageCount: Int, onMediaClick: (String, Boolean) -> Unit) {
    var showComments by remember { mutableStateOf(false) }
    
    NovaCard(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Seraphina", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("2h ago", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), fontSize = 10.sp)
                }
                Spacer(modifier = Modifier.weight(1f))
                var showMenu by remember { mutableStateOf(false) }
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreHoriz, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        DropdownMenuItem(
                            text = { Text("Hide Post", color = MaterialTheme.colorScheme.onBackground) },
                            onClick = { showMenu = false },
                            leadingIcon = { Icon(Icons.Default.VisibilityOff, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)) }
                        )
                        DropdownMenuItem(
                            text = { Text("Report", color = Color.Red) },
                            onClick = { showMenu = false },
                            leadingIcon = { Icon(Icons.Default.Report, contentDescription = null, tint = Color.Red) }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                "Sharing some highlights from my recent trip! The energy was incredible. 📸✨",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 14.sp
            )
            
            Spacer(modifier = Modifier.height(12.dp))

            if (imageCount == 0) {
                NovaVideoView(
                    url = "https://example.com/video.mp4",
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    onClick = { onMediaClick("video_url", true) }
                )
            } else {
                com.nova.app.feature.profile.ImageGrid(imageCount)
                // Note: The profile ImageGrid doesn't have click listeners yet in this mock,
                // but we can simulate by adding a clickable overlay or updating the grid.
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {}) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
                }
                Text("1.2k", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), fontSize = 12.sp)
                
                Spacer(modifier = Modifier.width(8.dp))
                
                IconButton(onClick = { showComments = true }) {
                    Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
                }
                Text("84", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), fontSize = 12.sp)
                
                Spacer(modifier = Modifier.width(8.dp))
                
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                IconButton(onClick = {}) {
                    Icon(Icons.Default.BookmarkBorder, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
                }
            }
        }
    }
    
    if (showComments) {
        CommentBottomSheet(onDismiss = { showComments = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentBottomSheet(onDismiss: () -> Unit) {
    var commentText by remember { mutableStateOf("") }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        dragHandle = { BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight(0.8f)
                .padding(bottom = 16.dp)
                .imePadding()
        ) {
            Text(
                "Comments", 
                style = MaterialTheme.typography.titleLarge, 
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )
            
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(10) {
                    CommentItem()
                }
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NovaTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = "Add a comment...",
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(
                    onClick = { commentText = "" },
                    modifier = Modifier.clip(CircleShape).background(PurpleMain)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Post", tint = MaterialTheme.colorScheme.onBackground)
                }
            }
        }
    }
}

@Composable
fun CommentItem() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text("User Name", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text("This looks amazing! Where is this location? 😍", color = Color.LightGray, fontSize = 12.sp)
            Text("1h ago", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
        }
    }
}
