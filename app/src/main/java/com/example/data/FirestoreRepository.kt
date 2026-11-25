package com.example.data

import android.content.Context
import android.util.Log
import androidx.annotation.Keep
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.json.JSONObject

enum class OperationType {
    CREATE, UPDATE, DELETE, LIST, GET, WRITE
}

@Keep
@IgnoreExtraProperties
data class FirestoreGalleryImage(
    @DocumentId val id: String = "",
    val userId: String = "",
    val authorName: String = "NovaHub Creator",
    val title: String = "",
    val imageUrl: String = "",
    val category: String = "Urban Night",
    val description: String = "",
    val visibility: String = "PUBLIC",
    val likesCount: Long = 0L,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class FirestorePublicProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "",
    val handle: String = "",
    val roleTitle: String = "Creator",
    val bio: String = "",
    val avatarInitials: String = "NH",
    val avatarUrl: String = "",
    val accentTheme: String = "GOLD",
    val highScoreTotal: Long = 0L,
    val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class FirestoreArcadeScore(
    @DocumentId val id: String = "",
    val userId: String = "",
    val playerName: String = "",
    val avatarInitials: String = "NH",
    val gameId: String = "",
    val gameTitle: String = "",
    val score: Long = 0L,
    val createdAt: Timestamp? = null
)

/**
 * Curated fallback remote URLs shown when visitors have not signed in yet, or before Firestore sync.
 */
fun getCuratedRemoteGallerySamples(): List<FirestoreGalleryImage> = listOf(
    FirestoreGalleryImage(
        id = "curated_remote_1",
        userId = "novahub_curated",
        authorName = "NovaHub Curated",
        title = "Cyberpunk Neon Rain Blvd",
        imageUrl = "https://images.unsplash.com/photo-1519608487953-e999c86e7455?auto=format&fit=crop&w=900&q=80",
        category = "Urban Night",
        description = "Rain-soaked asphalt reflecting glowing neon signage along midnight city avenues.",
        visibility = "PUBLIC",
        likesCount = 48
    ),
    FirestoreGalleryImage(
        id = "curated_remote_2",
        userId = "novahub_curated",
        authorName = "NovaHub Curated",
        title = "Golden Hour Mountain Mist",
        imageUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=900&q=80",
        category = "Golden Dawn",
        description = "First light cutting through high-altitude pine valleys and emerald reflections.",
        visibility = "PUBLIC",
        likesCount = 64
    ),
    FirestoreGalleryImage(
        id = "curated_remote_3",
        userId = "novahub_curated",
        authorName = "NovaHub Curated",
        title = "Brutalist Concrete Skylines",
        imageUrl = "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?auto=format&fit=crop&w=900&q=80",
        category = "Brutalist",
        description = "Architectural geometry and stark glass facades ascending toward crisp evening light.",
        visibility = "PUBLIC",
        likesCount = 39
    ),
    FirestoreGalleryImage(
        id = "curated_remote_4",
        userId = "novahub_curated",
        authorName = "NovaHub Curated",
        title = "Obsidian Synthwave Horizon",
        imageUrl = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?auto=format&fit=crop&w=900&q=80",
        category = "Arcade",
        description = "Retro arcade hardware illuminated by warm amber and cyan workstation glow.",
        visibility = "PUBLIC",
        likesCount = 72
    ),
    FirestoreGalleryImage(
        id = "curated_remote_5",
        userId = "novahub_curated",
        authorName = "NovaHub Curated",
        title = "River Delta Twilight Mirror",
        imageUrl = "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=900&q=80",
        category = "Golden Dawn",
        description = "Wide open open-road horizon captured under amber dusk chromatic dispersion.",
        visibility = "PUBLIC",
        likesCount = 31
    ),
    FirestoreGalleryImage(
        id = "curated_remote_6",
        userId = "novahub_curated",
        authorName = "NovaHub Curated",
        title = "Nocturnal Street Cinema",
        imageUrl = "https://images.unsplash.com/photo-1514565131-fce0801e5785?auto=format&fit=crop&w=900&q=80",
        category = "Urban Night",
        description = "Long-exposure metropolitan light trails cutting across shadowed avenues.",
        visibility = "PUBLIC",
        likesCount = 55
    )
)

class FirestoreRepository(context: Context) {

    private val appContext = context.applicationContext
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(
        appContext.getString(R.string.firestore_database_id)
    )

    companion object {
        private const val TAG = "FirestoreRepository"
        const val COLLECTION_USERS = "users"
        const val COLLECTION_PUBLIC_PROFILES = "public_profiles"
        const val COLLECTION_GALLERY_IMAGES = "gallery_images"
        const val COLLECTION_ARCADE_SCORES = "arcade_scores"
    }

    private fun handleFirestoreError(
        error: Exception,
        operationType: OperationType,
        path: String
    ): Nothing {
        val currentUser = Firebase.auth.currentUser
        val payload = JSONObject().apply {
            put("error", error.message ?: "Unknown Firestore error")
            put("operationType", operationType.name)
            put("path", path)
            put("authInfo", JSONObject().apply {
                put("userId", currentUser?.uid ?: "")
                put("email", currentUser?.email ?: "")
                put("isAuthenticated", currentUser != null)
            })
        }
        Log.e(TAG, "Firestore Error: $payload", error)
        throw error
    }

    fun observePublicGalleryImages(): Flow<Result<List<FirestoreGalleryImage>>> = callbackFlow {
        val uid = Firebase.auth.currentUser?.uid
        if (uid == null) {
            trySend(Result.success(emptyList()))
            awaitClose { }
            return@callbackFlow
        }

        val registration = db.collection(COLLECTION_GALLERY_IMAGES)
            .whereEqualTo("visibility", "PUBLIC")
            .limit(60)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "observePublicGalleryImages listener failed", error)
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(
                            FirestoreGalleryImage::class.java,
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        )
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed deserializing gallery doc ${doc.id}", e)
                        null
                    }
                }?.sortedByDescending { it.createdAt?.toDate()?.time ?: 0L } ?: emptyList()

                trySend(Result.success(list))
            }

        awaitClose { registration.remove() }
    }

    suspend fun seedInitialRemoteGalleryIfEmpty(): Result<Unit> {
        val user = Firebase.auth.currentUser ?: return Result.success(Unit)
        return try {
            val existing = db.collection(COLLECTION_GALLERY_IMAGES)
                .whereEqualTo("visibility", "PUBLIC")
                .limit(1)
                .get()
                .await()

            if (existing.isEmpty) {
                val samples = getCuratedRemoteGallerySamples().take(4)
                val batch = db.batch()
                samples.forEachIndexed { idx, sample ->
                    val docId = "seed_${user.uid.take(8)}_$idx"
                    val docRef = db.collection(COLLECTION_GALLERY_IMAGES).document(docId)
                    val data = mapOf(
                        "userId" to user.uid,
                        "authorName" to (user.displayName?.ifBlank { "NovaHub Explorer" } ?: "NovaHub Explorer"),
                        "title" to sample.title,
                        "imageUrl" to sample.imageUrl,
                        "category" to sample.category,
                        "description" to sample.description,
                        "visibility" to "PUBLIC",
                        "likesCount" to 0L,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    ).filterValues { it != null }
                    batch.set(docRef, data)
                }
                batch.commit().await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Remote gallery auto-seed skipped", e)
            Result.failure(e)
        }
    }

    suspend fun addRemoteGalleryImage(
        title: String,
        imageUrl: String,
        category: String,
        description: String,
        visibility: String = "PUBLIC"
    ): Result<String> {
        val user = Firebase.auth.currentUser
            ?: return Result.failure(IllegalStateException("Please sign in with Google to add remote gallery URLs."))
        val cleanUrl = imageUrl.trim()
        if (!cleanUrl.startsWith("https://") && !cleanUrl.startsWith("http://")) {
            return Result.failure(IllegalArgumentException("Image URL must start with https:// or http://"))
        }

        val docRef = db.collection(COLLECTION_GALLERY_IMAGES).document()
        val data = mapOf(
            "userId" to user.uid,
            "authorName" to (user.displayName?.ifBlank { "NovaHub Creator" } ?: "NovaHub Creator").take(60),
            "title" to title.trim().ifBlank { "Untitled Remote Still" }.take(100),
            "imageUrl" to cleanUrl.take(600),
            "category" to category.trim().ifBlank { "Urban Night" }.take(40),
            "description" to description.trim().take(300),
            "visibility" to if (visibility == "PRIVATE") "PRIVATE" else "PUBLIC",
            "likesCount" to 0L,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        ).filterValues { it != null }

        return try {
            docRef.set(data).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            try {
                handleFirestoreError(e, OperationType.CREATE, "$COLLECTION_GALLERY_IMAGES/${docRef.id}")
            } catch (logged: Exception) {
                Result.failure(logged)
            }
        }
    }

    suspend fun likeGalleryImage(image: FirestoreGalleryImage): Result<Unit> {
        Firebase.auth.currentUser
            ?: return Result.failure(IllegalStateException("Sign in with Google to like gallery items."))
        val docRef = db.collection(COLLECTION_GALLERY_IMAGES).document(image.id)
        return try {
            val nextLikes = (image.likesCount + 1L).coerceAtLeast(0L)
            docRef.update(
                mapOf(
                    "likesCount" to nextLikes,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            try {
                handleFirestoreError(e, OperationType.UPDATE, "$COLLECTION_GALLERY_IMAGES/${image.id}")
            } catch (logged: Exception) {
                Result.failure(logged)
            }
        }
    }

    suspend fun deleteGalleryImage(imageId: String): Result<Unit> {
        Firebase.auth.currentUser
            ?: return Result.failure(IllegalStateException("Sign in with Google to delete your image."))
        val docRef = db.collection(COLLECTION_GALLERY_IMAGES).document(imageId)
        return try {
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            try {
                handleFirestoreError(e, OperationType.DELETE, "$COLLECTION_GALLERY_IMAGES/$imageId")
            } catch (logged: Exception) {
                Result.failure(logged)
            }
        }
    }

    suspend fun syncUserProfile(
        displayName: String,
        handle: String,
        email: String,
        roleTitle: String,
        bio: String,
        avatarInitials: String,
        avatarUrl: String,
        accentTheme: String,
        highScoreTotal: Long
    ): Result<Unit> {
        val user = Firebase.auth.currentUser ?: return Result.success(Unit)
        val cleanName = displayName.trim().ifBlank { "NovaHub Explorer" }.take(60)
        val cleanInitials = avatarInitials.trim().ifBlank { "NH" }.take(4).uppercase()
        val cleanRole = roleTitle.trim().ifBlank { "Creator" }.take(40)
        val cleanTheme = if (accentTheme in listOf("GOLD", "CYAN", "EMERALD")) accentTheme else "GOLD"
        val cleanAvatarUrl = if (avatarUrl.startsWith("http://") || avatarUrl.startsWith("https://")) avatarUrl.take(500) else ""

        return try {
            val privateData = mapOf(
                "displayName" to cleanName,
                "handle" to handle.trim().take(40),
                "email" to (email.ifBlank { user.email ?: "" }).take(120),
                "roleTitle" to cleanRole,
                "bio" to bio.trim().take(240),
                "avatarInitials" to cleanInitials,
                "avatarUrl" to cleanAvatarUrl,
                "accentTheme" to cleanTheme,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            db.collection(COLLECTION_USERS)
                .document(user.uid)
                .set(privateData, SetOptions.merge())
                .await()

            val publicData = mapOf(
                "userId" to user.uid,
                "displayName" to cleanName,
                "handle" to handle.trim().take(40),
                "roleTitle" to cleanRole,
                "bio" to bio.trim().take(240),
                "avatarInitials" to cleanInitials,
                "avatarUrl" to cleanAvatarUrl,
                "accentTheme" to cleanTheme,
                "highScoreTotal" to highScoreTotal.coerceAtLeast(0L),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            db.collection(COLLECTION_PUBLIC_PROFILES)
                .document(user.uid)
                .set(publicData, SetOptions.merge())
                .await()

            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            try {
                handleFirestoreError(e, OperationType.WRITE, "$COLLECTION_USERS/${user.uid}")
            } catch (logged: Exception) {
                Result.failure(logged)
            }
        }
    }

    fun observePublicProfiles(): Flow<List<FirestorePublicProfile>> = callbackFlow {
        val uid = Firebase.auth.currentUser?.uid
        if (uid == null) {
            trySend(emptyList())
            awaitClose { }
            return@callbackFlow
        }

        val reg = db.collection(COLLECTION_PUBLIC_PROFILES)
            .limit(20)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "observePublicProfiles failed", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(
                            FirestorePublicProfile::class.java,
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        )
                    } catch (_: Exception) {
                        null
                    }
                }?.sortedByDescending { it.highScoreTotal } ?: emptyList()
                trySend(list)
            }
        awaitClose { reg.remove() }
    }

    suspend fun submitArcadeScore(
        playerName: String,
        avatarInitials: String,
        gameId: String,
        gameTitle: String,
        score: Int
    ): Result<Unit> {
        val user = Firebase.auth.currentUser ?: return Result.success(Unit)
        if (score < 0 || gameId !in listOf("void_runner", "cyber_breaker", "quantum_2048")) {
            return Result.success(Unit)
        }
        val docRef = db.collection(COLLECTION_ARCADE_SCORES).document()
        val data = mapOf(
            "userId" to user.uid,
            "playerName" to playerName.trim().ifBlank { "NovaHub Player" }.take(60),
            "avatarInitials" to avatarInitials.trim().ifBlank { "NH" }.take(4).uppercase(),
            "gameId" to gameId,
            "gameTitle" to gameTitle.take(60),
            "score" to score.toLong(),
            "createdAt" to FieldValue.serverTimestamp()
        )
        return try {
            docRef.set(data).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "submitArcadeScore skipped", e)
            Result.failure(e)
        }
    }

    fun observeLeaderboard(): Flow<List<FirestoreArcadeScore>> = callbackFlow {
        val uid = Firebase.auth.currentUser?.uid
        if (uid == null) {
            trySend(emptyList())
            awaitClose { }
            return@callbackFlow
        }

        val reg = db.collection(COLLECTION_ARCADE_SCORES)
            .limit(40)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "observeLeaderboard failed", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val scores = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(
                            FirestoreArcadeScore::class.java,
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        )
                    } catch (_: Exception) {
                        null
                    }
                }?.sortedByDescending { it.score }?.take(15) ?: emptyList()
                trySend(scores)
            }
        awaitClose { reg.remove() }
    }
}
