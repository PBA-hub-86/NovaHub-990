package com.example.data

import com.example.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.security.MessageDigest

data class StudioGame(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val techStack: List<String>,
    val status: String,
    val isPlayableInApp: Boolean,
    val imageResId: Int,
    val accentColorHex: Long,
    val gameplayNote: String,
    val webGameUrl: String = "",
    val isWebGame: Boolean = true,
    val imageUri: String = "",
    val isUserPublished: Boolean = false,
    val dbId: Long = 0L
)

data class StudioStill(
    val id: String,
    val title: String,
    val location: String,
    val category: String,
    val focalDescription: String,
    val exif: String,
    val imageResId: Int,
    val year: String = "2025"
)

class StudioRepository(private val dao: StudioDao) {

    // Database access
    val allMessages: Flow<List<StudioMessage>> = dao.getAllMessages()
    val allFavorites: Flow<List<FavoriteStill>> = dao.getAllFavorites()
    val allCreations: Flow<List<UserCreation>> = dao.getAllCreations()
    val creationCount: Flow<Int> = dao.getCreationCount()

    fun getHighestScore(gameId: String): Flow<Int?> = dao.getHighestScore(gameId)
    fun getHighScores(gameId: String): Flow<List<GameScoreRecord>> = dao.getHighScores(gameId)
    fun getTotalArcadeScore(): Flow<Int?> = dao.getTotalArcadeScore()

    suspend fun saveMessage(message: StudioMessage): Long = dao.insertMessage(message)
    suspend fun deleteMessage(id: Long) = dao.deleteMessageById(id)

    suspend fun saveGameScore(score: GameScoreRecord): Long = dao.insertGameScore(score)

    suspend fun saveCreation(creation: UserCreation): Long = dao.insertCreation(creation)
    suspend fun deleteCreation(id: Long) = dao.deleteCreationById(id)

    // User-Published Games
    val allPublishedGames: Flow<List<PublishedGame>> = dao.getAllPublishedGames()

    suspend fun publishGame(game: PublishedGame): Long = dao.insertPublishedGame(game)
    suspend fun deletePublishedGame(id: Long) = dao.deletePublishedGameById(id)

    // Studio Writings & Essays Feature (Persistent Room Architecture)
    val allWritings: Flow<List<StudioWriting>> = dao.getAllWritings()
    val publishedWritings: Flow<List<StudioWriting>> = dao.getPublishedWritings()

    fun getWritingById(id: Long): Flow<StudioWriting?> = dao.getWritingById(id)
    fun getWritingsByCategory(category: String): Flow<List<StudioWriting>> = dao.getWritingsByCategory(category)
    fun searchWritings(query: String): Flow<List<StudioWriting>> = dao.searchWritings(query)

    suspend fun saveWriting(writing: StudioWriting): Long = dao.insertWriting(writing)
    suspend fun updateWriting(writing: StudioWriting) = dao.updateWriting(writing)
    suspend fun deleteWriting(id: Long) = dao.deleteWritingById(id)

    // Social Media Platform Methods
    val activeSocialPosts: Flow<List<SocialPost>> = dao.getActiveSocialPosts()
    val allSocialPosts: Flow<List<SocialPost>> = dao.getAllSocialPosts()
    val allChatMessages: Flow<List<ChatMessage>> = dao.getAllChatMessages()
    val allCallLogs: Flow<List<VoiceCallLog>> = dao.getAllCallLogs()
    val platformSettings: Flow<PlatformSettings?> = dao.getPlatformSettings()

    suspend fun createSocialPost(post: SocialPost): Long = dao.insertSocialPost(post)
    suspend fun updateSocialPost(post: SocialPost) = dao.updateSocialPost(post)
    suspend fun deleteSocialPost(id: Long) = dao.deleteSocialPost(id)
    suspend fun reportPost(id: Long) = dao.reportSocialPost(id)
    suspend fun toggleLikePost(post: SocialPost) {
        val updated = post.copy(
            isLikedByUser = !post.isLikedByUser,
            likesCount = if (post.isLikedByUser) maxOf(0, post.likesCount - 1) else post.likesCount + 1
        )
        dao.updateSocialPost(updated)
    }

