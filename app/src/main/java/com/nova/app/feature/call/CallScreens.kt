package com.nova.app.feature.call

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nova.app.ui.theme.*
import kotlin.math.roundToInt

@Composable
fun VoiceCallScreen(name: String, onHangUp: () -> Unit) {
    var isMinimized by remember { mutableStateOf(false) }
    var isMicOn by remember { mutableStateOf(true) }

    if (isMinimized) {
        FloatingCallWindow(
            name = name,
            isMicOn = isMicOn,
            isVideoOn = false,
            onExpand = { isMinimized = false },
            onToggleMic = { isMicOn = !isMicOn },
            onToggleVideo = {},
            onHangUp = onHangUp
        )
    } else {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            // Minimize button
            IconButton(
                onClick = { isMinimized = true },
                modifier = Modifier.padding(top = 48.dp, start = 16.dp).align(Alignment.TopStart)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Minimize", tint = MaterialTheme.colorScheme.onBackground)
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(modifier = Modifier.size(120.dp).clip(CircleShape).background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)))
                Spacer(modifier = Modifier.height(24.dp))
                Text(name, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
                Text("Calling...", color = PurpleMain, fontSize = 16.sp)
                
                Spacer(modifier = Modifier.height(100.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CallControlButton(
                        icon = if (isMicOn) Icons.Default.Mic else Icons.Default.MicOff,
                        backgroundColor = if (isMicOn) Color.White.copy(alpha = 0.1f) else Color.Red,
                        onClick = { isMicOn = !isMicOn }
                    )
                    CallControlButton(
                        icon = Icons.Default.VolumeUp,
                        backgroundColor = Color.White.copy(alpha = 0.1f),
                        onClick = {}
                    )
                    CallControlButton(
                        icon = Icons.Default.CallEnd,
                        backgroundColor = Color.Red,
                        onClick = onHangUp
                    )
                }
            }
        }
    }
}

@Composable
fun VideoCallScreen(name: String, onHangUp: () -> Unit) {
    var isMicOn by remember { mutableStateOf(true) }
    var isVideoOn by remember { mutableStateOf(true) }
    var isCallAnswered by remember { mutableStateOf(false) }
    var isMinimized by remember { mutableStateOf(false) }

    if (isMinimized) {
        FloatingCallWindow(
            name = name,
            isMicOn = isMicOn,
            isVideoOn = isVideoOn,
            onExpand = { isMinimized = false },
            onToggleMic = { isMicOn = !isMicOn },
            onToggleVideo = { isVideoOn = !isVideoOn },
            onHangUp = onHangUp
        )
    } else {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            if (isCallAnswered) {
                Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray))
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp).align(Alignment.Center),
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(name, style = MaterialTheme.typography.headlineMedium, color = Color.White)
                    Text("Calling...", color = PurplePink, fontSize = 16.sp)
                }
            }
            
            // Minimize button
            IconButton(
                onClick = { isMinimized = true },
                modifier = Modifier.padding(top = 48.dp, start = 16.dp).align(Alignment.TopStart)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Minimize", tint = Color.White)
            }

            if (isVideoOn) {
                Box(
                    modifier = Modifier
                        .padding(top = 60.dp, end = 24.dp)
                        .size(100.dp, 150.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(Color.Gray)
                        .align(Alignment.TopEnd)
                )
            }
            
            Column(
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 60.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CallControlButton(
                        icon = if (isMicOn) Icons.Default.Mic else Icons.Default.MicOff,
                        backgroundColor = if (isMicOn) Color.White.copy(alpha = 0.2f) else Color.Red,
                        onClick = { isMicOn = !isMicOn }
                    )
                    CallControlButton(
                        icon = if (isVideoOn) Icons.Default.Videocam else Icons.Default.VideocamOff,
                        backgroundColor = if (isVideoOn) Color.White.copy(alpha = 0.2f) else Color.Red,
                        onClick = { isVideoOn = !isVideoOn }
                    )
                    CallControlButton(
                        icon = Icons.Default.Cameraswitch,
                        backgroundColor = Color.White.copy(alpha = 0.2f),
                        onClick = {}
                    )
                    CallControlButton(
                        icon = Icons.Default.CallEnd,
                        backgroundColor = Color.Red,
                        onClick = onHangUp
                    )
                }
            }

            // Simulate answer for preview
            if (!isCallAnswered) {
                Button(
                    onClick = { isCallAnswered = true },
                    modifier = Modifier.align(Alignment.Center).offset(y = 150.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f))
                ) {
                    Text("Simulate Answer", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun FloatingCallWindow(
    name: String,
    isMicOn: Boolean,
    isVideoOn: Boolean,
    onExpand: () -> Unit,
    onToggleMic: () -> Unit,
    onToggleVideo: () -> Unit,
    onHangUp: () -> Unit
) {
    var offsetX by remember { mutableStateOf(500f) }
    var offsetY by remember { mutableStateOf(200f) }
    var showControls by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
    ) {
        Card(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .size(width = 140.dp, height = 200.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable { showControls = !showControls },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Background: Remote person's view (Placeholder)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(60.dp))
                }

                if (showControls) {
                    // Overlay when tapped
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f))
                    ) {
                        // Expand icon in center
                        IconButton(
                            onClick = onExpand,
                            modifier = Modifier.align(Alignment.Center).size(48.dp)
                        ) {
                            Icon(Icons.Default.OpenInFull, contentDescription = "Expand", tint = Color.White)
                        }

                        // Mini control bar at bottom
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = onToggleMic, modifier = Modifier.size(28.dp)) {
                                Icon(if (isMicOn) Icons.Default.Mic else Icons.Default.MicOff, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                            IconButton(onClick = onToggleVideo, modifier = Modifier.size(28.dp)) {
                                Icon(if (isVideoOn) Icons.Default.Videocam else Icons.Default.VideocamOff, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                            IconButton(onClick = onHangUp, modifier = Modifier.size(28.dp).clip(CircleShape).background(Color.Red)) {
                                Icon(Icons.Default.CallEnd, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CallControlButton(
    icon: ImageVector,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Int = 56
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(backgroundColor)
    ) {
        Icon(icon, contentDescription = null, tint = Color.White)
    }
}

@Preview
@Composable
fun VideoCallScreenPreview() {
    NOVATheme {
        VideoCallScreen(name = "Alex Johnson", onHangUp = {})
    }
}

@Preview
@Composable
fun FloatingCallWindowPreview() {
    NOVATheme {
        Box(modifier = Modifier.fillMaxSize().background(Color.Gray)) {
            FloatingCallWindow(
                name = "Alex Johnson",
                isMicOn = true,
                isVideoOn = true,
                onExpand = {},
                onToggleMic = {},
                onToggleVideo = {},
                onHangUp = {}
            )
        }
    }
}
