package com.example.ui.screens

import android.app.Activity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.EsportsButton
import com.example.ui.theme.*
import com.example.viewmodel.EsportsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: EsportsViewModel,
    onLoginSuccess: () -> Unit
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val authError by viewModel.authError.collectAsState()
    
    // Redirect if already logged in
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            onLoginSuccess()
        }
    }

    var selectedTab by remember { mutableStateOf(0) } // 0: Email/Password, 1: Phone OTP
    
    // Email credentials state
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var inGameName by remember { mutableStateOf("") }
    var isRegistering by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    // Phone credentials state
    var phoneNumber by remember { mutableStateOf("") }
    var otpSent by remember { mutableStateOf(false) }
    var otpCode by remember { mutableStateOf("") }

    // Forgot password state
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }
    var resetSuccessMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .windowInsetsPadding(WindowInsets.safeDrawing),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().widthIn(max = 480.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // App Logo
            Image(
                painter = painterResource(id = R.drawable.img_app_icon),
                contentDescription = "TR ESPORTS Logo",
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, PrimaryRed, RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "TR ESPORTS",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PrimaryRed,
                letterSpacing = 2.sp
            )
            
            Text(
                text = "LEVEL UP YOUR FREE FIRE COMPETITIVE JOURNEY",
                fontSize = 10.sp,
                color = TextGray,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Custom Tab Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(SurfaceDark, RoundedCornerShape(25.dp))
                    .border(1.dp, BorderGray, RoundedCornerShape(25.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TabButton(
                    text = "EMAIL",
                    isSelected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    modifier = Modifier.weight(1f)
                )
                TabButton(
                    text = "PHONE (OTP)",
                    isSelected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Error Display
            if (authError != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = authError ?: "",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Tab Content
            AnimatedContent(targetState = selectedTab, label = "auth_tabs") { tab ->
                when (tab) {
                    0 -> {
                        // Email Sign In / Register
                        Column(modifier = Modifier.fillMaxWidth()) {
                            if (isRegistering) {
                                OutlinedTextField(
                                    value = inGameName,
                                    onValueChange = { inGameName = it },
                                    label = { Text("In-Game Name (IGN)") },
                                    placeholder = { Text("e.g. TR_Alpha") },
                                    leadingIcon = { Icon(Icons.Default.SportsEsports, contentDescription = "IGN", tint = PrimaryRed) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = PrimaryRed,
                                        unfocusedBorderColor = BorderGray,
                                        focusedLabelColor = PrimaryRed
                                    ),
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }

                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email Address") },
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email", tint = PrimaryRed) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryRed,
                                    unfocusedBorderColor = BorderGray,
                                    focusedLabelColor = PrimaryRed
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Password") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password", tint = PrimaryRed) },
                                trailingIcon = {
                                    val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(icon, contentDescription = "Toggle password", tint = TextGray)
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryRed,
                                    unfocusedBorderColor = BorderGray,
                                    focusedLabelColor = PrimaryRed
                                ),
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                shape = RoundedCornerShape(12.dp)
                            )

                            if (!isRegistering) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Text(
                                        text = "Forgot Password?",
                                        color = PrimaryRed,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        textDecoration = TextDecoration.Underline,
                                        modifier = Modifier.clickable { showForgotPasswordDialog = true }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                        // Phone OTP Login
                        Column(modifier = Modifier.fillMaxWidth()) {
            EsportsButton(
    text = if (otpSent) "VERIFY & LOGIN" else "SEND OTP CODE",
    onClick = {

        if (!otpSent) {

            if (phoneNumber.length >= 10) {

                otpSent = true

                viewModel.sendOtp(
                    "+91$phoneNumber"
                )

            }

        } else {

            viewModel.loginWithPhone(
                "+91$phoneNumber",
                otpCode
            )

        }

    },
    modifier = Modifier.fillMaxWidth()
)              
                            OutlinedTextField(
                                value = phoneNumber,
                                onValueChange = { phoneNumber = it },
                                label = { Text("Phone Number") },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone", tint = PrimaryRed) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryRed,
                                    unfocusedBorderColor = BorderGray,
                                    focusedLabelColor = PrimaryRed
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                shape = RoundedCornerShape(12.dp),
                                enabled = !otpSent
                            )

                            if (otpSent) {
                                OutlinedTextField(
                                    value = otpCode,
                                    onValueChange = { otpCode = it },
                                    label = { Text("Enter OTP Code") },
                                    placeholder = { Text("Enter OTP") },
                                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "OTP", tint = PrimaryRed) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = PrimaryRed,
                                        unfocusedBorderColor = BorderGray,
                                        focusedLabelColor = PrimaryRed
                                    ),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

            // Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f).height(1.dp).background(BorderGray))
                Text(
                    text = "OR SIGN IN WITH",
                    color = TextGray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Box(modifier = Modifier.weight(1f).height(1.dp).background(BorderGray))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Google Login Button
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, BorderGray),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable {
                        // Simulate Google Sign-In
                        viewModel.loginWithGoogle("competitive_pro@gmail.com", "TR Gladiator")
                    }
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Simulated Google Icon
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = "Google Sign In",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "CONTINUE WITH GOOGLE",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }

    // Forgot Password Simulation Dialog
    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = {
                showForgotPasswordDialog = false
                resetSuccessMessage = null
            },
            containerColor = SurfaceDark,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LockReset, contentDescription = "Reset", tint = PrimaryRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Forgot Password", color = TextWhite, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text(
                        "Enter your registered email address and we will simulate sending a password reset link.",
                        color = TextGray,
                        fontSize = 14.dp.value.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    OutlinedTextField(
                        value = resetEmail,
                        onValueChange = { resetEmail = it },
                        label = { Text("Email Address") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryRed,
                            unfocusedBorderColor = BorderGray,
                            focusedLabelColor = PrimaryRed
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (resetSuccessMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = resetSuccessMessage ?: "",
                            color = LiveGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (resetEmail.isNotBlank()) {
                            resetSuccessMessage = "Reset password token simulated successfully for $resetEmail!"
                        }
                    }
                ) {
                    Text("SEND RESET LINK", color = PrimaryRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotPasswordDialog = false }) {
                    Text("CLOSE", color = TextGray)
                }
            }
        )
    }
}

@Composable
fun TabButton(
    text: String,isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) PrimaryRed else Color.Transparent
    val textColor = if (isSelected) Color.White else TextGray
    
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(21.dp))
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}
