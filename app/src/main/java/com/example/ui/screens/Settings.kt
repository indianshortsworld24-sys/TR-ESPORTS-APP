package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EsportsButton
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import com.example.viewmodel.EsportsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: EsportsViewModel,
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current

    var notificationEnabled by remember { mutableStateOf(true) }
    var matchReminderEnabled by remember { mutableStateOf(true) }
    var selectedLanguage by remember { mutableStateOf("English") }
    var showLanguageMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SETTINGS & CONFIG", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark)
            )
        },
        containerColor = BackgroundDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Theme Configuration
            SectionHeader(title = "App Theme")
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, BorderGray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.DarkMode, contentDescription = "Dark Theme", tint = PrimaryRed)
                        Column {
                            Text("Competitive Dark Force Theme", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Locked to Red+Gold Esports style for premium aesthetic", color = TextGray, fontSize = 10.sp)
                        }
                    }
                    Switch(
                        checked = true,
                        onCheckedChange = {
                            Toast.makeText(context, "Esports Dark Theme is locked for brand compliance!", Toast.LENGTH_SHORT).show()
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PrimaryRed
                        )
                    )
                }
            }

            // 2. Comms Configurations
            SectionHeader(title = "Notifications & Reminders")
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, BorderGray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = "Push", tint = PrimaryRed)
                            Column {
                                Text("Global Push Alerts", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Receive alerts for published room IDs", color = TextGray, fontSize = 10.sp)
                            }
                        }
                        Switch(
                            checked = notificationEnabled,
                            onCheckedChange = { notificationEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrimaryRed)
                        )
                    }

                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderGray))

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Icon(Icons.Default.WatchLater, contentDescription = "Match Alarm", tint = PrimaryRed)
                            Column {
                                Text("10-Minute Arena Reminders", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Siren alarm when matches go live", color = TextGray, fontSize = 10.sp)
                            }
                        }
                        Switch(
                            checked = matchReminderEnabled,
                            onCheckedChange = { matchReminderEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrimaryRed)
                        )
                    }
                }
            }

            // 3. Language settings
            SectionHeader(title = "Language Options")
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, BorderGray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showLanguageMenu = true }
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Icon(Icons.Default.Language, contentDescription = "Lang", tint = PrimaryRed)
                            Column {
                                Text("App Language Selection", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Current language: $selectedLanguage", color = TextGray, fontSize = 10.sp)
                            }
                        }
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Down", tint = TextGray)
                    }

                    DropdownMenu(
                        expanded = showLanguageMenu,
                        onDismissRequest = { showLanguageMenu = false },
                        modifier = Modifier.background(SurfaceDark).border(1.dp, BorderGray)
                    ) {
                        listOf("English", "Spanish", "Portuguese", "Hindi", "Bahasa Indonesia").forEach { lang ->
                            DropdownMenuItem(
                                text = { Text(lang, color = TextWhite, fontWeight = FontWeight.Bold) },
                                onClick = {
                                    selectedLanguage = lang
                                    showLanguageMenu = false
                                    Toast.makeText(context, "Language changed to $lang!", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }

            // 4. About Info
            SectionHeader(title = "App Info")
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, BorderGray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Application Version", color = TextGray, fontSize = 12.sp)
                        Text("v2.5.0-PRO", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Server Latency", color = TextGray, fontSize = 12.sp)
                        Text("24ms (Optimal)", color = LiveGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Anti-Cheat Engine", color = TextGray, fontSize = 12.sp)
                        Text("TR_SHIELD Active", color = LiveGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. LOG OUT
            Button(
                onClick = {
                    viewModel.logout()
                    onLogout()
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("DISCONNECT & LOGOUT", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
