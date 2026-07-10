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
import com.example.data.model.Tournament
import com.example.ui.components.EsportsButton
import com.example.ui.components.EsportsCard
import com.example.ui.components.EsportsOutlinedButton
import com.example.ui.components.TournamentStatusBadge
import com.example.ui.theme.*
import com.example.viewmodel.EsportsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: EsportsViewModel,
    onNavigateBack: () -> Unit
) {
    val tournaments by viewModel.allTournaments.collectAsState()
    val context = LocalContext.current
    var adminTab by remember { mutableStateOf(0) } // 0: Create, 1: Publish Room, 2: Certify Winners

    // Create Tournament Form State
    var title by remember { mutableStateOf("") }
    var entryFee by remember { mutableStateOf("") }
    var prizePool by remember { mutableStateOf("") }
    var mapSelected by remember { mutableStateOf("Bermuda") }
    var matchTime by remember { mutableStateOf("") }
    var slotsText by remember { mutableStateOf("48") }
    var gameModeSelected by remember { mutableStateOf("Squad") }
    var rulesText by remember { mutableStateOf("1. Mobile only.\n2. Hacks will trigger permanent ban.\n3. Verify screenshots.") }

    // Publish Room state
    var selectedTournamentForRoom by remember { mutableStateOf<Tournament?>(null) }
    var inputRoomId by remember { mutableStateOf("") }
    var inputRoomPassword by remember { mutableStateOf("") }

    // Certify results state
    var selectedTournamentForCertify by remember { mutableStateOf<Tournament?>(null) }
    var firstPlaceTeamName by remember { mutableStateOf("Team TR Esports") }
    var secondPlaceTeamName by remember { mutableStateOf("Viper Gaming") }
    var thirdPlaceTeamName by remember { mutableStateOf("GodLike FF") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ADMIN COMMAND DECK", fontWeight = FontWeight.ExtraBold) },
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
            // Admin Tabs
            ScrollableTabRow(
                selectedTabIndex = adminTab,
                containerColor = SurfaceDark,
                contentColor = PrimaryRed,
                edgePadding = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Tab(selected = adminTab == 0, onClick = { adminTab = 0 }, text = { Text("CREATE ARENA", fontWeight = FontWeight.Bold, fontSize = 11.sp) })
                Tab(selected = adminTab == 1, onClick = { adminTab = 1 }, text = { Text("PUBLISH ROOM", fontWeight = FontWeight.Bold, fontSize = 11.sp) })
                Tab(selected = adminTab == 2, onClick = { adminTab = 2 }, text = { Text("CERTIFY WINNERS", fontWeight = FontWeight.Bold, fontSize = 11.sp) })
            }

            // Tab Content
            when (adminTab) {
                0 -> {
                    // Create Arena Form
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Tournament Title") }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed, focusedLabelColor = PrimaryRed), modifier = Modifier.fillMaxWidth())
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = entryFee, onValueChange = { entryFee = it }, label = { Text("Entry Fee ($)") }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed, focusedLabelColor = PrimaryRed), modifier = Modifier.weight(1f))
                            OutlinedTextField(value = prizePool, onValueChange = { prizePool = it }, label = { Text("Prize Pool ($)") }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed, focusedLabelColor = PrimaryRed), modifier = Modifier.weight(1f))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = slotsText, onValueChange = { slotsText = it }, label = { Text("Total Slots") }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed, focusedLabelColor = PrimaryRed), modifier = Modifier.weight(1f))
                            OutlinedTextField(value = matchTime, onValueChange = { matchTime = it }, label = { Text("Match Date/Time") }, placeholder = { Text("e.g. 15 July, 05:00 PM") }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed, focusedLabelColor = PrimaryRed), modifier = Modifier.weight(1f))
                        }

                        // Map selections
                        Text("SELECT MATCH MAP:", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Bermuda", "Purgatory", "Kalahari", "Alpine").forEach { m ->
                                Box(
                                    modifier = Modifier
                                        .border(1.dp, if (mapSelected == m) PrimaryRed else BorderGray, RoundedCornerShape(14.dp))
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(if (mapSelected == m) PrimaryRed.copy(alpha = 0.15f) else Color.Transparent)
                                        .clickable { mapSelected = m }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(m, color = if (mapSelected == m) PrimaryRed else TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Mode selector
                        Text("GAME MODE FORMAT:", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Solo", "Duo", "Squad", "Clash Squad").forEach { mode ->
                                Box(
                                    modifier = Modifier
                                        .border(1.dp, if (gameModeSelected == mode) PrimaryRed else BorderGray, RoundedCornerShape(14.dp))
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(if (gameModeSelected == mode) PrimaryRed.copy(alpha = 0.15f) else Color.Transparent)
                                        .clickable { gameModeSelected = mode }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(mode, color = if (gameModeSelected == mode) PrimaryRed else TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        OutlinedTextField(value = rulesText, onValueChange = { rulesText = it }, label = { Text("Arena Regulations") }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed, focusedLabelColor = PrimaryRed), modifier = Modifier.fillMaxWidth().height(100.dp))

                        Spacer(modifier = Modifier.height(12.dp))

                        EsportsButton(
                            text = "CREATE ESPORTS TOURNAMENT",
                            onClick = {
                                if (title.isNotBlank() && entryFee.isNotBlank() && prizePool.isNotBlank()) {
                                    viewModel.adminCreateTournament(
                                        title = title,
                                        entryFee = entryFee.toDoubleOrNull() ?: 0.0,
                                        prizePool = prizePool.toDoubleOrNull() ?: 0.0,
                                        map = mapSelected,
                                        matchTime = matchTime.ifBlank { "TBD" },
                                        slots = slotsText.toIntOrNull() ?: 48,
                                        rules = rulesText,
                                        gameMode = gameModeSelected
                                    )
                                    Toast.makeText(context, "Tournament Arena Live!", Toast.LENGTH_SHORT).show()
                                    // Clear form
                                    title = ""
                                    entryFee = ""
                                    prizePool = ""
                                    matchTime = ""
                                } else {
                                    Toast.makeText(context, "Please enter all fields!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
                1 -> {
                    // Publish Room credentials
                    val publishableTournaments = tournaments.filter { it.status != "COMPLETED" }
                    if (publishableTournaments.isEmpty()) {
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            Text("No active matches to publish room info.", color = TextGray)
                        }
                    } else {
                        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(publishableTournaments) { t ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                                    border = BorderStroke(1.dp, BorderGray),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                            Text(t.title, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            TournamentStatusBadge(status = t.status)
                                        }
                                        Text("Time: ${t.matchTime}", color = TextGray, fontSize = 11.sp)
                                        
                                        if (t.roomId.isNotBlank()) {
                                            Text("Active Credentials: ID ${t.roomId} / Pass ${t.roomPassword}", color = LiveGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))
                                        
                                        EsportsOutlinedButton(
                                            text = if (t.roomId.isBlank()) "PUBLISH ROOM DETAILS" else "UPDATE CREDENTIALS",
                                            onClick = { selectedTournamentForRoom = t },
                                            modifier = Modifier.fillMaxWidth().height(36.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // Certify Pending screenshot results
                    val pendingTournaments = tournaments.filter { it.status == "RESULT_PENDING" || it.status == "LIVE" }
                    if (pendingTournaments.isEmpty()) {
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            Text("No results currently awaiting verification.", color = TextGray)
                        }
                    } else {
                        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(pendingTournaments) { t ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                                    border = BorderStroke(1.dp, if (t.status == "RESULT_PENDING") PrimaryRed else BorderGray),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                            Text(t.title, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            TournamentStatusBadge(status = t.status)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        
                                        if (t.status == "RESULT_PENDING") {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Description, contentDescription = "Screns", tint = AccentGold, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Screenshots Uploaded: Placement & Kills", color = AccentGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        } else {
                                            Text("Awaiting matches to complete before awarding winnings.", color = TextGray, fontSize = 10.sp)
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))
                                        EsportsButton(
                                            text = "CERTIFY WINNERS",
                                            onClick = { selectedTournamentForCertify = t },
                                            modifier = Modifier.fillMaxWidth().height(36.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Publish Room dialog overlay
    if (selectedTournamentForRoom != null) {
        val t = selectedTournamentForRoom!!
        AlertDialog(
            onDismissRequest = { selectedTournamentForRoom = null },
            containerColor = SurfaceDark,
            title = { Text("PUBLISH MATCH ROOM", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Publishing credentials for: ${t.title}. Credentials instantly release to all registered competitors.", color = TextGray, fontSize = 11.sp)
                    OutlinedTextField(value = inputRoomId, onValueChange = { inputRoomId = it }, label = { Text("Room ID") }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed, focusedLabelColor = PrimaryRed), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = inputRoomPassword, onValueChange = { inputRoomPassword = it }, label = { Text("Room Password") }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed, focusedLabelColor = PrimaryRed), modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (inputRoomId.isNotBlank() && inputRoomPassword.isNotBlank()) {
                            viewModel.adminPublishRoomInfo(t.id, inputRoomId, inputRoomPassword)
                            selectedTournamentForRoom = null
                            inputRoomId = ""
                            inputRoomPassword = ""
                            Toast.makeText(context, "Room credentials published!", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("PUBLISH NOW", color = LiveGreen, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedTournamentForRoom = null }) {
                    Text("CANCEL", color = TextGray)
                }
            }
        )
    }

    // Certify results dialog overlay
    if (selectedTournamentForCertify != null) {
        val t = selectedTournamentForCertify!!
        AlertDialog(
            onDismissRequest = { selectedTournamentForCertify = null },
            containerColor = SurfaceDark,
            title = { Text("CERTIFY WINNERS", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Declare podium finishers for: ${t.title}. Prize money ($${t.prizePool}) will be divided and automatically deposited to winner wallets (60% to 1st, 30% to 2nd).", color = TextGray, fontSize = 11.sp)

                    // Simulated screenshot viewer
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(BorderGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Image, contentDescription = "Scren", tint = TextGray)
                            Text("SUBMITTED EVIDENCE SCREENSHOT (PLACEMENT & KILLS)", color = TextGray, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedTextField(value = firstPlaceTeamName, onValueChange = { firstPlaceTeamName = it }, label = { Text("1st Place Winner Team") }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed, focusedLabelColor = PrimaryRed), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = secondPlaceTeamName, onValueChange = { secondPlaceTeamName = it }, label = { Text("2nd Place Winner Team") }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed, focusedLabelColor = PrimaryRed), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = thirdPlaceTeamName, onValueChange = { thirdPlaceTeamName = it }, label = { Text("3rd Place Winner Team") }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed, focusedLabelColor = PrimaryRed), modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (firstPlaceTeamName.isNotBlank() && secondPlaceTeamName.isNotBlank()) {
                            viewModel.adminApproveResultAndAward(t.id, firstPlaceTeamName, secondPlaceTeamName, thirdPlaceTeamName)
                            selectedTournamentForCertify = null
                            Toast.makeText(context, "Podium certified! Prizes distributed.", Toast.LENGTH_LONG).show()
                        }
                    }
                ) {
                    Text("APPROVE & DEPOSIT PRIZES", color = LiveGreen, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedTournamentForCertify = null }) {
                    Text("CANCEL", color = TextGray)
                }
            }
        )
    }
}
