package com.nova.app.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nova.app.core.ui.NovaButton
import com.nova.app.core.ui.NovaChip
import com.nova.app.core.ui.NovaTextField
import com.nova.app.core.ui.NovaTopBar
import com.nova.app.ui.theme.PurpleMain

@Composable
fun ProfileSetupScreen(onComplete: () -> Unit) {
    var step by remember { mutableIntStateOf(1) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            NovaTopBar(title = "Setup Profile", onBack = { if (step > 1) step-- else onComplete() })
            
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = when(step) {
                        1 -> "Your Information"
                        2 -> "Your Interests"
                        else -> "Profile Media"
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Step $step of 3",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                when(step) {
                    1 -> BasicInfoStep()
                    2 -> InterestsStep()
                    3 -> MediaStep()
                }
                
                Spacer(modifier = Modifier.height(120.dp))
            }
        }
        
        NovaButton(
            text = if (step < 3) "Continue" else "Finish Setup",
            onClick = {
                if (step < 3) step++ else onComplete()
            },
            modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicInfoStep() {
    var name by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var gender by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                // Disable future dates
                return utcTimeMillis <= System.currentTimeMillis()
            }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { 
                showDatePicker = false
            },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val date = java.util.Date(it)
                        val format = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                        birthDate = format.format(date)
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            },
            colors = DatePickerDefaults.colors(containerColor = MaterialTheme.colorScheme.background)
        ) {
            DatePicker(
                state = datePickerState,
                title = null,
                headline = null,
                showModeToggle = false,
                colors = DatePickerDefaults.colors(
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    headlineContentColor = MaterialTheme.colorScheme.onBackground,
                    weekdayContentColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    subheadContentColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    yearContentColor = MaterialTheme.colorScheme.onBackground,
                    currentYearContentColor = PurpleMain,
                    selectedYearContentColor = MaterialTheme.colorScheme.onBackground,
                    selectedYearContainerColor = PurpleMain,
                    dayContentColor = MaterialTheme.colorScheme.onBackground,
                    selectedDayContentColor = MaterialTheme.colorScheme.onBackground,
                    selectedDayContainerColor = PurpleMain,
                    todayContentColor = PurpleMain,
                    todayDateBorderColor = PurpleMain
                )
            )
        }
    }
    
    Column {
        NovaTextField(
            label = "Full Name",
            value = name, 
            onValueChange = { name = it }, 
            placeholder = "Enter your display name"
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Column {
            NovaTextField(
                label = "Date of Birth",
                value = birthDate,
                onValueChange = { birthDate = it },
                placeholder = "DD/MM/YYYY",
                trailingIcon = Icons.Default.CalendarToday,
                onTrailingIconClick = { showDatePicker = true }
            )
            Text(
                text = "Must be 15+ years old. Cannot be changed later.",
                color = Color.Red.copy(alpha = 0.7f),
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Text(
            text = "Gender",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            GenderChip("Male", gender == "Male") { gender = "Male" }
            GenderChip("Female", gender == "Female") { gender = "Female" }
            GenderChip("Other", gender == "Other") { gender = "Other" }
        }
        Text(
            text = "Cannot be changed later.",
            color = Color.Red.copy(alpha = 0.7f),
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))
        
        NovaTextField(
            label = "Bio / Description",
            value = bio, 
            onValueChange = { bio = it }, 
            placeholder = "Tell the world about yourself...",
            modifier = Modifier.height(120.dp)
        )
    }
}

@Composable
fun GenderChip(label: String, selected: Boolean, onClick: () -> Unit) {
    NovaChip(text = label, selected = selected, onClick = onClick)
}

@Composable
fun InterestsStep() {
    val interests = listOf("Travel", "Music", "Photography", "Gaming", "Art", "Sports", "Cooking", "Nature", "Movies", "Tech")
    val selectedInterests = remember { mutableStateListOf<String>() }
    
    @OptIn(ExperimentalLayoutApi::class)
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        interests.forEach { interest ->
            NovaChip(
                text = interest,
                selected = selectedInterests.contains(interest),
                onClick = {
                    if (selectedInterests.contains(interest)) selectedInterests.remove(interest)
                    else selectedInterests.add(interest)
                }
            )
        }
    }
}

@Composable
fun MediaStep() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Upload your best photos", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(0.8f)
                        .background(Color.DarkGray, MaterialTheme.shapes.medium),
                    contentAlignment = Alignment.Center
                ) {
                    Text("+", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.headlineLarge)
                }
            }
        }
    }
}
