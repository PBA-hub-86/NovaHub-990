package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.HourglassEmpty
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StudioGame
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioCardBorderSubtle
import com.example.ui.theme.StudioCyan
import com.example.ui.theme.StudioEmerald
import com.example.ui.theme.StudioGold
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceElevated
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GamesSection(
    games: List<StudioGame>,
    highScore: Int,
    onLaunchGame: () -> Unit,
    onLaunchWebGame: ((StudioGame) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var isGridView by remember { mutableStateOf(true) }

    val placeholderUpcomingGames = listOf(
        PlaceholderGame(
            title = "Orbital Synapse",
            genre = "Kinetic Physics WebGL",
            badge = "Incoming",
            statusNote = "In Prototype",
            accentColor = StudioCyan
        ),
        PlaceholderGame(
            title = "Monsoon Drifter 3D",
            genre = "Pseudo-3D Vaporwave",
            badge = "Q4 Alpha",
            statusNote = "Procedural Audio",
            accentColor = StudioGold
        )
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
            .testTag("games_section")
    ) {
        // Section index
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "02 / BROWSER GAMES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = StudioGold,
                letterSpacing = 1.5.sp
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(StudioSurfaceElevated)
                        .border(1.dp, StudioCardBorder, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null,
                            tint = StudioCyan,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "GRID PLAY READY",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = StudioCyan,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Grid / List View Toggle
                IconButton(
                    onClick = { isGridView = !isGridView },
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(StudioSurfaceElevated)
                        .border(1.dp, StudioCardBorder, RoundedCornerShape(6.dp))
                        .size(28.dp)
                        .testTag("toggle_games_layout_btn")
                ) {
                    Icon(
                        imageVector = if (isGridView) Icons.Default.ViewAgenda else Icons.Default.GridView,
                        contentDescription = "Toggle Grid or List View",
                        tint = StudioTextSecondary,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Browser Games & Kinetic Experiments",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = StudioTextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Each game is an exploration in lean interactive loops — launch instantly within the in-app sandbox or full browser.",
            style = MaterialTheme.typography.bodyLarge,
            color = StudioTextSecondary,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (isGridView) {
            // 2-Column Responsive Grid Layout
            val chunkedGames = games.chunked(2)
            chunkedGames.forEach { rowGames ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowGames.forEach { game ->
                        GameGridCard(
                            game = game,
                            highScore = if (game.id == "void_runner") highScore else null,
                            onPlayNative = if (game.isPlayableInApp) onLaunchGame else null,
                            onPlayWeb = if (game.isWebGame && onLaunchWebGame != null) {
                                { onLaunchWebGame(game) }
                            } else null,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowGames.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            // Placeholder Game Cards in the Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                placeholderUpcomingGames.forEach { placeholder ->
                    GamePlaceholderCard(
                        placeholder = placeholder,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        } else {
            // Detailed Showcase Cards
            games.forEachIndexed { index, game ->
                GameCard(
                    game = game,
                    highScore = if (game.id == "void_runner") highScore else null,
                    onPlayNative = if (game.isPlayableInApp) onLaunchGame else null,
                    onPlayWeb = if (game.isWebGame && onLaunchWebGame != null) {
                        { onLaunchWebGame(game) }
                    } else null,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    }
}

private data class PlaceholderGame(
    val title: String,
    val genre: String,
    val badge: String,
    val statusNote: String,
    val accentColor: Color
)

@Composable
private fun GamePlaceholderCard(
    placeholder: PlaceholderGame,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = StudioSurface.copy(alpha = 0.6f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorderSubtle),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.testTag("game_placeholder_${placeholder.title}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header with badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(StudioSurfaceElevated)
                        .border(1.dp, StudioCardBorder, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = placeholder.badge.uppercase(),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = placeholder.accentColor,
                        letterSpacing = 0.5.sp
                    )
                }

                Icon(
                    imageVector = Icons.Default.HourglassEmpty,
                    contentDescription = null,
                    tint = StudioTextMuted,
                    modifier = Modifier.size(13.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Dashed or muted preview box placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 10f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(StudioSurfaceElevated)
                    .border(1.dp, StudioCardBorderSubtle, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.RocketLaunch,
                        contentDescription = null,
                        tint = StudioTextMuted.copy(alpha = 0.7f),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "NEW LAB EXPERIMENT",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudioTextMuted,
                        letterSpacing = 0.8.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = placeholder.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = StudioTextPrimary,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = placeholder.genre,
                fontSize = 10.sp,
                color = StudioTextSecondary,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Disabled/Upcoming action slot
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(StudioSurfaceElevated)
                    .border(1.dp, StudioCardBorderSubtle, RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.HourglassEmpty,
                        contentDescription = null,
                        tint = StudioTextMuted,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = placeholder.statusNote,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = StudioTextMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun GameGridCard(
    game: StudioGame,
    highScore: Int?,
    onPlayNative: (() -> Unit)?,
    onPlayWeb: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = StudioSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .testTag("game_grid_card_${game.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Artwork Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 10f)
            ) {
                Image(
                    painter = painterResource(id = game.imageResId),
                    contentDescription = game.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )

                // Top-right status pill
                Box(
                    modifier = Modifier
                        .padding(6.dp)
                        .align(Alignment.TopEnd)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF08090C).copy(alpha = 0.85f))
                        .border(1.dp, StudioCardBorder, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (game.isPlayableInApp) "NATIVE" else "WEB",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (game.isPlayableInApp) StudioGold else StudioCyan,
                        letterSpacing = 0.5.sp
                    )
                }

                if (highScore != null && highScore > 0) {
                    Box(
                        modifier = Modifier
                            .padding(6.dp)
                            .align(Alignment.BottomStart)
                            .clip(RoundedCornerShape(4.dp))
                            .background(StudioGold)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "BEST: $highScore",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF08090C)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(
                    text = game.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = StudioTextPrimary,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = game.subtitle,
                    fontSize = 10.sp,
                    color = StudioTextSecondary,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Launch Button
                val launchAction = onPlayNative ?: onPlayWeb
                Button(
                    onClick = { launchAction?.invoke() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (onPlayNative != null) StudioGold else StudioSurfaceElevated,
                        contentColor = if (onPlayNative != null) Color(0xFF08090C) else StudioTextPrimary
                    ),
                    border = if (onPlayNative == null) androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder) else null,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .testTag("launch_grid_${game.id}_btn")
                ) {
                    Icon(
                        imageVector = if (onPlayNative != null) Icons.Default.PlayArrow else Icons.Default.Public,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (onPlayNative != null) "Play Now" else "Launch",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GameCard(
    game: StudioGame,
    highScore: Int?,
    onPlayNative: (() -> Unit)?,
    onPlayWeb: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = StudioSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("game_card_${game.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Game Art Banner with Status Badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 8.5f)
            ) {
                Image(
                    painter = painterResource(id = game.imageResId),
                    contentDescription = "Artwork for ${game.title}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )

                // Dark subtle gradient overlay
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    StudioSurface.copy(alpha = 0.85f)
                                )
                            )
                        )
                )

                // Top badges row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF08090C).copy(alpha = 0.85f))
                            .border(1.dp, StudioCardBorder, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = game.status.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (game.isPlayableInApp) StudioEmerald else StudioCyan,
                            letterSpacing = 0.8.sp
                        )
                    }

                    if (highScore != null && highScore > 0) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF08090C).copy(alpha = 0.85f))
                                .border(1.dp, StudioGold.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "RECORD: $highScore",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = StudioGold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Details
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = game.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = StudioTextPrimary
                    )

                    if (game.isPlayableInApp) {
                        Text(
                            text = "● PLAYABLE NOW",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = StudioGold,
                            letterSpacing = 0.8.sp
                        )
                    } else if (game.isWebGame) {
                        Text(
                            text = "● WEB READY",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = StudioCyan,
                            letterSpacing = 0.8.sp
                        )
                    }
                }

                Text(
                    text = game.subtitle,
                    fontSize = 12.sp,
                    color = StudioGold,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = game.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = StudioTextSecondary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Tech stack tags
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    game.techStack.forEach { tech ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(StudioSurfaceElevated)
                                .border(1.dp, StudioCardBorderSubtle, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = tech,
                                fontSize = 10.sp,
                                color = StudioTextMuted
                            )
                        }
                    }
                }

                // Action buttons for launching the game
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (onPlayNative != null) {
                        Button(
                            onClick = onPlayNative,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = StudioGold,
                                contentColor = Color(0xFF08090C)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("launch_native_${game.id}_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Play Native 60fps",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    if (onPlayWeb != null) {
                        Button(
                            onClick = onPlayWeb,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (onPlayNative != null) StudioSurfaceElevated else StudioGold,
                                contentColor = if (onPlayNative != null) StudioTextPrimary else Color(0xFF08090C)
                            ),
                            border = if (onPlayNative != null) androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder) else null,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("launch_web_${game.id}_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Public,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (onPlayNative != null) "Play in Browser" else "Launch Web Game",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
