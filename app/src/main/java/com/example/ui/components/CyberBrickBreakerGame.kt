package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.example.ui.theme.StudioRose
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceElevated
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary
import com.example.util.SoundFxManager
import kotlin.math.abs
import kotlin.random.Random

private data class CyberBrick(
    val id: Int,
    val col: Int,
    val row: Int,
    var hp: Int,
    val maxHp: Int,
    val color: Color
)

private data class BreakerPowerUp(
    val id: Int,
    var x: Float,
    var y: Float,
    val type: String // "WIDE", "POINTS", "LIFE"
)

@Composable
fun CyberBrickBreakerGame(
    highScore: Int,
    onScoreSaved: (score: Int, bricksBroken: Int) -> Unit,
    onCloseGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onCloseGame() }

    var level by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    var bricksBroken by remember { mutableIntStateOf(0) }
    var combo by remember { mutableIntStateOf(0) }
    var lives by remember { mutableIntStateOf(3) }
    var isPaused by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }
    var scoreSubmitted by remember { mutableStateOf(false) }

    var paddleCenterX by remember { mutableFloatStateOf(0.5f) }
    var paddleWidth by remember { mutableFloatStateOf(0.24f) }
    var wideTimer by remember { mutableFloatStateOf(0f) }

    var ballX by remember { mutableFloatStateOf(0.5f) }
    var ballY by remember { mutableFloatStateOf(0.76f) }
    var ballVx by remember { mutableFloatStateOf(0.32f) }
    var ballVy by remember { mutableFloatStateOf(-0.46f) }

    val bricks = remember { mutableStateListOf<CyberBrick>() }
    val powerUps = remember { mutableStateListOf<BreakerPowerUp>() }
    var nextPowerId by remember { mutableIntStateOf(0) }

    fun buildLevelBricks(targetLevel: Int) {
        bricks.clear()
        val rows = (4 + targetLevel).coerceAtMost(7)
        val cols = 6
        val palette = listOf(StudioRose, StudioGold, StudioCyan, StudioEmerald, Color(0xFFA78BFA))
        var idx = 0
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val hp = if (r == 0 && targetLevel >= 2) 2 else if (r == 1 && targetLevel >= 3) 2 else 1
                bricks.add(
                    CyberBrick(
                        id = idx++,
                        col = c,
                        row = r,
                        hp = hp,
                        maxHp = hp,
                        color = palette[r % palette.size]
                    )
                )
            }
        }
    }

    fun resetBall() {
        ballX = paddleCenterX
        ballY = 0.78f
        val dir = if (Random.nextBoolean()) 1f else -1f
        val speedBoost = (level - 1) * 0.04f
        ballVx = dir * (0.28f + speedBoost)
        ballVy = -(0.46f + speedBoost)
    }

    fun restartFullGame() {
        level = 1
        score = 0
        bricksBroken = 0
        combo = 0
        lives = 3
        paddleWidth = 0.24f
        wideTimer = 0f
        paddleCenterX = 0.5f
        powerUps.clear()
        buildLevelBricks(1)
        resetBall()
        isPaused = false
        isGameOver = false
        scoreSubmitted = false
    }

    LaunchedEffect(Unit) {
        buildLevelBricks(1)
    }

    LaunchedEffect(isGameOver, isPaused, level) {
        if (isGameOver || isPaused) return@LaunchedEffect
        var lastFrame = 0L
        while (!isGameOver && !isPaused) {
            withFrameMillis { frameMs ->
                if (lastFrame == 0L) {
                    lastFrame = frameMs
                    return@withFrameMillis
                }
                val dt = (frameMs - lastFrame).coerceIn(1L, 33L) / 1000f
                lastFrame = frameMs

                if (wideTimer > 0f) {
                    wideTimer -= dt
                    if (wideTimer <= 0f) {
                        paddleWidth = 0.24f
                    }
                }

                // Move ball
                ballX += ballVx * dt
                ballY += ballVy * dt

                val ballRadius = 0.018f
                // Side walls
                if (ballX - ballRadius < 0f) {
                    ballX = ballRadius
                    ballVx = abs(ballVx)
                    SoundFxManager.playBrickBounce()
                } else if (ballX + ballRadius > 1f) {
                    ballX = 1f - ballRadius
                    ballVx = -abs(ballVx)
                    SoundFxManager.playBrickBounce()
                }
                // Ceiling
                if (ballY - ballRadius < 0.10f) {
                    ballY = 0.10f + ballRadius
                    ballVy = abs(ballVy)
                    SoundFxManager.playBrickBounce()
                }

                // Paddle collision
                val paddleY = 0.84f
                val paddleHeight = 0.022f
                val halfPaddle = paddleWidth / 2f
                if (ballVy > 0f &&
                    ballY + ballRadius >= paddleY &&
                    ballY - ballRadius <= paddleY + paddleHeight &&
                    ballX >= paddleCenterX - halfPaddle - ballRadius &&
                    ballX <= paddleCenterX + halfPaddle + ballRadius
                ) {
                    val hitOffset = ((ballX - paddleCenterX) / halfPaddle).coerceIn(-1f, 1f)
                    ballVx = hitOffset * 0.45f
                    ballVy = -abs(ballVy).coerceAtLeast(0.45f)
                    ballY = paddleY - ballRadius
                    combo = 0
                    SoundFxManager.playBrickBounce()
                }

                // Brick collisions
                val cols = 6
                val paddingX = 0.04f
                val brickGap = 0.015f
                val totalGridW = 1f - (paddingX * 2f)
                val brickW = (totalGridW - brickGap * (cols - 1)) / cols
                val brickH = 0.036f
                val topOffset = 0.15f

                val brickIter = bricks.listIterator()
                while (brickIter.hasNext()) {
                    val b = brickIter.next()
                    val bx = paddingX + b.col * (brickW + brickGap)
                    val by = topOffset + b.row * (brickH + brickGap)
                    val hit = (ballX + ballRadius > bx) &&
                        (ballX - ballRadius < bx + brickW) &&
                        (ballY + ballRadius > by) &&
                        (ballY - ballRadius < by + brickH)

                    if (hit) {
                        val overlapLeft = abs((ballX + ballRadius) - bx)
                        val overlapRight = abs((bx + brickW) - (ballX - ballRadius))
                        val overlapTop = abs((ballY + ballRadius) - by)
                        val overlapBottom = abs((by + brickH) - (ballY - ballRadius))
                        val minH = minOf(overlapLeft, overlapRight)
                        val minV = minOf(overlapTop, overlapBottom)
                        if (minH < minV) {
                            ballVx = -ballVx
                        } else {
                            ballVy = -ballVy
                        }

                        b.hp -= 1
                        combo += 1
                        score += 50 + (combo * 10)

                        if (b.hp <= 0) {
                            bricksBroken += 1
                            SoundFxManager.playBrickBreak()
                            if (Random.nextFloat() < 0.22f) {
                                val pType = when (Random.nextInt(3)) {
                                    0 -> "WIDE"
                                    1 -> "LIFE"
                                    else -> "POINTS"
                                }
                                powerUps.add(
                                    BreakerPowerUp(
                                        id = ++nextPowerId,
                                        x = bx + brickW / 2f,
                                        y = by + brickH,
                                        type = pType
                                    )
                                )
                            }
                            brickIter.remove()
                        } else {
                            SoundFxManager.playBrickBounce()
                        }
                        break
                    }
                }

                // Move & collect powerups
                val pIter = powerUps.listIterator()
                while (pIter.hasNext()) {
                    val p = pIter.next()
                    p.y += 0.28f * dt
                    val caught = p.y >= paddleY - 0.02f &&
                        p.y <= paddleY + 0.04f &&
                        abs(p.x - paddleCenterX) <= halfPaddle + 0.04f
                    if (caught) {
                        SoundFxManager.playPowerUp()
                        when (p.type) {
                            "WIDE" -> {
                                paddleWidth = 0.36f
                                wideTimer = 8f
                            }
                            "LIFE" -> {
                                lives = (lives + 1).coerceAtMost(5)
                            }
                            else -> {
                                score += 250
                            }
                        }
                        pIter.remove()
                    } else if (p.y > 1.05f) {
                        pIter.remove()
                    }
                }

                // Level clear check
                if (bricks.isEmpty()) {
                    SoundFxManager.playPowerUp()
                    level += 1
                    score += 300 * level
                    buildLevelBricks(level)
                    resetBall()
                }

                // Bottom miss check
                if (ballY > 0.96f) {
                    lives -= 1
                    combo = 0
                    if (lives <= 0) {
                        SoundFxManager.playGameOver()
                        isGameOver = true
                        if (!scoreSubmitted) {
                            scoreSubmitted = true
                            onScoreSaved(score, bricksBroken)
                        }
                    } else {
                        SoundFxManager.playGameOver()
                        resetBall()
                    }
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .testTag("cyber_breaker_screen")
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(isGameOver, isPaused) {
                    if (!isGameOver && !isPaused) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val dx = dragAmount.x / size.width
                            val half = paddleWidth / 2f
                            paddleCenterX = (paddleCenterX + dx).coerceIn(half + 0.02f, 1f - half - 0.02f)
                        }
                    }
                }
        ) {
            val w = size.width
            val h = size.height

            // Cyber Grid backdrop
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF060911), Color(0xFF0A1220), Color(0xFF0E1629))
                )
            )

            // Subtle grid lines
            val stepX = w / 8f
            for (i in 1..7) {
                drawLine(
                    color = StudioCyan.copy(alpha = 0.05f),
                    start = Offset(i * stepX, 0f),
                    end = Offset(i * stepX, h),
                    strokeWidth = 1f
                )
            }

            // Bricks
            val cols = 6
            val paddingX = 0.04f
            val brickGap = 0.015f
            val totalGridW = 1f - (paddingX * 2f)
            val brickW = (totalGridW - brickGap * (cols - 1)) / cols
            val brickH = 0.036f
            val topOffset = 0.15f

            for (b in bricks) {
                val bx = (paddingX + b.col * (brickW + brickGap)) * w
                val by = (topOffset + b.row * (brickH + brickGap)) * h
                val bw = brickW * w
                val bh = brickH * h
                val alpha = if (b.hp > 1) 1f else 0.85f
                drawRoundRect(
                    color = b.color.copy(alpha = alpha),
                    topLeft = Offset(bx, by),
                    size = Size(bw, bh),
                    cornerRadius = CornerRadius(8f, 8f)
                )
                if (b.hp > 1) {
                    drawRoundRect(
                        color = Color.White.copy(alpha = 0.55f),
                        topLeft = Offset(bx + 3f, by + 3f),
                        size = Size(bw - 6f, bh * 0.25f),
                        cornerRadius = CornerRadius(4f, 4f)
                    )
                }
            }

            // Powerups
            for (p in powerUps) {
                val px = p.x * w
                val py = p.y * h
                val pColor = when (p.type) {
                    "WIDE" -> StudioCyan
                    "LIFE" -> StudioRose
                    else -> StudioGold
                }
                drawCircle(
                    color = pColor.copy(alpha = 0.28f),
                    radius = w * 0.035f,
                    center = Offset(px, py)
                )
                drawCircle(
                    color = pColor,
                    radius = w * 0.020f,
                    center = Offset(px, py)
                )
            }

            // Paddle
            val padLeft = (paddleCenterX - paddleWidth / 2f) * w
            val padTop = 0.84f * h
            val padW = paddleWidth * w
            val padH = 0.022f * h
            drawRoundRect(
                color = if (wideTimer > 0f) StudioEmerald.copy(alpha = 0.35f) else StudioCyan.copy(alpha = 0.25f),
                topLeft = Offset(padLeft - 6f, padTop - 4f),
                size = Size(padW + 12f, padH + 8f),
                cornerRadius = CornerRadius(14f, 14f)
            )
            drawRoundRect(
                color = if (wideTimer > 0f) StudioEmerald else StudioCyan,
                topLeft = Offset(padLeft, padTop),
                size = Size(padW, padH),
                cornerRadius = CornerRadius(12f, 12f)
            )

            // Energy Ball
            val bx = ballX * w
            val by = ballY * h
            val bRad = 0.018f * w
            drawCircle(
                color = StudioGold.copy(alpha = 0.32f),
                radius = bRad * 1.75f,
                center = Offset(bx, by)
            )
            drawCircle(
                color = Color.White,
                radius = bRad,
                center = Offset(bx, by)
            )
        }

        // Top HUD
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (!scoreSubmitted && score > 0) {
                        scoreSubmitted = true
                        onScoreSaved(score, bricksBroken)
                    }
                    onCloseGame()
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(StudioSurface.copy(alpha = 0.88f))
                    .border(1.dp, StudioCardBorder, CircleShape)
                    .testTag("close_cyber_breaker_btn")
            ) {
                Icon(Icons.Default.Close, contentDescription = "Exit Cyber Brick Breaker", tint = StudioTextPrimary)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(StudioSurface.copy(alpha = 0.88f))
                    .border(1.dp, StudioCardBorder, RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("LVL", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = StudioTextMuted)
                    Text("$level", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = StudioCyan, fontFamily = FontFamily.Monospace)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("SCORE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = StudioTextMuted)
                    Text("$score", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = StudioGold, fontFamily = FontFamily.Monospace)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("BEST", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = StudioTextMuted)
                    Text("${maxOf(highScore, score)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = StudioEmerald, fontFamily = FontFamily.Monospace)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Favorite, contentDescription = "Lives", tint = StudioRose, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("$lives", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = StudioRose, fontFamily = FontFamily.Monospace)
                }
            }

            IconButton(
                onClick = { isPaused = !isPaused },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(StudioSurface.copy(alpha = 0.88f))
                    .border(1.dp, StudioCardBorder, CircleShape)
            ) {
                Icon(
                    imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                    contentDescription = if (isPaused) "Resume" else "Pause",
                    tint = StudioCyan
                )
            }
        }

        // Bottom control hint
        if (!isGameOver) {
            Text(
                text = "Slide anywhere horizontally to steer paddle • Catch glowing orbs for boosts",
                fontSize = 11.sp,
                color = StudioTextSecondary,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 18.dp)
            )
        }

        // Game Over Overlay
        AnimatedVisibility(
            visible = isGameOver,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = StudioSurfaceElevated),
                border = BorderStroke(1.dp, StudioCyan.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(28.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("SESSION COMPLETE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StudioCyan, letterSpacing = 1.4.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Cyber Brick Breaker", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = StudioTextPrimary)
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(StudioDarkBg)
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("FINAL SCORE", fontSize = 10.sp, color = StudioTextMuted)
                            Text("$score", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = StudioGold, fontFamily = FontFamily.Monospace)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("BRICKS CLEARED", fontSize = 10.sp, color = StudioTextMuted)
                            Text("$bricksBroken", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = StudioCyan, fontFamily = FontFamily.Monospace)
                        }
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { restartFullGame() },
                            colors = ButtonDefaults.buttonColors(containerColor = StudioCyan, contentColor = StudioDarkBg),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Play Again", fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = onCloseGame,
                            colors = ButtonDefaults.buttonColors(containerColor = StudioSurface, contentColor = StudioTextPrimary),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, StudioCardBorder),
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) {
                            Text("Return", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}
