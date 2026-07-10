package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.ui.components.EsportsButton
import com.example.ui.theme.*
import com.example.viewmodel.EsportsViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(viewModel: EsportsViewModel) {
    var selectedChannel by remember { mutableStateOf(0) } // 0: Scrims, 1: Broadcast, 2: Help bot

    val scrimMessages by viewModel.tournamentChat.collectAsState()
    val broadcastMessages by viewModel.broadcastChat.collectAsState()
    val supportMessages by viewModel.supportChat.collectAsState()

    val currentMessages = when (selectedChannel) {
        0 -> scrimMessages
        1 -> broadcastMessages
        else -> supportMessages
    }

    val channelTypeString = when (selectedChannel) {
        0 -> "TOURNAMENT"
        1 -> "BROADCAST"
        else -> "SUPPORT"
    }

    var messageInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Scroll to bottom on new messages
    LaunchedEffect(currentMessages.size) {
        if (currentMessages.isNotEmpty()) {
            listState.animateScrollToItem(currentMessages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("COMMS CHANNELS", fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp) },
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Channel Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .background(SurfaceDark, RoundedCornerShape(22.dp))
                    .border(1.dp, BorderGray, RoundedCornerShape(22.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ChannelTabButton(text = "SCRIMS", isSelected = selectedChannel == 0, onClick = { selectedChannel = 0 }, modifier = Modifier.weight(1f))
                ChannelTabButton(text = "ADMIN FEED", isSelected = selectedChannel == 1, onClick = { selectedChannel = 1 }, modifier = Modifier.weight(1.2f))
                ChannelTabButton(text = "SUPPORT BOT", isSelected = selectedChannel == 2, onClick = { selectedChannel = 2 }, modifier = Modifier.weight(1.2f))
            }

            // Chat Messages list
            Box(modifier = Modifier.weight(1f)) {
                if (currentMessages.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No messages in this channel.", color = TextGray)
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(currentMessages) { msg ->
                            ChatMessageBubble(message = msg, isSystem = msg.senderName == "System" || msg.senderName == "Admin Broadcast")
                        }
                    }
                }
            }

            // Input panel (Only if not Broadcast feed)
            if (selectedChannel != 1) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageInput,
                        onValueChange = { messageInput = it },
                        placeholder = { Text("Type competitive signal...", fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryRed,
                            unfocusedBorderColor = BorderGray,
                            focusedTextColor = TextWhite
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(26.dp)
                    )

                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(PrimaryRed)
                            .clickable {
                                if (messageInput.isNotBlank()) {
                                    viewModel.sendMessage(messageInput, channelTypeString)
                                    messageInput = ""
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            } else {
                // Read Only warning
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = BorderStroke(1.dp, BorderGray),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Text(
                        "READ-ONLY SIGNAL FEED: Broadcast communications are locked for administrators only.",
                        color = AccentGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChannelTabButton(
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
        Text(text = text, color = txt, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.5.sp)
    }
}

@Composable
fun ChatMessageBubble(message: ChatMessage, isSystem: Boolean) {
    val bubbleBg = if (isSystem) PrimaryRed.copy(alpha = 0.15f) else SurfaceDark
    val borderClr = if (isSystem) PrimaryRed.copy(alpha = 0.5f) else BorderGray
    val alignment = if (isSystem) Alignment.CenterHorizontally else Alignment.Start

    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val formattedTime = sdf.format(Date(message.timestamp))

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isSystem) Alignment.CenterHorizontally else Alignment.Start
    ) {
        if (!isSystem) {
            Text(
                message.senderName,
                fontSize = 10.sp,
                color = AccentGold,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 6.dp, bottom = 2.dp)
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(bubbleBg)
                .border(1.dp, borderClr, RoundedCornerShape(12.dp))
                .padding(12.dp)
                .widthIn(max = 280.dp)
        ) {
            Column {
                Text(
                    text = message.message,
                    color = TextWhite,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = formattedTime,
                    color = TextGray,
                    fontSize = 8.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
