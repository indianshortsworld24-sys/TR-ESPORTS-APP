package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.EsportsButton
import com.example.ui.components.EsportsCard
import com.example.ui.components.EsportsOutlinedButton
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import com.example.viewmodel.EsportsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: EsportsViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToTeam: () -> Unit,
    onNavigateToSupport: () -> Unit,
    onNavigateToAdmin: () -> Unit
) {
    val profile by viewModel.userProfile.collectAsState()
    val context = LocalContext.current

    var showEditDialog by remember { mutableStateOf(false) }

    // Edit state holders
    var ign by remember { mutableStateOf("") }
    var ffUid by remember { mutableStateOf("") }
    var levelText by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var device by remember { mutableStateOf("") }
    var teamName by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }

    // Referral input
    var referralInputCode by remember { mutableStateOf("") }

    LaunchedEffect(profile) {
        profile?.let {
            ign = it.ign
            ffUid = it.ffUid
            levelText = it.level.toString()
            country = it.country
            device = it.device
            teamName = it.teamName
            bio = it.bio
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("COMPETITOR DOSSIER", fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp) },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
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
            // 1. Core Profile Details Card
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, BorderGray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(BorderGray)
                            .border(2.dp, PrimaryRed, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        // Simulated profile avatar (reusing app icon or default gamer style)
                        Icon(Icons.Default.SportsEsports, contentDescription = "Avatar", tint = PrimaryRed, modifier = Modifier.size(48.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = profile?.ign ?: "SOLDIER",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextWhite
                    )

                    Text(
                        text = "UID: ${profile?.ffUid ?: "Unknown"}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentGold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = profile?.bio ?: "No bio defined yet.",
                        fontSize = 12.sp,
                        color = TextGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        VerticalBadge(label = "LEVEL", value = profile?.level?.toString() ?: "1")
                        VerticalBadge(label = "COUNTRY", value = profile?.country ?: "Global")
                        VerticalBadge(label = "TEAM", value = profile?.teamName ?: "Free Agent")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showEditDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceDarkElevated),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, BorderGray),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TextWhite, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("EDIT DOSSIER", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onNavigateToTeam,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                        ) {
                            Icon(Icons.Default.Group, contentDescription = "Squad", tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("MANAGE SQUAD", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 2. Competitive Combat Stats
            SectionHeader(title = "Competitor Stats")

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(title = "MATCHES", value = profile?.matchesPlayed?.toString() ?: "0", icon = Icons.Default.SportsEsports, modifier = Modifier.weight(1f))
                StatCard(title = "WINS", value = profile?.wins?.toString() ?: "0", icon = Icons.Default.EmojiEvents, modifier = Modifier.weight(1f))
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(title = "KILLS", value = profile?.kills?.toString() ?: "0", icon = Icons.Default.MilitaryTech, modifier = Modifier.weight(1f))
                StatCard(title = "HEADSHOTS", value = profile?.headshots?.toString() ?: "0", icon = Icons.Default.MyLocation, modifier = Modifier.weight(1f))
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(title = "MVP TALLY", value = profile?.mvpCount?.toString() ?: "0", icon = Icons.Default.Stars, modifier = Modifier.weight(1f))
                val kdText = remember(profile) {
                    val kills = profile?.kills?.toFloat() ?: 0f
                    val matches = profile?.matchesPlayed?.toFloat() ?: 1f
                    val kd = if (matches > 0) kills / matches else 0f
                    String.format("%.2f", kd)
                }
                StatCard(title = "K/D RATIO", value = kdText, icon = Icons.Default.TrendingUp, modifier = Modifier.weight(1f))
            }

            // 3. Referral Rewards System
            SectionHeader(title = "Referral & Recruits")

            EsportsCard(borderGlowColor = AccentGold) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "RECRUIT AND SECURE REWARDS",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextWhite
                    )
                    Text(
                        "Share your referral key with fellow competitors. When they claim it, both of you secure +$100.0 credits instantly!",
                        fontSize = 10.sp,
                        color = TextGray,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Share Key Card
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceDarkElevated)
                            .border(1.dp, BorderGray, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("YOUR REFERRAL CODE", fontSize = 8.sp, color = TextGray, fontWeight = FontWeight.Bold)
                            Text(profile?.referralCode ?: "TRE0000", fontSize = 16.sp, color = AccentGold, fontWeight = FontWeight.ExtraBold)
                        }

                        Button(
                            onClick = {
                                val code = profile?.referralCode ?: "TRE0000"
                                val clip = ClipData.newPlainText("ref_code", code)
                                (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(clip)
                                Toast.makeText(context, "Referral Code Copied!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark),
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(1.dp, BorderGray)
                        ) {
                            Text("COPY CODE", color = TextWhite, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (profile?.referredBy?.isNotEmpty() == true) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = LiveGreen.copy(alpha = 0.12f)),
                            border = BorderStroke(1.dp, LiveGreen.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Check, contentDescription = "Applied", tint = LiveGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Referral Bonus already claimed from code: ${profile?.referredBy}", color = LiveGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        // Claim input
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = referralInputCode,
                                onValueChange = { referralInputCode = it },
                                label = { Text("Enter Recruiter's Code", fontSize = 11.sp) },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed, focusedLabelColor = PrimaryRed),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp),
                                shape = RoundedCornerShape(8.dp)
                            )

                            Button(
                                onClick = {
                                    if (referralInputCode.isNotBlank()) {
                                        viewModel.applyReferralCode(referralInputCode)
                                        referralInputCode = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentGold, contentColor = Color.Black),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(50.dp)
                            ) {
                                Text("CLAIM", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
            }

            // 4. Quick Links Box
            SectionHeader(title = "Competitor Resources")

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceDark)
                    .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
            ) {
                ResourceRow(icon = Icons.Default.SupportAgent, title = "Support Ticket Help Desk") { onNavigateToSupport() }
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderGray))
                
                // Secret path: If admin or demo, show Admin Dashboard!
                ResourceRow(icon = Icons.Default.AdminPanelSettings, title = "Administrator Command Center") { onNavigateToAdmin() }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // EDIT PROFILE DIALOG
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            containerColor = SurfaceDark,
            title = { Text("EDIT DOSSIER", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    OutlinedTextField(value = ign, onValueChange = { ign = it }, label = { Text("In-Game Name (IGN)") }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed, focusedLabelColor = PrimaryRed), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = ffUid, onValueChange = { ffUid = it }, label = { Text("Free Fire UID") }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed, focusedLabelColor = PrimaryRed), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = levelText, onValueChange = { levelText = it }, label = { Text("Profile Level") }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed, focusedLabelColor = PrimaryRed), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = country, onValueChange = { country = it }, label = { Text("Country") }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed, focusedLabelColor = PrimaryRed), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = device, onValueChange = { device = it }, label = { Text("Gamer Device") }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed, focusedLabelColor = PrimaryRed), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = teamName, onValueChange = { teamName = it }, label = { Text("Competitive Squad Name") }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed, focusedLabelColor = PrimaryRed), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = bio, onValueChange = { bio = it }, label = { Text("Short bio") }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed, focusedLabelColor = PrimaryRed), modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val lvl = levelText.toIntOrNull() ?: 1
                        viewModel.updateProfile(ign, ffUid, lvl, country, device, teamName, bio)
                        showEditDialog = false
                    }
                ) {
                    Text("SAVE CHANGES", color = PrimaryRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("CANCEL", color = TextGray)
                }
            }
        )
    }
}

@Composable
fun VerticalBadge(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 8.sp, color = TextGray, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 13.sp, color = TextWhite, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceDark)
            .border(1.dp, BorderGray, RoundedCornerShape(8.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = title, fontSize = 9.sp, color = TextGray, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = value, fontSize = 18.sp, color = TextWhite, fontWeight = FontWeight.ExtraBold)
            }

            Icon(icon, contentDescription = "Stat Icon", tint = PrimaryRed.copy(alpha = 0.5f), modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
fun ResourceRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = "Resource", tint = PrimaryRed, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = title, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        Icon(Icons.Default.ArrowForward, contentDescription = "Go", tint = TextGray, modifier = Modifier.size(16.dp))
    }
}
