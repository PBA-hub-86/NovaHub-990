package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.util.SoundFxManager
import kotlin.math.abs
import kotlin.random.Random

private enum class MoveDirection { UP, DOWN, LEFT, RIGHT }

@Composable
fun Quantum2048Game(
    highScore: Int,
    onScoreSaved: (score: Int, maxTile: Int) -> Unit,
    onCloseGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onCloseGame() }

    fun spawnTile(grid: List<Int>): List<Int> {
        val emptyIndices = grid.indices.filter { grid[it] == 0 }
        if (emptyIndices.isEmpty()) return grid
        val chosen = emptyIndices[Random.nextInt(emptyIndices.size)]
        val value = if (Random.nextFloat() < 0.9f) 2 else 4
        return grid.toMutableList().also { it[chosen] = value }
    }

    fun initialGrid(): List<Int> {
        var g = List(16) { 0 }
        g = spawnTile(g)
        g = spawnTile(g)
        return g
    }

    var grid by remember { mutableStateOf(initialGrid()) }
    var previousGrid by remember { mutableStateOf<List<Int>?>(null) }
    var previousScore by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var movesCount by remember { mutableIntStateOf(0) }
    var isGameOver by remember { mutableStateOf(false) }
    var hasWon2048 by remember { mutableStateOf(false) }
    var scoreSaved by remember { mutableStateOf(false) }

    fun hasAvailableMoves(g: List<Int>): Boolean {
        if (g.any { it == 0 }) return true
        for (r in 0 until 4) {
            for (c in 0 until 4) {
                val v = g[r * 4 + c]
                if (c < 3 && v == g[r * 4 + (c + 1)]) return true
                if (r < 3 && v == g[(r + 1) * 4 + c]) return true
            }
        }
        return false
    }

    fun slideLine(line: List<Int>): Pair<List<Int>, Int> {
        val nonZero = line.filter { it != 0 }
        val merged = mutableListOf<Int>()
        var gained = 0
        var i = 0
        while (i < nonZero.size) {
            if (i + 1 < nonZero.size && nonZero[i] == nonZero[i + 1]) {
                val sum = nonZero[i] * 2
                merged.add(sum)
                gained += sum
                i += 2
            } else {
                merged.add(nonZero[i])
                i += 1
            }
        }
        while (merged.size < 4) merged.add(0)
        return merged to gained
    }

    fun performMove(dir: MoveDirection) {
        if (isGameOver) return
        val next = grid.toMutableList()
        var gainedScore = 0
        var changed = false

        when (dir) {
            MoveDirection.LEFT -> {
                for (r in 0 until 4) {
                    val row = (0 until 4).map { c -> grid[r * 4 + c] }
                    val (slid, gained) = slideLine(row)
                    gainedScore += gained
                    for (c in 0 until 4) {
                        if (next[r * 4 + c] != slid[c]) changed = true
                        next[r * 4 + c] = slid[c]
                    }
                }
            }
            MoveDirection.RIGHT -> {
                for (r in 0 until 4) {
                    val row = (3 downTo 0).map { c -> grid[r * 4 + c] }
                    val (slid, gained) = slideLine(row)
                    gainedScore += gained
                    for (c in 0 until 4) {
                        val valAt = slid[3 - c]
                        if (next[r * 4 + c] != valAt) changed = true
                        next[r * 4 + c] = valAt
                    }
                }
            }
            MoveDirection.UP -> {
                for (c in 0 until 4) {
                    val col = (0 until 4).map { r -> grid[r * 4 + c] }
                    val (slid, gained) = slideLine(col)
                    gainedScore += gained
                    for (r in 0 until 4) {
                        if (next[r * 4 + c] != slid[r]) changed = true
                        next[r * 4 + c] = slid[r]
                    }
                }
            }
            MoveDirection.DOWN -> {
                for (c in 0 until 4) {
                    val col = (3 downTo 0).map { r -> grid[r * 4 + c] }
                    val (slid, gained) = slideLine(col)
                    gainedScore += gained
                    for (r in 0 until 4) {
                        val valAt = slid[3 - r]
                        if (next[r * 4 + c] != valAt) changed = true
                        next[r * 4 + c] = valAt
                    }
                }
            }
        }

        if (changed) {
            previousGrid = grid
            previousScore = score
            val updated = spawnTile(next)
            grid = updated
            score += gainedScore
            movesCount += 1
            if (gainedScore > 0) {
                SoundFxManager.playTileMerge()
            } else {
                SoundFxManager.playUiClick()
            }
            val topTile = updated.maxOrNull() ?: 2
            if (topTile >= 2048 && !hasWon2048) {
                hasWon2048 = true
                SoundFxManager.playPowerUp()
                onScoreSaved(score, topTile)
            }
            if (!hasAvailableMoves(updated)) {
                isGameOver = true
                SoundFxManager.playGameOver()
                if (!scoreSaved) {
                    scoreSaved = true
                    onScoreSaved(score, topTile)
                }
            }
        }
    }

    fun restart() {
        grid = initialGrid()
        previousGrid = null
        previousScore = 0
        score = 0
        movesCount = 0
        isGameOver = false
        hasWon2048 = false
        scoreSaved = false
    }

    var dragDeltaX = 0f
    var dragDeltaY = 0f

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp)
            .testTag("quantum_2048_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (score > 0 && !scoreSaved) {
                        scoreSaved = true
                        onScoreSaved(score, grid.maxOrNull() ?: 2)
                    }
                    onCloseGame()
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(StudioSurface)
                    .border(1.dp, StudioCardBorder, CircleShape)
                    .testTag("close_quantum_2048_btn")
            ) {
                Icon(Icons.Default.Close, contentDescription = "Exit Quantum 2048", tint = StudioTextPrimary)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "QUANTUM 2048",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = StudioEmerald,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = if (hasWon2048) "2048 Reached! Keep pushing!" else "Merge neon cells to 2048",
                    fontSize = 11.sp,
                    color = StudioTextSecondary
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                IconButton(
                    onClick = {
                        previousGrid?.let { prev ->
                            grid = prev
                            score = previousScore
                            previousGrid = null
                            isGameOver = false
                            SoundFxManager.playUiClick()
                        }
                    },
                    enabled = previousGrid != null,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(StudioSurface)
                        .border(1.dp, StudioCardBorder, CircleShape)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Undo,
                        contentDescription = "Undo move",
                        tint = if (previousGrid != null) StudioCyan else StudioTextMuted
                    )
                }

                IconButton(
                    onClick = { restart() },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(StudioSurface)
                        .border(1.dp, StudioCardBorder, CircleShape)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Restart 2048", tint = StudioGold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Score HUD Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ScoreStatChip("SCORE", "$score", StudioGold, Modifier.weight(1f))
            ScoreStatChip("BEST", "${maxOf(highScore, score)}", StudioEmerald, Modifier.weight(1f))
            ScoreStatChip("MAX TILE", "${grid.maxOrNull() ?: 2}", StudioCyan, Modifier.weight(1f))
            ScoreStatChip("MOVES", "$movesCount", StudioTextPrimary, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 4x4 Interactive Swipe Grid
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = StudioSurface),
            border = BorderStroke(1.5.dp, StudioEmerald.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .pointerInput(isGameOver) {
                    detectDragGestures(
                        onDragStart = {
                            dragDeltaX = 0f
                            dragDeltaY = 0f
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            dragDeltaX += dragAmount.x
                            dragDeltaY += dragAmount.y
                        },
                        onDragEnd = {
                            val threshold = 40f
                            if (abs(dragDeltaX) > abs(dragDeltaY) && abs(dragDeltaX) > threshold) {
                                performMove(if (dragDeltaX > 0) MoveDirection.RIGHT else MoveDirection.LEFT)
                            } else if (abs(dragDeltaY) > threshold) {
                                performMove(if (dragDeltaY > 0) MoveDirection.DOWN else MoveDirection.UP)
                            }
                        }
                    )
                }
        ) {
            Box(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (r in 0 until 4) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (c in 0 until 4) {
                                val value = grid[r * 4 + c]
                                val (bg, fg) = tileColors(value)
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = bg,
                                    border = BorderStroke(1.dp, if (value == 0) StudioCardBorderSubtle else fg.copy(alpha = 0.4f)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxSize()
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        if (value > 0) {
                                            Text(
                                                text = "$value",
                                                fontSize = when {
                                                    value >= 1024 -> 18.sp
                                                    value >= 128 -> 22.sp
                                                    else -> 26.sp
                                                },
                                                fontWeight = FontWeight.Black,
                                                color = fg,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (isGameOver) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xE008090C)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("GRID LOCKED", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = StudioRose, letterSpacing = 1.4.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Final Score: $score", fontSize = 22.sp, fontWeight = FontWeight.Black, color = StudioGold)
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { restart() },
                                colors = ButtonDefaults.buttonColors(containerColor = StudioEmerald, contentColor = StudioDarkBg)
                            ) {
                                Text("New Matrix", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Accessible Tap Controls (in addition to swipe)
        Text(
            text = "Swipe board or tap direction pad",
            fontSize = 11.sp,
            color = StudioTextMuted
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DirectionPadBtn(Icons.AutoMirrored.Filled.ArrowBack, "Slide Left") { performMove(MoveDirection.LEFT) }
            DirectionPadBtn(Icons.Default.ArrowUpward, "Slide Up") { performMove(MoveDirection.UP) }
            DirectionPadBtn(Icons.Default.ArrowDownward, "Slide Down") { performMove(MoveDirection.DOWN) }
            DirectionPadBtn(Icons.AutoMirrored.Filled.ArrowForward, "Slide Right") { performMove(MoveDirection.RIGHT) }
        }
    }
}

@Composable
private fun ScoreStatChip(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = StudioSurfaceElevated,
        border = BorderStroke(1.dp, StudioCardBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = StudioTextMuted)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color, fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
private fun DirectionPadBtn(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(50.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(StudioSurfaceElevated)
            .border(1.dp, StudioCardBorder, RoundedCornerShape(12.dp))
    ) {
        Icon(imageVector = icon, contentDescription = description, tint = StudioEmerald)
    }
}

private fun tileColors(value: Int): Pair<Color, Color> = when (value) {
    0 -> StudioDarkBg to StudioTextMuted
    2 -> Color(0xFF161D2C) to StudioTextPrimary
    4 -> Color(0xFF1B273A) to StudioCyan
    8 -> Color(0xFF123632) to StudioEmerald
    16 -> Color(0xFF312816) to StudioGold
    32 -> Color(0xFF3B2029) to StudioRose
    64 -> Color(0xFF282042) to Color(0xFFA78BFA)
    128 -> Color(0xFF0E4A42) to Color(0xFF6EE7B7)
    256 -> Color(0xFF4B3712) to Color(0xFFFDE047)
    512 -> Color(0xFF4E1B2B) to Color(0xFFFDA4AF)
    1024 -> Color(0xFF0C4A6E) to Color(0xFF7DD3FC)
    else -> Color(0xFFE5A950) to Color(0xFF08090C)
}