    suspend fun sendChatMessage(message: ChatMessage): Long = dao.insertChatMessage(message)
    suspend fun deleteChatMessage(id: Long) = dao.deleteChatMessage(id)

    suspend fun recordCallLog(log: VoiceCallLog): Long = dao.insertCallLog(log)
    suspend fun updatePlatformSettings(settings: PlatformSettings) = dao.savePlatformSettings(settings)

    // Write-In Draft Autosave
    val currentWriteInDraft: Flow<WriteInDraft?> = dao.getWriteInDraft()
    suspend fun saveWriteInDraft(draft: WriteInDraft) = dao.saveWriteInDraft(draft)
    suspend fun clearWriteInDraft() = dao.clearWriteInDraft()

    suspend fun seedInitialSocialIfEmpty() {
        val posts = dao.getAllSocialPosts().first()
        if (posts.isEmpty()) {
            val defaultPosts = listOf(
                SocialPost(
                    authorName = "NovaHub Official",
                    authorRole = "Community Hub",
                    authorAvatarInitials = "NH",
                    textContent = "Welcome to NovaHub! Explore instant HTML5 games (including Care Survey 89 and our built-in arcade), share photography in the Cloud Gallery, and join live community discussions.",
                    mediaType = "IMAGE",
                    mediaThumbnail = R.drawable.ai_cyberpunk_alley_1789701580711,
                    likesCount = 42,
                    sharesCount = 12,
                    isLikedByUser = true,
                    timeAgo = "10m ago"
                ),
                SocialPost(
                    authorName = "Ayesha Rahman",
                    authorRole = "Indie Game Designer",
                    authorAvatarInitials = "AR",
                    textContent = "Testing the new Cyber Brick Breaker & Quantum 2048 arcade cabinet inside NovaHub! Drop your best scores on the live leaderboard.",
                    mediaType = "IMAGE",
                    mediaThumbnail = R.drawable.game_void_runner_1789660196140,
                    likesCount = 28,
                    sharesCount = 5,
                    isLikedByUser = false,
                    timeAgo = "1h ago"
                ),
                SocialPost(
                    authorName = "Tahmid Hasan",
                    authorRole = "Cinematographer",
                    authorAvatarInitials = "TH",
                    textContent = "Monsoon twilight reflections and aerial riverfront sweeps uploaded to our high-res gallery. All saved posts and scores persist reliably.",
                    mediaType = "VIDEO",
                    mediaThumbnail = R.drawable.ai_veo_drone_river_1789701598733,
                    likesCount = 35,
                    sharesCount = 9,
                    isLikedByUser = false,
                    timeAgo = "3h ago"
                )
            )
            for (p in defaultPosts) {
                dao.insertSocialPost(p)
            }
        }

        val messages = dao.getAllChatMessages().first()
        if (messages.isEmpty()) {
            val sampleChats = listOf(
                ChatMessage(
                    senderName = "Ayesha Rahman",
                    senderAvatar = "AR",
                    messageText = "Hey NovaHub community! Loving the refreshed game portal and profile achievements.",
                    isFromCurrentUser = false,
                    timeFormatted = "10:15 AM"
                ),
                ChatMessage(
                    senderName = "NovaHub Official",
                    senderAvatar = "NH",
                    messageText = "Glad you like it, Ayesha! Try Cyber Brick Breaker, Quantum 2048, and Care Survey 89 right from the arcade.",
                    isFromCurrentUser = true,
                    timeFormatted = "10:18 AM"
                ),
                ChatMessage(
                    senderName = "System Moderator",
                    senderAvatar = "NH",
                    messageText = "Community safety filter active: respectful discussions and clean content keep NovaHub welcoming for everyone.",
                    isFromCurrentUser = false,
                    timeFormatted = "10:20 AM"
                )
            )
            for (c in sampleChats) {
                dao.insertChatMessage(c)
            }
        }

        val settings = dao.getPlatformSettings().first()
        if (settings == null) {
            dao.savePlatformSettings(
                PlatformSettings(
                    siteName = "NovaHub Public Community",
                    darkModeEnabled = true,
                    autoPlayMedia = false,
                    safeContentFilter = true,
                    allowDirectCalls = true,
                    allowPublicGameUploads = true,
                    soundEffectsEnabled = true,
                    notificationsEnabled = true
                )
            )
        }
    }

