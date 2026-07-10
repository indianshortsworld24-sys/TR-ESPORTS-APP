package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Tournament
import com.example.ui.components.EsportsCard
import com.example.ui.theme.*
import com.example.viewmodel.EsportsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(viewModel: EsportsViewModel) {
    val tournaments by viewModel.allTournaments.collectAsState()
    val leaderboardEntries by viewModel.tournamentLeaderboard.collectAsState()
    val selectedTournamentId by viewModel.selectedTournamentIdForLeaderboard.collectAsState()

    val availableTournaments = remember(tournaments) {
        tournaments.filter { it.status == "COMPLETED" || it.status == "RESULT_PENDING" || it.status == "LIVE" }
    }

    val selectedTournament = remember(availableTournaments, selectedTournamentId) {
        availableTournaments.firstOrNull { it.id == selectedTournamentId } ?: availableTournaments.firstOrNull()
    }

    // Set default selected if not set yet
    LaunchedEffect(availableTournaments) {
        if (selectedTournamentId == 0L && availableTournaments.isNotEmpty()) {
            viewModel.selectTournamentLeaderboard(availableTournaments.first().id)
        }
    }

    var dropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LEADERBOARDS", fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp) },
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
            // Select Tournament Header
            Text(
                "SELECT ES-TOURNAMENT ARENA:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextGray
            )

            // Custom Dropdown trigger card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceDark)
                    .border(1.dp, BorderGray, RoundedCornerShape(8.dp))
                    .clickable { dropdownExpanded = true }
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Leaderboard, contentDescription = "Cup", tint = PrimaryRed)
                        Text(
                            text = selectedTournament?.title ?: "No Completed Matches Yet",
                            color = TextWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = TextGray)
                }

                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(SurfaceDark)
                        .border(1.dp, BorderGray)
                ) {
                    availableTournaments.forEach { t ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = t.title,
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            },
                            onClick = {
                                viewModel.selectTournamentLeaderboard(t.id)
                                dropdownExpanded = false
                            },
                            modifier = Modifier.background(SurfaceDark)
                        )
                    }
                }
            }

            // Points Table Header explanation
            EsportsCard(borderGlowColor = BorderGray) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "1st Place = 12 pts\n2nd Place = 9 pts",
                        fontSize = 10.sp,
                        color = TextGray,
                        textAlign = TextAlign.Center
                    )
                    Box(modifier = Modifier.size(1.dp, 30.dp).background(BorderGray))
                    Text(
                        text = "3rd Place = 8 pts\n4th Place = 7 pts",
                        fontSize = 10.sp,
                        color = TextGray,
                        textAlign = TextAlign.Center
                    )
                    Box(modifier = Modifier.size(1.dp, 30.dp).background(BorderGray))
                    Text(
                        text = "Each Kill = 1 pt\nCS best of 7 rules",
                        fontSize = 10.sp,
                        color = AccentGold,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Scoreboard column titles
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("#", color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(36.dp), textAlign = TextAlign.Center)
                Text("SQUAD NAME", color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text("KILLS", color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(60.dp), textAlign = TextAlign.Center)
                Text("PLACE", color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(60.dp), textAlign = TextAlign.Center)
                Text("TOTAL", color = AccentGold, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.width(60.dp), textAlign = TextAlign.Center)
            }

            // Leaderboard list
            if (leaderboardEntries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.MilitaryTech, contentDescription = "Ref", tint = BorderGray, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (selectedTournament?.status == "LIVE") "Match is live right now! Standing results will compute once admin certifies screenshots." else "No certified scoreboards available.",
                            color = TextGray,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(leaderboardEntries) { index, entry ->
                        LeaderboardRow(rank = index + 1, teamName = entry.teamName, kills = entry.kills, placement = entry.placement, points = entry.totalPoints)
                    }
                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun LeaderboardRow(
    rank: Int,
    teamName: String,
    kills: Int,
    placement: Int,
    points: Int
) {
    val rowBg = when (rank) {
        1 -> PrimaryRed.copy(alpha = 0.08f)
        2 -> AccentGold.copy(alpha = 0.06f)
        else -> SurfaceDark
    }
    val rankColor = when (rank) {
        1 -> PrimaryRed
        2 -> AccentGold
        3 -> Color(0xFFCD7F32) // bronze
        else -> TextGray
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = rowBg),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, if (rank == 1) PrimaryRed.copy(alpha = 0.3f) else BorderGray),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank
            Box(
                modifier = Modifier.width(36.dp),
                contentAlignment = Alignment.Center
            ) {
                if (rank <= 3) {
                    Icon(
                        Icons.Default.EmojiEvents,
                        contentDescription = "Event Cup",
                        tint = rankColor,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Text(
                        text = rank.toString(),
                        color = rankColor,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                }
            }

            // Team Name
            Text(
                text = teamName,
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Kills
            Text(
                text = kills.toString(),
                color = TextWhite,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                modifier = Modifier.width(60.dp),
                textAlign = TextAlign.Center
            )

            // Placement Rank
            Text(
                text = "${placement}th",
                color = TextGray,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                modifier = Modifier.width(60.dp),
                textAlign = TextAlign.Center
            )

            // Total Points
            Text(
                text = points.toString(),
                color = if (rank == 1) PrimaryRed else AccentGold,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                modifier = Modifier.width(60.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}
