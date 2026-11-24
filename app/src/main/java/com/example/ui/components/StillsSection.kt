package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.data.StudioStill
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioCyan
import com.example.ui.theme.StudioGold
import com.example.ui.theme.StudioRose
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceElevated
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary

@Composable
fun StillsSection(
    stills: List<StudioStill>,
    selectedCategory: String,
    favoriteIds: Set<String>,
    onSelectCategory: (String) -> Unit,
    onOpenStill: (StudioStill) -> Unit,
    onToggleFavorite: (String) -> Unit,
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val categories = listOf("All", "Urban Night", "Golden Dawn", "Brutalist Shadow")
    var isGridView by remember { mutableStateOf(false) }

    val categoryFiltered = if (selectedCategory == "All") {
        stills
    } else {
        stills.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    val filteredStills = if (searchQuery.isBlank()) {
        categoryFiltered
    } else {
        val q = searchQuery.trim().lowercase()
        categoryFiltered.filter { still ->
            still.title.lowercase().contains(q) ||
            still.category.lowercase().contains(q) ||
            still.location.lowercase().contains(q) ||
            still.exif.lowercase().contains(q) ||
            still.focalDescription.lowercase().contains(q)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
            .testTag("stills_section")
    ) {
        // Section index and view mode switcher
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "03 / STILLS & PHOTOGRAPHY",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = StudioGold,
                letterSpacing = 1.5.sp
            )

            // Showcase Count & Grid/Feature toggle
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
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = StudioGold,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${filteredStills.size} EXPOSURES",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = StudioGold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Layout switcher
                IconButton(
                    onClick = { isGridView = !isGridView },
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(StudioSurfaceElevated)
                        .border(1.dp, StudioCardBorder, RoundedCornerShape(6.dp))
                        .size(28.dp)
                        .testTag("toggle_gallery_layout_btn")
                ) {
                    Icon(
                        imageVector = if (isGridView) Icons.Default.ViewAgenda else Icons.Default.GridView,
                        contentDescription = "Switch Gallery Layout",
                        tint = StudioTextSecondary,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Fine Art Photography & Curatorial Stills",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = StudioTextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Nocturnal light, monsoon reflections & quiet geometry. Tap any frame to inspect optics, story notes, and camera EXIF data.",
            style = MaterialTheme.typography.bodyLarge,
            color = StudioTextSecondary,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Category Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                val isSelected = category == selectedCategory
                val bg = if (isSelected) StudioGold else StudioSurface
                val border = if (isSelected) StudioGold else StudioCardBorder
                val textCol = if (isSelected) Color(0xFF08090C) else StudioTextSecondary

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(bg)
                        .border(1.dp, border, RoundedCornerShape(16.dp))
                        .clickable { onSelectCategory(category) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("filter_$category")
                ) {
                    Text(
                        text = category,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = textCol
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Stills Gallery Cards: Grid or Feature Mode
        if (filteredStills.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(StudioSurfaceElevated)
                    .border(1.dp, StudioCardBorder, RoundedCornerShape(16.dp))
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (searchQuery.isNotBlank()) "No photography stills match \"$searchQuery\"" else "No stills in this category",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudioTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Try clearing the search query to explore the complete darkroom archive.",
                        fontSize = 13.sp,
                        color = StudioTextMuted
                    )
                    if (searchQuery.isNotBlank()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        androidx.compose.material3.OutlinedButton(
                            onClick = { onSearchQueryChange("") },
                            colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(contentColor = StudioGold),
                            border = androidx.compose.foundation.BorderStroke(1.dp, StudioGold),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Clear Search Filter", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else if (isGridView) {
            // 2-Column Responsive Matrix
            val chunkedStills = filteredStills.chunked(2)
            chunkedStills.forEach { rowStills ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowStills.forEach { still ->
                        val isFav = favoriteIds.contains(still.id)
                        StillGridCard(
                            still = still,
                            isFavorite = isFav,
                            onCardClick = { onOpenStill(still) },
                            onToggleFavorite = { onToggleFavorite(still.id) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowStills.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        } else {
            // Full Feature Widescreen Presentation Cards
            filteredStills.forEach { still ->
                val isFav = favoriteIds.contains(still.id)
                StillCard(
                    still = still,
                    isFavorite = isFav,
                    onCardClick = { onOpenStill(still) },
                    onToggleFavorite = { onToggleFavorite(still.id) },
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun StillGridCard(
    still: StudioStill,
    isFavorite: Boolean,
    onCardClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = StudioSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .clickable(onClick = onCardClick)
            .testTag("still_grid_card_${still.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
                Image(
                    painter = painterResource(id = still.imageResId),
                    contentDescription = still.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )

                // Favorite button top right
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .padding(6.dp)
                        .align(Alignment.TopEnd)
                        .clip(CircleShape)
                        .background(Color(0xFF08090C).copy(alpha = 0.75f))
                        .size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite Still",
                        tint = if (isFavorite) StudioRose else StudioTextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                }

                // Category pill on bottom
                Box(
                    modifier = Modifier
                        .padding(6.dp)
                        .align(Alignment.BottomStart)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF08090C).copy(alpha = 0.85f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = still.category.uppercase(),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudioGold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = still.title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = StudioTextPrimary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = still.location,
                    fontSize = 10.sp,
                    color = StudioCyan,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = still.exif,
                    fontSize = 9.sp,
                    color = StudioGold,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun StillCard(
    still: StudioStill,
    isFavorite: Boolean,
    onCardClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = StudioSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick)
            .testTag("still_card_${still.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 10f)
            ) {
                Image(
                    painter = painterResource(id = still.imageResId),
                    contentDescription = still.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )

                // Bottom vignette
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xFF08090C).copy(alpha = 0.7f)
                                )
                            )
                        )
                )

                // Category pill on top left
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF08090C).copy(alpha = 0.8f))
                        .border(1.dp, StudioCardBorder, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = still.category.uppercase(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudioGold,
                        letterSpacing = 1.sp
                    )
                }

                // Favorite button on top right
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.TopEnd)
                        .clip(CircleShape)
                        .background(Color(0xFF08090C).copy(alpha = 0.75f))
                        .border(1.dp, StudioCardBorder, CircleShape)
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Bookmark Still",
                        tint = if (isFavorite) StudioRose else StudioTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Location badge on bottom left
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.BottomStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = StudioCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = still.location,
                        fontSize = 11.sp,
                        color = StudioCyan,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Info row
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = still.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = StudioTextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = still.focalDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = StudioTextSecondary,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = still.exif,
                        fontSize = 11.sp,
                        color = StudioGold,
                        fontFamily = FontFamily.Monospace
                    )

                    Text(
                        text = "Inspect →",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StudioTextMuted
                    )
                }
            }
        }
    }
}
