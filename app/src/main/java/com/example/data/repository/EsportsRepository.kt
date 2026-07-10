package com.example.data.repository

import com.example.data.local.EsportsDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*

class EsportsRepository(private val esportsDao: EsportsDao) {

    // User Profile
    val userProfile: Flow<UserProfile?> = esportsDao.getUserProfile().map { it.firstOrNull() }
    
    suspend fun insertUserProfile(profile: UserProfile) {
        esportsDao.insertUserProfile(profile)
    }

    suspend fun updateUserProfile(profile: UserProfile) {
        esportsDao.updateUserProfile(profile)
    }

    // Tournaments
    val allTournaments: Flow<List<Tournament>> = esportsDao.getAllTournaments()
    
    fun getTournamentById(id: Long): Flow<Tournament?> = esportsDao.getTournamentById(id).map { it.firstOrNull() }

    suspend fun insertTournament(tournament: Tournament): Long {
        return esportsDao.insertTournament(tournament)
    }

    suspend fun updateTournament(tournament: Tournament) {
        esportsDao.updateTournament(tournament)
    }

    suspend fun deleteTournament(id: Long) {
        esportsDao.deleteTournamentById(id)
    }

    // Teams
    val allTeams: Flow<List<Team>> = esportsDao.getAllTeams()

    suspend fun createTeam(team: Team): Long {
        return esportsDao.insertTeam(team)
    }

    suspend fun deleteTeam(id: Long) {
        esportsDao.deleteTeamById(id)
    }

    // Leaderboard
    fun getLeaderboardByTournamentId(tournamentId: Long): Flow<List<LeaderboardEntry>> {
        return esportsDao.getLeaderboardByTournamentId(tournamentId)
    }

    suspend fun insertLeaderboardEntry(entry: LeaderboardEntry) {
        esportsDao.insertLeaderboardEntry(entry)
    }

    suspend fun generateLeaderboardForTournament(tournamentId: Long, entries: List<LeaderboardEntry>) {
        esportsDao.deleteLeaderboardForTournament(tournamentId)
        esportsDao.insertLeaderboardEntries(entries)
    }

    // Transactions
    val allTransactions: Flow<List<Transaction>> = esportsDao.getAllTransactions()

    suspend fun addTransaction(transaction: Transaction) {
        esportsDao.insertTransaction(transaction)
    }

    // Daily Missions
    val allMissions: Flow<List<DailyMission>> = esportsDao.getAllMissions()

    suspend fun updateMission(mission: DailyMission) {
        esportsDao.updateMission(mission)
    }

    // Chat Messages
    fun getMessagesByType(chatType: String): Flow<List<ChatMessage>> {
        return esportsDao.getMessagesByType(chatType)
    }

    suspend fun sendChatMessage(senderName: String, messageText: String, chatType: String) {
        val chatMessage = ChatMessage(
            senderName = senderName,
            message = messageText,
            timestamp = System.currentTimeMillis(),
            chatType = chatType
        )
        esportsDao.insertMessage(chatMessage)
    }

    // Notifications
    val allNotifications: Flow<List<Notification>> = esportsDao.getAllNotifications()

    suspend fun addNotification(title: String, message: String, type: String) {
        esportsDao.insertNotification(
            Notification(
                title = title,
                message = message,
                timestamp = System.currentTimeMillis(),
                isRead = false,
                type = type
            )
        )
    }

    suspend fun markNotificationsRead() {
        esportsDao.markAllNotificationsAsRead()
    }

    // Support Tickets
    val allTickets: Flow<List<SupportTicket>> = esportsDao.getAllTickets()

