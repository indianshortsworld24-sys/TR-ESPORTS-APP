package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.*

@Database(
    entities = [
        UserProfile::class,
        Tournament::class,
        Team::class,
        LeaderboardEntry::class,
        Transaction::class,
        DailyMission::class,
        ChatMessage::class,
        Notification::class,
        SupportTicket::class
    ],
    version = 1,
    exportSchema = false
)
abstract class EsportsDatabase : RoomDatabase() {

    abstract fun esportsDao(): EsportsDao

    companion object {
        @Volatile
        private var INSTANCE: EsportsDatabase? = null

        fun getDatabase(context: Context): EsportsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EsportsDatabase::class.java,
                    "esports_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