    suspend fun seedInitialWritingsIfEmpty() {
        val existingCount = dao.getWritingCount().first()
        if (existingCount > 0) return

        val initialWritings = listOf(
            StudioWriting(
                title = "Kinetic Feedback Loops: Engineering Satisfying 2D Canvas Physics",
                slug = "kinetic-feedback-loops-2d-canvas",
                category = "Game Design",
                summary = "Why player feel in indie arcade games is 90% micro-acceleration curves, screen-shakes, and responsive input timing.",
                content = """
When designing Void Runner and our kinetic browser experiments, the primary challenge wasn't graphics or complex rendering pipelines—it was the tactile feedback loop.

In minimalist 2D arcade games, 'game feel' lives in the subtle dampening of velocities. Linear movement feels robotic and detached. By introducing exponential decay on horizontal drifts and instantaneous responsiveness on lane swaps, the player feels physically tethered to the avatar.

Key Principles We Adhere To:
1. Sub-16ms Input Latency: Always compute physics updates before draw dispatches.
2. Satisfying Inertia: Let trailing particles inherit 30% of parent velocity to telegraph trajectory.
3. Multi-Sensory Feedback: Haptic pulses paired with micro audio clicks create synesthetic weight.

Games should feel responsive from the very first tap.
                """.trimIndent(),
                readTimeMinutes = 4,
                author = "NovaHub Editorial",
                tags = "Game Design, HTML5, Canvas, Physics, Game Feel",
                isPinned = true,
                isDraft = false,
                dateFormatted = "Sep 2026"
            ),
            StudioWriting(
                title = "Nocturnes of Dhaka: Documenting Monsoon Rain on 35mm",
                slug = "nocturnes-of-dhaka-monsoon-35mm",
                category = "Nocturne & Stills",
                summary = "Reflections on nocturnal photography through the waterlogged alleys of Old Dhaka and Hatirjheel.",
                content = """
Rain in Dhaka transforms the city into a continuous liquid mirror. As night settles and traffic thins out along Mirpur Road, incandescent street lamps, neon signboards, and rickshaw taillights bleed into the asphalt.

For the Stills series, I rely heavily on 35mm and 50mm fast primes opened wide at f/1.4 to f/2.0. Shooting in the rain requires embracing grain and flare:
- High ISO Grain as Texture: At ISO 1600-3200, the digital noise echoes analog film grain, blending water droplets with ambient photon scatter.
- Chiaroscuro Compositions: Deep obsidian shadows frame narrow ribbons of golden street light.
- The Ephemeral Riverbanks: Along the Meghna and Padma estuaries at first light, mist acts as a natural softbox, diffusing early sunrise into amber calm.

A camera is not merely a recording device; it is a tool for quiet contemplation amid urban velocity.
                """.trimIndent(),
                readTimeMinutes = 5,
                author = "NovaHub Editorial",
                tags = "Photography, Stills, 35mm, Dhaka, Rain, Chiaroscuro",
                isPinned = false,
                isDraft = false,
                dateFormatted = "Aug 2026"
            ),
            StudioWriting(
                title = "Anti-Bloat Architecture: The Case for Lean Digital Craftsmanship",
                slug = "anti-bloat-architecture-digital-craftsmanship",
                category = "Creative Code",
                summary = "Why personal studios and applications should respect device resources, battery life, and mental bandwidth.",
                content = """
Modern web and mobile software often suffers from extreme dependency inflation. A simple portfolio or personal space should not demand megabytes of redundant tracking scripts or convoluted framework chains.

In this personal studio application:
- Zero External Telemetry: Local Room SQLite handles all persistence offline without latency or external tracking.
- Jetpack Compose M3 Design: Pure declarative layouts that run seamlessly at 60-120fps.
- Instant Load States: No skeleton shimmers or artificial spinners; data loads instantaneously from disk.

Software that respects the user's device and privacy feels fundamentally different—it feels like an instrument crafted with intention.
                """.trimIndent(),
                readTimeMinutes = 3,
                author = "NovaHub Editorial",
                tags = "Architecture, Clean Code, Performance, Privacy, Room",
                isPinned = true,
                isDraft = false,
                dateFormatted = "Sep 2026"
            ),
            StudioWriting(
                title = "Field Notes: Ambient Soundscapes & Procedural Audio",
                slug = "field-notes-ambient-soundscapes-procedural-audio",
                category = "Field Notes",
                summary = "Exploring procedural audio synthesis, low-pass filter sweeps, and atmospheric sonic backgrounds.",
                content = """
Visual atmospheres are incomplete without sound. When building Void Runner and our browser sandbox, we experimented with Web Audio API oscillators and algorithmic frequency modulation.

Instead of looping static MP3 files, procedural sound generation offers dynamic adaptability:
- Speed-Tied Harmonics: As player velocity climbs, resonance frequencies subtly pitch upward.
- Dynamic Low-Pass Filters: Entering pause states or sub-menus sweeps cutoff frequencies downward, creating an underwater muffled ambiance.
- Minimalist Harmonic Chords: Pentatonic scales prevent ear fatigue across extended gameplay sessions.

Audio should complement the silence, never overpower it.
                """.trimIndent(),
                readTimeMinutes = 4,
                author = "NovaHub Editorial",
                tags = "Audio, Web Audio API, Sound Design, Field Notes",
                isPinned = false,
                isDraft = false,
                dateFormatted = "Jul 2026"
            )
        )

        for (writing in initialWritings) {
            dao.insertWriting(writing)
        }
    }

