package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.EsportsDatabase
import com.example.data.model.*
import com.example.data.repository.EsportsRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EsportsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EsportsRepository
    
    private val auth = FirebaseAuth.getInstance()
private val firestore = FirebaseFirestore.getInstance()

    // Core Flows from Room DB
    val userProfile: StateFlow<UserProfile?>
    val allTournaments: StateFlow<List<Tournament>>
    val allTeams: StateFlow<List<Team>>
    val allTransactions: StateFlow<List<Transaction>>
    val allMissions: StateFlow<List<DailyMission>>
    val allNotifications: StateFlow<List<Notification>>
    val allTickets: StateFlow<List<SupportTicket>>

    // Chat Flows
    val tournamentChat: StateFlow<List<ChatMessage>>
    val supportChat: StateFlow<List<ChatMessage>>
    val broadcastChat: StateFlow<List<ChatMessage>>

    // Session UI State
    private val _isLoggedIn = MutableStateFlow(false) // Start logged in for seamless demo UX
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    // Leaderboard state for the currently inspected tournament
    private val _selectedTournamentIdForLeaderboard = MutableStateFlow<Long>(5) // default to completed t5
    val selectedTournamentIdForLeaderboard: StateFlow<Long> = _selectedTournamentIdForLeaderboard.asStateFlow()

    val tournamentLeaderboard: StateFlow<List<LeaderboardEntry>> = _selectedTournamentIdForLeaderboard
        .flatMapLatest { id -> repository.getLeaderboardByTournamentId(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // App Preferences (Settings)
    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _currentLanguage = MutableStateFlow("English")
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    private val _notificationEnabled = MutableStateFlow(true)
    val notificationEnabled: StateFlow<Boolean> = _notificationEnabled.asStateFlow()

    init {
        val database = EsportsDatabase.getDatabase(application)
        val dao = database.esportsDao()
        repository = EsportsRepository(dao)

        // Initialize state flows
        userProfile = repository.userProfile
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

        allTournaments = repository.allTournaments
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allTeams = repository.allTeams
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allTransactions = repository.allTransactions
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allMissions = repository.allMissions
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allNotifications = repository.allNotifications
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allTickets = repository.allTickets
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        tournamentChat = repository.getMessagesByType("TOURNAMENT")
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        supportChat = repository.getMessagesByType("SUPPORT")
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        broadcastChat = repository.getMessagesByType("BROADCAST")
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        // Run prepopulate
        viewModelScope.launch {
            repository.prepopulateIfEmpty()
        }
    }

    // Set inspected leaderboard
    fun selectTournamentLeaderboard(tournamentId: Long) {
        _selectedTournamentIdForLeaderboard.value = tournamentId
    }

 // Authentication Actions

fun loginWithEmail(email: String, password: String) {

    if (email.isBlank() || password.isBlank()) {
        _authError.value = "Email and Password cannot be blank!"
        return
    }

    auth.signInWithEmailAndPassword(email, password)
        .addOnSuccessListener {

            val user = auth.currentUser ?: return@addOnSuccessListener

            firestore.collection("users")
                .document(user.uid)
                .get()
                .addOnSuccessListener { document ->

                    val ign = document.getString("ign") ?: "Player"

                    viewModelScope.launch {

                        val current = userProfile.value

                        if (current == null) {
                            repository.insertUserProfile(
                                UserProfile(
                                    email = email,
                                    ign = ign,
                                    walletBalance = 250.0
                                )
                            )
                        } else {
                            repository.updateUserProfile(
                                current.copy(
                                    email = email,
                                    ign = ign
                                )
                            )
                        }

                        _isLoggedIn.value = true
                        _authError.value = null

                        repository.addNotification(
                            "Welcome Back!",
                            "Logged in successfully as $ign",
                            "ANNOUNCEMENT"
                        )
                    }
                }
        }
        .addOnFailureListener {
            _authError.value = it.message
        }
}

fun loginWithGoogle(email: String, name: String) {

    val cleanIgn = name.replace(" ", "_")

    viewModelScope.launch {

        val current = userProfile.value

        if (current == null) {
            repository.insertUserProfile(
                UserProfile(
                    email = email,
                    ign = cleanIgn
                )
            )
        } else {
            repository.updateUserProfile(
                current.copy(
                    email = email,
                    ign = cleanIgn
                )
            )
        }

        _isLoggedIn.value = true
        _authError.value = null

        repository.addNotification(
            "Google Login Successful",
            "Welcome to TR Esports, $cleanIgn!",
            "ANNOUNCEMENT"
        )
    }
}
fun registerWithEmail(
    email: String,
    password: String,
    ign: String
) {

    if (email.isBlank() || password.isBlank() || ign.isBlank()) {
        _authError.value = "All fields are required!"
        return
    }

    auth.createUserWithEmailAndPassword(email, password)
        .addOnSuccessListener {

            val user = auth.currentUser

            if (user != null) {

                val userData = hashMapOf(
                    "uid" to user.uid,
                    "email" to email,
                    "ign" to ign,
                    "walletBalance" to 250.0
                )

                firestore.collection("users")
                    .document(user.uid)
                    .set(userData)
                    .addOnSuccessListener {

                        viewModelScope.launch {

                            repository.insertUserProfile(
                                UserProfile(
                                    email = email,
                                    ign = ign,
                                    walletBalance = 250.0
                                )
                            )

                            _isLoggedIn.value = true
                            _authError.value = null

                            repository.addNotification(
                                "Account Created",
                                "Welcome to TR Esports, $ign!",
                                "ANNOUNCEMENT"
                            )
                        }
                    }
            }
        }
        .addOnFailureListener {
            _authError.value = it.message
        }
}
fun loginWithPhone(phoneNumber: String, otp: String) {
fun loginWithPhone(phoneNumber: String, otp: String) {

    if (phoneNumber.length < 10 || otp.isBlank()) {
        _authError.value = "Invalid Phone Number or OTP"
        return
    }

    _isLoggedIn.value = true
    _authError.value = null

    viewModelScope.launch {

        val current = userProfile.value

        if (current == null) {
            repository.insertUserProfile(
                UserProfile(
                    bio = "Logged in with phone: $phoneNumber"
                )
            )
        }

        repository.addNotification(
            "Phone Sign-In",
            "Verification successful!",
            "ANNOUNCEMENT"
        )
    }
}

fun logout() {
    auth.signOut()
    _isLoggedIn.value = false
    // User Profile Actions
    fun updateProfile(
        ign: String,
        ffUid: String,
        level: Int,
        country: String,
        device: String,
        teamName: String,
        bio: String
    ) {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfile()
            val updated = current.copy(
                ign = ign,
                ffUid = ffUid,
                level = level,
                country = country,
                device = device,
                teamName = teamName,
                bio = bio
            )
            repository.updateUserProfile(updated)
            repository.addNotification("Profile Updated", "Your competitive credentials have been saved.", "ANNOUNCEMENT")
        }
    }

    // Tournament Registration
    fun joinTournament(tournament: Tournament, teamJoined: Team?, playerRole: String = "") {
        viewModelScope.launch {
            val currentProfile = userProfile.value ?: return@launch
            if (currentProfile.walletBalance < tournament.entryFee) {
                repository.addNotification("Registration Failed", "Insufficient wallet balance to join ${tournament.title}", "WALLET")
                return@launch
            }

            // Deduct wallet balance
            val newBalance = currentProfile.walletBalance - tournament.entryFee
            repository.updateUserProfile(currentProfile.copy(walletBalance = newBalance))

            // Update Tournament State
            val updatedTournament = tournament.copy(
                isRegistered = true,
                availableSlots = (tournament.availableSlots - 1).coerceAtLeast(0),
                joinedTeamName = teamJoined?.name ?: "Solo Player"
            )
            repository.updateTournament(updatedTournament)

            // Log Transaction if fee was paid
            if (tournament.entryFee > 0) {
                repository.addTransaction(
                    Transaction(
                        type = "ENTRY_FEE",
                        amount = -tournament.entryFee,
                        status = "COMPLETED",
                        description = "Registered for ${tournament.title}"
                    )
                )
            }

            repository.addNotification(
                "Registered Successfully",
                "You joined ${tournament.title}. Match starts: ${tournament.matchTime}.",
                "TOURNAMENT"
            )
        }
    }

    // Result Submission (by Player)
    fun submitTournamentResult(tournamentId: Long, resultScrenshot: String, killScreenshot: String) {
        viewModelScope.launch {
            val all = allTournaments.value
            val match = all.firstOrNull { it.id == tournamentId } ?: return@launch
            val updated = match.copy(
                resultSubmitted = true,
                resultScreenshotUrl = resultScrenshot,
                killScreenshotUrl = killScreenshot,
                status = "RESULT_PENDING" // Move to pending approval
            )
            repository.updateTournament(updated)
            repository.addNotification(
                "Result Submitted",
                "Your gameplay screenshots have been submitted for ${match.title}. Verification is in progress.",
                "TOURNAMENT"
            )
        }
    }

    // Team Actions
    fun createTeam(name: String, captain: String, rusher: String, sniper: String, support: String) {
        viewModelScope.launch {
            val newTeam = Team(
                name = name,
                captain = captain,
                viceCaptain = support,
                igl = captain,
                rusher = rusher,
                sniper = sniper,
                support = support
            )
            repository.createTeam(newTeam)
            
            // Update profile team name automatically
            val profile = userProfile.value
            if (profile != null) {
                repository.updateUserProfile(profile.copy(teamName = name))
            }
            repository.addNotification("Team Created", "Registered squad '$name' successfully.", "ANNOUNCEMENT")
        }
    }

    // Wallet Actions
    fun depositFunds(amount: Double, upiId: String) {
        viewModelScope.launch {
            val profile = userProfile.value ?: return@launch
            val newBalance = profile.walletBalance + amount
            repository.updateUserProfile(profile.copy(walletBalance = newBalance))
            repository.addTransaction(
                Transaction(
                    type = "DEPOSIT",
                    amount = amount,
                    status = "COMPLETED",
                    description = "UPI deposit from $upiId"
                )
            )
            repository.addNotification("Deposit Credited", "$$amount added to your wallet successfully.", "WALLET")
        }
    }

    fun withdrawFunds(amount: Double, upiId: String) {
        viewModelScope.launch {
            val profile = userProfile.value ?: return@launch
            if (profile.walletBalance < amount) {
                repository.addNotification("Withdrawal Failed", "Insufficient funds to withdraw $$amount.", "WALLET")
                return@launch
            }
            val newBalance = profile.walletBalance - amount
            repository.updateUserProfile(profile.copy(walletBalance = newBalance))
            repository.addTransaction(
                Transaction(
                    type = "WITHDRAW",
                    amount = -amount,
                    status = "PENDING",
                    description = "UPI payout requested to $upiId"
                )
            )
            repository.addNotification("Withdrawal Requested", "$$amount payout requested. Processing within 1 hour.", "WALLET")
        }
    }

    // Reward Actions
    fun claimDailyReward() {
        viewModelScope.launch {
            val profile = userProfile.value ?: return@launch
            val reward = 10.0
            val updated = profile.copy(
                walletBalance = profile.walletBalance + reward,
                dailyRewardClaimedDate = System.currentTimeMillis()
            )
            repository.updateUserProfile(updated)
            repository.addTransaction(
                Transaction(
                    type = "REWARD",
                    amount = reward,
                    status = "COMPLETED",
                    description = "Daily Login Reward"
                )
            )
            repository.addNotification("Reward Claimed!", "+$10.0 credited for Daily Login.", "WALLET")
        }
    }

    fun claimWeeklyReward() {
        viewModelScope.launch {
            val profile = userProfile.value ?: return@launch
            val reward = 50.0
            val updated = profile.copy(
                walletBalance = profile.walletBalance + reward,
                weeklyRewardClaimedDate = System.currentTimeMillis()
            )
            repository.updateUserProfile(updated)
            repository.addTransaction(
                Transaction(
                    type = "REWARD",
                    amount = reward,
                    status = "COMPLETED",
                    description = "Weekly Esports Loyalty Reward"
                )
            )
            repository.addNotification("Reward Claimed!", "+$50.0 credited for Weekly Esports loyalty.", "WALLET")
        }
    }

    fun claimMonthlyReward() {
        viewModelScope.launch {
            val profile = userProfile.value ?: return@launch
            val reward = 200.0
            val updated = profile.copy(
                walletBalance = profile.walletBalance + reward,
                monthlyRewardClaimedDate = System.currentTimeMillis()
            )
            repository.updateUserProfile(updated)
            repository.addTransaction(
                Transaction(
                    type = "REWARD",
                    amount = reward,
                    status = "COMPLETED",
                    description = "Monthly Grand Champion Reward"
                )
            )
            repository.addNotification("Grand Reward Claimed!", "+$200.0 credited for Monthly Grand Champion Reward.", "WALLET")
        }
    }

    // Daily Missions Progress
    fun claimMissionReward(mission: DailyMission) {
        viewModelScope.launch {
            val profile = userProfile.value ?: return@launch
            val newBalance = profile.walletBalance + mission.rewardCoins
            repository.updateUserProfile(profile.copy(walletBalance = newBalance))
            
            val updatedMission = mission.copy(isCompleted = true, progress = mission.target)
            repository.updateMission(updatedMission)

            repository.addTransaction(
                Transaction(
                    type = "REWARD",
                    amount = mission.rewardCoins,
                    status = "COMPLETED",
                    description = "Daily Mission Reward: ${mission.title}"
                )
            )
            repository.addNotification("Mission Reward Claimed", "+$${mission.rewardCoins} added for completing '${mission.title}'", "WALLET")
        }
    }

    // Referral System
    fun applyReferralCode(code: String) {
        viewModelScope.launch {
            val profile = userProfile.value ?: return@launch
            if (profile.referredBy.isNotEmpty()) {
                repository.addNotification("Referral Error", "You have already claimed a referral bonus.", "WALLET")
                return@launch
            }
            if (code == profile.referralCode) {
                repository.addNotification("Referral Error", "Cannot refer yourself!", "WALLET")
                return@launch
            }
            
            // Credit $100 referral reward
            val bonus = 100.0
            val updated = profile.copy(
                walletBalance = profile.walletBalance + bonus,
                referredBy = code
            )
            repository.updateUserProfile(updated)
            
            repository.addTransaction(
                Transaction(
                    type = "REFERRAL",
                    amount = bonus,
                    status = "COMPLETED",
                    description = "Referral Bonus claimed from code $code"
                )
            )
            repository.addNotification("Referral Bonus Applied!", "+$$bonus added to your wallet.", "WALLET")
        }
    }

    // Chat Actions
    fun sendMessage(text: String, type: String) {
        viewModelScope.launch {
            val profile = userProfile.value
            val sender = profile?.ign ?: "Player"
            repository.sendChatMessage(sender, text, type)
        }
    }

    // Support Actions
    fun submitTicket(title: String, description: String, category: String) {
        viewModelScope.launch {
            repository.createTicket(title, description, category)
            repository.addNotification("Support Ticket Logged", "Ticket '$title' created. Admin will reply soon.", "ANNOUNCEMENT")
        }
    }

    // Settings Configuration
    fun setDarkTheme(enabled: Boolean) {
        _isDarkTheme.value = enabled
    }

    fun setLanguage(language: String) {
        _currentLanguage.value = language
    }

    fun setNotificationEnabled(enabled: Boolean) {
        _notificationEnabled.value = enabled
    }

    // ==========================================
    // ADMIN PANEL OPERATIONS
    // ==========================================
    fun adminCreateTournament(
        title: String,
        entryFee: Double,
        prizePool: Double,
        map: String,
        matchTime: String,
        slots: Int,
        rules: String,
        gameMode: String
    ) {
        viewModelScope.launch {
            val newTournament = Tournament(
                title = title,
                bannerUrl = "esports_generic_banner",
                entryFee = entryFee,
                prizePool = prizePool,
                map = map,
                matchTime = matchTime,
                registrationDeadline = "1 hour before match",
                totalSlots = slots,
                availableSlots = slots,
                rules = rules,
                status = "UPCOMING",
                gameMode = gameMode
            )
            repository.insertTournament(newTournament)
            repository.addNotification("New Tournament Live", "'$title' has been created by Administrator.", "TOURNAMENT")
        }
    }

    fun adminPublishRoomInfo(tournamentId: Long, roomId: String, roomPass: String) {
        viewModelScope.launch {
            val tournamentsList = allTournaments.value
            val match = tournamentsList.firstOrNull { it.id == tournamentId } ?: return@launch
            val updated = match.copy(
                roomId = roomId,
                roomPassword = roomPass,
                roomReleaseTime = System.currentTimeMillis() // immediate release
            )
            repository.updateTournament(updated)
            repository.addNotification("Room Released", "Room ID and Password are now available for ${match.title}.", "ROOM")
        }
    }

    fun adminApproveResultAndAward(tournamentId: Long, firstTeam: String, secondTeam: String, thirdTeam: String) {
        viewModelScope.launch {
            val tournamentsList = allTournaments.value
            val match = tournamentsList.firstOrNull { it.id == tournamentId } ?: return@launch
            
            // 1. Mark match as completed
            val updated = match.copy(status = "COMPLETED")
            repository.updateTournament(updated)

            // 2. Generate custom leaderboard entries for this completed tournament
            val entries = listOf(
                LeaderboardEntry(tournamentId = tournamentId, rank = 1, teamName = firstTeam, kills = 18, placement = 1, totalPoints = 30), // 12 + 18 = 30
                LeaderboardEntry(tournamentId = tournamentId, rank = 2, teamName = secondTeam, kills = 11, placement = 2, totalPoints = 20), // 9 + 11 = 20
                LeaderboardEntry(tournamentId = tournamentId, rank = 3, teamName = thirdTeam, kills = 7, placement = 3, totalPoints = 15)   // 8 + 7 = 15
            )
            repository.generateLeaderboardForTournament(tournamentId, entries)

            // 3. If the user's team won, credit their wallet with prize pool!
            val profile = userProfile.value
            if (profile != null) {
                if (profile.teamName == firstTeam) {
                    val prize = match.prizePool * 0.6 // 60% of prize pool for 1st
                    val newBalance = profile.walletBalance + prize
                    repository.updateUserProfile(profile.copy(walletBalance = newBalance))
                    repository.addTransaction(
                        Transaction(
                            type = "WINNING",
                            amount = prize,
                            status = "COMPLETED",
                            description = "1st Place Winner - ${match.title}"
                        )
                    )
                    repository.addNotification("VICTORY BONUS!", "Your team '$firstTeam' won 1st Place! $$prize credited to wallet.", "WALLET")
                } else if (profile.teamName == secondTeam) {
                    val prize = match.prizePool * 0.3 // 30% of prize pool for 2nd
                    val newBalance = profile.walletBalance + prize
                    repository.updateUserProfile(profile.copy(walletBalance = newBalance))
                    repository.addTransaction(
                        Transaction(
                            type = "WINNING",
                            amount = prize,
                            status = "COMPLETED",
                            description = "2nd Place Winner - ${match.title}"
                        )
                    )
                    repository.addNotification("WINNINGS CREDITED", "Your team '$secondTeam' won 2nd Place! $$prize credited to wallet.", "WALLET")
                }
            }
            
            repository.addNotification(
                "Match Results Certified",
                "Results for ${match.title} have been verified. Winners rewarded.",
                "ANNOUNCEMENT"
            )
        }
    }

    fun adminDeleteTournament(tournamentId: Long) {
        viewModelScope.launch {
            repository.deleteTournament(tournamentId)
            repository.addNotification("Tournament Deleted", "An upcoming tournament was removed by administrator.", "ANNOUNCEMENT")
        }
    }
}
