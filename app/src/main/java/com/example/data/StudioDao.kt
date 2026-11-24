package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StudioDao {
    // Studio Messages ("A Way to Write In")
    @Query("SELECT * FROM studio_messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<StudioMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: StudioMessage): Long

    @Query("DELETE FROM studio_messages WHERE id = :id")
    suspend fun deleteMessageById(id: Long)

    // Game Scores
    @Query("SELECT * FROM game_scores WHERE gameId = :gameId ORDER BY score DESC LIMIT 10")
    fun getHighScores(gameId: String): Flow<List<GameScoreRecord>>

    @Query("SELECT MAX(score) FROM game_scores WHERE gameId = :gameId")
    fun getHighestScore(gameId: String): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameScore(score: GameScoreRecord): Long

    // Favorite Stills
    @Query("SELECT * FROM favorite_stills")
    fun getAllFavorites(): Flow<List<FavoriteStill>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteStill)

    @Query("DELETE FROM favorite_stills WHERE stillId = :stillId")
    suspend fun removeFavorite(stillId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_stills WHERE stillId = :stillId)")
    fun isFavorite(stillId: String): Flow<Boolean>

    // User-Generated Creations (Images & Videos)
    @Query("SELECT * FROM user_creations ORDER BY timestamp DESC")
    fun getAllCreations(): Flow<List<UserCreation>>

    @Query("SELECT * FROM user_creations WHERE type = :type ORDER BY timestamp DESC")
    fun getCreationsByType(type: String): Flow<List<UserCreation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCreation(creation: UserCreation): Long

    @Query("DELETE FROM user_creations WHERE id = :id")
    suspend fun deleteCreationById(id: Long)

    @Query("SELECT COUNT(*) FROM user_creations")
    fun getCreationCount(): Flow<Int>

    // Studio Users & Authentication
    @Query("SELECT * FROM studio_users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): StudioUser?

    @Query("SELECT * FROM studio_users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): StudioUser?

    @Query("SELECT * FROM studio_users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): StudioUser?

    @Query("SELECT * FROM studio_users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<StudioUser>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: StudioUser): Long

    @Query("DELETE FROM studio_users WHERE id = :id")
    suspend fun deleteUserById(id: Long)

    // User-Published Games
    @Query("SELECT * FROM published_games ORDER BY timestamp DESC")
    fun getAllPublishedGames(): Flow<List<PublishedGame>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPublishedGame(game: PublishedGame): Long

    @Query("DELETE FROM published_games WHERE id = :id")
    suspend fun deletePublishedGameById(id: Long)

    // Studio Writings & Essays Feature
    @Query("SELECT * FROM studio_writings ORDER BY isPinned DESC, timestamp DESC")
    fun getAllWritings(): Flow<List<StudioWriting>>

    @Query("SELECT * FROM studio_writings WHERE isDraft = 0 ORDER BY isPinned DESC, timestamp DESC")
    fun getPublishedWritings(): Flow<List<StudioWriting>>

    @Query("SELECT * FROM studio_writings WHERE id = :id")
    fun getWritingById(id: Long): Flow<StudioWriting?>

    @Query("SELECT * FROM studio_writings WHERE category = :category AND isDraft = 0 ORDER BY isPinned DESC, timestamp DESC")
    fun getWritingsByCategory(category: String): Flow<List<StudioWriting>>

    @Query("SELECT * FROM studio_writings WHERE (title LIKE '%' || :query || '%' OR summary LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%') ORDER BY timestamp DESC")
    fun searchWritings(query: String): Flow<List<StudioWriting>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWriting(writing: StudioWriting): Long

    @Update
    suspend fun updateWriting(writing: StudioWriting)

    @Query("DELETE FROM studio_writings WHERE id = :id")
    suspend fun deleteWritingById(id: Long)

    @Query("SELECT COUNT(*) FROM studio_writings")
    fun getWritingCount(): Flow<Int>

    // Social Posts (Shared Social Media Feed)
    @Query("SELECT * FROM social_posts WHERE isReported = 0 ORDER BY timestamp DESC")
    fun getActiveSocialPosts(): Flow<List<SocialPost>>

    @Query("SELECT * FROM social_posts ORDER BY timestamp DESC")
    fun getAllSocialPosts(): Flow<List<SocialPost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSocialPost(post: SocialPost): Long

    @Update
    suspend fun updateSocialPost(post: SocialPost)

    @Query("DELETE FROM social_posts WHERE id = :id")
    suspend fun deleteSocialPost(id: Long)

    @Query("UPDATE social_posts SET isReported = 1, moderationStatus = 'FLAGGED' WHERE id = :id")
    suspend fun reportSocialPost(id: Long)

    // Chat Messages
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllChatMessages(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessage): Long

    @Query("DELETE FROM chat_messages WHERE id = :id")
    suspend fun deleteChatMessage(id: Long)

    // Voice Call Logs
    @Query("SELECT * FROM voice_call_logs ORDER BY timestamp DESC")
    fun getAllCallLogs(): Flow<List<VoiceCallLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCallLog(log: VoiceCallLog): Long

    // Platform Settings
    @Query("SELECT * FROM platform_settings WHERE id = 1 LIMIT 1")
    fun getPlatformSettings(): Flow<PlatformSettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePlatformSettings(settings: PlatformSettings)

    // Write-In Draft Autosave
    @Query("SELECT * FROM write_in_drafts WHERE id = 1 LIMIT 1")
    fun getWriteInDraft(): Flow<WriteInDraft?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWriteInDraft(draft: WriteInDraft)

    @Query("DELETE FROM write_in_drafts WHERE id = 1")
    suspend fun clearWriteInDraft()
}
