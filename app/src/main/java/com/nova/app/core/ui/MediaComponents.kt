package com.nova.app.core.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nova.app.ui.theme.*

@Composable
fun NovaVideoView(
    url: String,
    autoPlayOnWifi: Boolean = true,
    isOnWifi: Boolean = true,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    var isPlaying by remember { mutableStateOf(autoPlayOnWifi && isOnWifi) }
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.DarkGray)
            .clickable { onClick() }
    ) {
        // Placeholder for actual video surface
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.PlayArrow, 
                contentDescription = null, 
                modifier = Modifier.size(48.dp), 
                tint = Color.White.copy(alpha = 0.5f)
            )
        }

        // Mute Indicator
        Icon(
            imageVector = Icons.Default.VolumeOff,
            contentDescription = "Muted",
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
                .size(20.dp)
        )
        
        if (!isPlaying) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = { isPlaying = true }) {
                    Icon(Icons.Default.PlayCircleFilled, contentDescription = "Play", tint = Color.White, modifier = Modifier.size(64.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaViewer(
    mediaUrl: String,
    isVideo: Boolean,
    onDismiss: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(true) }
    var playbackSpeed by remember { mutableStateOf(1.0f) }
    var quality by remember { mutableStateOf("1080p") }
    var progress by remember { mutableStateOf(0.3f) }
    var isMuted by remember { mutableStateOf(false) }
    var showControls by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable { showControls = !showControls }
    ) {
        // Main Media (Image or Video Placeholder)
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (isVideo) {
                Icon(Icons.Default.Movie, contentDescription = null, modifier = Modifier.size(120.dp), tint = Color.DarkGray)
            } else {
                Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(120.dp), tint = Color.DarkGray)
            }
        }

        // Top Controls
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss, modifier = Modifier.background(Color.Black.copy(0.4f), CircleShape)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { /* Handle download */ }, modifier = Modifier.background(Color.Black.copy(0.4f), CircleShape)) {
                        Icon(Icons.Default.Download, contentDescription = "Download", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    IconButton(onClick = { /* Handle share */ }, modifier = Modifier.background(Color.Black.copy(0.4f), CircleShape)) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                    }
                }
            }
        }

        // Bottom Controls (Only for Video)
        if (isVideo) {
            AnimatedVisibility(
                visible = showControls,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(bottom = 32.dp, top = 16.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    // Seek Bar
                    Slider(
                        value = progress,
                        onValueChange = { progress = it },
                        colors = SliderDefaults.colors(
                            thumbColor = PurpleMain,
                            activeTrackColor = PurpleMain,
                            inactiveTrackColor = Color.Gray
                        )
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Play/Pause & Time
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { isPlaying = !isPlaying }) {
                                Icon(
                                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Text("0:42 / 2:15", color = Color.White, fontSize = 12.sp)
                        }
                        
                        // Settings (Speed, Quality, Mute)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextButton(onClick = { 
                                playbackSpeed = when(playbackSpeed) {
                                    1.0f -> 1.5f
                                    1.5f -> 2.0f
                                    else -> 1.0f
                                }
                            }) {
                                Text("${playbackSpeed}x", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            
                            TextButton(onClick = { 
                                quality = when(quality) {
                                    "1080p" -> "720p"
                                    "720p" -> "480p"
                                    else -> "1080p"
                                }
                            }) {
                                Text(quality, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            
                            IconButton(onClick = { isMuted = !isMuted }) {
                                Icon(
                                    if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun VideoViewPreview() {
    NOVATheme {
        NovaVideoView(
            url = "",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp)
        )
    }
}

@Preview
@Composable
fun MediaViewerPreview() {
    NOVATheme {
        MediaViewer(
            mediaUrl = "",
            isVideo = true,
            onDismiss = {}
        )
    }
}
