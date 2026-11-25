package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        StudioMessage::class,
        GameScoreRecord::class,
        FavoriteStill::class,
        UserCreation::class,
        StudioUser::class,
        PublishedGame::class,
        StudioWriting::class,
        SocialPost::class,
        ChatMessage::class,
        VoiceCallLog::class,
        PlatformSettings::class,
        WriteInDraft::class
    ],
    version = 8,
    exportSchema = false
)
abstract class StudioDatabase : RoomDatabase() {
    abstract fun studioDao(): StudioDao

    companion object {
        @Volatile
        private var INSTANCE: StudioDatabase? = null

        fun getDatabase(context: Context): StudioDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StudioDatabase::class.java,
                    "novahub_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
