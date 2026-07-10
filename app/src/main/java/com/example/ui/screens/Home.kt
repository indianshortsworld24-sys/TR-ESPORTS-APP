package com.example.ui.screens

import android.text.format.DateUtils
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.DailyMission
import com.example.data.model.Tournament
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.EsportsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: EsportsViewModel,
    onNavigateToTournament: (Tournament) -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToChat: () -> Unit
) {
    val profile by viewModel.userProfile.collectAsState()
    val tournaments by viewModel.allTournaments.collectAsState()
    val missions by viewModel.allMissions.collectAsState()
    val notifications by viewModel.allNotifications.collectAsState()

    val featuredTournaments = remember(tournaments) {
        tournaments.filter { it.status != "COMPLETED" }.take(3)
    }

    val unreadNotifications = remember(notifications) {
        notifications.count { !it.isRead }
    }

    // Check if daily reward can be claimed today
    val isDailyClaimed = remember(profile) {
        val lastClaim = profile?.dailyRewardClaimedDate ?: 0
        DateUtils.isToday(lastClaim)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_app_icon),
                            contentDescription = "App Icon",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Column {
                            Text(
                                text = "TR ESPORTS",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PrimaryRed,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "WELCOME, ${profile?.ign ?: "SOLDIER"}",
                                fontSize = 9.sp,
                                color = TextGray,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                },
                actions = {
                    // Wallet Balance Quick View
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceDark)
                            .border(1.dp, BorderGray, RoundedCornerShape(16.dp))
                            .clickable { onNavigateToWallet() }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.AccountBalanceWallet,
                            contentDescription = "Wallet",
                            tint = AccentGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "$${profile?.walletBalance ?: 0.0}",
                            color = TextWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Notifications / Chat Quick Link
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SurfaceDark)
                            .clickable { onNavigateToChat() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Chat,
                            contentDescription = "Chat Rooms",
                            tint = TextWhite,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        },
        containerColor = BackgroundDark
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Featured Header Image Slider
            item {
                ImageSlider(modifier = Modifier.fillMaxWidth())
            }

            // 2. Quick Actions Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    HomeQuickAction(
                        icon = Icons.Default.SportsEsports,
                        title = "Duo Squad",
                        subtitle = "Room Codes",
                        onClick = { onNavigateToChat() }
                    )
                    HomeQuickAction(
                        icon = Icons.Default.ChatBubble,
                        title = "Global Chat",
                        subtitle = "Find Team",
                        onClick = { onNavigateToChat() }
                    )
                    HomeQuickAction(
                        icon = Icons.Default.Stars,
                        title = "Leaderboard",
                        subtitle = "Point Stats",
                        onClick = { onNavigateToWallet() /* Handled via main bottom nav */ }
                    )
                }
            }

            // 3. Daily Claim Reward Card
            item {
                EsportsCard(borderGlowColor = AccentGold) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CardGiftcard, contentDescription = "Gift", tint = AccentGold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "DAILY REWARD CALENDAR",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextWhite
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isDailyClaimed) "You have claimed today's login prize! Come back tomorrow." else "Claim your daily check-in bounty: +$10.0 Credits",
                                fontSize = 11.sp,
                                color = TextGray
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { viewModel.claimDailyReward() },
                            enabled = !isDailyClaimed,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentGold,
                                contentColor = Color.Black,
                                disabledContainerColor = BorderGray,
                                disabledContentColor = TextGray
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (isDailyClaimed) "CLAIMED" else "CLAIM",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }

            // 4. Featured Tournaments list
            item {
                SectionHeader(title = "Featured Arenas")
            }

            if (featuredTournaments.isEmpty()) {
                item {
                    EmptyStatePlaceholder(text = "No active arenas available at the moment.")
                }
            } else {
                items(featuredTournaments) { tournament ->
                    FeaturedTournamentCard(
                        tournament = tournament,
                        onClick = { onNavigateToTournament(tournament) }
                    )
                }
            }

            // 5. Daily Battle Missions
            item {
                SectionHeader(title = "Daily Battle Missions")
            }

            items(missions) { mission ->
                DailyMissionCard(
                    mission = mission,
                    onClaimReward = { viewModel.claimMissionReward(mission) }
                )
            }

            // 6. Esports News & Highlights
            item {
                SectionHeader(title = "TR Esports News")
            }

            item {
                NewsHighlightCard(
                    title = "Free Fire World Series (FFWS) 2026 Format Revealed",
                    summary = "Garena announces a massive prize pool of $2,000,000 and dynamic regional qualifier brackets. TR Esports to host primary simulation scrims.",
                    date = "Today • 2 hours ago"
                )
            }

            item {
                NewsHighlightCard(
                    title = "Bermuda Map Rework: Competitive Tactics Shift",
                    summary = "Observatory and Peak undergo key layout adjustments in the latest patch. Read how to secure optimal placement in squad drops.",
                    date = "Yesterday • Scrim Highlights"
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun HomeQuickAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceDark)
            .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                icon,
                contentDescription = title,
                tint = PrimaryRed,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                textAlign = TextAlign.Center
            )
            Text(
                subtitle,
                fontSize = 8.sp,
                color = TextGray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun FeaturedTournamentCard(
    tournament: Tournament,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderGray),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(120.dp)) {
            // Background visual pattern
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(SurfaceDark, PrimaryRed.copy(alpha = 0.15f))
                        )
                    )
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TournamentStatusBadge(status = tournament.status)
                    Text(
                        text = tournament.gameMode.uppercase(),
                        color = AccentGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }

                Column {
                    Text(
                        text = tournament.title,
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PRIZE: $${tournament.prizePool}",
                            color = AccentGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (tournament.entryFee == 0.0) "ENTRY: FREE" else "ENTRY: $${tournament.entryFee}",
                            color = TextWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DailyMissionCard(
    mission: DailyMission,
    onClaimReward: () -> Unit
) {
    val progressPercent = (mission.progress.toFloat() / mission.target.toFloat()).coerceIn(0f, 1f)
    val completed = mission.progress >= mission.target

    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, BorderGray),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = mission.title,
                        color = TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = mission.description,
                        color = TextGray,
                        fontSize = 11.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.MonetizationOn,
                        contentDescription = "Reward Coins",
                        tint = AccentGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "+$${mission.rewardCoins}",
                        color = AccentGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Progress bar
                LinearProgressIndicator(
                    progress = { progressPercent },
                    color = if (completed) LiveGreen else PrimaryRed,
                    trackColor = BorderGray,
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                )

                Text(
                    text = "${mission.progress}/${mission.target}",
                    color = if (completed) LiveGreen else TextWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                if (completed && !mission.isCompleted) {
                    Button(
                        onClick = onClaimReward,
                        colors = ButtonDefaults.buttonColors(containerColor = LiveGreen, contentColor = Color.Black),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("CLAIM", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (mission.isCompleted) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = LiveGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun NewsHighlightCard(
    title: String,
    summary: String,
    date: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, BorderGray),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                color = TextWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = summary,
                color = TextGray,
                fontSize = 11.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = date,
                    color = PrimaryRed,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "Read More",
                    tint = TextGray,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyStatePlaceholder(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(SurfaceDark, RoundedCornerShape(8.dp))
            .border(1.dp, BorderGray, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = TextGray,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}
