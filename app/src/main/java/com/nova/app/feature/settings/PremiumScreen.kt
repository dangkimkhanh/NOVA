package com.nova.app.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nova.app.core.ui.NovaButton
import com.nova.app.core.ui.NovaGlassBox
import com.nova.app.ui.theme.*

@Composable
fun PremiumScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                text = "NOVA PREMIUM",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    brush = Brush.linearGradient(listOf(PurpleMain, PurplePink))
                )
            )
            Text(
                text = "Unlock the full potential of connection",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            NovaGlassBox(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    PremiumFeature("Unlimited Likes")
                    PremiumFeature("AI Dating Coach")
                    PremiumFeature("See Who Liked You")
                    PremiumFeature("Travel Mode")
                    PremiumFeature("Undo Swipes")
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            PricingPlan("Monthly", "$9.99/mo", false)
            Spacer(modifier = Modifier.height(16.dp))
            PricingPlan("Yearly", "$5.83/mo", true)
            
            Spacer(modifier = Modifier.height(48.dp))
            
            NovaButton(text = "Go Premium", onClick = {})
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun PremiumFeature(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Icon(Icons.Default.Check, contentDescription = null, tint = SuccessGreen)
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp)
    }
}

@Composable
fun PricingPlan(title: String, price: String, isBest: Boolean) {
    NovaGlassBox(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isBest) Modifier.background(PurpleMain.copy(alpha = 0.1f)) else Modifier)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(title, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
                if (isBest) Text("SAVE 40%", color = PurpleMain, fontSize = 10.sp, fontWeight = FontWeight.Black)
            }
            Text(price, color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Black)
        }
    }
}
