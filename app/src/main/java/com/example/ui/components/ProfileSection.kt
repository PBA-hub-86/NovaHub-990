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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.data.FirestoreArcadeScore
import com.example.data.StudioUser
import com.example.ui.theme.LocalStudioAccent
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioCardBorderSubtle
import com.example.ui.theme.StudioCyan
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.StudioEmerald
import com.example.ui.theme.StudioGold
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceElevated
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary
import com.example.ui.theme.accentColorForTheme

private data class ProfileAchievement(
    val id: String,
    val title: String,
    val requirement: String,
    val unlocked: Boolean
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileSection(
    user: StudioUser,
    isFirebaseSignedIn: Boolean,
    voidRunnerBest: Int,
    cyberBreakerBest: Int,
    quantum2048Best: Int,
    totalArcadeScore: Int,
    socialPostsCount: Int,
    writingsCount: Int,
    remoteImagesCount: Int,
    activeAccentTheme: String,
    leaderboardScores: List<FirestoreArcadeScore>,
    onSelectAccentTheme: (String) -> Unit,
    onSaveProfile: (displayName: String, handle: String, roleTitle: String, bio: String, initials: String, avatarUrl: String, accentTheme: String) -> Unit,
    onOpenAuth: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = LocalStudioAccent.current
    val context = LocalContext.current
    var isEditOpen by remember { mutableStateOf(false) }

    // Dynamic XP & Level calculation derived from real user activity
    val totalXp = remember(totalArcadeScore, socialPostsCount, writingsCount, remoteImagesCount) {
        (totalArcadeScore / 10) + (socialPostsCount * 40) + (writingsCount * 60) + (remoteImagesCount * 50)
    }
    val level = (totalXp / 250) + 1
    val levelProgress = (totalXp % 250) / 250f

    val achievements = remember(
        totalArcadeScore, voidRunnerBest, cyberBreakerBest, quantum2048Best, socialPostsCount, writingsCount, remoteImagesCount
    ) {
        listOf(
            ProfileAchievement("spark", "First Sparks", "Reach 100+ total arcade pts", totalArcadeScore >= 100),
            ProfileAchievement("void", "Void Pilot", "Score 300+ in Void Runner", voidRunnerBest >= 300),
            ProfileAchievement("breaker", "Cyber Smasher", "Score 250+ in Brick Breaker", cyberBreakerBest >= 250),
            ProfileAchievement("quantum", "Quantum Mind", "Score 512+ in Quantum 2048", quantum2048Best >= 512),
            ProfileAchievement("shutter", "Cloud Curator", "Share or browse 4+ Cloud stills", remoteImagesCount >= 4),
            ProfileAchievement("author", "Storyteller", "Publish 3+ studio essays/notes", writingsCount >= 3)
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp)
            .testTag("profile_section")
    ) {
        // Section Header
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
                        .background(accent)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "06 / CREATOR PROFILE & ACHIEVEMENTS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = accent,
                    letterSpacing = 1.4.sp
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = accent.copy(alpha = 0.14f),
                border = BorderStroke(1.dp, accent.copy(alpha = 0.55f)),
                modifier = Modifier
                    .clickable { isEditOpen = true }
                    .testTag("edit_profile_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = accent, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("Edit Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = accent)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Creator Profile",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = StudioTextPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Your public NovaHub identity, XP progression, custom studio theme, achievement badges, and live arcade standings.",
            style = MaterialTheme.typography.bodyMedium,
            color = StudioTextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Creator Identity Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = StudioSurfaceElevated),
            border = BorderStroke(1.dp, accent.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar (Coil remote URL if valid else styled monogram)
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(accent.copy(alpha = 0.16f))
                            .border(2.dp, accent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (user.avatarUrl.startsWith("https://") || user.avatarUrl.startsWith("http://")) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(user.avatarUrl)
                                    .crossfade(true)
                                    .memoryCachePolicy(CachePolicy.ENABLED)
                                    .build(),
                                contentDescription = user.username,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text(
                                text = user.avatarInitials.ifBlank { "NH" },
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = accent
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = user.username,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = StudioTextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (isFirebaseSignedIn) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Google Verified",
                                    tint = StudioCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Text(
                            text = "${user.handle.ifBlank { "@novahub" }} • ${user.roleTitle}",
                            fontSize = 12.sp,
                            color = accent,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = user.bio.ifBlank { "Building games, capturing nocturnal stills, and sharing creative experiments on NovaHub." },
                            fontSize = 12.sp,
                            color = StudioTextSecondary,
                            lineHeight = 17.sp,
                            maxLines = 2
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Level & XP Progress
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Level $level Explorer",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudioTextPrimary
                    )
                    Text(
                        text = "$totalXp XP total",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = accent
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { levelProgress.coerceIn(0.05f, 1f) },
                    color = accent,
                    trackColor = StudioDarkBg,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .clip(RoundedCornerShape(4.dp))
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Theme Accent Switcher
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Palette, contentDescription = null, tint = accent, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Studio Theme Accent", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = StudioTextSecondary)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("GOLD", "CYAN", "EMERALD").forEach { themeKey ->
                            val swatch = accentColorForTheme(themeKey)
                            val selected = activeAccentTheme == themeKey
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (selected) swatch else StudioDarkBg,
                                border = BorderStroke(1.dp, swatch),
                                modifier = Modifier
                                    .clickable { onSelectAccentTheme(themeKey) }
                                    .testTag("profile_theme_$themeKey")
                            ) {
                                Text(
                                    text = themeKey,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected) StudioDarkBg else swatch,
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }

                if (!isFirebaseSignedIn) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = onOpenAuth,
                        border = BorderStroke(1.dp, accent.copy(alpha = 0.6f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = accent, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Link Google Account for Cloud Sync", color = accent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Arcade & Studio Metrics Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ProfileMetricBox("Void Runner", voidRunnerBest.toString(), StudioGold, Modifier.weight(1f))
            ProfileMetricBox("Cyber Breaker", cyberBreakerBest.toString(), StudioCyan, Modifier.weight(1f))
            ProfileMetricBox("Quantum 2048", quantum2048Best.toString(), StudioEmerald, Modifier.weight(1f))
            ProfileMetricBox("Total Arcade", totalArcadeScore.toString(), accent, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Unlocked Achievement Badges
        Text(
            text = "Achievement Badges (${achievements.count { it.unlocked }}/${achievements.size})",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = StudioTextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            maxItemsInEachRow = 2
        ) {
            achievements.forEach { badge ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (badge.unlocked) accent.copy(alpha = 0.1f) else StudioSurface,
                    border = BorderStroke(
                        1.dp,
                        if (badge.unlocked) accent.copy(alpha = 0.6f) else StudioCardBorderSubtle
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (badge.unlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (badge.unlocked) accent else StudioTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = badge.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (badge.unlocked) StudioTextPrimary else StudioTextMuted,
                                maxLines = 1
                            )
                            Text(
                                text = badge.requirement,
                                fontSize = 10.sp,
                                color = StudioTextMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Cloud Arcade Leaderboard
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = StudioSurface),
            border = BorderStroke(1.dp, StudioCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Leaderboard, contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Global Arcade Leaderboard",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = StudioTextPrimary
                        )
                    }
                    Text(
                        text = if (isFirebaseSignedIn) "Live Cloud" else "Local Best",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = StudioEmerald
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (leaderboardScores.isEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(StudioSurfaceElevated)
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SportsEsports, contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "${user.username} (You)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StudioTextPrimary
                                )
                                Text(
                                    text = if (isFirebaseSignedIn) "Play any arcade game to post a cloud score!" else "Sign in with Google to join the global rankings.",
                                    fontSize = 11.sp,
                                    color = StudioTextSecondary
                                )
                            }
                        }
                        Text(
                            text = "${totalArcadeScore} pts",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = accent
                        )
                    }
                } else {
                    leaderboardScores.take(5).forEachIndexed { index, entry ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(StudioSurfaceElevated)
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "#${index + 1}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (index == 0) StudioGold else accent,
                                    modifier = Modifier.width(28.dp)
                                )
                                Column {
                                    Text(
                                        text = entry.playerName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = StudioTextPrimary,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = entry.gameTitle,
                                        fontSize = 10.sp,
                                        color = StudioTextMuted
                                    )
                                }
                            }
                            Text(
                                text = "${entry.score}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = accent
                            )
                        }
                    }
                }
            }
        }
    }

    if (isEditOpen) {
        EditProfileDialog(
            user = user,
            activeAccentTheme = activeAccentTheme,
            onDismiss = { isEditOpen = false },
            onSave = { name, handle, role, bio, initials, avatarUrl, theme ->
                onSaveProfile(name, handle, role, bio, initials, avatarUrl, theme)
                isEditOpen = false
            }
        )
    }
}