    suspend fun seedInitialCreationsIfEmpty() {
        val existingCount = dao.getCreationCount().first()
        if (existingCount > 0) return

        val initialCreations = listOf(
            UserCreation(
                title = "Neon Monsoon Alleyway",
                type = "IMAGE",
                prompt = "Cyberpunk street vendor in rain-slicked neon alley, cinematic 8k octane render, Hasselblad 85mm f/1.4",
                modelTag = "Gemini 2.5 Flash",
                imageResId = R.drawable.ai_cyberpunk_alley_1789701580711,
                resolution = "8K UHD",
                aspectRatio = "16:9",
                generatedOutput = "Master Prompt: Cyberpunk street vendor in rain-slicked neon alley, cinematic 8k octane render, Hasselblad 85mm f/1.4 lens, volumetric lighting, atmospheric rain mist, Kodachrome film color grading."
            ),
            UserCreation(
                title = "Delta River Mist Drone Sweep",
                type = "VIDEO",
                prompt = "Cinematic drone view flying over mist-covered tropical delta river at sunrise, golden sunbeams piercing morning clouds, lens flare, 35mm widescreen",
                modelTag = "Veo 3.1 & Sora",
                imageResId = R.drawable.ai_veo_drone_river_1789701598733,
                videoDuration = "0:30",
                resolution = "4K 60FPS",
                aspectRatio = "16:9",
                generatedOutput = "Shot 1 (0-10s): Drone establishes river mist with golden sunlight rays.\nShot 2 (10-20s): Smooth dolly forward with counter-clockwise orbit.\nShot 3 (20-30s): Velocity ramp into horizon bokeh."
            ),
            UserCreation(
                title = "Obsidian Hypercar Twilight Run",
                type = "IMAGE",
                prompt = "Futuristic electric hypercar on elevated obsidian bridge at twilight, illuminated led streaks, sleek aerodynamic silhouette, octane render 8k",
                modelTag = "Gemini 2.5 Flash",
                imageResId = R.drawable.ai_obsidian_supercar_1789701616237,
                resolution = "8K UHD",
                aspectRatio = "16:9",
                generatedOutput = "Master Prompt: Futuristic electric hypercar on elevated obsidian bridge at twilight, illuminated led streaks, sleek aerodynamic silhouette, reflections of purple sunset and neon skyline, octane render 8k."
            ),
            UserCreation(
                title = "Midnight Neon Bazaar Tracking",
                type = "VIDEO",
                prompt = "First person perspective cinematic camera gliding through bustling neon bazaar market at midnight, reflections in rain puddles, motion blur, 60fps",
                modelTag = "Veo 3.1 & Sora",
                imageResId = R.drawable.ai_veo_cyber_walk_1789701633091,
                videoDuration = "0:24",
                resolution = "4K 60FPS",
                aspectRatio = "16:9",
                generatedOutput = "Filmic Storyboard: First person POV camera gliding swiftly through vibrant rain-washed bazaar alleyways. Ambient neon light reflections on puddle surfaces."
            )
        )
        for (item in initialCreations) {
            dao.insertCreation(item)
        }
    }

