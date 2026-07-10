package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Team
import com.example.data.model.Tournament
import com.example.ui.components.EsportsButton
import com.example.ui.components.EsportsCard
import com.example.ui.components.EsportsOutlinedButton
import com.example.ui.components.TournamentStatusBadge
import com.example.ui.theme.*
import com.example.viewmodel.EsportsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentScreen(
    viewModel: EsportsViewModel,
    onNavigateToChat: () -> Unit,
    onNavigateToTeam: () -> Unit
) {
    val tournaments by viewModel.allTournaments.collectAsState()
    val teams by viewModel.allTeams.collectAsState()
    val profile by viewModel.userProfile.collectAsState()

    var filterIndex by remember { mutableStateOf(0) } // 0: All, 1: Registered
    var statusFilter by remember { mutableStateOf("ALL") } // ALL, UPCOMING, LIVE, COMPLETED

    val filteredList = remember(tournaments, filterIndex, statusFilter) {
        tournaments.filter { t ->
            val matchReg = if (filterIndex == 1) t.isRegistered else true
            val matchStatus = if (statusFilter == "ALL") true else t.status == statusFilter
            matchReg && matchStatus
        }
    }

    var selectedTournament by remember { mutableStateOf<Tournament?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("COMPETITIVE ARENAS", fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
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
            // Filter tabs: All vs Registered
            TabRow(
                selectedTabIndex = filterIndex,
                containerColor = SurfaceDark,
                contentColor = PrimaryRed,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[filterIndex]),
                        color = PrimaryRed
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Tab(
                    selected = filterIndex == 0,
                    onClick = { filterIndex = 0 },
                    text = { Text("ALL BATTLES", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = filterIndex == 1,
                    onClick = { filterIndex = 1 },
                    text = { Text("MY REGISTRATIONS", fontWeight = FontWeight.Bold) }
                )
            }

            // Horizontal filters for status: UPCOMING, LIVE, COMPLETED
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusChips("ALL", statusFilter == "ALL") { statusFilter = "ALL" }
                StatusChips("UPCOMING", statusFilter == "UPCOMING") { statusFilter = "UPCOMING" }
                StatusChips("LIVE", statusFilter == "LIVE") { statusFilter = "LIVE" }
                StatusChips("COMPLETED", statusFilter == "COMPLETED") { statusFilter = "COMPLETED" }
            }

            // List of tournaments
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.SportsEsports, contentDescription = "None", tint = BorderGray, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No matches found for selected filters.", color = TextGray, fontSize = 14.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredList) { tournament ->
                        TournamentCard(
                            tournament = tournament,
                            onClick = { selectedTournament = tournament }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }

    // Tournament Detail Dialog (Expand view)
    if (selectedTournament != null) {
        val activeTournament = tournaments.firstOrNull { it.id == selectedTournament!!.id } ?: selectedTournament!!
        TournamentDetailsDialog(
            tournament = activeTournament,
            teamsList = teams,
            walletBalance = profile?.walletBalance ?: 0.0,
            onDismiss = { selectedTournament = null },
            onJoin = { teamSelected, role ->
                viewModel.joinTournament(activeTournament, teamSelected, role)
            },
            onSubmitResult = { resUrl, killUrl ->
                viewModel.submitTournamentResult(activeTournament.id, resUrl, killUrl)
            },
            onNavigateToChat = onNavigateToChat,
            onNavigateToTeam = onNavigateToTeam
        )
    }
}

@Composable
fun StatusChips(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor = if (isSelected) PrimaryRed else SurfaceDark
    val borderClr = if (isSelected) PrimaryRed else BorderGray
    val txtColor = if (isSelected) Color.White else TextGray

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.dp, borderClr, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(text = text, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = txtColor)
    }
}

@Composable
fun TournamentCard(
    tournament: Tournament,
    onClick: () -> Unit
) {
    val progress = (tournament.availableSlots.toFloat() / tournament.totalSlots.toFloat()).coerceIn(0f, 1f)
    val slotsFilled = tournament.totalSlots - tournament.availableSlots

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderGray),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TournamentStatusBadge(status = tournament.status)
                Text(
                    text = "MAP: ${tournament.map.uppercase()}",
                    color = AccentGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = tournament.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextWhite
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RowItem(label = "PRIZE POOL", value = "$${tournament.prizePool}", tint = AccentGold)
                RowItem(label = "ENTRY FEE", value = if (tournament.entryFee == 0.0) "FREE" else "$${tournament.entryFee}", tint = TextWhite)
                RowItem(label = "MODE", value = tournament.gameMode, tint = TextWhite)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Slots indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("SLOTS FILLED: $slotsFilled / ${tournament.totalSlots}", fontSize = 10.sp, color = TextGray, fontWeight = FontWeight.SemiBold)
                Text(text = if (tournament.availableSlots == 0) "FULL" else "${tournament.availableSlots} LEFT", fontSize = 10.sp, color = if (tournament.availableSlots == 0) PrimaryRed else LiveGreen, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { 1f - progress },
                color = PrimaryRed,
                trackColor = BorderGray,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )
        }
    }
}

