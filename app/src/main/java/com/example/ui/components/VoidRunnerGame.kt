package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioCyan
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.StudioEmerald
import com.example.ui.theme.StudioGold
import com.example.ui.theme.StudioGoldLight
import com.example.ui.theme.StudioRose
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary
import kotlin.math.hypot
import kotlin.random.Random

data class GameObstacle(
    val id: Long,
    var x: Float,
    var y: Float,
    val width: Float,
    val height: Float,
    val speed: Float,
    val isShard: Boolean // true = collectible golden shard, false = danger hazard
)

@Composable
fun VoidRunnerGame(
    highScore: Int,
    onScoreSaved: (score: Int, shards: Int) -> Unit,
    onCloseGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isGameOver by remember { mutableStateOf(false) }
    var currentScore by remember { mutableIntStateOf(0) }
    var shardsCollected by remember { mutableIntStateOf(0) }
    var playerNormalizedX by remember { mutableFloatStateOf(0.5f) } // 0.0f to 1.0f

    val obstacles = remember { mutableStateListOf<GameObstacle>() }
    var nextSpawnTime by remember { mutableFloatStateOf(0f) }
    var obstacleIdCounter by remember { mutableStateOf(0L) }

    fun restartGame() {
        obstacles.clear()
        currentScore = 0
        shardsCollected = 0
        playerNormalizedX = 0.5f
        isGameOver = false
        nextSpawnTime = 0f
    }

    // Game loop
    LaunchedEffect(isGameOver) {
        if (isGameOver) return@LaunchedEffect

        var lastFrameTime = 0L
        while (!isGameOver) {
            withFrameMillis { frameTimeMs ->
                if (lastFrameTime == 0L) {
                    lastFrameTime = frameTimeMs
                    return@withFrameMillis
                }

                val dt = (frameTimeMs - lastFrameTime).coerceIn(1L, 33L) / 1000f
                lastFrameTime = frameTimeMs

                // Increment score gradually over time
                currentScore += 1

                // Difficulty speed scale
                val baseSpeed = 0.35f + (currentScore / 2500f).coerceAtMost(0.45f)

                // Spawn obstacles or shards
                nextSpawnTime -= dt
                if (nextSpawnTime <= 0f) {
                    val isShard = Random.nextFloat() < 0.35f
                    val obsWidth = if (isShard) 0.07f else Random.nextFloat() * 0.18f + 0.12f
                    val obsHeight = if (isShard) 0.04f else 0.045f
                    val spawnX = Random.nextFloat() * (1f - obsWidth)

                    obstacles.add(
                        GameObstacle(
                            id = ++obstacleIdCounter,
                            x = spawnX,
                            y = -obsHeight,
                            width = obsWidth,
                            height = obsHeight,
                            speed = baseSpeed * (if (isShard) 1.1f else 1.0f),
                            isShard = isShard
                        )
                    )
                    nextSpawnTime = Random.nextFloat() * 0.35f + 0.30f
                }

                // Move obstacles
                val iterator = obstacles.listIterator()
                val playerY = 0.88f
                val playerWidth = 0.10f
                val playerHeight = 0.05f

                while (iterator.hasNext()) {
                    val obs = iterator.next()
                    obs.y += obs.speed * dt

                    // Check collision with player
                    val overlapX = (playerNormalizedX - playerWidth / 2f < obs.x + obs.width) &&
                            (playerNormalizedX + playerWidth / 2f > obs.x)
                    val overlapY = (playerY - playerHeight / 2f < obs.y + obs.height) &&
                            (playerY + playerHeight / 2f > obs.y)

                    if (overlapX && overlapY) {
                        if (obs.isShard) {
                            // Collect shard
                            shardsCollected += 1
                            currentScore += 100
                            iterator.remove()
                        } else {
                            // Hit hazard -> Game Over
                            isGameOver = true
                            onScoreSaved(currentScore, shardsCollected)
                            break
                        }
                    } else if (obs.y > 1.1f) {
                        // Remove out of bounds
                        iterator.remove()
                    }
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .testTag("void_runner_screen")
    ) {
        // Main Game Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(isGameOver) {
                    if (!isGameOver) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val deltaXNorm = dragAmount.x / size.width
                            playerNormalizedX = (playerNormalizedX + deltaXNorm).coerceIn(0.08f, 0.92f)
                        }
                    }
                }
        ) {
            val canvasW = size.width
            val canvasH = size.height

            // Background ambient speed lines / stars
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF08090C), Color(0xFF0D111A), Color(0xFF141926))
                )
            )

            // Draw falling items
            for (obs in obstacles) {
                val left = obs.x * canvasW
                val top = obs.y * canvasH
                val width = obs.width * canvasW
                val height = obs.height * canvasH

                if (obs.isShard) {
                    // Golden energy shard (rhombus prism)
                    val shardPath = Path().apply {
                        moveTo(left + width / 2f, top)
                        lineTo(left + width, top + height / 2f)
                        lineTo(left + width / 2f, top + height)
                        lineTo(left, top + height / 2f)
                        close()
                    }
                    drawPath(shardPath, color = StudioGold)
                    // Glow halo
                    drawCircle(
                        color = StudioGold.copy(alpha = 0.3f),
                        radius = width * 0.7f,
                        center = Offset(left + width / 2f, top + height / 2f)
                    )
                } else {
                    // Obsidian Void Hazard (sharp polygon)
                    val hazardPath = Path().apply {
                        moveTo(left, top)
                        lineTo(left + width, top)
                        lineTo(left + width * 0.8f, top + height)
                        lineTo(left + width * 0.2f, top + height)
                        close()
                    }
                    drawPath(hazardPath, color = Color(0xFFE11D48)) // Danger crimson / rose
                    // Core accent
                    drawCircle(
                        color = Color(0xFFFDA4AF),
                        radius = 3f,
                        center = Offset(left + width / 2f, top + height / 2f)
                    )
                }
            }

            // Draw Player Ship (Golden Resonance Prism)
            val px = playerNormalizedX * canvasW
            val py = 0.88f * canvasH
            val pw = 0.10f * canvasW
            val ph = 0.05f * canvasH

            // Engine thrust glow
            drawCircle(
                color = StudioCyan.copy(alpha = 0.5f),
                radius = pw * 0.55f,
                center = Offset(px, py + ph * 0.4f)
            )

            // Ship Triangle
            val playerPath = Path().apply {
                moveTo(px, py - ph) // Tip
                lineTo(px + pw / 2f, py + ph / 2f) // Right wing
                lineTo(px, py + ph / 4f) // Center indent
                lineTo(px - pw / 2f, py + ph / 2f) // Left wing
                close()
            }
            drawPath(playerPath, color = StudioGoldLight)

            // Inner core
            drawCircle(
                color = StudioCyan,
                radius = 5f,
                center = Offset(px, py)
            )
        }

        // Top HUD Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Close game button
            IconButton(
                onClick = onCloseGame,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(StudioSurface.copy(alpha = 0.8f))
                    .border(1.dp, StudioCardBorder, CircleShape)
                    .testTag("close_game_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Exit Void Runner",
                    tint = StudioTextPrimary
                )
            }

            // Stats row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(StudioSurface.copy(alpha = 0.85f))
                    .border(1.dp, StudioCardBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "SCORE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudioTextMuted
                    )
                    Text(
                        text = "$currentScore",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudioGold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "SHARDS",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudioTextMuted
                    )
                    Text(
                        text = "✦ $shardsCollected",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudioCyan,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "RECORD",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudioTextMuted
                    )
                    Text(
                        text = "${maxOf(highScore, currentScore)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StudioTextSecondary,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Onscreen Touch Controls for accessibility & easy navigation
        if (!isGameOver) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(StudioSurface.copy(alpha = 0.7f))
                        .border(1.dp, StudioCardBorder, CircleShape)
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = {
                                    playerNormalizedX = (playerNormalizedX - 0.08f).coerceAtLeast(0.08f)
                                },
                                onDrag = { change, _ ->
                                    change.consume()
                                    playerNormalizedX = (playerNormalizedX - 0.04f).coerceAtLeast(0.08f)
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Steer Left",
                        tint = StudioGold,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Text(
                    text = "Drag canvas or use arrows",
                    fontSize = 11.sp,
                    color = StudioTextMuted,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(StudioSurface.copy(alpha = 0.7f))
                        .border(1.dp, StudioCardBorder, CircleShape)
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = {
                                    playerNormalizedX = (playerNormalizedX + 0.08f).coerceAtMost(0.92f)
                                },
                                onDrag = { change, _ ->
                                    change.consume()
                                    playerNormalizedX = (playerNormalizedX + 0.04f).coerceAtMost(0.92f)
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Steer Right",
                        tint = StudioGold,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        // Game Over Overlay
        AnimatedVisibility(
            visible = isGameOver,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = StudioSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "RESONANCE SHATTERED",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudioRose,
                        letterSpacing = 1.5.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Run Complete",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = StudioTextPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0C0E14))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("FINAL SCORE", fontSize = 10.sp, color = StudioTextMuted)
                            Text(
                                text = "$currentScore",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = StudioGold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("SHARDS", fontSize = 10.sp, color = StudioTextMuted)
                            Text(
                                text = "✦ $shardsCollected",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = StudioCyan,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { restartGame() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = StudioGold,
                                contentColor = Color(0xFF08090C)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("restart_game_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Play Again", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onCloseGame,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = StudioSurface,
                                contentColor = StudioTextPrimary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .border(1.dp, StudioCardBorder, RoundedCornerShape(8.dp))
                        ) {
                            Text("Return", fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}
