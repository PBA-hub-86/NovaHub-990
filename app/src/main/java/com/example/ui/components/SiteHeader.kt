package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StudioUser
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioCyan
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.StudioEmerald
import com.example.ui.theme.StudioGold
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceElevated
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary

@Composable
fun SiteHeader(
    activeSection: String,
    currentUser: StudioUser? = null,
    onNavigate: (String) -> Unit,
    onQuickPlay: () -> Unit,
    onOpenAuth: () -> Unit = {},
    onOpenSearch: () -> Unit = {},
    onOpenChat: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    matchingCreationsCount: Int = 0,
    matchingWritingsCount: Int = 0,
    onNavigateToGallery: () -> Unit = {},
    onNavigateToWriting: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val navItems = listOf(
        "social" to "Social Feed 🔥",
        "portal" to "Game Portal 🎮",
        "masonry" to "Masonry Stills 📷",
        "writing" to "Blog / Articles ✍️",
        "contact" to "Write In ✉️",
        "about" to "About"
    )

    // Pulsing studio live availability indicator
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Surface(
        color = StudioDarkBg.copy(alpha = 0.96f),
        modifier = modifier
            .fillMaxWidth()
            .testTag("site_header")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Top branding row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Studio Monogram & Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onNavigate("hero") }
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(StudioGold)
                            .border(1.dp, StudioGold.copy(alpha = 0.6f), RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "NH",
                            color = Color(0xFF08090C),
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.SansSerif,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "NOVAHUB",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = StudioTextPrimary,
                                letterSpacing = 1.2.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• PUBLIC",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = StudioGold,
                                letterSpacing = 1.sp
                            )
                        }

                        // Availability status
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(StudioEmerald.copy(alpha = pulseAlpha))
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Open Community • Games & Social Hub",
                                fontSize = 10.sp,
                                color = StudioTextMuted
                            )
                        }
                    }
                }

                // Quick Action buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Universal Search Button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(StudioSurface)
                            .border(1.dp, StudioCardBorder, RoundedCornerShape(8.dp))
                            .clickable(onClick = onOpenSearch)
                            .testTag("header_search_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Studio",
                            tint = StudioGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Social Live Chat Button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(StudioSurface)
                            .border(1.dp, StudioCardBorder, RoundedCornerShape(8.dp))
                            .clickable(onClick = onOpenChat)
                            .testTag("header_chat_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "Open Community Chat",
                            tint = StudioCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Platform Settings Button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(StudioSurface)
                            .border(1.dp, StudioCardBorder, RoundedCornerShape(8.dp))
                            .clickable(onClick = onOpenSettings)
                            .testTag("header_settings_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Platform Settings & Safety",
                            tint = StudioGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Play Void Runner icon button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(StudioSurface)
                            .border(1.dp, StudioCardBorder, RoundedCornerShape(8.dp))
                            .clickable(onClick = onQuickPlay)
                            .testTag("header_quick_play"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = "Play Void Runner mini-game",
                            tint = StudioGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Account / Login Button
                    Box(
                        modifier = Modifier
                            .height(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (currentUser != null) StudioEmerald.copy(alpha = 0.18f) else StudioSurfaceElevated)
                            .border(1.dp, if (currentUser != null) StudioEmerald.copy(alpha = 0.5f) else StudioCardBorder, RoundedCornerShape(8.dp))
                            .clickable(onClick = onOpenAuth)
                            .padding(horizontal = 8.dp)
                            .testTag("header_account_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (currentUser != null) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(StudioEmerald),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = currentUser.avatarInitials,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF08090C)
                                    )
                                }
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = currentUser.username.split(" ").firstOrNull() ?: "User",
                                    color = StudioEmerald,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Login / Register",
                                    tint = StudioGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "লগইন",
                                    color = StudioGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Write In button
                    Box(
                        modifier = Modifier
                            .height(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(StudioGold)
                            .clickable { onNavigate("contact") }
                            .padding(horizontal = 10.dp)
                            .testTag("write_in_header_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Create,
                                contentDescription = "Write in",
                                tint = Color(0xFF08090C),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Write In",
                                color = Color(0xFF08090C),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Studio Universal Search Bar (Filters gallery images & blog posts in real-time)
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text(
                        text = "Search gallery images & blog posts...",
                        color = StudioTextMuted,
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search studio",
                        tint = if (searchQuery.isNotEmpty()) StudioGold else StudioTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { onSearchQueryChange("") },
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("studio_search_clear_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search",
                                tint = StudioGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = StudioSurface,
                    unfocusedContainerColor = StudioSurface,
                    focusedBorderColor = StudioGold,
                    unfocusedBorderColor = StudioCardBorder,
                    focusedTextColor = StudioTextPrimary,
                    unfocusedTextColor = StudioTextPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("studio_search_bar")
            )

            if (searchQuery.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filtered view: $matchingCreationsCount gallery • $matchingWritingsCount posts",
                        fontSize = 11.sp,
                        color = StudioGold,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Gallery ($matchingCreationsCount) ↓",
                            fontSize = 11.sp,
                            color = StudioCyan,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { onNavigateToGallery() }
                                .padding(vertical = 2.dp, horizontal = 4.dp)
                        )
                        Text(
                            text = "Blog ($matchingWritingsCount) ↓",
                            fontSize = 11.sp,
                            color = StudioEmerald,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { onNavigateToWriting() }
                                .padding(vertical = 2.dp, horizontal = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Navigation pills row (Horizontal scrollable)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                navItems.forEach { (route, label) ->
                    val isSelected = activeSection == route
                    val bg = if (isSelected) StudioGold.copy(alpha = 0.15f) else Color.Transparent
                    val border = if (isSelected) StudioGold else StudioCardBorder
                    val textColor = if (isSelected) StudioGold else StudioTextSecondary

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(bg)
                            .border(1.dp, border, RoundedCornerShape(20.dp))
                            .clickable { onNavigate(route) }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                            .testTag("nav_$route"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = textColor,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
