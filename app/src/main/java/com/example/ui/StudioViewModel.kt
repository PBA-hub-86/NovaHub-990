package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FavoriteStill
import com.example.data.GameScoreRecord
import com.example.data.PublishedGame
import com.example.data.StudioDatabase
import com.example.data.StudioGame
import com.example.data.StudioMessage
import com.example.data.StudioRepository
import com.example.data.StudioStill
import com.example.data.StudioUser
import com.example.data.StudioWriting
import com.example.data.UserCreation
import com.example.data.SocialPost
import com.example.data.ChatMessage
import com.example.data.VoiceCallLog
import com.example.data.PlatformSettings
import com.example.data.WriteInDraft
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.util.Log
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AuthUiState(
    val currentUser: StudioUser? = null,
    val isAuthDialogOpen: Boolean = false,
    val isRegisterMode: Boolean = false, // false = Login, true = Register / Create Account
    val usernameInput: String = "",
    val emailInput: String = "",
    val passwordInput: String = "",
    val roleTitleInput: String = "Creator",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

data class ContactFormState(
    val name: String = "",
    val email: String = "",
    val category: String = "Collaboration",
    val message: String = "",
    val isSubmitting: Boolean = false,
    val submitSuccess: Boolean = false,
    val errorMessage: String? = null
)

class StudioViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StudioRepository

    init {
        val db = StudioDatabase.getDatabase(application)
        repository = StudioRepository(db.studioDao())
        viewModelScope.launch {
            repository.seedInitialCreationsIfEmpty()
            repository.seedDefaultUserIfEmpty()
            repository.seedInitialWritingsIfEmpty()
            repository.seedInitialSocialIfEmpty()

            // Restore saved Write-In rich text draft from Room DB
            repository.currentWriteInDraft.firstOrNull()?.let { draft ->
                if (draft != null && draft.messageText.isNotBlank()) {
                    _contactForm.value = _contactForm.value.copy(
                        name = draft.senderName,
                        email = draft.senderEmail,
                        category = draft.category,
                        message = draft.messageText
                    )
                }
            }
        }
    }

    // User Authentication State & Actions
    private val _authUiState = MutableStateFlow(AuthUiState())
    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    fun openAuthDialog(isRegister: Boolean = false) {
        _authUiState.value = _authUiState.value.copy(
            isAuthDialogOpen = true,
            isRegisterMode = isRegister,
            errorMessage = null,
            successMessage = null
        )
    }

    fun closeAuthDialog() {
        _authUiState.value = _authUiState.value.copy(
            isAuthDialogOpen = false,
            errorMessage = null,
            successMessage = null
        )
    }

    fun toggleAuthMode() {
        val current = _authUiState.value
        _authUiState.value = current.copy(
            isRegisterMode = !current.isRegisterMode,
            errorMessage = null,
            successMessage = null
        )
    }

    fun setAuthUsernameInput(value: String) {
        _authUiState.value = _authUiState.value.copy(usernameInput = value, errorMessage = null)
    }

    fun setAuthEmailInput(value: String) {
        _authUiState.value = _authUiState.value.copy(emailInput = value, errorMessage = null)
    }

    fun setAuthPasswordInput(value: String) {
        _authUiState.value = _authUiState.value.copy(passwordInput = value, errorMessage = null)
    }

    fun setAuthRoleTitleInput(value: String) {
        _authUiState.value = _authUiState.value.copy(roleTitleInput = value)
    }

    fun login() {
        val state = _authUiState.value
        val identifier = state.emailInput.ifBlank { state.usernameInput }
        if (identifier.isBlank()) {
            _authUiState.value = state.copy(errorMessage = "ইমেইল অথবা ইউজারনেম লিখুন")
            return
        }
        if (state.passwordInput.isBlank()) {
            _authUiState.value = state.copy(errorMessage = "পাসওয়ার্ড লিখুন")
            return
        }

        viewModelScope.launch {
            _authUiState.value = state.copy(isLoading = true, errorMessage = null)
            val result = repository.authenticateUser(identifier, state.passwordInput)
            result.onSuccess { user ->
                _authUiState.value = _authUiState.value.copy(
                    currentUser = user,
                    isLoading = false,
                    isAuthDialogOpen = false,
                    passwordInput = "",
                    successMessage = "স্বাগতম ${user.username}! লগইন সফল হয়েছে"
                )
            }.onFailure { error ->
                _authUiState.value = _authUiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "লগইন ব্যর্থ হয়েছে"
                )
            }
        }
    }

    fun register() {
        val state = _authUiState.value
        if (state.usernameInput.isBlank()) {
            _authUiState.value = state.copy(errorMessage = "ইউজারনেম লিখুন")
            return
        }
        if (state.emailInput.isBlank() || !state.emailInput.contains("@")) {
            _authUiState.value = state.copy(errorMessage = "সঠিক ইমেইল ঠিকানা দিন")
            return
        }
        if (state.passwordInput.length < 4) {
            _authUiState.value = state.copy(errorMessage = "পাসওয়ার্ড কমপক্ষে ৪ অক্ষরের হতে হবে")
            return
        }

        viewModelScope.launch {
            _authUiState.value = state.copy(isLoading = true, errorMessage = null)
            val result = repository.registerUser(
                username = state.usernameInput.trim(),
                email = state.emailInput.trim(),
                password = state.passwordInput,
                roleTitle = state.roleTitleInput.ifBlank { "Creator" }
            )
            result.onSuccess { newUser ->
                _authUiState.value = _authUiState.value.copy(
                    currentUser = newUser,
                    isLoading = false,
                    isAuthDialogOpen = false,
                    passwordInput = "",
                    successMessage = "একাউন্ট তৈরি সফল হয়েছে!"
                )
            }.onFailure { error ->
                _authUiState.value = _authUiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "একাউন্ট তৈরিতে সমস্যা হয়েছে"
                )
            }
        }
    }

    fun signInWithGoogleIdToken(idToken: String) {
        viewModelScope.launch {
            _authUiState.value = _authUiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = Firebase.auth.signInWithCredential(credential).await()
                val firebaseUser = authResult.user
                if (firebaseUser != null) {
                    val displayName = firebaseUser.displayName?.ifBlank { "NovaHub Explorer" } ?: "NovaHub Explorer"
                    val email = firebaseUser.email ?: ""
                    val initials = displayName.trim().take(2).uppercase().ifEmpty { "NH" }

                    // Sync profile to provisioned Firestore database when authenticated
                    try {
                        val dbId = getApplication<Application>().getString(R.string.firestore_database_id)
                        val db = FirebaseFirestore.getInstance(dbId)
                        db.collection("users").document(firebaseUser.uid).set(
                            mapOf(
                                "displayName" to displayName,
                                "email" to email,
                                "role" to "Verified Member",
                                "avatarInitials" to initials
                            ),
                            SetOptions.merge()
                        ).await()
                    } catch (e: Exception) {
                        Log.w("StudioViewModel", "Optional Firestore profile sync skipped", e)
                    }

                    val studioUser = StudioUser(
                        username = displayName,
                        email = email,
                        passwordHash = "",
                        roleTitle = "Google Verified",
                        bio = "NovaHub Community Member",
                        avatarInitials = initials
                    )
                    _authUiState.value = _authUiState.value.copy(
                        currentUser = studioUser,
                        isLoading = false,
                        isAuthDialogOpen = false,
                        successMessage = "স্বাগতম $displayName! Google সাইন-ইন সফল হয়েছে"
                    )
                } else {
                    _authUiState.value = _authUiState.value.copy(
                        isLoading = false,
                        errorMessage = "Google সাইন-ইন সম্পন্ন করা যায়নি"
                    )
                }
            } catch (e: Exception) {
                Log.e("StudioViewModel", "Google Sign-In failed", e)
                _authUiState.value = _authUiState.value.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Google সাইন-ইন ব্যর্থ হয়েছে"
                )
            }
        }
    }

    fun setAuthLoading(loading: Boolean) {
        _authUiState.value = _authUiState.value.copy(isLoading = loading, errorMessage = null)
    }

    fun setAuthError(message: String) {
        _authUiState.value = _authUiState.value.copy(isLoading = false, errorMessage = message)
    }

    fun logout() {
        try {
            Firebase.auth.signOut()
        } catch (_: Exception) {}
        _authUiState.value = _authUiState.value.copy(
            currentUser = null,
            passwordInput = "",
            successMessage = "লগআউট করা হয়েছে"
        )
    }

    fun clearAuthFeedback() {
        _authUiState.value = _authUiState.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }

    // Navigation section tracking
    private val _currentSection = MutableStateFlow("hero")
    val currentSection: StateFlow<String> = _currentSection.asStateFlow()

    fun setCurrentSection(section: String) {
        _currentSection.value = section
    }

    // Curated + User Published Games Flow
    val allGames: StateFlow<List<StudioGame>> = repository.allPublishedGames
        .map { publishedList ->
            val publishedStudioGames = publishedList.map { pub ->
                StudioGame(
                    id = pub.gameId,
                    title = pub.title,
                    subtitle = pub.subtitle,
                    description = pub.description,
                    techStack = pub.techStack.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                    status = "Community Game",
                    isPlayableInApp = false,
                    imageResId = com.example.R.drawable.ai_cyberpunk_alley_1789701580711,
                    accentColorHex = 0xFF38BDF8,
                    gameplayNote = "Created by ${pub.authorName}. HTML5 browser playable inside the app.",
                    webGameUrl = pub.webGameUrl,
                    isWebGame = true,
                    imageUri = pub.imageUri,
                    isUserPublished = true,
                    dbId = pub.id
                )
            }
            repository.getCuratedGames() + publishedStudioGames
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = repository.getCuratedGames()
        )

    val games: List<StudioGame>
        get() = allGames.value

    // Publish Game Dialog State
    private val _isPublishGameDialogOpen = MutableStateFlow(false)
    val isPublishGameDialogOpen: StateFlow<Boolean> = _isPublishGameDialogOpen.asStateFlow()

    fun openPublishGameDialog() {
        _isPublishGameDialogOpen.value = true
    }

    fun closePublishGameDialog() {
        _isPublishGameDialogOpen.value = false
    }

    fun publishNewGame(
        title: String,
        subtitle: String,
        description: String,
        webGameUrl: String,
        techStack: String,
        authorName: String,
        imageUri: String = ""
    ) {
        viewModelScope.launch {
            val gameId = "pub_" + System.currentTimeMillis()
            repository.publishGame(
                PublishedGame(
                    gameId = gameId,
                    title = title.trim().ifBlank { "Untitled Game" },
                    subtitle = subtitle.trim().ifBlank { "Indie Browser Experience" },
                    description = description.trim().ifBlank { "Interactive web game built and published by the community." },
                    webGameUrl = webGameUrl.trim(),
                    techStack = techStack.trim().ifBlank { "HTML5, WebGL, JavaScript" },
                    authorName = authorName.trim().ifBlank { _authUiState.value.currentUser?.username ?: "Community Creator" },
                    imageUri = imageUri
                )
            )
            _isPublishGameDialogOpen.value = false
        }
    }

    fun deletePublishedGame(dbId: Long) {
        viewModelScope.launch {
            repository.deletePublishedGame(dbId)
        }
    }

    // Void Runner High Score
    val voidRunnerHighScore: StateFlow<Int> = repository.getHighestScore("void_runner")
        .map { it ?: 0 }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    fun recordGameScore(score: Int, shards: Int) {
        viewModelScope.launch {
            repository.saveGameScore(
                GameScoreRecord(
                    gameId = "void_runner",
                    score = score,
                    shardsCollected = shards
                )
            )
        }
    }

    // Stills Portfolio Data & Filtering
    val allStills: List<StudioStill> = repository.getCuratedStills()

    private val _selectedStillCategory = MutableStateFlow("All")
    val selectedStillCategory: StateFlow<String> = _selectedStillCategory.asStateFlow()

    fun selectStillCategory(cat: String) {
        _selectedStillCategory.value = cat
    }

    // Favorites
    val favoriteStillIds: StateFlow<Set<String>> = repository.allFavorites
        .map { list -> list.map { it.stillId }.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    fun toggleFavoriteStill(stillId: String) {
        viewModelScope.launch {
            val isFav = favoriteStillIds.value.contains(stillId)
            repository.toggleFavorite(stillId, isFav)
        }
    }

    // Lightbox modal state
    private val _activeLightboxStill = MutableStateFlow<StudioStill?>(null)
    val activeLightboxStill: StateFlow<StudioStill?> = _activeLightboxStill.asStateFlow()

    fun openLightbox(still: StudioStill) {
        _activeLightboxStill.value = still
    }

    fun nextLightboxStill() {
        val current = _activeLightboxStill.value ?: return
        val currentCategory = _selectedStillCategory.value
        val list = if (currentCategory == "All") allStills else allStills.filter { it.category.equals(currentCategory, ignoreCase = true) }
        val idx = list.indexOfFirst { it.id == current.id }
        if (idx != -1 && list.isNotEmpty()) {
            val nextIdx = (idx + 1) % list.size
            _activeLightboxStill.value = list[nextIdx]
        }
    }

    fun previousLightboxStill() {
        val current = _activeLightboxStill.value ?: return
        val currentCategory = _selectedStillCategory.value
        val list = if (currentCategory == "All") allStills else allStills.filter { it.category.equals(currentCategory, ignoreCase = true) }
        val idx = list.indexOfFirst { it.id == current.id }
        if (idx != -1 && list.isNotEmpty()) {
            val prevIdx = if (idx - 1 < 0) list.size - 1 else idx - 1
            _activeLightboxStill.value = list[prevIdx]
        }
    }

    fun closeLightbox() {
        _activeLightboxStill.value = null
    }

    // User Creations Gallery State & Filtering
    val userCreations: StateFlow<List<UserCreation>> = repository.allCreations
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedCreationFilter = MutableStateFlow("All")
    val selectedCreationFilter: StateFlow<String> = _selectedCreationFilter.asStateFlow()

    fun selectCreationFilter(filter: String) {
        _selectedCreationFilter.value = filter
    }

    // Studio-wide Search Query (Filters gallery images & blog posts simultaneously)
    private val _studioSearchQuery = MutableStateFlow("")
    val studioSearchQuery: StateFlow<String> = _studioSearchQuery.asStateFlow()

    fun setStudioSearchQuery(query: String) {
        _studioSearchQuery.value = query
        _writingSearchQuery.value = query
    }

    fun clearStudioSearch() {
        _studioSearchQuery.value = ""
        _writingSearchQuery.value = ""
    }

    val filteredCreations: StateFlow<List<UserCreation>> = combine(
        userCreations,
        _selectedCreationFilter,
        _studioSearchQuery
    ) { creations, filter, query ->
        val typeFiltered = when (filter) {
            "Images" -> creations.filter { it.type.equals("IMAGE", ignoreCase = true) }
            "Videos" -> creations.filter { it.type.equals("VIDEO", ignoreCase = true) }
            else -> creations
        }
        if (query.isBlank()) {
            typeFiltered
        } else {
            val q = query.trim().lowercase()
            typeFiltered.filter { creation ->
                creation.title.lowercase().contains(q) ||
                creation.prompt.lowercase().contains(q) ||
                creation.modelTag.lowercase().contains(q) ||
                creation.generatedOutput.lowercase().contains(q) ||
                creation.type.lowercase().contains(q) ||
                creation.resolution.lowercase().contains(q)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val filteredStills: StateFlow<List<StudioStill>> = combine(
        _selectedStillCategory,
        _studioSearchQuery
    ) { category, query ->
        val catFiltered = if (category == "All") allStills else allStills.filter { it.category.equals(category, ignoreCase = true) }
        if (query.isBlank()) {
            catFiltered
        } else {
            val q = query.trim().lowercase()
            catFiltered.filter { still ->
                still.title.lowercase().contains(q) ||
                still.category.lowercase().contains(q) ||
                still.location.lowercase().contains(q) ||
                still.exif.lowercase().contains(q) ||
                still.focalDescription.lowercase().contains(q)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = allStills
    )

    private val _activeCreationDetail = MutableStateFlow<UserCreation?>(null)
    val activeCreationDetail: StateFlow<UserCreation?> = _activeCreationDetail.asStateFlow()

    fun openCreationDetail(creation: UserCreation) {
        _activeCreationDetail.value = creation
    }

    fun closeCreationDetail() {
        _activeCreationDetail.value = null
    }

    fun saveNewCreation(
        title: String,
        type: String,
        prompt: String,
        modelTag: String,
        imageResId: Int = 0,
        videoDuration: String = "",
        resolution: String = "4K HDR",
        aspectRatio: String = "16:9",
        generatedOutput: String = "",
        mediaUri: String = ""
    ) {
        viewModelScope.launch {
            repository.saveCreation(
                UserCreation(
                    title = title,
                    type = type,
                    prompt = prompt,
                    modelTag = modelTag,
                    imageResId = imageResId,
                    videoDuration = videoDuration,
                    resolution = resolution,
                    aspectRatio = aspectRatio,
                    generatedOutput = generatedOutput,
                    mediaUri = mediaUri
                )
            )
        }
    }

    fun deleteCreation(id: Long) {
        viewModelScope.launch {
            repository.deleteCreation(id)
            if (_activeCreationDetail.value?.id == id) {
                _activeCreationDetail.value = null
            }
        }
    }

    // Interactive Game Modal state
    private val _isGameActive = MutableStateFlow(false)
    val isGameActive: StateFlow<Boolean> = _isGameActive.asStateFlow()

    fun setGameActive(active: Boolean) {
        _isGameActive.value = active
    }

    // Web Browser Game Viewer Modal State
    private val _activeWebGame = MutableStateFlow<StudioGame?>(null)
    val activeWebGame: StateFlow<StudioGame?> = _activeWebGame.asStateFlow()

    fun launchWebGame(game: StudioGame) {
        _activeWebGame.value = game
    }

    fun closeWebGame() {
        _activeWebGame.value = null
    }

    // Studio Writing & Essays Feature (Room persistent storage)
    val allWritings: StateFlow<List<StudioWriting>> = repository.allWritings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedWritingCategory = MutableStateFlow("All")
    val selectedWritingCategory: StateFlow<String> = _selectedWritingCategory.asStateFlow()

    private val _writingSearchQuery = MutableStateFlow("")
    val writingSearchQuery: StateFlow<String> = _writingSearchQuery.asStateFlow()

    val filteredWritings: StateFlow<List<StudioWriting>> = combine(
        allWritings,
        _selectedWritingCategory,
        _writingSearchQuery,
        _studioSearchQuery
    ) { writings, cat, wQuery, sQuery ->
        val activeQuery = if (sQuery.isNotBlank()) sQuery else wQuery
        val q = activeQuery.trim().lowercase()
        writings.filter { writing ->
            val matchesCategory = (cat == "All" || writing.category.equals(cat, ignoreCase = true))
            val matchesQuery = q.isBlank() ||
                writing.title.lowercase().contains(q) ||
                writing.summary.lowercase().contains(q) ||
                writing.content.lowercase().contains(q) ||
                writing.tags.lowercase().contains(q) ||
                writing.category.lowercase().contains(q) ||
                writing.author.lowercase().contains(q)
            matchesCategory && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _activeReadingWriting = MutableStateFlow<StudioWriting?>(null)
    val activeReadingWriting: StateFlow<StudioWriting?> = _activeReadingWriting.asStateFlow()

    private val _isComposeWritingDialogOpen = MutableStateFlow(false)
    val isComposeWritingDialogOpen: StateFlow<Boolean> = _isComposeWritingDialogOpen.asStateFlow()

    // Quick Note input state (handled by ViewModel)
    private val _noteText = MutableStateFlow("")
    val noteText: StateFlow<String> = _noteText.asStateFlow()

    private val _noteSaveSuccess = MutableStateFlow(false)
    val noteSaveSuccess: StateFlow<Boolean> = _noteSaveSuccess.asStateFlow()

    fun updateNoteText(newText: String) {
        _noteText.value = newText
        _noteSaveSuccess.value = false
    }

    fun saveNote() {
        val text = _noteText.value.trim()
        if (text.isBlank()) return

        viewModelScope.launch {
            val lines = text.lines().map { it.trim() }.filter { it.isNotEmpty() }
            val noteTitle = if (lines.isNotEmpty()) {
                val firstLine = lines.first()
                if (firstLine.length > 50) firstLine.take(47) + "..." else firstLine
            } else {
                "Quick Studio Note"
            }
            val noteSummary = if (lines.size > 1) {
                val rest = lines.drop(1).joinToString(" ")
                if (rest.length > 100) rest.take(97) + "..." else rest
            } else {
                text.take(80)
            }
            val slug = "note-${System.currentTimeMillis()}"
            val authorName = _authUiState.value.currentUser?.username ?: "NovaHub Member"
            val nowStamp = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date())

            val newNote = StudioWriting(
                title = noteTitle,
                slug = slug,
                category = "Field Notes",
                summary = noteSummary,
                content = text,
                readTimeMinutes = maxOf(1, text.split("\\s+".toRegex()).size / 150),
                author = authorName,
                tags = "Journal, Field Notes, Daily Note",
                isPinned = false,
                isDraft = false,
                dateFormatted = nowStamp
            )
            repository.saveWriting(newNote)
            _noteText.value = ""
            _noteSaveSuccess.value = true
        }
    }

    fun dismissNoteSuccess() {
        _noteSaveSuccess.value = false
    }

    fun selectWritingCategory(category: String) {
        _selectedWritingCategory.value = category
    }

    fun setWritingSearchQuery(query: String) {
        _writingSearchQuery.value = query
        _studioSearchQuery.value = query
    }

    fun openWritingReader(writing: StudioWriting) {
        _activeReadingWriting.value = writing
    }

    fun closeWritingReader() {
        _activeReadingWriting.value = null
    }

    fun openComposeWritingDialog() {
        _isComposeWritingDialogOpen.value = true
    }

    fun closeComposeWritingDialog() {
        _isComposeWritingDialogOpen.value = false
    }

    fun saveNewWriting(
        title: String,
        category: String,
        summary: String,
        content: String,
        tags: String,
        readTimeMinutes: Int = 3,
        author: String = "",
        isPinned: Boolean = false
    ) {
        viewModelScope.launch {
            val authorName = author.trim().ifBlank {
                _authUiState.value.currentUser?.username ?: "NovaHub Member"
            }
            val slug = title.trim().lowercase().replace(Regex("[^a-z0-9]+"), "-")
            val newEntry = StudioWriting(
                title = title.trim().ifBlank { "Untitled Writing" },
                slug = slug,
                category = category.trim().ifBlank { "Essays" },
                summary = summary.trim().ifBlank { content.take(120).trim() + "..." },
                content = content.trim(),
                readTimeMinutes = if (readTimeMinutes > 0) readTimeMinutes else maxOf(1, content.split("\\s+".toRegex()).size / 200),
                author = authorName,
                tags = tags.trim(),
                isPinned = isPinned,
                isDraft = false,
                dateFormatted = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
            )
            repository.saveWriting(newEntry)
            _isComposeWritingDialogOpen.value = false
        }
    }

    fun deleteWriting(id: Long) {
        viewModelScope.launch {
            repository.deleteWriting(id)
            if (_activeReadingWriting.value?.id == id) {
                _activeReadingWriting.value = null
            }
        }
    }

    fun togglePinWriting(writing: StudioWriting) {
        viewModelScope.launch {
            repository.updateWriting(writing.copy(isPinned = !writing.isPinned))
        }
    }

    // Contact ("A way to write in")
    private val _contactForm = MutableStateFlow(ContactFormState())
    val contactForm: StateFlow<ContactFormState> = _contactForm.asStateFlow()

    val savedDispatches: StateFlow<List<StudioMessage>> = repository.allMessages
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private var draftSaveJob: Job? = null

    private fun autoSaveDraft() {
        draftSaveJob?.cancel()
        draftSaveJob = viewModelScope.launch {
            delay(400) // Debounce auto-save
            val current = _contactForm.value
            val words = if (current.message.isBlank()) 0 else current.message.trim().split("\\s+".toRegex()).size
            repository.saveWriteInDraft(
                WriteInDraft(
                    id = 1,
                    senderName = current.name,
                    senderEmail = current.email,
                    category = current.category,
                    messageText = current.message,
                    wordCount = words,
                    lastUpdated = System.currentTimeMillis()
                )
            )
        }
    }

    fun updateName(name: String) {
        _contactForm.value = _contactForm.value.copy(name = name, errorMessage = null)
        autoSaveDraft()
    }

    fun updateEmail(email: String) {
        _contactForm.value = _contactForm.value.copy(email = email, errorMessage = null)
        autoSaveDraft()
    }

    fun updateCategory(category: String) {
        _contactForm.value = _contactForm.value.copy(category = category)
        autoSaveDraft()
    }

    fun updateMessage(msg: String) {
        _contactForm.value = _contactForm.value.copy(message = msg, errorMessage = null)
        autoSaveDraft()
    }

    fun clearWriteInDraft() {
        _contactForm.value = _contactForm.value.copy(message = "")
        viewModelScope.launch {
            repository.clearWriteInDraft()
        }
    }

    fun addPersonalStill(title: String, location: String, uri: String) {
        viewModelScope.launch {
            repository.saveCreation(
                UserCreation(
                    title = title,
                    type = "IMAGE",
                    prompt = location,
                    modelTag = "Personal Still",
                    mediaUri = uri,
                    resolution = "Fine Art RAW",
                    aspectRatio = "3:2"
                )
            )
        }
    }

    fun submitDispatch(onSuccessEmailIntent: (name: String, email: String, category: String, message: String) -> Unit) {
        val current = _contactForm.value
        if (current.name.isBlank()) {
            _contactForm.value = current.copy(errorMessage = "Please enter your name or moniker.")
            return
        }
        if (current.email.isBlank() || !current.email.contains("@")) {
            _contactForm.value = current.copy(errorMessage = "Please provide a valid contact email.")
            return
        }
        if (current.message.isBlank() || current.message.length < 5) {
            _contactForm.value = current.copy(errorMessage = "Your message is a little too brief. Tell us more.")
            return
        }

        viewModelScope.launch {
            _contactForm.value = current.copy(isSubmitting = true, errorMessage = null)
            val messageRecord = StudioMessage(
                senderName = current.name.trim(),
                senderEmail = current.email.trim(),
                category = current.category,
                message = current.message.trim()
            )
            repository.saveMessage(messageRecord)
            repository.clearWriteInDraft()

            _contactForm.value = ContactFormState(submitSuccess = true)

            // Trigger real email intent callback so user can open direct email client
            onSuccessEmailIntent(
                messageRecord.senderName,
                messageRecord.senderEmail,
                messageRecord.category,
                messageRecord.message
            )
        }
    }

    fun resetSubmitSuccess() {
        _contactForm.value = _contactForm.value.copy(submitSuccess = false)
    }

    fun deleteDispatch(id: Long) {
        viewModelScope.launch {
            repository.deleteMessage(id)
        }
    }

    // Studio Universal Search
    private val _searchUiState = MutableStateFlow(SearchUiState())
    val searchUiState: StateFlow<SearchUiState> = _searchUiState.asStateFlow()

    fun openSearch() {
        _searchUiState.value = _searchUiState.value.copy(
            isSearchActive = true,
            results = computeSearchResults(_searchUiState.value.query)
        )
    }

    fun closeSearch() {
        _searchUiState.value = _searchUiState.value.copy(
            isSearchActive = false,
            query = "",
            results = emptyList()
        )
    }

    fun updateSearchQuery(query: String) {
        _searchUiState.value = _searchUiState.value.copy(
            query = query,
            results = computeSearchResults(query)
        )
    }

    private fun computeSearchResults(rawQuery: String): List<SearchResultItem> {
        val q = rawQuery.trim().lowercase()
        val results = mutableListOf<SearchResultItem>()

        if (q.isEmpty()) {
            // Provide quick direct section navigation suggestions
            results.add(SearchResultItem.NavigationItem("Social Media Feed", "Live community posts, photo sharing & live discussions", "social"))
            results.add(SearchResultItem.NavigationItem("Game Portal & Arcade", "Browser-based games & instant webview play", "portal"))
            results.add(SearchResultItem.NavigationItem("Blog & Articles (/blog)", "Essays, devlogs & date-stamped journal entries", "writing"))
            results.add(SearchResultItem.NavigationItem("Masonry Stills Gallery", "Staggered photo archive loaded with Coil", "masonry"))
            results.add(SearchResultItem.NavigationItem("Contact / Write In", "Send dispatches directly to NovaHub Community", "contact"))
            return results
        }

        // Search Games
        games.filter {
            it.title.lowercase().contains(q) ||
            it.subtitle.lowercase().contains(q) ||
            it.description.lowercase().contains(q) ||
            it.techStack.any { tech -> tech.lowercase().contains(q) }
        }.forEach { game ->
            results.add(
                SearchResultItem.GameItem(
                    title = game.title,
                    subtitle = game.subtitle,
                    techStack = game.techStack.joinToString(", ")
                )
            )
        }

        // Search Stills
        allStills.filter {
            it.title.lowercase().contains(q) ||
            it.category.lowercase().contains(q) ||
            it.location.lowercase().contains(q) ||
            it.exif.lowercase().contains(q) ||
            it.focalDescription.lowercase().contains(q)
        }.forEach { still ->
            results.add(
                SearchResultItem.StillItem(
                    title = still.title,
                    category = still.category,
                    location = still.location,
                    stillId = still.id
                )
            )
        }

        // Search User Creations
        userCreations.value.filter {
            it.title.lowercase().contains(q) ||
            it.prompt.lowercase().contains(q) ||
            it.type.lowercase().contains(q) ||
            it.generatedOutput.lowercase().contains(q) ||
            it.modelTag.lowercase().contains(q)
        }.forEach { creation ->
            results.add(
                SearchResultItem.CreationItem(
                    title = creation.title,
                    prompt = creation.prompt,
                    type = creation.type,
                    creationId = creation.id
                )
            )
        }

        // Search Writings & Essays
        allWritings.value.filter {
            it.title.lowercase().contains(q) ||
            it.summary.lowercase().contains(q) ||
            it.content.lowercase().contains(q) ||
            it.category.lowercase().contains(q) ||
            it.tags.lowercase().contains(q)
        }.forEach { writing ->
            results.add(
                SearchResultItem.WritingItem(
                    title = writing.title,
                    category = writing.category,
                    summary = writing.summary,
                    writingId = writing.id
                )
            )
        }

        // Search Sections
        val sections = listOf(
            Triple("Hero & Overview", "Home section with studio manifesto & featured highlights", "hero"),
            Triple("Social Hub & Feed", "Modern community feed with media uploads, likes, and chat", "social"),
            Triple("Game Portal", "Interactive browser games, HTML5 canvas, and community builds", "portal"),
            Triple("Masonry Stills Archive", "Personal and curated photography in staggered masonry layout", "masonry"),
            Triple("Writing & Essays", "Studio essays on game feel, 35mm nocturnal stills & creative code", "writing"),
            Triple("Write In / Direct Inbox", "Send inquiries with rich-text editor and autosaving drafts", "contact"),
            Triple("About NovaHub", "Public community platform, game portal & photography vision", "about")
        )

        sections.filter { (title, desc, _) ->
            title.lowercase().contains(q) || desc.lowercase().contains(q)
        }.forEach { (title, desc, route) ->
            results.add(SearchResultItem.NavigationItem(title, desc, route))
        }

        return results
    }

    // ==========================================
    // MODERN SOCIAL MEDIA PLATFORM ARCHITECTURE
    // ==========================================

    // Active Social Posts Stream
    val socialPosts: StateFlow<List<SocialPost>> = repository.activeSocialPosts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Create New Social Post UI State
    private val _newPostContent = MutableStateFlow("")
    val newPostContent: StateFlow<String> = _newPostContent.asStateFlow()

    private val _newPostMediaType = MutableStateFlow("NONE") // "NONE", "IMAGE", "VIDEO"
    val newPostMediaType: StateFlow<String> = _newPostMediaType.asStateFlow()

    private val _newPostMediaUri = MutableStateFlow("")
    val newPostMediaUri: StateFlow<String> = _newPostMediaUri.asStateFlow()

    private val _isPostUploading = MutableStateFlow(false)
    val isPostUploading: StateFlow<Boolean> = _isPostUploading.asStateFlow()

    private val _postSuccessMessage = MutableStateFlow<String?>(null)
    val postSuccessMessage: StateFlow<String?> = _postSuccessMessage.asStateFlow()

    fun updateNewPostContent(content: String) {
        _newPostContent.value = content
    }

    fun setNewPostMedia(mediaType: String, uri: String) {
        _newPostMediaType.value = mediaType
        _newPostMediaUri.value = uri
    }

    fun clearNewPostMedia() {
        _newPostMediaType.value = "NONE"
        _newPostMediaUri.value = ""
    }

    fun publishSocialPost() {
        val text = _newPostContent.value.trim()
        val mediaType = _newPostMediaType.value
        val mediaUri = _newPostMediaUri.value

        if (text.isBlank() && mediaUri.isBlank()) return

        // Safety content filter / anti-abuse check
        val bannedWords = listOf("abuse", "spam", "hack", "scam", "malware", "hate")
        val containsAbuse = bannedWords.any { text.lowercase().contains(it) }

        val currentUser = _authUiState.value.currentUser
        val authorName = currentUser?.username ?: "Guest Explorer"
        val authorRole = currentUser?.roleTitle ?: "Visitor"
        val authorInitials = currentUser?.avatarInitials ?: "GE"

        viewModelScope.launch {
            _isPostUploading.value = true
            val newPost = SocialPost(
                authorName = authorName,
                authorRole = authorRole,
                authorAvatarInitials = authorInitials,
                textContent = text,
                mediaType = mediaType,
                mediaUri = mediaUri,
                likesCount = 0,
                sharesCount = 0,
                isLikedByUser = false,
                isReported = containsAbuse,
                moderationStatus = if (containsAbuse) "FLAGGED" else "CLEAN",
                timeAgo = "Just now"
            )
            repository.createSocialPost(newPost)
            _newPostContent.value = ""
            _newPostMediaType.value = "NONE"
            _newPostMediaUri.value = ""
            _isPostUploading.value = false
            _postSuccessMessage.value = if (containsAbuse) "পোস্টটি পর্যালোচনায় আছে" else "পোস্ট সফলভাবে প্রকাশিত হয়েছে!"
        }
    }

    fun dismissPostSuccessMessage() {
        _postSuccessMessage.value = null
    }

    fun toggleLikePost(post: SocialPost) {
        viewModelScope.launch {
            repository.toggleLikePost(post)
        }
    }

    fun reportPost(postId: Long) {
        viewModelScope.launch {
            repository.reportPost(postId)
            _postSuccessMessage.value = "পোস্টটি অপব্যবহার হিসেবে রিপোর্ট করা হয়েছে ও সরানো হয়েছে"
        }
    }

    fun deletePost(postId: Long) {
        viewModelScope.launch {
            repository.deleteSocialPost(postId)
        }
    }

    // ==========================================
    // REAL-TIME CHAT & MESSAGING
    // ==========================================

    val chatMessages: StateFlow<List<ChatMessage>> = repository.allChatMessages
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _chatInputText = MutableStateFlow("")
    val chatInputText: StateFlow<String> = _chatInputText.asStateFlow()

    private val _isChatDialogOpen = MutableStateFlow(false)
    val isChatDialogOpen: StateFlow<Boolean> = _isChatDialogOpen.asStateFlow()

    fun openChatDialog() {
        _isChatDialogOpen.value = true
    }

    fun closeChatDialog() {
        _isChatDialogOpen.value = false
    }

    fun updateChatInput(text: String) {
        _chatInputText.value = text
    }

    fun sendChatMessage(mediaUri: String = "") {
        val text = _chatInputText.value.trim()
        if (text.isBlank() && mediaUri.isBlank()) return

        val currentUser = _authUiState.value.currentUser
        val sender = currentUser?.username ?: "Guest Explorer"
        val initials = currentUser?.avatarInitials ?: "GE"

        viewModelScope.launch {
            val nowFormatted = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
            repository.sendChatMessage(
                ChatMessage(
                    senderName = sender,
                    senderAvatar = initials,
                    messageText = text,
                    mediaUri = mediaUri,
                    isFromCurrentUser = true,
                    timeFormatted = nowFormatted
                )
            )
            _chatInputText.value = ""
        }
    }

    // ==========================================
    // VOICE CALL INTERACTION SYSTEM
    // ==========================================

    private val _isVoiceCallActive = MutableStateFlow(false)
    val isVoiceCallActive: StateFlow<Boolean> = _isVoiceCallActive.asStateFlow()

    private val _activeCallPeer = MutableStateFlow("Community Support")
    val activeCallPeer: StateFlow<String> = _activeCallPeer.asStateFlow()

    private val _callSeconds = MutableStateFlow(0)
    val callSeconds: StateFlow<Int> = _callSeconds.asStateFlow()

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    fun startVoiceCall(peerName: String) {
        _activeCallPeer.value = peerName
        _isVoiceCallActive.value = true
        _callSeconds.value = 0
        _isMuted.value = false
    }

    fun toggleMuteCall() {
        _isMuted.value = !_isMuted.value
    }

    fun incrementCallSecond() {
        _callSeconds.value = _callSeconds.value + 1
    }

    fun endVoiceCall() {
        val duration = _callSeconds.value
        val peer = _activeCallPeer.value
        _isVoiceCallActive.value = false
        _callSeconds.value = 0
        viewModelScope.launch {
            repository.recordCallLog(
                VoiceCallLog(
                    peerName = peer,
                    callDurationSeconds = duration,
                    callType = "VOICE",
                    status = "COMPLETED"
                )
            )
        }
    }

    // ==========================================
    // PLATFORM SETTINGS & ABUSE PREVENTION
    // ==========================================

    val platformSettings: StateFlow<PlatformSettings> = repository.platformSettings
        .map { it ?: PlatformSettings() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PlatformSettings()
        )

    private val _isSettingsDialogOpen = MutableStateFlow(false)
    val isSettingsDialogOpen: StateFlow<Boolean> = _isSettingsDialogOpen.asStateFlow()

    fun openSettingsDialog() {
        _isSettingsDialogOpen.value = true
    }

    fun closeSettingsDialog() {
        _isSettingsDialogOpen.value = false
    }

    fun toggleSafeContentFilter() {
        val current = platformSettings.value
        viewModelScope.launch {
            repository.updatePlatformSettings(current.copy(safeContentFilter = !current.safeContentFilter))
        }
    }

    fun toggleAutoPlayMedia() {
        val current = platformSettings.value
        viewModelScope.launch {
            repository.updatePlatformSettings(current.copy(autoPlayMedia = !current.autoPlayMedia))
        }
    }

    fun toggleAllowCalls() {
        val current = platformSettings.value
        viewModelScope.launch {
            repository.updatePlatformSettings(current.copy(allowDirectCalls = !current.allowDirectCalls))
        }
    }

    fun toggleAllowGameUploads() {
        val current = platformSettings.value
        viewModelScope.launch {
            repository.updatePlatformSettings(current.copy(allowPublicGameUploads = !current.allowPublicGameUploads))
        }
    }

    // Compatibility aliases for dialog and call state
    val isChatOpen: StateFlow<Boolean> get() = _isChatDialogOpen.asStateFlow()
    val callPeerName: StateFlow<String> get() = _activeCallPeer.asStateFlow()
    val callDurationSeconds: StateFlow<Int> get() = _callSeconds.asStateFlow()
    val isCallMuted: StateFlow<Boolean> get() = _isMuted.asStateFlow()
    val isSettingsOpen: StateFlow<Boolean> get() = _isSettingsDialogOpen.asStateFlow()

    fun openChat() = openChatDialog()
    fun closeChat() = closeChatDialog()
    fun openSettings() = openSettingsDialog()
    fun closeSettings() = closeSettingsDialog()
    fun tickCallSecond() = incrementCallSecond()
    fun toggleLikeSocialPost(post: SocialPost) = toggleLikePost(post)
    fun reportSocialPost(postId: Long) = reportPost(postId)
    fun deleteSocialPost(postId: Long) = deletePost(postId)
}
