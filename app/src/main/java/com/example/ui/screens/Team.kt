package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.Team
import com.example.ui.components.EsportsButton
import com.example.ui.components.EsportsCard
import com.example.ui.components.EsportsOutlinedButton
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import com.example.viewmodel.EsportsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamScreen(
    viewModel: EsportsViewModel,
    onNavigateBack: () -> Unit
) {
    val teams by viewModel.allTeams.collectAsState()
    val profile by viewModel.userProfile.collectAsState()
    val context = LocalContext.current

    var showCreateTeamDialog by remember { mutableStateOf(false) }

    // Form states
    var teamNameInput by remember { mutableStateOf("") }
    var userRoleInput by remember { mutableStateOf("IGL") }
    var viceCaptainNameInput by remember { mutableStateOf("") }
    var rusherNameInput by remember { mutableStateOf("") }
    var sniperNameInput by remember { mutableStateOf("") }
    var supportNameInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("COMPETITIVE SQUADS", fontWeight = FontWeight.ExtraBold) },
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
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Intro explaining squad significance
            EsportsCard(borderGlowColor = PrimaryRed) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("FORM SQUADS TO DOMINATE", fontWeight = FontWeight.ExtraBold, color = TextWhite, fontSize = 13.sp)
                    Text("Registering for Duo/Squad matches requires a coordinated tactical team. Establish your rosters and delegate roles below.", color = TextGray, fontSize = 10.sp, modifier = Modifier.padding(top = 2.dp))
                }
            }

            // Create team action button
            EsportsButton(
                text = "COMMISSION NEW SQUAD",
                onClick = { showCreateTeamDialog = true },
                modifier = Modifier.fillMaxWidth().height(48.dp)
            )

            // Current lists
            SectionHeader(title = "Your Competitive Squads")

            if (teams.isEmpty()) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Group, contentDescription = "Squads", tint = BorderGray, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No squad registrations logged yet.", color = TextGray)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(teams) { team ->
                        SquadCard(team = team)
                    }
                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }

    // CREATE SQUAD DIALOG
    if (showCreateTeamDialog) {
        AlertDialog(
            onDismissRequest = { showCreateTeamDialog = false },
            containerColor = SurfaceDark,
            title = { Text("COMMISSION NEW SQUAD", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text("Define squad details below. You will be automatically assigned as Captain of the squad.", color = TextGray, fontSize = 11.sp)

                    OutlinedTextField(value = teamNameInput, onValueChange = { teamNameInput = it }, label = { Text("Squad Team Name") }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed, focusedLabelColor = PrimaryRed), modifier = Modifier.fillMaxWidth())

                    // Role of Creator
                    Text("Tactical Role of Captain (${profile?.ign ?: "Player"}):", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("IGL", "Rusher", "Sniper", "Support").forEach { role ->
                            Box(
                                modifier = Modifier
                                    .border(1.dp, if (userRoleInput == role) PrimaryRed else BorderGray, RoundedCornerShape(14.dp))
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (userRoleInput == role) PrimaryRed.copy(alpha = 0.15f) else Color.Transparent)
                                    .clickable { userRoleInput = role }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(role, color = if (userRoleInput == role) PrimaryRed else TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    OutlinedTextField(value = viceCaptainNameInput, onValueChange = { viceCaptainNameInput = it }, label = { Text("Vice-Captain IGN") }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed, focusedLabelColor = PrimaryRed), modifier = Modifier.fillMaxWidth())

                    Text("SQUAD MEMBER RECRUITS:", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    OutlinedTextField(value = rusherNameInput, onValueChange = { rusherNameInput = it }, label = { Text("Rusher IGN") }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed, focusedLabelColor = PrimaryRed), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = sniperNameInput, onValueChange = { sniperNameInput = it }, label = { Text("Sniper IGN") }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed, focusedLabelColor = PrimaryRed), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = supportNameInput, onValueChange = { supportNameInput = it }, label = { Text("Support IGN") }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed, focusedLabelColor = PrimaryRed), modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (teamNameInput.isNotBlank() && viceCaptainNameInput.isNotBlank()) {
                            viewModel.createTeam(
                                name = teamNameInput,
                                captain = profile?.ign ?: "Me",
                                rusher = if (userRoleInput == "Rusher") profile?.ign ?: "Me" else rusherNameInput.ifBlank { "Pending" },
                                sniper = if (userRoleInput == "Sniper") profile?.ign ?: "Me" else sniperNameInput.ifBlank { "Pending" },
                                support = if (userRoleInput == "Support") profile?.ign ?: "Me" else supportNameInput.ifBlank { "Pending" }
                            )
                            showCreateTeamDialog = false
                            teamNameInput = ""
                            viceCaptainNameInput = ""
                            rusherNameInput = ""
                            sniperNameInput = ""
                            supportNameInput = ""
                            Toast.makeText(context, "Squad registered successfully!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Please enter Squad Name & Vice-Captain!", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("COMMISSION SQUAD", color = LiveGreen, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateTeamDialog = false }) {
                    Text("CANCEL", color = TextGray)
                }
            }
        )
    }
}

@Composable
fun SquadCard(team: Team) {
    Card(
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, BorderGray),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(team.name, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Verified, contentDescription = "Verified", tint = LiveGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Active", color = LiveGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Captain details
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("SQUAD CAPTAIN", fontSize = 8.sp, color = TextGray, fontWeight = FontWeight.Bold)
                    Text(team.captain, fontSize = 12.sp, color = AccentGold, fontWeight = FontWeight.Bold)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("VICE-CAPTAIN", fontSize = 8.sp, color = TextGray, fontWeight = FontWeight.Bold)
                    Text(team.viceCaptain, fontSize = 12.sp, color = TextWhite, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderGray))
            Spacer(modifier = Modifier.height(10.dp))

            // Roster designation matrix
            Text("TACTICAL ROSTER MANDATE", fontSize = 9.sp, color = TextGray, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                RosterBadge(role = "IGL", name = team.igl)
                RosterBadge(role = "RUSHER", name = team.rusher)
                RosterBadge(role = "SNIPER", name = team.sniper)
                RosterBadge(role = "SUPPORT", name = team.support)
            }
        }
    }
}

@Composable
fun RosterBadge(role: String, name: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(role, fontSize = 8.sp, color = PrimaryRed, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(2.dp))
        Text(name, fontSize = 11.sp, color = TextWhite, fontWeight = FontWeight.SemiBold)
    }
}
