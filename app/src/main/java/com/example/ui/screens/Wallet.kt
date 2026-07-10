package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Transaction
import com.example.ui.components.EsportsButton
import com.example.ui.components.EsportsCard
import com.example.ui.components.EsportsOutlinedButton
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import com.example.viewmodel.EsportsViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(viewModel: EsportsViewModel) {
    val profile by viewModel.userProfile.collectAsState()
    val transactions by viewModel.allTransactions.collectAsState()
    val context = LocalContext.current

    var showDepositDialog by remember { mutableStateOf(false) }
    var depositAmount by remember { mutableStateOf("") }
    var depositUpi by remember { mutableStateOf("") }

    var showWithdrawDialog by remember { mutableStateOf(false) }
    var withdrawAmount by remember { mutableStateOf("") }
    var withdrawUpi by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MY ESPORTS WALLET", fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp) },
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
            // Balance showcase card
            item {
                EsportsCard(borderGlowColor = AccentGold) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "AVAILABLE BALANCE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextGray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$${profile?.walletBalance ?: 0.0}",
                            fontSize = 38.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AccentGold
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { showDepositDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed, contentColor = Color.White),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Deposit")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("DEPOSIT", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = { showWithdrawDialog = true },
                                border = BorderStroke(1.dp, BorderGray),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                            ) {
                                Icon(Icons.Default.FileDownload, contentDescription = "Withdraw", tint = AccentGold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("WITHDRAW", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Quick UPI notice info
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = BorderStroke(1.dp, BorderGray),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = "Secured", tint = LiveGreen, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "UPI deposits and payouts are processed with high encryption. Payouts complete in under 60 minutes.",
                            color = TextGray,
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }
                }
            }

            // Transactions Header
            item {
                SectionHeader(title = "Transaction History")
            }

            if (transactions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No transactions logged yet.", color = TextGray, fontSize = 12.sp)
                    }
                }
            } else {
                items(transactions) { tx ->
                    TransactionRow(transaction = tx)
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Deposit Dialog
    if (showDepositDialog) {
        AlertDialog(
            onDismissRequest = { showDepositDialog = false },
            containerColor = SurfaceDark,
            title = { Text("LOAD CREDITS", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Select load amount and enter your virtual UPI handle below.", color = TextGray, fontSize = 12.sp)
                    
                    OutlinedTextField(
                        value = depositAmount,
                        onValueChange = { depositAmount = it },
                        label = { Text("Amount ($)") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed, focusedLabelColor = PrimaryRed),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = depositUpi,
                        onValueChange = { depositUpi = it },
                        label = { Text("UPI Virtual Address ID") },
                        placeholder = { Text("e.g. player@upi") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed, focusedLabelColor = PrimaryRed),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val amt = depositAmount.toDoubleOrNull()
                        if (amt != null && amt > 0 && depositUpi.isNotBlank()) {
                            viewModel.depositFunds(amt, depositUpi)
                            showDepositDialog = false
                            depositAmount = ""
                            depositUpi = ""
                        } else {
                            Toast.makeText(context, "Please enter valid fields!", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("PAY SECURELY", color = PrimaryRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDepositDialog = false }) {
                    Text("CANCEL", color = TextGray)
                }
            }
        )
    }

    // Withdraw Dialog
    if (showWithdrawDialog) {
        AlertDialog(
            onDismissRequest = { showWithdrawDialog = false },
            containerColor = SurfaceDark,
            title = { Text("WITHDRAW WINNINGS", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Request payouts to your verified UPI account. Balance must meet withdrawal specifications.", color = TextGray, fontSize = 12.sp)
                    
                    OutlinedTextField(
                        value = withdrawAmount,
                        onValueChange = { withdrawAmount = it },
                        label = { Text("Amount ($)") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed, focusedLabelColor = PrimaryRed),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = withdrawUpi,
                        onValueChange = { withdrawUpi = it },
                        label = { Text("UPI Account Handle") },
                        placeholder = { Text("e.g. player@okaxis") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed, focusedLabelColor = PrimaryRed),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val amt = withdrawAmount.toDoubleOrNull()
                        if (amt != null && amt > 0 && withdrawUpi.isNotBlank()) {
                            if (amt <= (profile?.walletBalance ?: 0.0)) {
                                viewModel.withdrawFunds(amt, withdrawUpi)
                                showWithdrawDialog = false
                                withdrawAmount = ""
                                withdrawUpi = ""
                            } else {
                                Toast.makeText(context, "Insufficient balance!", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Please enter valid fields!", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("REQUEST PAYOUT", color = AccentGold, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawDialog = false }) {
                    Text("CANCEL", color = TextGray)
                }
            }
        )
    }
}

@Composable
fun TransactionRow(transaction: Transaction) {
    val isPositive = transaction.amount > 0
    val amountColor = if (isPositive) LiveGreen else PrimaryRed
    val sign = if (isPositive) "+" else ""
    val icon = when (transaction.type) {
        "DEPOSIT" -> Icons.Default.AddCard
        "WITHDRAW" -> Icons.Default.FileUpload
        "WINNING" -> Icons.Default.EmojiEvents
        "ENTRY_FEE" -> Icons.Default.SportsEsports
        else -> Icons.Default.Stars
    }

    val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val formattedDate = sdf.format(Date(transaction.timestamp))

    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, BorderGray),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isPositive) LiveGreenGlow else PrimaryRedGlow),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = "Tx Icon",
                        tint = if (isPositive) LiveGreen else PrimaryRed,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {
                    Text(
                        text = transaction.description,
                        color = TextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = formattedDate,
                            color = TextGray,
                            fontSize = 10.sp
                        )
                        Box(modifier = Modifier.size(3.dp).background(TextGray, RoundedCornerShape(1.5.dp)))
                        Text(
                            text = transaction.status,
                            color = if (transaction.status == "COMPLETED") LiveGreen else AccentGold,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Text(
                text = "$sign$${transaction.amount}",
                color = amountColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}
