package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.*
import com.example.viewmodel.EsportsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: EsportsViewModel = viewModel()
                val isLoggedIn by viewModel.isLoggedIn.collectAsState()
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "splash",
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 1. Splash Screen
                    composable("splash") {
                        SplashScreen(
                            onNavigateToNext = {
                                if (isLoggedIn) {
                                    navController.navigate("main") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                } else {
                                    navController.navigate("auth") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                }
                            }
                        )
                    }

                    // 2. Auth Screen
                    composable("auth") {
                        AuthScreen(
                            viewModel = viewModel,
                            onLoginSuccess = {
                                navController.navigate("main") {
                                    popUpTo("auth") { inclusive = true }
                                }
                            }
                        )
                    }

                    // 3. Main Screen (With Bottom Navigation Hub)
                    composable("main") {
                        MainNavigationHub(
                            viewModel = viewModel,
                            onLogout = {
                                navController.navigate("auth") {
                                    popUpTo("main") { inclusive = true }
                                }
                            },
                            onNavigateToRoute = { route ->
                                navController.navigate(route)
                            }
                        )
                    }

                    // 4. Secondary Sub-screens
                    composable("team") {
                        TeamScreen(
                            viewModel = viewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable("settings") {
                        SettingsScreen(
                            viewModel = viewModel,
                            onNavigateBack = { navController.popBackStack() },
                            onLogout = {
                                navController.navigate("auth") {
                                    popUpTo("main") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("support") {
                        SupportScreen(
                            viewModel = viewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable("admin") {
                        AdminScreen(
                            viewModel = viewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainNavigationHub(
    viewModel: EsportsViewModel,
    onLogout: () -> Unit,
    onNavigateToRoute: (String) -> Unit
) {
    val nestedController = rememberNavController()
    val navItems = listOf(
        BottomNavItem("home", "HOME", Icons.Default.Home),
        BottomNavItem("battles", "BATTLES", Icons.Default.SportsEsports),
        BottomNavItem("standings", "STANDINGS", Icons.Default.Leaderboard),
        BottomNavItem("wallet", "WALLET", Icons.Default.AccountBalanceWallet),
        BottomNavItem("dossier", "DOSSIER", Icons.Default.Person)
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceDark,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .border(BorderStroke(1.dp, BorderGray))
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                val navBackStackEntry by nestedController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                navItems.forEach { item ->
                    val selected = currentRoute == item.route
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            nestedController.navigate(item.route) {
                                popUpTo(nestedController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                item.icon,
                                contentDescription = item.label,
                                tint = if (selected) PrimaryRed else TextGray,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontSize = 9.sp,
                                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Bold,
                                color = if (selected) PrimaryRed else TextGray
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = PrimaryRed.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        },
        containerColor = BackgroundDark,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = nestedController,
            startDestination = "home",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToTournament = { tournament ->
                        // Quick navigate to battles tab & expand details
                        nestedController.navigate("battles")
                    },
                    onNavigateToWallet = {
                        nestedController.navigate("wallet")
                    },
                    onNavigateToChat = {
                        onNavigateToRoute("support") // routes to Helpdesk & Chat Comms
                    }
                )
            }

            composable("battles") {
                TournamentScreen(
                    viewModel = viewModel,
                    onNavigateToChat = {
                        onNavigateToRoute("support")
                    },
                    onNavigateToTeam = {
                        onNavigateToRoute("team")
                    }
                )
            }

            composable("standings") {
                LeaderboardScreen(viewModel = viewModel)
            }

            composable("wallet") {
                WalletScreen(viewModel = viewModel)
            }

            composable("dossier") {
                ProfileScreen(
                    viewModel = viewModel,
                    onNavigateToSettings = { onNavigateToRoute("settings") },
                    onNavigateToTeam = { onNavigateToRoute("team") },
                    onNavigateToSupport = { onNavigateToRoute("support") },
                    onNavigateToAdmin = { onNavigateToRoute("admin") }
                )
            }
        }
    }
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