    suspend fun createTicket(title: String, description: String, category: String) {
        esportsDao.insertTicket(
            SupportTicket(
                title = title,
                description = description,
                category = category,
                status = "OPEN",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    // Prepopulate DB
    suspend fun prepopulateIfEmpty() {
        val existingProfile = esportsDao.getUserProfile().firstOrNull()
        if (existingProfile == null) {
            // 1. User profile
            esportsDao.insertUserProfile(
                UserProfile(
                    uid = "tr_user_1",
                    email = "player@tresports.com",
                    ign = "TR_Alpha",
                    ffUid = "482019483",
                    level = 68,
                    country = "India",
                    device = "ASUS ROG Phone 8 Pro",
                    teamName = "Team TR Esports",
                    bio = "Free Fire Competitive Rusher. IGL for Team TR Esports. Let's dominate!",
                    matchesPlayed = 340,
                    wins = 112,
                    kills = 1489,
                    headshots = 456,
                    mvpCount = 42,
                    walletBalance = 250.0,
                    referralCode = "TRE9283",
                    referredBy = "",
                    dailyRewardClaimedDate = 0,
                    weeklyRewardClaimedDate = 0,
                    monthlyRewardClaimedDate = 0
                )
            )

            // 2. Default Teams
            val team1Id = esportsDao.insertTeam(
                Team(
                    name = "Team TR Esports",
                    captain = "TR_Alpha",
                    viceCaptain = "TR_Omega",
                    igl = "TR_Alpha",
                    rusher = "TR_Beast",
                    sniper = "TR_Deadeye",
                    support = "TR_Omega",
                    logoUrl = "https://example.com/logo1.png"
                )
            )
            esportsDao.insertTeam(
                Team(
                    name = "Viper Gaming",
                    captain = "Viper_Leader",
                    viceCaptain = "Viper_Rusher",
                    igl = "Viper_Leader",
                    rusher = "Viper_Axe",
                    sniper = "Viper_Silent",
                    support = "Viper_Medic"
                )
            )
            esportsDao.insertTeam(
                Team(
                    name = "GodLike FF",
                    captain = "GL_Mortal",
                    viceCaptain = "GL_Nova",
                    igl = "GL_Mortal",
                    rusher = "GL_Clutch",
                    sniper = "GL_Scope",
                    support = "GL_Nova"
                )
            )

            // 3. Tournaments
            val t1Id = esportsDao.insertTournament(
                Tournament(
                    title = "FREE FIRE CHAMPIONS LEAGUE",
                    bannerUrl = "esports_champions_league", // mapped in UI or fetched
                    entryFee = 20.0,
                    prizePool = 5000.0,
                    map = "Bermuda",
                    matchTime = "12 July 2026, 06:00 PM",
                    registrationDeadline = "11 July 2026, 11:59 PM",
                    totalSlots = 48,
                    availableSlots = 14,
                    rules = "1. Emulator players are strictly not allowed.\n2. Hacks, scripts, or configuration files result in a permanent ban.\n3. Every squad member must have matching team tags.\n4. Screen recording is recommended in case of disputes.\n5. Points are calculated using standard Free Fire Esports point rules (Each kill = 1 pt, 1st place = 12 pts).",
                    status = "UPCOMING",
                    gameMode = "Squad",
                    roomId = "",
                    roomPassword = "",
                    roomReleaseTime = 0
                )
            )

            val t2Id = esportsDao.insertTournament(
                Tournament(
                    title = "TR ULTIMATE SQUAD SHOWDOWN",
                    bannerUrl = "squad_showdown",
                    entryFee = 5.0,
                    prizePool = 1000.0,
                    map = "Purgatory",
                    matchTime = "10 July 2026, 08:30 PM",
                    registrationDeadline = "10 July 2026, 06:00 PM",
                    totalSlots = 48,
                    availableSlots = 24,
                    rules = "1. Standard squad competition.\n2. Standard point system applies.\n3. Matches start exactly at scheduled time.\n4. Join room at least 10 minutes prior.",
                    status = "UPCOMING",
                    gameMode = "Squad"
                )
            )

            val t3Id = esportsDao.insertTournament(
                Tournament(
                    title = "CLASH SQUAD RUSH: PRO SERIES",
                    bannerUrl = "clash_squad_rush",
                    entryFee = 2.0,
                    prizePool = 500.0,
                    map = "Kalahari",
                    matchTime = "09 July 2026, 09:00 PM", // Live / scheduled around now
                    registrationDeadline = "09 July 2026, 08:00 PM",
                    totalSlots = 16,
                    availableSlots = 0,
                    rules = "1. Clash Squad Mode.\n2. No double sniper, no grenade spamming.\n3. Best of 7 rounds.\n4. Screenshots required to prove win.",
                    status = "LIVE",
                    gameMode = "Clash Squad",
                    roomId = "72940291",
                    roomPassword = "tresportsgaming",
                    roomReleaseTime = System.currentTimeMillis() - 600000 // Released 10 mins ago
                )
            )

            val t4Id = esportsDao.insertTournament(
                Tournament(
                    title = "BERMUDA SOLDIER SOLO RUSH",
                    bannerUrl = "solo_rush",
                    entryFee = 0.0, // FREE
                    prizePool = 300.0,
                    map = "Bermuda",
                    matchTime = "14 July 2026, 04:00 PM",
                    registrationDeadline = "14 July 2026, 02:00 PM",
                    totalSlots = 50,
                    availableSlots = 42,
                    rules = "1. Solo tournament.\n2. Survival points carry heavy weight.\n3. Teaming up will lead to disqualification.",
                    status = "UPCOMING",
                    gameMode = "Solo"
                )
            )

            val t5Id = esportsDao.insertTournament(
                Tournament(
                    title = "DUO SURVIVAL MAESTROS",
                    bannerUrl = "duo_survival",
                    entryFee = 10.0,
                    prizePool = 1500.0,
                    map = "Alpine",
                    matchTime = "08 July 2026, 05:00 PM",
                    registrationDeadline = "08 July 2026, 04:00 PM",
                    totalSlots = 25,
                    availableSlots = 0,
                    rules = "1. Duo survival competition.\n2. No third-party software allowed.",
                    status = "COMPLETED",
                    gameMode = "Duo"
                )
            )

            // 4. Leaderboard for completed Duo tournament (t5Id)
            esportsDao.insertLeaderboardEntries(
                listOf(
                    LeaderboardEntry(tournamentId = 5, rank = 1, teamName = "Team TR Esports", kills = 23, placement = 1, totalPoints = 35), // 12 + 23 = 35
                    LeaderboardEntry(tournamentId = 5, rank = 2, teamName = "Viper Gaming", kills = 12, placement = 2, totalPoints = 21),    // 9 + 12 = 21
                    LeaderboardEntry(tournamentId = 5, rank = 3, teamName = "GodLike FF", kills = 10, placement = 3, totalPoints = 18),      // 8 + 10 = 18
                    LeaderboardEntry(tournamentId = 5, rank = 4, teamName = "Velo Elite", kills = 9, placement = 4, totalPoints = 16),       // 7 + 9 = 16
                    LeaderboardEntry(tournamentId = 5, rank = 5, teamName = "Soul Clan", kills = 8, placement = 5, totalPoints = 14)          // 6 + 8 = 14
                )
            )

            // 5. Daily Missions
            esportsDao.insertMissions(
                listOf(
                    DailyMission(title = "War-Torn", description = "Complete 3 Esports Matches", rewardCoins = 10.0, progress = 1, target = 3),
                    DailyMission(title = "Apex Hunter", description = "Get 15 Kills in Squad Mode Tournaments", rewardCoins = 15.0, progress = 8, target = 15),
                    DailyMission(title = "Victory Cry", description = "Win 1 Clash Squad Tournament Match", rewardCoins = 5.0, progress = 0, target = 1),
                    DailyMission(title = "Sharpshooter", description = "Secure 5 Headshot Kills", rewardCoins = 8.0, progress = 3, target = 5)
                )
            )

            // 6. Transactions
            esportsDao.insertTransaction(Transaction(type = "REWARD", amount = 250.0, status = "COMPLETED", description = "New User Welcome Reward"))
            esportsDao.insertTransaction(Transaction(type = "DEPOSIT", amount = 10.0, status = "COMPLETED", description = "UPI Wallet Load"))
            esportsDao.insertTransaction(Transaction(type = "ENTRY_FEE", amount = -10.0, status = "COMPLETED", description = "Duo Survival Entry Fee"))

            // 7. Chat messages
            esportsDao.insertMessage(ChatMessage(senderName = "System", message = "Welcome to TR ESPORTS Global Chat!", chatType = "TOURNAMENT"))
            esportsDao.insertMessage(ChatMessage(senderName = "TR_Omega", message = "Yo! Who is joining the $5000 Champions League Squad tournament?", chatType = "TOURNAMENT"))
            esportsDao.insertMessage(ChatMessage(senderName = "Sniper_God", message = "I am a tier-1 sniper looking for a squad!", chatType = "TOURNAMENT"))
            esportsDao.insertMessage(ChatMessage(senderName = "TR_Alpha", message = "Welcome sniper, apply to Team TR Esports! We have 1 slot open.", chatType = "TOURNAMENT"))

            esportsDao.insertMessage(ChatMessage(senderName = "Support Bot", message = "Welcome to TR Support Help Center! How can we assist you today?", chatType = "SUPPORT"))
            esportsDao.insertMessage(ChatMessage(senderName = "TR_Alpha", message = "Hey, when will the $1500 Duo tournament rewards be distributed?", chatType = "SUPPORT"))
            esportsDao.insertMessage(ChatMessage(senderName = "Support Bot", message = "Winnings for Completed matches are verified and distributed within 1 hour after screen validation.", chatType = "SUPPORT"))

            esportsDao.insertMessage(ChatMessage(senderName = "Admin Broadcast", message = "[ALERT] Clash Squad Room ID & Password is now released. Join immediately!", chatType = "BROADCAST"))
            esportsDao.insertMessage(ChatMessage(senderName = "Admin Broadcast", message = "[EVENT] TR Champions League registrations close in 24 hours. Register your squad now!", chatType = "BROADCAST"))

            // 8. Notifications
            esportsDao.insertNotification(Notification(title = "Welcome Reward Added", message = "$250.0 is credited as a sign-up reward! Feel free to join paid tournaments.", type = "WALLET"))
            esportsDao.insertNotification(Notification(title = "New Tournament Open", message = "FREE FIRE CHAMPIONS LEAGUE Squad ($5000 prize pool) is open for registrations!", type = "TOURNAMENT"))
            esportsDao.insertNotification(Notification(title = "Match Room Released", message = "Clash Squad Rush: Pro Series Room ID & Password are now visible in tournament details.", type = "ROOM"))
        }
    }
}
