package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
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
import com.example.ui.components.EsportsButton
import com.example.ui.components.EsportsCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import com.example.viewmodel.EsportsViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(
    viewModel: EsportsViewModel,
    onNavigateBack: () -> Unit
) {
    val tickets by viewModel.allTickets.collectAsState()
    val context = LocalContext.current

    var selectedSection by remember { mutableStateOf(0) } // 0: FAQ, 1: Report Problem, 2: Open Tickets

    // Form states
    var ticketTitle by remember { mutableStateOf("") }
    var ticketDesc by remember { mutableStateOf("") }
    var ticketCategory by remember { mutableStateOf("GAMEPLAY") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SUPPORT CENTER", fontWeight = FontWeight.ExtraBold) },
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
            // Support tab selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .background(SurfaceDark, RoundedCornerShape(22.dp))
                    .border(1.dp, BorderGray, RoundedCornerShape(22.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                SupportTabButton(text = "FAQS", isSelected = selectedSection == 0, onClick = { selectedSection = 0 }, modifier = Modifier.weight(1f))
                SupportTabButton(text = "FILE TICKET", isSelected = selectedSection == 1, onClick = { selectedSection = 1 }, modifier = Modifier.weight(1.2f))
                SupportTabButton(text = "MY TICKETS", isSelected = selectedSection == 2, onClick = { selectedSection = 2 }, modifier = Modifier.weight(1.2f))
            }

            when (selectedSection) {
                0 -> {
                    // FAQ List
                    val faqs = listOf(
                        "How do I join a custom room?" to "Go to the Tournaments tab, register for an upcoming match, and copy the Room ID & Password which unlocks exactly 10 minutes prior to kickoff. Enter these inside the Free Fire app Custom Game lobby.",
                        "Are emulator players allowed?" to "No, TR Esports is strictly a competitive mobile platform. Emulator players will be auto-flagged and kicked by anti-cheat systems.",
                        "What is the point scoring system?" to "We adhere strictly to Free Fire Esports rules: each kill awards 1 point. Placement awards are: 1st place = 12 pts, 2nd = 9 pts, 3rd = 8, 4th = 7, 5th = 6, 6th = 5, 7th = 4, 8th = 3, 9th = 2, 10th = 1.",
                        "How are wallet withdrawals processed?" to "Payouts are wired to your virtual UPI address. Go to Wallet, select Withdraw, enter details. Payouts verify and settle in under 60 minutes."
                    )

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(faqs) { faq ->
                            FaqItem(question = faq.first, answer = faq.second)
                        }
                    }
                }
                1 -> {
                    // File Ticket Form
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text("CREATE A COMPLAINT / PROBLEM TICKET", fontWeight = FontWeight.Bold, color = TextWhite, fontSize = 13.sp)
                        Text("Fill the report dossiers below. Admin agents review gameplay disputes and transaction glitches real-time.", color = TextGray, fontSize = 11.sp)

                        OutlinedTextField(value = ticketTitle, onValueChange = { ticketTitle = it }, label = { Text("Issue Title") }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed, focusedLabelColor = PrimaryRed), modifier = Modifier.fillMaxWidth())

                        Text("ISSUE CATEGORY:", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("GAMEPLAY", "WALLET", "BUG", "OTHER").forEach { cat ->
                                Box(
                                    modifier = Modifier
                                        .border(1.dp, if (ticketCategory == cat) PrimaryRed else BorderGray, RoundedCornerShape(14.dp))
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(if (ticketCategory == cat) PrimaryRed.copy(alpha = 0.15f) else Color.Transparent)
                                        .clickable { ticketCategory = cat }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(cat, color = if (ticketCategory == cat) PrimaryRed else TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        OutlinedTextField(value = ticketDesc, onValueChange = { ticketDesc = it }, label = { Text("Detailed Description") }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed, focusedLabelColor = PrimaryRed), modifier = Modifier.fillMaxWidth().height(120.dp))

                        Spacer(modifier = Modifier.height(16.dp))

                        EsportsButton(
                            text = "FILE TICKET DOSSIER",
                            onClick = {
                                if (ticketTitle.isNotBlank() && ticketDesc.isNotBlank()) {
                                    viewModel.submitTicket(ticketTitle, ticketDesc, ticketCategory)
                                    ticketTitle = ""
                                    ticketDesc = ""
                                    Toast.makeText(context, "Ticket logged successfully!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Please complete fields!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                2 -> {
                    // Open tickets tracking
                    if (tickets.isEmpty()) {
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.SupportAgent, contentDescription = "None", tint = BorderGray, modifier = Modifier.size(64.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("You have no filed complaint tickets.", color = TextGray)
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(tickets) { t ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                                    border = BorderStroke(1.dp, BorderGray),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                            Text(t.title, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Box(
                                                modifier = Modifier
                                                    .background(if (t.status == "OPEN") PrimaryRed.copy(alpha = 0.15f) else LiveGreen.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                    .border(1.dp, if (t.status == "OPEN") PrimaryRed else LiveGreen, RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Text(t.status, color = if (t.status == "OPEN") PrimaryRed else LiveGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(t.description, color = TextGray, fontSize = 12.sp)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Category: ${t.category}", color = AccentGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
                                            Text(sdf.format(Date(t.timestamp)), color = TextGray, fontSize = 9.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SupportTabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (isSelected) PrimaryRed else Color.Transparent
    val txt = if (isSelected) Color.White else TextGray

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(18.dp))
            .background(bg)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = txt, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
fun FaqItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = BorderStroke(1.dp, if (expanded) PrimaryRed.copy(alpha = 0.4f) else BorderGray),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = question, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Expand",
                    tint = TextGray
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = answer, color = TextGray, fontSize = 12.sp, lineHeight = 18.sp)
                }
            }
        }
    }
}