@Composable
private fun ProfileMetricBox(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = StudioSurface,
        border = BorderStroke(1.dp, StudioCardBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = color,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 9.sp,
                color = StudioTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun EditProfileDialog(
    user: StudioUser,
    activeAccentTheme: String,
    onDismiss: () -> Unit,
    onSave: (displayName: String, handle: String, roleTitle: String, bio: String, initials: String, avatarUrl: String, accentTheme: String) -> Unit
) {
    var name by remember { mutableStateOf(user.username) }
    var handle by remember { mutableStateOf(user.handle) }
    var role by remember { mutableStateOf(user.roleTitle) }
    var bio by remember { mutableStateOf(user.bio) }
    var initials by remember { mutableStateOf(user.avatarInitials) }
    var avatarUrl by remember { mutableStateOf(user.avatarUrl) }
    var selectedTheme by remember { mutableStateOf(activeAccentTheme) }
    val accent = accentColorForTheme(selectedTheme)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = StudioSurface),
            border = BorderStroke(1.dp, accent.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Edit Creator Profile", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = StudioTextPrimary)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = StudioTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Display Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accent,
                        unfocusedBorderColor = StudioCardBorder,
                        focusedTextColor = StudioTextPrimary,
                        unfocusedTextColor = StudioTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = handle,
                        onValueChange = { handle = it },
                        label = { Text("@Handle") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accent,
                            unfocusedBorderColor = StudioCardBorder,
                            focusedTextColor = StudioTextPrimary,
                            unfocusedTextColor = StudioTextPrimary
                        ),
                        modifier = Modifier.weight(1.4f)
                    )
                    OutlinedTextField(
                        value = initials,
                        onValueChange = { initials = it.take(3).uppercase() },
                        label = { Text("Initials") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accent,
                            unfocusedBorderColor = StudioCardBorder,
                            focusedTextColor = StudioTextPrimary,
                            unfocusedTextColor = StudioTextPrimary
                        ),
                        modifier = Modifier.weight(0.8f)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = role,
                    onValueChange = { role = it },
                    label = { Text("Role / Specialty") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accent,
                        unfocusedBorderColor = StudioCardBorder,
                        focusedTextColor = StudioTextPrimary,
                        unfocusedTextColor = StudioTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = avatarUrl,
                    onValueChange = { avatarUrl = it },
                    label = { Text("Avatar Image URL (Optional HTTPS)") },
                    placeholder = { Text("https://...", color = StudioTextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accent,
                        unfocusedBorderColor = StudioCardBorder,
                        focusedTextColor = StudioTextPrimary,
                        unfocusedTextColor = StudioTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Short Bio") },
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accent,
                        unfocusedBorderColor = StudioCardBorder,
                        focusedTextColor = StudioTextPrimary,
                        unfocusedTextColor = StudioTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text("Accent Theme", fontSize = 12.sp, color = StudioTextSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("GOLD", "CYAN", "EMERALD").forEach { themeKey ->
                        val swatch = accentColorForTheme(themeKey)
                        val selected = selectedTheme == themeKey
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selected) swatch else StudioSurfaceElevated,
                            border = BorderStroke(1.dp, swatch),
                            modifier = Modifier.clickable { selectedTheme = themeKey }
                        ) {
                            Text(
                                text = themeKey,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selected) StudioDarkBg else swatch,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            onSave(
                                name.trim().ifBlank { "NovaHub Creator" },
                                handle.trim().ifBlank { "@novahub" },
                                role.trim().ifBlank { "Creator" },
                                bio.trim(),
                                initials.trim().ifBlank { "NH" },
                                avatarUrl.trim(),
                                selectedTheme
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = accent, contentColor = StudioDarkBg),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save Profile", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