    suspend fun toggleFavorite(stillId: String, isFav: Boolean) {
        if (isFav) {
            dao.removeFavorite(stillId)
        } else {
            dao.addFavorite(FavoriteStill(stillId = stillId))
        }
    }

    // User Authentication & Profile
    val allUsers: Flow<List<StudioUser>> = dao.getAllUsers()
    val primaryUser: Flow<StudioUser?> = dao.observePrimaryUser()

    suspend fun saveOrUpdateUserProfile(user: StudioUser): StudioUser {
        val existing = if (user.id > 0L) dao.getUserById(user.id) else dao.getPrimaryUser()
        return if (existing != null) {
            val updated = existing.copy(
                username = user.username,
                email = user.email.ifBlank { existing.email },
                roleTitle = user.roleTitle,
                bio = user.bio,
                avatarInitials = user.avatarInitials,
                handle = user.handle,
                avatarUrl = user.avatarUrl,
                accentTheme = user.accentTheme
            )
            dao.updateUser(updated)
            updated
        } else {
            val newId = dao.insertUser(user)
            user.copy(id = newId)
        }
    }

    suspend fun registerUser(
        username: String,
        email: String,
        password: String,
        roleTitle: String = "Community Creator",
        bio: String = "Member of NovaHub Community"
    ): Result<StudioUser> {
        val cleanEmail = email.trim().lowercase()
        val cleanUsername = username.trim()
        if (cleanUsername.isBlank()) {
            return Result.failure(IllegalArgumentException("Username cannot be empty"))
        }
        if (cleanEmail.isBlank() || !cleanEmail.contains("@")) {
            return Result.failure(IllegalArgumentException("Please enter a valid email address"))
        }
        if (password.length < 4) {
            return Result.failure(IllegalArgumentException("Password must be at least 4 characters"))
        }

        val existingByEmail = dao.getUserByEmail(cleanEmail)
        if (existingByEmail != null) {
            return Result.failure(IllegalStateException("An account with this email already exists"))
        }

        val existingByUsername = dao.getUserByUsername(cleanUsername)
        if (existingByUsername != null) {
            return Result.failure(IllegalStateException("This username is already taken"))
        }

        val initials = cleanUsername.take(2).uppercase()
        val hashed = hashPassword(password)
        val cleanHandle = "@" + cleanUsername.lowercase().replace(Regex("[^a-z0-9]"), "").ifEmpty { "creator" }
        val newUser = StudioUser(
            username = cleanUsername,
            email = cleanEmail,
            passwordHash = hashed,
            roleTitle = roleTitle,
            bio = bio,
            avatarInitials = if (initials.isNotEmpty()) initials else "NH",
            handle = cleanHandle
        )
        val id = dao.insertUser(newUser)
        return Result.success(newUser.copy(id = id))
    }

    suspend fun authenticateUser(emailOrUsername: String, password: String): Result<StudioUser> {
        val query = emailOrUsername.trim()
        if (query.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Please enter your email/username and password"))
        }
        val user = if (query.contains("@")) {
            dao.getUserByEmail(query.lowercase())
        } else {
            dao.getUserByUsername(query)
        } ?: return Result.failure(IllegalArgumentException("Account not found. Please register first."))

        val hashed = hashPassword(password)
        if (user.passwordHash != hashed && user.passwordHash != password) {
            return Result.failure(IllegalArgumentException("Incorrect password. Please try again."))
        }
        return Result.success(user)
    }

    private fun hashPassword(input: String): String {
        return try {
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(input.toByteArray())
            digest.fold("") { str, it -> str + "%02x".format(it) }
        } catch (e: Exception) {
            input
        }
    }

    suspend fun seedDefaultUserIfEmpty() {
        val count = dao.getUserByEmail("hello@novahub.community")
        if (count == null) {
            dao.insertUser(
                StudioUser(
                    username = "NovaHub Explorer",
                    email = "hello@novahub.community",
                    passwordHash = hashPassword("novahub2026"),
                    roleTitle = "Arcade Creator",
                    bio = "Open creative hub explorer crafting indie games, stills & digital stories.",
                    avatarInitials = "NH",
                    handle = "@novahub"
                )
            )
        }
    }

