package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.R
import com.example.data.StudioGame
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioCardBorderSubtle
import com.example.ui.theme.StudioCyan
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.StudioEmerald
import com.example.ui.theme.StudioGold
import com.example.ui.theme.StudioRose
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceElevated
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary

/**
 * Dedicated Game Portal UI Component
 * Displays a list of browser-based and in-app kinetic games with rich tags, live launch actions,
 * leaderboards, and user-published game integration.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GamePortalComponent(
    games: List<StudioGame>,
    highScore: Int,
    onLaunchGame: () -> Unit,
    onLaunchWebGame: (StudioGame) -> Unit,
    onOpenPublishGameDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedGenre by remember { mutableStateOf("All") }
    var isGridView by remember { mutableStateOf(true) }

    // Expanded suite of browser-based games
    val allGamesList = remember(games) {
        val additionalBrowserGames = listOf(
            StudioGame(
                id = "hextris_browser",
                title = "Hextris: Hex Grid",
                subtitle = "Fast Hexagonal Kinetic Puzzle",
                description = "Rotate the hexagon to match incoming colored blocks before they overflow the perimeter. Ultra-smooth HTML5 Canvas 60fps.",
                techStack = listOf("HTML5", "Canvas", "Audio Synth"),
                status = "Browser Game",
                isPlayableInApp = false,
                imageResId = R.drawable.still_dhaka_night_1789660149613,
                accentColorHex = 0xFFF59E0B,
                gameplayNote = "Instant browser game playable directly inside studio interface.",
                webGameUrl = "https://hextris.io/",
                isWebGame = true
            ),
            StudioGame(
                id = "pacman_classic_browser",
                title = "Pacman HTML5 Retro",
                subtitle = "Classic Arcade Labyrinth",
                description = "Authentic retro arcade maze runner rewritten for modern browsers. Eat pellets, navigate ghosts, and rack up highscores.",
                techStack = listOf("HTML5", "WebGL", "Chiptune Audio"),
                status = "Browser Game",
                isPlayableInApp = false,
                imageResId = R.drawable.game_void_runner_1789660196140,
                accentColorHex = 0xFFEAB308,
                gameplayNote = "Iconic browser arcade game running via internal webview sandbox.",
                webGameUrl = "https://freepacman.org/",
                isWebGame = true
            )
        )

        val existingIds = games.map { it.id }.toSet()
        games + additionalBrowserGames.filter { it.id !in existingIds }
    }

    val genres = listOf("All", "Web Games", "Arcade", "Puzzle", "Retro Racer", "Community Creations")

    val filteredGames = remember(allGamesList, selectedGenre) {
        when (selectedGenre) {
            "Web Games" -> allGamesList.filter { it.isWebGame }
            "Arcade" -> allGamesList.filter { it.subtitle.contains("Arcade", ignoreCase = true) || it.title.contains("Void", ignoreCase = true) || it.title.contains("Pacman", ignoreCase = true) }
            "Puzzle" -> allGamesList.filter { it.subtitle.contains("Puzzle", ignoreCase = true) || it.title.contains("Echoes", ignoreCase = true) || it.title.contains("Tetris", ignoreCase = true) || it.title.contains("Hex", ignoreCase = true) }
            "Retro Racer" -> allGamesList.filter { it.subtitle.contains("Racer", ignoreCase = true) || it.title.contains("Drift", ignoreCase = true) }
            "Community Creations" -> allGamesList.filter { it.isUserPublished }
            else -> allGamesList
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp)
            .testTag("game_portal_component")
    ) {
        // Section Header Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(StudioCyan)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "03 / GAME PORTAL & BROWSER ARCADE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = StudioCyan,
                    letterSpacing = 1.4.sp
                )
            }

            // Publish Web Game Button
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = StudioCyan.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, StudioCyan.copy(alpha = 0.5f)),
                modifier = Modifier
                    .clickable(onClick = onOpenPublishGameDialog)
                    .testTag("portal_publish_game_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = StudioCyan,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "+ গেম বা অ্যাপ যুক্ত করুন",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudioCyan
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Interactive Game Portal",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = StudioTextPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Browser-based games and tactile 60FPS experiments. Tap any title to launch and play immediately within the studio interface.",
            style = MaterialTheme.typography.bodyMedium,
            color = StudioTextSecondary,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Featured In-App Canvas Game Banner (Void Runner)
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = StudioSurfaceElevated),
            border = BorderStroke(1.dp, StudioGold.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onLaunchGame)
                .testTag("featured_void_runner_card")
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    AsyncImage(
                        model = R.drawable.game_void_runner_1789660196140,
                        contentDescription = "Void Runner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color(0xE008090C))
                                )
                            )
                    )

                    // Top Row Badges
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = StudioGold
                        ) {
                            Text(
                                text = "FEATURED IN-APP DEMO",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = StudioDarkBg,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        if (highScore > 0) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xCC000000),
                                border = BorderStroke(1.dp, StudioGold)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Leaderboard,
                                        contentDescription = null,
                                        tint = StudioGold,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "High Score: $highScore",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = StudioGold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }

                    // Bottom info on banner
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Void Runner: Kinetic Dodge",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = StudioTextPrimary
                        )
                        Text(
                            text = "Sub-16ms tactile 2D canvas physics & real-time particle collision engine.",
                            fontSize = 12.sp,
                            color = StudioTextSecondary
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Instant 60FPS • No Web Download Required",
                        fontSize = 11.sp,
                        color = StudioEmerald,
                        fontWeight = FontWeight.SemiBold
                    )

                    Button(
                        onClick = onLaunchGame,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StudioGold,
                            contentColor = StudioDarkBg
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Play Demo", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Featured Live Web Game Banner (Care Survey 89)
        val careSurveyGame = remember(allGamesList) {
            allGamesList.firstOrNull { it.id == "care_survey_89" } ?: StudioGame(
                id = "care_survey_89",
                title = "Care Survey 89",
                subtitle = "Interactive Web Game & Experience",
                description = "নতুন যুক্ত করা সরাসরি অনলাইন ওয়েব গেম ও ইন্টারঅ্যাক্টিভ এক্সপেরিয়েন্স — কোনো ডাউনলোড ছাড়াই ইন-অ্যাপ WebView-তে খেলুন।",
                techStack = listOf("HTML5", "Cloudflare Workers", "Interactive Web"),
                status = "Featured Live",
                isPlayableInApp = true,
                imageResId = R.drawable.ai_obsidian_supercar_1789701616237,
                accentColorHex = 0xFF10B981,
                gameplayNote = "Playable live inside NovaHub WebView Arcade.",
                webGameUrl = "https://care-survey-89.prantoofficial46.workers.dev/",
                isWebGame = true
            )
        }

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = StudioSurfaceElevated),
            border = BorderStroke(1.dp, StudioEmerald.copy(alpha = 0.65f)),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onLaunchWebGame(careSurveyGame) }
                .testTag("featured_care_survey_card")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = StudioEmerald
                    ) {
                        Text(
                            text = "LIVE WEB GAME • NEW",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = StudioDarkBg,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = careSurveyGame.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudioTextPrimary
                    )
                    Text(
                        text = careSurveyGame.description,
                        fontSize = 11.sp,
                        color = StudioTextSecondary,
                        lineHeight = 16.sp,
                        maxLines = 2
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = { onLaunchWebGame(careSurveyGame) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StudioEmerald,
                        contentColor = StudioDarkBg
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RocketLaunch,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("Play Now", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Genre filter pills & Grid/List view toggle row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                genres.forEach { genre ->
                    val isSelected = genre == selectedGenre
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) StudioCyan else StudioSurfaceElevated)
                            .border(
                                1.dp,
                                if (isSelected) StudioCyan else StudioCardBorderSubtle,
                                RoundedCornerShape(6.dp)
                            )
                            .clickable { selectedGenre = genre }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("portal_genre_$genre")
                    ) {
                        Text(
                            text = genre,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) StudioDarkBg else StudioTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Grid / List View Toggle button
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = StudioSurfaceElevated,
                border = BorderStroke(1.dp, StudioCardBorder),
                modifier = Modifier
                    .clickable { isGridView = !isGridView }
                    .testTag("portal_toggle_layout_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isGridView) Icons.Default.GridView else Icons.Default.ViewAgenda,
                        contentDescription = "Toggle Grid or List View",
                        tint = StudioCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isGridView) "Grid" else "List",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudioCyan
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Grid-based Dashboard UI vs List View of Browser-Based Games
        if (isGridView) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                maxItemsInEachRow = 2
            ) {
                filteredGames.forEach { game ->
                    BrowserGameGridCard(
                        game = game,
                        onLaunch = {
                            if (game.isPlayableInApp && !game.isWebGame) {
                                onLaunchGame()
                            } else {
                                onLaunchWebGame(game)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                filteredGames.forEach { game ->
                    BrowserGamePortalCard(
                        game = game,
                        onLaunch = {
                            if (game.isPlayableInApp && !game.isWebGame) {
                                onLaunchGame()
                            } else {
                                onLaunchWebGame(game)
                            }
                        }
                    )
                }
            }
        }
    }
}

/**
 * Grid-based Dashboard Card for Browser-Based Game
 * Displays rich game icon, title, tech tags, and triggers game launch in WebView
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BrowserGameGridCard(
    game: StudioGame,
    onLaunch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = Color(game.accentColorHex)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = StudioSurface),
        border = BorderStroke(1.dp, StudioCardBorder),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onLaunch)
            .testTag("game_grid_card_${game.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Top Row: Game Icon + Genre Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Game Icon / Thumbnail using Coil
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(StudioSurfaceElevated)
                        .border(1.dp, StudioCardBorderSubtle, RoundedCornerShape(10.dp))
                ) {
                    val imageSource = if (game.imageUri.isNotBlank()) game.imageUri else game.imageResId
                    AsyncImage(
                        model = imageSource,
                        contentDescription = game.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(accentColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = null,
                            tint = StudioDarkBg,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }

                // Playable Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(accentColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (game.isWebGame) "WEBVIEW" else "CANVAS",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title
            Text(
                text = game.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = StudioTextPrimary,
                maxLines = 1
            )

            // Subtitle
            Text(
                text = game.subtitle,
                fontSize = 11.sp,
                color = StudioTextSecondary,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Description snippet
            Text(
                text = game.description,
                fontSize = 10.sp,
                color = StudioTextMuted,
                lineHeight = 14.sp,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Launch Action Button
            Button(
                onClick = onLaunch,
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentColor,
                    contentColor = StudioDarkBg
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .testTag("launch_grid_game_${game.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Play Game",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Game Card for Browser-Based Game in the Portal
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BrowserGamePortalCard(
    game: StudioGame,
    onLaunch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val accentColor = Color(game.accentColorHex)

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = StudioSurface),
        border = BorderStroke(1.dp, StudioCardBorder),
        modifier = modifier
            .fillMaxWidth()
            .testTag("game_portal_card_${game.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Game Thumbnail (Using Coil)
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(StudioSurfaceElevated)
                        .border(1.dp, StudioCardBorderSubtle, RoundedCornerShape(10.dp))
                ) {
                    val imageSource = if (game.imageUri.isNotBlank()) {
                        game.imageUri
                    } else if (game.imageResId != 0) {
                        game.imageResId
                    } else {
                        R.drawable.game_void_runner_1789660196140
                    }

                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(imageSource)
                            .crossfade(true)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .build(),
                        contentDescription = game.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Title, Subtitle & Badge
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = game.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = StudioTextPrimary
                        )

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = accentColor.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, accentColor.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = if (game.isUserPublished) "COMMUNITY" else "BROWSER",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = accentColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = game.subtitle,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = accentColor
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = game.description,
                        fontSize = 12.sp,
                        color = StudioTextSecondary,
                        lineHeight = 17.sp,
                        maxLines = 2
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tech Stack Tags
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                game.techStack.forEach { tag ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(StudioSurfaceElevated)
                            .border(1.dp, StudioCardBorderSubtle, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = tag,
                            fontSize = 10.sp,
                            color = StudioTextMuted,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Launch Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = null,
                        tint = StudioEmerald,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Runs in Studio Sandbox",
                        fontSize = 11.sp,
                        color = StudioEmerald,
                        fontWeight = FontWeight.Medium
                    )
                }

                Button(
                    onClick = onLaunch,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor,
                        contentColor = StudioDarkBg
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RocketLaunch,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Launch In Studio", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
