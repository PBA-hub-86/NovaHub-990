package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.ActiveNativeGame
import com.example.ui.SearchResultItem
import com.example.ui.StudioViewModel
import com.example.ui.components.AboutSection
import com.example.ui.components.AuthDialog
import com.example.ui.components.ComposeWritingDialog
import com.example.ui.components.ContactSection
import com.example.ui.components.CyberBrickBreakerGame
import com.example.ui.components.FirestoreImageGalleryComponent
import com.example.ui.components.GamePortalComponent
import com.example.ui.components.HeroSection
import com.example.ui.components.MasonryStillsGalleryComponent
import com.example.ui.components.PlatformSettingsDialog
import com.example.ui.components.ProfileSection
import com.example.ui.components.PublishGameDialog
import com.example.ui.components.Quantum2048Game
import com.example.ui.components.RemoteGalleryUiState
import com.example.ui.components.SearchDialog
import com.example.ui.components.SiteFooter
import com.example.ui.components.SiteHeader
import com.example.ui.components.SocialChatDialog
import com.example.ui.components.SocialHubSection
import com.example.ui.components.StillLightboxDialog
import com.example.ui.components.UserCreationDetailDialog
import com.example.ui.components.VoiceCallDialog
import com.example.ui.components.VoidRunnerGame
import com.example.ui.components.WebGameViewerDialog
import com.example.ui.components.WritingReaderDialog
import com.example.ui.components.WritingSection
import com.example.ui.theme.NovaHubTheme
import com.example.ui.theme.StudioDarkBg
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: StudioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Configure App Check with launch intent before any UI or Firebase operations
        com.example.util.configureAppCheck(this, intent)
        enableEdgeToEdge()
        setContent {
            val platformSettings by viewModel.platformSettings.collectAsStateWithLifecycle()
            NovaHubTheme(accentTheme = platformSettings.accentTheme) {
                StudioApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun StudioApp(viewModel: StudioViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val currentSection by viewModel.currentSection.collectAsStateWithLifecycle()
    val activeNativeGame by viewModel.activeNativeGame.collectAsStateWithLifecycle()
    val highScore by viewModel.voidRunnerHighScore.collectAsStateWithLifecycle()
    val cyberBreakerHighScore by viewModel.cyberBreakerHighScore.collectAsStateWithLifecycle()
    val quantum2048HighScore by viewModel.quantum2048HighScore.collectAsStateWithLifecycle()
    val totalArcadeScore by viewModel.totalArcadeScore.collectAsStateWithLifecycle()
    val firebaseUid by viewModel.firebaseUid.collectAsStateWithLifecycle()
    val remoteGalleryState by viewModel.remoteGalleryState.collectAsStateWithLifecycle()
    val remoteGalleryActionMessage by viewModel.remoteGalleryActionMessage.collectAsStateWithLifecycle()
    val arcadeLeaderboard by viewModel.arcadeLeaderboard.collectAsStateWithLifecycle()
    val activeUserProfile by viewModel.activeUserProfile.collectAsStateWithLifecycle()
    val activeLightboxStill by viewModel.activeLightboxStill.collectAsStateWithLifecycle()
    val selectedStillCat by viewModel.selectedStillCategory.collectAsStateWithLifecycle()
    val favoriteStillIds by viewModel.favoriteStillIds.collectAsStateWithLifecycle()
    val contactForm by viewModel.contactForm.collectAsStateWithLifecycle()
    val savedDispatches by viewModel.savedDispatches.collectAsStateWithLifecycle()
    val userCreations by viewModel.userCreations.collectAsStateWithLifecycle()
    val selectedCreationFilter by viewModel.selectedCreationFilter.collectAsStateWithLifecycle()
    val activeCreationDetail by viewModel.activeCreationDetail.collectAsStateWithLifecycle()
    val activeWebGame by viewModel.activeWebGame.collectAsStateWithLifecycle()
    val authUiState by viewModel.authUiState.collectAsStateWithLifecycle()
    val searchUiState by viewModel.searchUiState.collectAsStateWithLifecycle()
    val writings by viewModel.filteredWritings.collectAsStateWithLifecycle()
    val selectedWritingCategory by viewModel.selectedWritingCategory.collectAsStateWithLifecycle()
    val writingSearchQuery by viewModel.writingSearchQuery.collectAsStateWithLifecycle()
    val studioSearchQuery by viewModel.studioSearchQuery.collectAsStateWithLifecycle()
    val filteredCreations by viewModel.filteredCreations.collectAsStateWithLifecycle()
    val filteredStills by viewModel.filteredStills.collectAsStateWithLifecycle()
    val activeReadingWriting by viewModel.activeReadingWriting.collectAsStateWithLifecycle()
    val isComposeWritingDialogOpen by viewModel.isComposeWritingDialogOpen.collectAsStateWithLifecycle()
    val noteText by viewModel.noteText.collectAsStateWithLifecycle()
    val noteSaveSuccess by viewModel.noteSaveSuccess.collectAsStateWithLifecycle()

    // Social & Community State
    val socialPosts by viewModel.socialPosts.collectAsStateWithLifecycle()
    val newPostContent by viewModel.newPostContent.collectAsStateWithLifecycle()
    val newPostMediaType by viewModel.newPostMediaType.collectAsStateWithLifecycle()
    val newPostMediaUri by viewModel.newPostMediaUri.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val chatInputText by viewModel.chatInputText.collectAsStateWithLifecycle()
    val isChatOpen by viewModel.isChatOpen.collectAsStateWithLifecycle()
    val isVoiceCallActive by viewModel.isVoiceCallActive.collectAsStateWithLifecycle()
    val callPeerName by viewModel.callPeerName.collectAsStateWithLifecycle()
    val callDurationSeconds by viewModel.callDurationSeconds.collectAsStateWithLifecycle()
    val isCallMuted by viewModel.isCallMuted.collectAsStateWithLifecycle()
    val platformSettings by viewModel.platformSettings.collectAsStateWithLifecycle()
    val isSettingsOpen by viewModel.isSettingsOpen.collectAsStateWithLifecycle()
    val isPublishGameDialogOpen by viewModel.isPublishGameDialogOpen.collectAsStateWithLifecycle()

    fun launchEmailIntent(recipient: String, subject: String = "", body: String = "") {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$recipient")
            if (subject.isNotEmpty()) putExtra(Intent.EXTRA_SUBJECT, subject)
            if (body.isNotEmpty()) putExtra(Intent.EXTRA_TEXT, body)
        }
        try {
            context.startActivity(Intent.createChooser(intent, "Transmit via Email"))
        } catch (e: Exception) {
            Toast.makeText(context, "Direct email to $recipient", Toast.LENGTH_LONG).show()
        }
    }

    fun scrollToSection(section: String) {
        viewModel.setCurrentSection(section)
        scope.launch {
            val targetIndex = when (section) {
                "hero" -> 0
                "social" -> 1
                "portal", "games" -> 2
                "cloud_gallery" -> 3
                "masonry", "stills", "creations" -> 4
                "writing" -> 5
                "profile" -> 6
                "contact" -> 7
                "about" -> 8
                else -> 0
            }
            listState.animateScrollToItem(targetIndex)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioDarkBg)
    ) {
        AnimatedContent(
            targetState = activeNativeGame,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "game_view_transition"
        ) { nativeGame ->
            when (nativeGame) {
                ActiveNativeGame.VOID_RUNNER -> {
                    VoidRunnerGame(
                        highScore = highScore,
                        onScoreSaved = { score, shards ->
                            viewModel.recordNativeGameScore("void_runner", "Void Runner", score, shards)
                        },
                        onCloseGame = {
                            viewModel.launchNativeGame(ActiveNativeGame.NONE)
                        }
                    )
                }
                ActiveNativeGame.CYBER_BREAKER -> {
                    CyberBrickBreakerGame(
                        highScore = cyberBreakerHighScore,
                        onScoreSaved = { score, bricksBroken ->
                            viewModel.recordNativeGameScore("cyber_breaker", "Cyber Brick Breaker", score, bricksBroken)
                        },
                        onCloseGame = {
                            viewModel.launchNativeGame(ActiveNativeGame.NONE)
                        }
                    )
                }
                ActiveNativeGame.QUANTUM_2048 -> {
                    Quantum2048Game(
                        highScore = quantum2048HighScore,
                        onScoreSaved = { score, maxTile ->
                            viewModel.recordNativeGameScore("quantum_2048", "Quantum 2048", score, maxTile)
                        },
                        onCloseGame = {
                            viewModel.launchNativeGame(ActiveNativeGame.NONE)
                        }
                    )
                }
                ActiveNativeGame.NONE -> {
                    Scaffold(
                        topBar = {
                            SiteHeader(
                                activeSection = currentSection,
                                currentUser = authUiState.currentUser,
                                onNavigate = { route -> scrollToSection(route) },
                                onQuickPlay = { viewModel.launchNativeGame(ActiveNativeGame.VOID_RUNNER) },
                                onOpenAuth = { viewModel.openAuthDialog() },
                                onOpenSearch = { viewModel.openSearch() },
                                onOpenChat = { viewModel.openChatDialog() },
                                onOpenSettings = { viewModel.openSettingsDialog() },
                                searchQuery = studioSearchQuery,
                                onSearchQueryChange = { viewModel.setStudioSearchQuery(it) },
                                matchingCreationsCount = filteredCreations.size,
                                matchingWritingsCount = writings.size,
                                onNavigateToGallery = { scrollToSection("masonry") },
                                onNavigateToWriting = { scrollToSection("writing") }
                            )
                        },
                        containerColor = StudioDarkBg
                    ) { innerPadding ->
                        val firstVisibleItemIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }
                        val firstVisibleItemScrollOffset by remember { derivedStateOf { listState.firstVisibleItemScrollOffset } }

                        // Scroll-triggered dynamic reveals for Portal (2), Stills (4), About (8)
                        val gamesIsScrolledIn = firstVisibleItemIndex >= 1 || (firstVisibleItemIndex == 0 && firstVisibleItemScrollOffset > 250)
                        val gamesAnimProgress by animateFloatAsState(
                            targetValue = if (gamesIsScrolledIn) 1f else 0.94f,
                            animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
                            label = "games_scroll_anim"
                        )
                        val gamesAlpha by animateFloatAsState(
                            targetValue = if (gamesIsScrolledIn) 1f else 0.7f,
                            animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
                            label = "games_scroll_alpha"
                        )

                        val stillsIsScrolledIn = firstVisibleItemIndex >= 3 || (firstVisibleItemIndex == 2 && firstVisibleItemScrollOffset > 250)
                        val stillsAnimProgress by animateFloatAsState(
                            targetValue = if (stillsIsScrolledIn) 1f else 0.94f,
                            animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
                            label = "stills_scroll_anim"
                        )
                        val stillsAlpha by animateFloatAsState(
                            targetValue = if (stillsIsScrolledIn) 1f else 0.7f,
                            animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
                            label = "stills_scroll_alpha"
                        )

                        val aboutIsScrolledIn = firstVisibleItemIndex >= 7 || (firstVisibleItemIndex == 6 && firstVisibleItemScrollOffset > 250)
                        val aboutAnimProgress by animateFloatAsState(
                            targetValue = if (aboutIsScrolledIn) 1f else 0.94f,
                            animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
                            label = "about_scroll_anim"
                        )
                        val aboutAlpha by animateFloatAsState(
                            targetValue = if (aboutIsScrolledIn) 1f else 0.7f,
                            animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
                            label = "about_scroll_alpha"
                        )

                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            item(key = "hero") {
                                HeroSection(
                                    onPlayGame = { viewModel.launchNativeGame(ActiveNativeGame.VOID_RUNNER) },
                                    onExploreStills = { scrollToSection("cloud_gallery") },
                                    onWriteIn = { scrollToSection("contact") },
                                    onOpenSocial = { scrollToSection("social") }
                                )
                            }

                        item(key = "social") {
                            SocialHubSection(
                                posts = socialPosts,
                                currentUser = authUiState.currentUser,
                                newPostContent = newPostContent,
                                newPostMediaType = newPostMediaType,
                                newPostMediaUri = newPostMediaUri,
                                onContentChange = { viewModel.updateNewPostContent(it) },
                                onSetMedia = { type, uri -> viewModel.setNewPostMedia(type, uri) },
                                onClearMedia = { viewModel.clearNewPostMedia() },
                                onPublishPost = { viewModel.publishSocialPost() },
                                onToggleLike = { post -> viewModel.toggleLikeSocialPost(post) },
                                onReportPost = { id -> viewModel.reportSocialPost(id) },
                                onDeletePost = { id -> viewModel.deleteSocialPost(id) },
                                onOpenChat = { viewModel.openChat() }
                            )
                        }

                        item(key = "portal") {
                            Box(
                                modifier = Modifier
                                    .graphicsLayer {
                                        scaleX = gamesAnimProgress
                                        scaleY = gamesAnimProgress
                                        alpha = gamesAlpha
                                    }
                            ) {
                                GamePortalComponent(
                                    games = viewModel.games,
                                    voidRunnerHighScore = highScore,
                                    cyberBreakerHighScore = cyberBreakerHighScore,
                                    quantum2048HighScore = quantum2048HighScore,
                                    soundEffectsEnabled = platformSettings.soundEffectsEnabled,
                                    onToggleSoundEffects = { viewModel.toggleSoundEffects() },
                                    onLaunchNativeGame = { game -> viewModel.launchNativeGame(game) },
                                    onLaunchWebGame = { game -> viewModel.launchWebGame(game) },
                                    onOpenPublishGameDialog = { viewModel.openPublishGameDialog() }
                                )
                            }
                        }

                        item(key = "cloud_gallery") {
                            FirestoreImageGalleryComponent(
                                uiState = remoteGalleryState,
                                isFirebaseSignedIn = firebaseUid != null,
                                currentFirebaseUid = firebaseUid,
                                actionMessage = remoteGalleryActionMessage,
                                onOpenAuth = { viewModel.openAuthDialog() },
                                onAddRemoteImage = { title, url, cat, desc, vis ->
                                    viewModel.addRemoteGalleryImage(title, url, cat, desc, vis)
                                },
                                onLikeImage = { img -> viewModel.likeRemoteGalleryImage(img) },
                                onDeleteImage = { id -> viewModel.deleteRemoteGalleryImage(id) },
                                onDismissMessage = { viewModel.clearRemoteGalleryActionMessage() }
                            )
                        }

                        item(key = "masonry") {
                            Box(
                                modifier = Modifier
                                    .graphicsLayer {
                                        scaleX = stillsAnimProgress
                                        scaleY = stillsAnimProgress
                                        alpha = stillsAlpha
                                    }
                            ) {
                                MasonryStillsGalleryComponent(
                                    curatedStills = viewModel.allStills,
                                    userUploads = userCreations,
                                    favoriteIds = favoriteStillIds,
                                    onToggleFavorite = { stillId -> viewModel.toggleFavoriteStill(stillId) },
                                    onAddPersonalPhoto = { title, loc, uri -> viewModel.addPersonalStill(title, loc, uri) },
                                    searchQuery = studioSearchQuery,
                                    onSearchQueryChange = { viewModel.setStudioSearchQuery(it) }
                                )
                            }
                        }

                        item(key = "writing") {
                            WritingSection(
                                writings = writings,
                                selectedCategory = selectedWritingCategory,
                                searchQuery = studioSearchQuery,
                                onSelectCategory = { cat -> viewModel.selectWritingCategory(cat) },
                                onSearchQueryChange = { q -> viewModel.setStudioSearchQuery(q) },
                                onReadWriting = { w -> viewModel.openWritingReader(w) },
                                onTogglePin = { w -> viewModel.togglePinWriting(w) },
                                onDeleteWriting = { id -> viewModel.deleteWriting(id) },
                                onOpenCompose = { viewModel.openComposeWritingDialog() },
                                currentUser = authUiState.currentUser,
                                noteText = noteText,
                                onNoteTextChange = { viewModel.updateNoteText(it) },
                                onSaveNote = { viewModel.saveNote() },
                                noteSaveSuccess = noteSaveSuccess
                            )
                        }

                        item(key = "profile") {
                            val remoteImagesCount = (remoteGalleryState as? RemoteGalleryUiState.Success)?.images?.size ?: 0
                            ProfileSection(
                                user = activeUserProfile,
                                isFirebaseSignedIn = firebaseUid != null,
                                voidRunnerBest = highScore,
                                cyberBreakerBest = cyberBreakerHighScore,
                                quantum2048Best = quantum2048HighScore,
                                totalArcadeScore = totalArcadeScore,
                                socialPostsCount = socialPosts.size,
                                writingsCount = writings.size,
                                remoteImagesCount = remoteImagesCount,
                                activeAccentTheme = platformSettings.accentTheme,
                                leaderboardScores = arcadeLeaderboard,
                                onSelectAccentTheme = { theme -> viewModel.setAccentTheme(theme) },
                                onSaveProfile = { displayName, handle, roleTitle, bio, initials, avatarUrl, accentTheme ->
                                    viewModel.saveUserProfile(
                                        displayName = displayName,
                                        handle = handle,
                                        roleTitle = roleTitle,
                                        bio = bio,
                                        initials = initials,
                                        avatarUrl = avatarUrl,
                                        accentTheme = accentTheme
                                    )
                                },
                                onOpenAuth = { viewModel.openAuthDialog() }
                            )
                        }

                        item(key = "contact") {
                            ContactSection(
                                formState = contactForm,
                                savedDispatches = savedDispatches,
                                currentUser = authUiState.currentUser,
                                onOpenAuth = { viewModel.openAuthDialog() },
                                onNameChange = { viewModel.updateName(it) },
                                onEmailChange = { viewModel.updateEmail(it) },
                                onCategoryChange = { viewModel.updateCategory(it) },
                                onMessageChange = { viewModel.updateMessage(it) },
                                onSubmit = {
                                    viewModel.submitDispatch { name, email, cat, msg ->
                                        launchEmailIntent(
                                            recipient = "hello@novahub.community",
                                            subject = "NovaHub Dispatch [$cat] from $name",
                                            body = "From: $name ($email)\nCategory: $cat\n\n$msg"
                                        )
                                    }
                                },
                                onDeleteDispatch = { id -> viewModel.deleteDispatch(id) },
                                onDirectEmail = { email ->
                                    launchEmailIntent(
                                        recipient = email,
                                        subject = "Inquiry for NovaHub Community"
                                    )
                                },
                                onClearDraft = { viewModel.clearWriteInDraft() }
                            )
                        }

                        item(key = "about") {
                            Box(
                                modifier = Modifier
                                    .graphicsLayer {
                                        scaleX = aboutAnimProgress
                                        scaleY = aboutAnimProgress
                                        alpha = aboutAlpha
                                    }
                            ) {
                                AboutSection()
                            }
                        }

                        item(key = "footer") {
                            SiteFooter(
                                onScrollToTop = { scrollToSection("hero") }
                            )
                        }
                    }
                }
            }
        }
        }

        // Lightbox dialog for viewing still photo in full detail
        activeLightboxStill?.let { still ->
            val isFav = favoriteStillIds.contains(still.id)
            StillLightboxDialog(
                still = still,
                isFavorite = isFav,
                onToggleFavorite = { viewModel.toggleFavoriteStill(still.id) },
                onNextStill = { viewModel.nextLightboxStill() },
                onPreviousStill = { viewModel.previousLightboxStill() },
                onDismiss = { viewModel.closeLightbox() }
            )
        }

        // Lightbox dialog for viewing user-generated creation detail
        activeCreationDetail?.let { creation ->
            UserCreationDetailDialog(
                creation = creation,
                onClose = { viewModel.closeCreationDetail() },
                onDelete = { id -> viewModel.deleteCreation(id) }
            )
        }

        // In-app interactive web browser game sandbox dialog
        activeWebGame?.let { webGame ->
            WebGameViewerDialog(
                game = webGame,
                onDismiss = { viewModel.closeWebGame() }
            )
        }

        // Auth Dialog for Login & Account Registration
        if (authUiState.isAuthDialogOpen) {
            AuthDialog(
                authUiState = authUiState,
                onUsernameChange = { viewModel.setAuthUsernameInput(it) },
                onEmailChange = { viewModel.setAuthEmailInput(it) },
                onPasswordChange = { viewModel.setAuthPasswordInput(it) },
                onRoleChange = { viewModel.setAuthRoleTitleInput(it) },
                onToggleMode = { viewModel.toggleAuthMode() },
                onLoginSubmit = { viewModel.login() },
                onRegisterSubmit = { viewModel.register() },
                onGoogleSignInToken = { viewModel.signInWithGoogleIdToken(it) },
                onAuthLoading = { viewModel.setAuthLoading(it) },
                onAuthError = { viewModel.setAuthError(it) },
                onLogout = { viewModel.logout() },
                onDismiss = { viewModel.closeAuthDialog() }
            )
        }

        // Writing Reader Dialog for full-screen distraction-free reading
        activeReadingWriting?.let { writing ->
            WritingReaderDialog(
                writing = writing,
                onDismiss = { viewModel.closeWritingReader() }
            )
        }

        // Compose and persist a new studio essay to Room database
        if (isComposeWritingDialogOpen) {
            ComposeWritingDialog(
                onSaveWriting = { title, category, summary, content, tags, readTime, author, isPinned ->
                    viewModel.saveNewWriting(title, category, summary, content, tags, readTime, author, isPinned)
                },
                onDismiss = { viewModel.closeComposeWritingDialog() },
                currentUser = authUiState.currentUser
            )
        }

        // Universal Search Dialog
        if (searchUiState.isSearchActive) {
            SearchDialog(
                searchState = searchUiState,
                onQueryChange = { viewModel.updateSearchQuery(it) },
                onSelectResult = { result ->
                    viewModel.closeSearch()
                    when (result) {
                        is SearchResultItem.GameItem -> {
                            scrollToSection("games")
                        }
                        is SearchResultItem.StillItem -> {
                            val targetStill = viewModel.allStills.firstOrNull { it.id == result.stillId }
                            if (targetStill != null) {
                                viewModel.openLightbox(targetStill)
                            } else {
                                scrollToSection("stills")
                            }
                        }
                        is SearchResultItem.CreationItem -> {
                            val targetCreation = viewModel.userCreations.value.firstOrNull { it.id == result.creationId }
                            if (targetCreation != null) {
                                viewModel.openCreationDetail(targetCreation)
                            } else {
                                scrollToSection("creations")
                            }
                        }
                        is SearchResultItem.WritingItem -> {
                            val targetWriting = viewModel.allWritings.value.firstOrNull { it.id == result.writingId }
                            if (targetWriting != null) {
                                viewModel.openWritingReader(targetWriting)
                            } else {
                                scrollToSection("writing")
                            }
                        }
                        is SearchResultItem.NavigationItem -> {
                            scrollToSection(result.targetSection)
                        }
                    }
                },
                onDismiss = { viewModel.closeSearch() }
            )
        }

        // Community Live Chat Dialog
        if (isChatOpen) {
            SocialChatDialog(
                messages = chatMessages,
                inputText = chatInputText,
                onInputChange = { viewModel.updateChatInput(it) },
                onSendMessage = { viewModel.sendChatMessage() },
                onStartCall = { peerName -> viewModel.startVoiceCall(peerName) },
                onDismiss = { viewModel.closeChat() }
            )
        }

        // Live Voice Call Overlay Dialog
        if (isVoiceCallActive) {
            VoiceCallDialog(
                peerName = callPeerName,
                callSeconds = callDurationSeconds,
                isMuted = isCallMuted,
                onToggleMute = { viewModel.toggleMuteCall() },
                onSecondTick = { viewModel.tickCallSecond() },
                onEndCall = { viewModel.endVoiceCall() }
            )
        }

        // Platform Settings Dialog
        if (isSettingsOpen) {
            PlatformSettingsDialog(
                settings = platformSettings,
                onToggleSafeContent = { viewModel.toggleSafeContentFilter() },
                onToggleAutoPlay = { viewModel.toggleAutoPlayMedia() },
                onToggleAllowCalls = { viewModel.toggleAllowCalls() },
                onToggleAllowGameUploads = { viewModel.toggleAllowGameUploads() },
                onToggleSoundEffects = { viewModel.toggleSoundEffects() },
                onSelectAccentTheme = { theme -> viewModel.setAccentTheme(theme) },
                onDismiss = { viewModel.closeSettings() }
            )
        }

        // Publish Web Game Dialog
        if (isPublishGameDialogOpen) {
            PublishGameDialog(
                onDismiss = { viewModel.closePublishGameDialog() },
                onPublish = { title, sub, desc, url, tech, author, imgUri ->
                    viewModel.publishNewGame(title, sub, desc, url, tech, author, imgUri)
                },
                initialAuthor = authUiState.currentUser?.username ?: "Community Creator"
            )
        }
    }
}