    // Static Curated Portfolio Data
    fun getCuratedGames(): List<StudioGame> = listOf(
        StudioGame(
            id = "care_survey_89",
            title = "Care Survey 89",
            subtitle = "Featured Interactive Web Game",
            description = "Interactive web experience and live challenge running inside NovaHub's secure HTML5 sandbox. Play immediately with zero downloads!",
            techStack = listOf("HTML5", "Cloudflare Workers", "Interactive Web"),
            status = "Featured Live",
            isPlayableInApp = false,
            imageResId = R.drawable.ai_obsidian_supercar_1789701616237,
            accentColorHex = 0xFF10B981,
            gameplayNote = "Featured public web game running seamlessly inside the secure HTML5 sandbox.",
            webGameUrl = "https://care-survey-89.prantoofficial46.workers.dev/",
            isWebGame = true
        ),
        StudioGame(
            id = "void_runner",
            title = "Void Runner",
            subtitle = "In-App Action / Kinetic Dodge",
            description = "A minimalist kinetic canvas game where you steer a resonance prism through shifting obsidian spires, absorbing luminous gold shards.",
            techStack = listOf("Compose Canvas", "Kinetic Physics", "60FPS Engine", "Sound FX"),
            status = "In-App Arcade",
            isPlayableInApp = true,
            imageResId = R.drawable.game_void_runner_1789660196140,
            accentColorHex = 0xFFE5A950,
            gameplayNote = "Instant 60fps native runner with real-time collision & procedural audio.",
            webGameUrl = "",
            isWebGame = false
        ),
        StudioGame(
            id = "cyber_breaker",
            title = "Cyber Brick Breaker",
            subtitle = "In-App Arcade / Neon Paddle",
            description = "Fast-paced cybernetic brick breaker built natively in Compose Canvas. Chain combo bounces, catch laser & wide-paddle powerups, and clear neon tiers.",
            techStack = listOf("Compose Canvas", "Brick Physics", "Power-Ups", "Sound FX"),
            status = "In-App Arcade",
            isPlayableInApp = true,
            imageResId = R.drawable.ai_cyberpunk_alley_1789701580711,
            accentColorHex = 0xFF38BDF8,
            gameplayNote = "Native 60fps arcade brick smasher with multi-hit armored tiles and combos.",
            webGameUrl = "",
            isWebGame = false
        ),
        StudioGame(
            id = "quantum_2048",
            title = "Quantum 2048",
            subtitle = "In-App Puzzle / Neon Matrix",
            description = "Swipe-driven 4x4 kinetic number puzzle with neon energy tiles, move undo support, and real-time merge synthesis sound effects.",
            techStack = listOf("Swipe Gestures", "State Undo", "60FPS Grid", "Sound FX"),
            status = "In-App Arcade",
            isPlayableInApp = true,
            imageResId = R.drawable.ai_obsidian_supercar_1789701616237,
            accentColorHex = 0xFF34D399,
            gameplayNote = "Instant swipe or D-pad puzzle right inside NovaHub with high-score tracking.",
            webGameUrl = "",
            isWebGame = false
        ),
        StudioGame(
            id = "chronicle_of_echoes",
            title = "Chronicle of Echoes",
            subtitle = "Atmospheric Narrative Puzzle",
            description = "An ambient soundscape investigation set in a submerged brutalist megastructure. Decode sonic patterns to restore temporal frequencies.",
            techStack = listOf("Web Audio API", "Three.js", "State Machine", "Procedural Ambient"),
            status = "Web Playable",
            isPlayableInApp = false,
            imageResId = R.drawable.still_shadow_monolith_1789660179400,
            accentColorHex = 0xFF38BDF8,
            gameplayNote = "Experimental interactive audio mechanics where sound dictates visual depth.",
            webGameUrl = "https://play2048.co/",
            isWebGame = true
        ),
        StudioGame(
            id = "dhaka_drift",
            title = "Dhaka Drift: Midnight",
            subtitle = "Retro Cyber Arcade Racer",
            description = "High-octane synthwave street racing through monsoon-slicked neon avenues of Dhaka at 3:00 AM. Avoid traffic, master power-slides.",
            techStack = listOf("Canvas 2D", "Pseudo-3D Raster", "Chiptune FM", "Leaderboard"),
            status = "Web Playable",
            isPlayableInApp = false,
            imageResId = R.drawable.still_dhaka_night_1789660149613,
            accentColorHex = 0xFFF43F5E,
            gameplayNote = "Fast paced retro arcade aesthetic with dynamic rain shaders and engine audio.",
            webGameUrl = "https://arcade.makecode.com/",
            isWebGame = true
        ),
        StudioGame(
            id = "neon_tetris_synth",
            title = "Neon Cascade: Grid Matrix",
            subtitle = "Cyberpunk Kinetic Block Puzzle",
            description = "Procedural reactive falling block puzzle synchronized to an algorithmic 120BPM synthwave soundtrack with haptic matrix clearing.",
            techStack = listOf("HTML5 Canvas", "Web Audio Synth", "Reactive Matrix", "Local Highscores"),
            status = "Web Playable",
            isPlayableInApp = false,
            imageResId = R.drawable.ai_cyberpunk_alley_1789701580711,
            accentColorHex = 0xFFA78BFA,
            gameplayNote = "Fast falling matrix with customizable drop speed and reactive glowing particles.",
            webGameUrl = "https://tetris.com/play-tetris",
            isWebGame = true
        )
    )

