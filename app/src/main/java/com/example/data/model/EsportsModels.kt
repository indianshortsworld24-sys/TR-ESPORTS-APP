package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val uid: String = "tr_user_1",
    val email: String = "player@tresports.com",
    val ign: String = "TR_Alpha",
    val ffUid: String = "482019483",
    val level: Int = 68,
    val country: String = "India",
    val device: String = "ASUS ROG Phone 8 Pro",
    val teamName: String = "Team TR Esports",
    val bio: String = "Free Fire Competitive Rusher. IGL for Team TR Esports. Let's dominate!",
    val matchesPlayed: Int = 340,
    val wins: Int = 112,
    val kills: Int = 1489,
    val headshots: Int = 456,
    val mvpCount: Int = 42,
    val walletBalance: Double = 250.0,
    val referralCode: String = "TRE9283",
    val referredBy: String = "",
    val dailyRewardClaimedDate: Long = 0,
    val weeklyRewardClaimedDate: Long = 0,
    val monthlyRewardClaimedDate: Long = 0
) : Serializable

@Entity(tableName = "tournaments")
data class Tournament(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val bannerUrl: String,
    val entryFee: Double,
    val prizePool: Double,
    val map: String,
    val matchTime: String,
    val registrationDeadline: String,
    val totalSlots: Int,
    val availableSlots: Int,
    val rules: String,
    val status: String, // UPCOMING, LIVE, RESULT_PENDING, COMPLETED
    val gameMode: String, // Solo, Duo, Squad, Clash Squad
    val roomId: String = "",
    val roomPassword: String = "",
    val roomReleaseTime: Long = 0, // epoch millis
    val isRegistered: Boolean = false,
    val joinedTeamName: String = "",
    val resultScreenshotUrl: String = "",
    val killScreenshotUrl: String = "",
    val resultSubmitted: Boolean = false
) : Serializable

@Entity(tableName = "teams")
data class Team(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val captain: String,
    val viceCaptain: String,
    val igl: String,
    val rusher: String,
    val sniper: String,
    val support: String,
    val logoUrl: String = ""
) : Serializable

@Entity(tableName = "leaderboard")
data class LeaderboardEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tournamentId: Long,
    val rank: Int,
    val teamName: String,
    val kills: Int,
    val placement: Int,
    val totalPoints: Int
) : Serializable

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // DEPOSIT, WITHDRAW, WINNING, ENTRY_FEE, REFERRAL, REWARD
    val amount: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String, // COMPLETED, PENDING
    val description: String
) : Serializable

@Entity(tableName = "daily_missions")
data class DailyMission(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val rewardCoins: Double,
    val progress: Int,
    val target: Int,
    val isCompleted: Boolean = false
) : Serializable

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val senderName: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val chatType: String // TOURNAMENT, SUPPORT, BROADCAST
) : Serializable

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val type: String // TOURNAMENT, ROOM, WALLET, ANNOUNCEMENT
) : Serializable

@Entity(tableName = "support_tickets")
data class SupportTicket(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val category: String, // WALLET, GAMEPLAY, BUG, OTHER
    val status: String, // OPEN, RESOLVED
    val timestamp: Long = System.currentTimeMillis()
) : Serializable