@Composable
fun RowItem(label: String, value: String, tint: Color) {
    Column {
        Text(text = label, fontSize = 9.sp, color = TextGray, fontWeight = FontWeight.Bold)
        Text(text = value, fontSize = 13.sp, color = tint, fontWeight = FontWeight.ExtraBold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentDetailsDialog(
    tournament: Tournament,
    teamsList: List<Team>,
    walletBalance: Double,
    onDismiss: () -> Unit,
    onJoin: (Team?, String) -> Unit,
    onSubmitResult: (String, String) -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToTeam: () -> Unit
) {
    val context = LocalContext.current
    var showJoinConfirm by remember { mutableStateOf(false) }
    var selectedTeamToJoin by remember { mutableStateOf<Team?>(null) }
    var selectedRole by remember { mutableStateOf("IGL") } // IGL, Rusher, Sniper, Support

    // Room visibility check
    val isRoomVisible = remember(tournament) {
        tournament.status == "LIVE" && tournament.isRegistered && tournament.roomId.isNotBlank()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark),
        content = {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(tournament.title, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold) },
                        navigationIcon = {
                            IconButton(onClick = onDismiss) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark)
                    )
                },
                containerColor = BackgroundDark
            ) { pad ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(pad)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Status Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TournamentStatusBadge(status = tournament.status)
                        Text(
                            text = "START TIME: ${tournament.matchTime}",
                            color = AccentGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Map info card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        border = BorderStroke(1.dp, BorderGray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            DetailItem(title = "MAP", desc = tournament.map)
                            DetailItem(title = "PRIZE POOL", desc = "$${tournament.prizePool}")
                            DetailItem(title = "ENTRY FEE", desc = if (tournament.entryFee == 0.0) "FREE" else "$${tournament.entryFee}")
                            DetailItem(title = "GAME MODE", desc = tournament.gameMode)
                        }
                    }

                    // Room System panel (Copy Room ID & Password)
                    if (isRoomVisible) {
                        EsportsCard(borderGlowColor = LiveGreen) {
                            Text(
                                "MATCH ROOM CREDENTIALS",
                                fontWeight = FontWeight.ExtraBold,
                                color = LiveGreen,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                "Enter Custom Room inside Free Fire with details below.",
                                fontSize = 10.sp,
                                color = TextGray
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("ROOM ID", fontSize = 9.sp, color = TextGray, fontWeight = FontWeight.Bold)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(tournament.roomId, color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        IconButton(
                                            onClick = {
                                                val clip = ClipData.newPlainText("room_id", tournament.roomId)
                                                (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(clip)
                                                Toast.makeText(context, "Room ID Copied!", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = AccentGold, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text("PASSWORD", fontSize = 9.sp, color = TextGray, fontWeight = FontWeight.Bold)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(tournament.roomPassword, color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        IconButton(
                                            onClick = {
                                                val clip = ClipData.newPlainText("room_pass", tournament.roomPassword)
                                                (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(clip)
                                                Toast.makeText(context, "Password Copied!", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = AccentGold, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    } else if (tournament.status == "LIVE" && tournament.isRegistered) {
                        EsportsCard(borderGlowColor = AccentGold) {
                            Text(
                                "ROOM CODE WILL BE RELEASED SOON",
                                fontWeight = FontWeight.Bold,
                                color = AccentGold,
                                fontSize = 12.sp
                            )
                            Text(
                                "Admin is setting up the Room. Credentials will appear here 10 minutes prior to kickoff. Keep this screen open.",
                                fontSize = 10.sp,
                                color = TextGray
                            )
                        }
                    }

                    // Registered notification
                    if (tournament.isRegistered) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = LiveGreen.copy(alpha = 0.12f)),
                            border = BorderStroke(1.dp, LiveGreen.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Joined", tint = LiveGreen)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Registered with: ${tournament.joinedTeamName}",
                                    color = LiveGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Rule Book
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("RULES & REGULATORY MANUAL", fontWeight = FontWeight.ExtraBold, color = TextWhite, fontSize = 13.sp)
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderGray))
                        Text(
                            text = tournament.rules,
                            color = TextGray,
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    // Result upload triggers (if joined)
                    if (tournament.isRegistered && (tournament.status == "LIVE" || tournament.status == "RESULT_PENDING")) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("GAMEPLAY RESULT SUBMISSION", fontWeight = FontWeight.ExtraBold, color = TextWhite, fontSize = 13.sp)
                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderGray))
                            
                            if (tournament.resultSubmitted) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                                    border = BorderStroke(1.dp, LiveGreen.copy(alpha = 0.4f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.AccessTime, contentDescription = "Verifying", tint = AccentGold)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("RESULTS VERIFICATION IN PROGRESS", color = AccentGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Text("Submitted screenshots are being verified by a live referee. Leaderboard points will compile shortly.", color = TextGray, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
                                    }
                                }
                            } else {
                                Text("Upload match completion evidence to secure points and winnings. Double screenshots (Placement + Kills summary) are required.", color = TextGray, fontSize = 10.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                EsportsOutlinedButton(
                                    text = "UPLOAD RESULT SCREENSHOTS",
                                    onClick = {
                                        // Simulate uploading image and trigger ViewModel
                                        onSubmitResult("img_res_ss_demo", "img_kill_ss_demo")
                                        Toast.makeText(context, "Screenshots uploaded! Admin notified.", Toast.LENGTH_LONG).show()
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // JOIN BUTTON or Chat Button
                    if (!tournament.isRegistered && tournament.status == "UPCOMING") {
                        EsportsButton(
                            text = "REGISTER FOR ARENA",
                            onClick = { showJoinConfirm = true },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else if (tournament.isRegistered) {
                        EsportsOutlinedButton(
                            text = "TOURNAMENT CHAT ROOM",
                            onClick = {
                                onNavigateToChat()
                                onDismiss()
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // JOIN CONFIRMATION MODAL
            if (showJoinConfirm) {
                AlertDialog(
                    onDismissRequest = { showJoinConfirm = false },
                    containerColor = SurfaceDark,
                    title = { Text("ARENA REGISTRATION", color = TextWhite, fontWeight = FontWeight.Bold) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Select registration mode and roles below. Registration fees will deduct instantly from wallet.", color = TextGray, fontSize = 12.sp)

                            Text("Wallet Balance: $$walletBalance", color = AccentGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Arena Fee: $$${tournament.entryFee}", color = PrimaryRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                            if (walletBalance < tournament.entryFee) {
                                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                                    Text("INSUFFICIENT WALLET BALANCE! Please reload funds in the Wallet Tab.", color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
                                }
                            }

                            if (tournament.gameMode != "Solo") {
                                Text("SELECT SQUAD TEAM:", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                if (teamsList.isEmpty()) {
                                    Column {
                                        Text("You have no squads registered yet!", color = TextGray, fontSize = 11.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("CREATE SQUAD", color = PrimaryRed, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.clickable {
                                            onNavigateToTeam()
                                            showJoinConfirm = false
                                        })
                                    }
                                } else {
                                    teamsList.forEach { team ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (selectedTeamToJoin?.id == team.id) PrimaryRed.copy(alpha = 0.2f) else BorderGray)
                                                .clickable { selectedTeamToJoin = team }
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(team.name, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            if (selectedTeamToJoin?.id == team.id) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = "Sel", tint = PrimaryRed)
                                            }
                                        }
                                    }
                                }
                            } else {
                                Text("JOINING SOLITARY RUSH AS SOLO COMPETITOR", color = AccentGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            // Role selection
                            Text("YOUR ROLES:", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf("IGL", "Rusher", "Sniper", "Support").forEach { role ->
                                    Box(
                                        modifier = Modifier
                                            .border(1.dp, if (selectedRole == role) PrimaryRed else BorderGray, RoundedCornerShape(14.dp))
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(if (selectedRole == role) PrimaryRed.copy(alpha = 0.15f) else Color.Transparent)
                                            .clickable { selectedRole = role }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(role, color = if (selectedRole == role) PrimaryRed else TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                if (walletBalance >= tournament.entryFee) {
                                    onJoin(selectedTeamToJoin, selectedRole)
                                    showJoinConfirm = false
                                }
                            },
                            enabled = walletBalance >= tournament.entryFee && (tournament.gameMode == "Solo" || selectedTeamToJoin != null)
                        ) {
                            Text("CONFIRM JOIN", color = PrimaryRed, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showJoinConfirm = false }) {
                            Text("CANCEL", color = TextGray)
                        }
                    }
                )
            }
        }
    )
}

@Composable
fun DetailItem(title: String, desc: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, fontSize = 8.sp, color = TextGray, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(2.dp))
        Text(desc, fontSize = 12.sp, color = TextWhite, fontWeight = FontWeight.ExtraBold)
    }
}
