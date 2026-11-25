package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "studio_messages")
data class StudioMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val senderName: String,
    val senderEmail: String,
    val category: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "game_scores")
data class GameScoreRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val gameId: String,
    val score: Int,
    val shardsCollected: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorite_stills")
data class FavoriteStill(
    @PrimaryKey
    val stillId: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_creations")
data class UserCreation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val type: String, // "IMAGE" or "VIDEO"
    val prompt: String,
    val modelTag: String, // "Gemini 2.5 Flash", "Veo 3.1", "Midjourney v6.1", "User Upload"
    val imageResId: Int = 0,
    val videoDuration: String = "", // e.g. "0:24", "0:15"
    val resolution: String = "4K HDR",
    val aspectRatio: String = "16:9",
    val generatedOutput: String = "",
    val mediaUri: String = "", // Uri string for user-picked image or video
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "published_games")
data class PublishedGame(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val gameId: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val webGameUrl: String,
    val techStack: String = "HTML5, Canvas, JavaScript", // Comma separated
    val authorName: String = "Community Creator",
    val imageResId: Int = 0,
    val imageUri: String = "",
    val genre: String = "Arcade",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "studio_users")
data class StudioUser(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val email: String,
    val passwordHash: String, // securely stored locally
    val roleTitle: String = "Creator",
    val bio: String = "NovaHub Creator & Explorer",
    val avatarInitials: String = "NH",
    val handle: String = "@novahub",
    val avatarUrl: String = "",
    val accentTheme: String = "GOLD",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "studio_writings")
data class StudioWriting(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val slug: String = "",
    val category: String, // "Game Design", "Nocturne & Stills", "Creative Code", "Field Notes", "Essays"
    val summary: String,
    val content: String,
    val readTimeMinutes: Int = 3,
    val author: String = "NovaHub Editorial",
    val tags: String = "", // Comma-separated: "Physics, Canvas, Game Loops"
    val isPinned: Boolean = false,
    val isDraft: Boolean = false,
    val dateFormatted: String = "Sep 2026",
    val timestamp: Long = System.currentTimeMillis()
)

// Modern Social Media Platform Entities
@Entity(tableName = "social_posts")
data class SocialPost(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val authorName: String = "NovaHub Member",
    val authorRole: String = "Creator",
    val authorAvatarInitials: String = "NH",
    val textContent: String = "",
    val mediaType: String = "NONE", // "NONE", "IMAGE", "VIDEO"
    val mediaUri: String = "",
    val mediaThumbnail: Int = 0,
    val likesCount: Int = 0,
    val sharesCount: Int = 0,
    val isLikedByUser: Boolean = false,
    val isReported: Boolean = false,
    val moderationStatus: String = "CLEAN", // "CLEAN", "FLAGGED", "UNDER_REVIEW"
    val timeAgo: String = "Just now",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val senderName: String,
    val senderAvatar: String = "NH",
    val receiverName: String = "Public Square",
    val messageText: String,
    val mediaUri: String = "",
    val isFromCurrentUser: Boolean = true,
    val isFlagged: Boolean = false,
    val timeFormatted: String = "12:00 PM",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "voice_call_logs")
data class VoiceCallLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val peerName: String,
    val peerAvatar: String = "NH",
    val callDurationSeconds: Int = 0,
    val callType: String = "VOICE", // "VOICE", "VIDEO"
    val status: String = "COMPLETED", // "MISSED", "COMPLETED", "ACTIVE"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "platform_settings")
data class PlatformSettings(
    @PrimaryKey
    val id: Int = 1,
    val siteName: String = "NovaHub Public Community",
    val darkModeEnabled: Boolean = true,
    val autoPlayMedia: Boolean = false,
    val safeContentFilter: Boolean = true,
    val allowDirectCalls: Boolean = true,
    val allowPublicGameUploads: Boolean = true,
    val soundEffectsEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val accentTheme: String = "GOLD"
)

@Entity(tableName = "write_in_drafts")
data class WriteInDraft(
    @PrimaryKey
    val id: Int = 1,
    val senderName: String = "",
    val senderEmail: String = "",
    val category: String = "Collaboration",
    val messageText: String = "",
    val wordCount: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
)