    fun getCuratedStills(): List<StudioStill> = listOf(
        StudioStill(
            id = "dhaka_monsoon_night",
            title = "Neon Monsoon Reflection",
            location = "Mirpur Road, Dhaka",
            category = "Urban Night",
            focalDescription = "Rainwater pooling against the asphalt, transforming street lamps and neon billboards into flowing liquid gold and cobalt ribbons.",
            exif = "35mm • f/1.4 • 1/80s • ISO 1250",
            imageResId = R.drawable.still_dhaka_night_1789660149613,
            year = "2025"
        ),
        StudioStill(
            id = "padma_golden_mist",
            title = "Silent Waters at First Light",
            location = "Padma Riverbank, Munshiganj",
            category = "Golden Dawn",
            focalDescription = "A lone country dinghy cutting through morning river mist as the sun breaks the horizon in deep amber stillness.",
            exif = "50mm • f/2.0 • 1/400s • ISO 200",
            imageResId = R.drawable.still_golden_dawn_1789660165447,
            year = "2025"
        ),
        StudioStill(
            id = "brutalist_solitude",
            title = "Shadow & Concrete Monolith",
            location = "Sher-e-Bangla Nagar",
            category = "Brutalist Shadow",
            focalDescription = "Razor-sharp sunset shadows cleaving raw exposed concrete planes in geometric silence, inspired by Kahn's monumentalism.",
            exif = "28mm • f/5.6 • 1/250s • ISO 100",
            imageResId = R.drawable.still_shadow_monolith_1789660179400,
            year = "2025"
        ),
        StudioStill(
            id = "cyberpunk_monsoon_alley",
            title = "Monsoon Alley Neon Horizon",
            location = "Old Dhaka Labyrinth",
            category = "Urban Night",
            focalDescription = "Hazy overhead cable networks crisscrossing glowing emerald and crimson tea stall lanterns in monsoon downpour.",
            exif = "85mm • f/1.4 • 1/125s • ISO 800",
            imageResId = R.drawable.ai_cyberpunk_alley_1789701580711,
            year = "2025"
        ),
        StudioStill(
            id = "delta_aerial_dawn",
            title = "Delta Riverbank Vapor Sweep",
            location = "Meghna Estuary, Chandpur",
            category = "Golden Dawn",
            focalDescription = "Gentle rippling tide lines glowing copper as low-hanging river mist evaporates under sunrise rays.",
            exif = "24mm • f/4.0 • 1/500s • ISO 100",
            imageResId = R.drawable.ai_veo_drone_river_1789701598733,
            year = "2025"
        ),
        StudioStill(
            id = "obsidian_nocturne",
            title = "Obsidian Nocturne & Steel",
            location = "Hatirjheel Overpass",
            category = "Brutalist Shadow",
            focalDescription = "Sculptural aerodynamic curvature and stark obsidian structural silhouette illuminated by ambient city glow.",
            exif = "50mm • f/1.8 • 1/60s • ISO 1600",
            imageResId = R.drawable.ai_obsidian_supercar_1789701616237,
            year = "2025"
        )
    )
}
