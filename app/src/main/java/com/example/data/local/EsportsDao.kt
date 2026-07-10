package com.example.data.local

import androidx.room.*
import com.example.data.model.UserProfile
import com.example.data.model.Tournament
import com.example.data.model.Team
import com.example.data.model.LeaderboardEntry
import com.example.data.model.Transaction
import com.example.data.model.DailyMission
import com.example.data.model.ChatMessage
import com.example.data.model.Notification
import com.example.data.model.SupportTicket
import kotlinx.coroutines.flow.Flow

@Dao
interface EsportsDao {

    // User Profile
    @Query("SELECT * FROM user_profile")
    fun getUserProfile(): Flow<List<UserProfile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(userProfile: UserProfile)

    @Update
    suspend fun updateUserProfile(userProfile: UserProfile)

    // Tournaments
    @Query("SELECT * FROM tournaments ORDER BY id DESC")
    fun getAllTournaments(): Flow<List<Tournament>>

    @Query("SELECT * FROM tournaments WHERE id = :id")
    fun getTournamentById(id: Long): Flow<List<Tournament>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTournament(tournament: Tournament): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTournaments(tournaments: List<Tournament>)

    @Update
    suspend fun updateTournament(tournament: Tournament)

    @Query("DELETE FROM tournaments WHERE id = :id")
    suspend fun deleteTournamentById(id: Long)

    // Teams
    @Query("SELECT * FROM teams ORDER BY id DESC")
    fun getAllTeams(): Flow<List<Team>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeam(team: Team): Long

    @Query("DELETE FROM teams WHERE id = :id")
    suspend fun deleteTeamById(id: Long)

    // Leaderboard
    @Query("SELECT * FROM leaderboard WHERE tournamentId = :tournamentId ORDER BY totalPoints DESC, kills DESC")
    fun getLeaderboardByTournamentId(tournamentId: Long): Flow<List<LeaderboardEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaderboardEntry(entry: LeaderboardEntry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaderboardEntries(entries: List<LeaderboardEntry>)

    @Query("DELETE FROM leaderboard WHERE tournamentId = :tournamentId")
    suspend fun deleteLeaderboardForTournament(tournamentId: Long)

    // Transactions
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    // Daily Missions
    @Query("SELECT * FROM daily_missions ORDER BY id ASC")
    fun getAllMissions(): Flow<List<DailyMission>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMissions(missions: List<DailyMission>)

    @Update
    suspend fun updateMission(mission: DailyMission)

    // Chat Messages
    @Query("SELECT * FROM chat_messages WHERE chatType = :chatType ORDER BY timestamp ASC")
    fun getMessagesByType(chatType: String): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    // Notifications
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<Notification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: Notification)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllNotificationsAsRead()

    // Support Tickets
    @Query("SELECT * FROM support_tickets ORDER BY timestamp DESC")
    fun getAllTickets(): Flow<List<SupportTicket>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: SupportTicket)
}
