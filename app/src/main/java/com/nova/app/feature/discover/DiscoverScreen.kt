package com.nova.app.feature.discover

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nova.app.core.ui.NovaCard
import com.nova.app.core.ui.NovaChip
import com.nova.app.core.ui.NovaTopBar
import com.nova.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen() {
    var showFilters by remember { mutableStateOf(false) }
    var selectedGender by remember { mutableStateOf("Both") }
    var ageRange by remember { mutableStateOf(18f..35f) }
    
    val sheetState = rememberModalBottomSheetState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            NovaTopBar(
                title = "Discover",
                actions = {
                    IconButton(onClick = { showFilters = true }) {
                        Icon(
                            Icons.Default.Tune,
                            contentDescription = "Filter",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            )
            
            Box(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
                SwipeCard(
                    name = "Seraphina",
                    age = 24,
                    matchScore = 98,
                    interests = listOf("Art", "Music", "Travel")
                )
            }
            
            ActionButtons()
            Spacer(modifier = Modifier.height(100.dp))
        }

        if (showFilters) {
            ModalBottomSheet(
                onDismissRequest = { showFilters = false },
                sheetState = sheetState,
                containerColor = BgCardDark,
                contentColor = Color.White
            ) {
                FilterContent(
                    selectedGender = selectedGender,
                    ageRange = ageRange,
                    onGenderChange = { selectedGender = it },
                    onAgeRangeChange = { ageRange = it },
                    onApply = { showFilters = false }
                )
            }
        }
    }
}

@Composable
fun FilterContent(
    selectedGender: String,
    ageRange: ClosedFloatingPointRange<Float>,
    onGenderChange: (String) -> Unit,
    onAgeRangeChange: (ClosedFloatingPointRange<Float>) -> Unit,
    onApply: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = "Filters",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Gender",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            NovaChip(text = "Male", selected = selectedGender == "Male", onClick = { onGenderChange("Male") })
            NovaChip(text = "Female", selected = selectedGender == "Female", onClick = { onGenderChange("Female") })
            NovaChip(text = "Both", selected = selectedGender == "Both", onClick = { onGenderChange("Both") })
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Age Range",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "${ageRange.start.toInt()} - ${ageRange.endInclusive.toInt()}",
                style = MaterialTheme.typography.bodyMedium,
                color = PurplePink
            )
        }
        
        RangeSlider(
            value = ageRange,
            onValueChange = onAgeRangeChange,
            valueRange = 15f..50f,
            modifier = Modifier.padding(top = 8.dp),
            colors = SliderDefaults.colors(
                thumbColor = PurplePink,
                activeTrackColor = PurplePink,
                inactiveTrackColor = Color.Gray.copy(alpha = 0.3f)
            )
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        Button(
            onClick = onApply,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PurpleMain),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Apply Filters", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SwipeCard(name: String, age: Int, matchScore: Int, interests: List<String>) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(32.dp))
            .background(Color.DarkGray)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                        startY = 400f
                    )
                )
        )
        
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$name, $age",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFFF06292), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.clip(CircleShape).background(SuccessGreen).size(8.dp))
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PurpleMain.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "AI Match: $matchScore%",
                        color = PurpleMain,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                interests.take(3).forEach { interest ->
                    NovaChip(text = interest, selected = true)
                }
            }
        }
    }
}

@Composable
fun ActionButtons() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ActionButton(Icons.Default.Close, MaterialTheme.colorScheme.onBackground, MaterialTheme.colorScheme.surface.copy(alpha = 0.6f), 64.dp)
        ActionButton(Icons.Default.Favorite, MaterialTheme.colorScheme.onBackground, Brush.linearGradient(listOf(PurpleMain, PurplePink)), 80.dp)
        ActionButton(Icons.Default.FlashOn, PurpleMain, MaterialTheme.colorScheme.surface.copy(alpha = 0.6f), 64.dp)
    }
}

@Composable
fun ActionButton(icon: ImageVector, iconColor: Color, background: Any, size: androidx.compose.ui.unit.Dp) {
    val modifier = Modifier
        .size(size)
        .clip(CircleShape)
        .then(
            if (background is Brush) Modifier.background(background)
            else Modifier.background(background as Color)
        )
        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
    
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(size / 2))
    }
}

@Preview
@Composable
fun DiscoverScreenPreview() {
    NOVATheme {
        DiscoverScreen()
    }
}

@Preview
@Composable
fun FilterContentPreview() {
    NOVATheme {
        Surface(color = BgDark) {
            FilterContent(
                selectedGender = "Female",
                ageRange = 20f..30f,
                onGenderChange = {},
                onAgeRangeChange = {},
                onApply = {}
            )
        }
    }
}
