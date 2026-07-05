package com.nova.app.feature.discover

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nova.app.core.ui.NovaButton
import com.nova.app.ui.theme.BgDark
import com.nova.app.ui.theme.PurpleMain
import com.nova.app.ui.theme.PurplePink

@Composable
fun MatchSuccessScreen(onSendMessage: () -> Unit, onKeepSwiping: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark.copy(alpha = 0.95f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "IT'S A MATCH!",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    brush = Brush.linearGradient(listOf(PurpleMain, PurplePink))
                ),
                modifier = Modifier.scale(scale)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy((-20).dp)) {
                MatchAvatar(Color.Gray) // User
                MatchAvatar(Color.LightGray) // Match
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Text(
                text = "You and Seraphina liked each other",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
            
            Spacer(modifier = Modifier.height(64.dp))
            
            NovaButton(
                text = "Send a Message",
                onClick = onSendMessage,
                modifier = Modifier.padding(horizontal = 48.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Keep Swiping",
                color = Color.Gray,
                modifier = Modifier.clickable { onKeepSwiping() },
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun MatchAvatar(color: Color) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(color)
            .background(
                Brush.radialGradient(
                    listOf(PurpleMain.copy(alpha = 0.3f), Color.Transparent)
                )
            )
    )
}
