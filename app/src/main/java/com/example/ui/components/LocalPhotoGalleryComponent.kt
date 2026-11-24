package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Grid3x3
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.R
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
 * Data model representing local photo assets stored in the application resources
 */
data class LocalPhotoAsset(
    val id: String,
    val title: String,
    val resId: Int,
    val category: String,
    val location: String,
    val exif: String,
    val resolution: String,
    val focalLength: String,
    val description: String,
    val year: String = "2025-2026",
    val tag: String = "RAW 35mm"
)

/**
 * Standard catalog of curated local photo assets bundled directly in the studio
 */
fun getLocalStudioPhotoAssets(): List<LocalPhotoAsset> = listOf(
    LocalPhotoAsset(
        id = "dhaka_monsoon_night",
        title = "Neon Monsoon Reflection",
        resId = R.drawable.still_dhaka_night_1789660149613,
        category = "Urban Night",
        location = "Mirpur Road, Dhaka",
        exif = "35mm • f/1.4 • 1/80s • ISO 1250",
        resolution = "3840 × 2400 (4K UHD)",
        focalLength = "35mm Prime",
        description = "Rainwater pooling against the asphalt, transforming street lamps and neon billboards into flowing liquid gold and cobalt ribbons.",
        tag = "Nocturne"
    ),
    LocalPhotoAsset(
        id = "padma_golden_mist",
        title = "Silent Waters at First Light",
        resId = R.drawable.still_golden_dawn_1789660165447,
        category = "Golden Dawn",
        location = "Padma Riverbank, Munshiganj",
        exif = "50mm • f/2.0 • 1/400s • ISO 200",
        resolution = "4032 × 2520 (Fine Art)",
        focalLength = "50mm Prime",
        description = "A lone country dinghy cutting through morning river mist as the sun breaks the horizon in deep amber stillness.",
        tag = "Chiaroscuro"
    ),
    LocalPhotoAsset(
        id = "brutalist_solitude",
        title = "Shadow & Concrete Monolith",
        resId = R.drawable.still_shadow_monolith_1789660179400,
        category = "Brutalist",
        location = "Sher-e-Bangla Nagar",
        exif = "28mm • f/5.6 • 1/250s • ISO 100",
        resolution = "3840 × 2400 (Architectural)",
        focalLength = "28mm Wide",
        description = "Razor-sharp sunset shadows cleaving raw exposed concrete planes in geometric silence, inspired by Kahn's monumentalism.",
        tag = "Geometry"
    ),
    LocalPhotoAsset(
        id = "cyberpunk_monsoon_alley",
        title = "Monsoon Alley Neon Horizon",
        resId = R.drawable.ai_cyberpunk_alley_1789701580711,
        category = "Cyberpunk",
        location = "Old Dhaka Labyrinth",
        exif = "85mm • f/1.4 • 1/125s • ISO 800",
        resolution = "3840 × 2160 (Cinematic)",
        focalLength = "85mm Portrait",
        description = "Hazy overhead cable networks crisscrossing glowing emerald and crimson tea stall lanterns in a torrential monsoon downpour.",
        tag = "Atmospheric"
    ),
    LocalPhotoAsset(
        id = "delta_aerial_dawn",
        title = "Delta Riverbank Vapor Sweep",
        resId = R.drawable.ai_veo_drone_river_1789701598733,
        category = "Golden Dawn",
        location = "Meghna Estuary, Chandpur",
        exif = "24mm • f/4.0 • 1/500s • ISO 100",
        resolution = "3840 × 2160 (Aerial Scan)",
        focalLength = "24mm Ultra-Wide",
        description = "Gentle rippling tide lines glowing copper as low-hanging river mist evaporates under the first rays of dawn.",
        tag = "Estuary"
    ),
    LocalPhotoAsset(
        id = "obsidian_nocturne",
        title = "Obsidian Nocturne & Steel",
        resId = R.drawable.ai_obsidian_supercar_1789701616237,
        category = "Brutalist",
        location = "Hatirjheel Overpass",
        exif = "50mm • f/1.8 • 1/60s • ISO 1600",
        resolution = "3840 × 2160 (Specular)",
        focalLength = "50mm Prime",
        description = "Sculptural aerodynamic curvature and stark obsidian structural silhouette illuminated by ambient architectural city glow.",
        tag = "Industrial"
    ),
    LocalPhotoAsset(
        id = "cyber_walk_neon",
        title = "Cybernetic Street Neon Stroll",
        resId = R.drawable.ai_veo_cyber_walk_1789701633091,
        category = "Cyberpunk",
        location = "Dhanmondi Lakefront Alley",
        exif = "35mm • f/1.2 • 1/100s • ISO 1000",
        resolution = "3840 × 2160 (HDR Neon)",
        focalLength = "35mm Fast Prime",
        description = "Reflective puddles scattering cyan and violet spectrums across the walking promenade under humid midnight haze.",
        tag = "Street"
    ),
    LocalPhotoAsset(
        id = "void_kinetic_matrix",
        title = "Kinetic Void Resonance",
        resId = R.drawable.game_void_runner_1789660196140,
        category = "Abstract",
        location = "Studio Generative Engine",
        exif = "Algorithmic • Shader 2.0 • 60 FPS",
        resolution = "1920 × 1080 (Vector Matrix)",
        focalLength = "Synthesized",
        description = "Luminous geometric spires and resonant gold particle shards drifting across a dark void canvas.",
        tag = "Generative"
    )
)

/**
 * Grid-based Gallery Component to display local photo assets using Coil
 * for efficient asynchronous loading, bitmap caching, and smooth rendering.
 */
@Composable
fun LocalPhotoGalleryComponent(
    photos: List<LocalPhotoAsset> = remember { getLocalStudioPhotoAssets() },
    favoriteIds: Set<String> = emptySet(),
    onToggleFavorite: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var gridColumns by remember { mutableIntStateOf(2) } // 2-col or 3-col
    var selectedPhotoForLightbox by remember { mutableStateOf<LocalPhotoAsset?>(null) }

    val categories = remember {
        listOf("All", "Urban Night", "Golden Dawn", "Brutalist", "Cyberpunk", "Abstract")
    }

    // Filter logic
    val filteredPhotos = remember(selectedCategory, searchQuery, photos) {
        photos.filter { photo ->
            val matchesCategory = selectedCategory == "All" || photo.category.equals(selectedCategory, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                    photo.title.contains(searchQuery, ignoreCase = true) ||
                    photo.location.contains(searchQuery, ignoreCase = true) ||
                    photo.category.contains(searchQuery, ignoreCase = true) ||
                    photo.exif.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
            .testTag("local_photo_gallery_component")
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
                        .background(StudioGold)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "LOCAL PHOTO ASSETS • COIL POWERED",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = StudioGold,
                    letterSpacing = 1.4.sp
                )
            }

            // Coil Memory Badge & Column Switcher
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = StudioSurfaceElevated,
                    border = BorderStroke(1.dp, StudioCardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = StudioEmerald,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Coil Caching",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = StudioEmerald
                        )
                    }
                }

                // Toggle 2-column or 3-column layout
                IconButton(
                    onClick = { gridColumns = if (gridColumns == 2) 3 else 2 },
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(StudioSurfaceElevated)
                        .border(1.dp, StudioCardBorder, RoundedCornerShape(6.dp))
                        .testTag("toggle_gallery_columns_btn")
                ) {
                    Icon(
                        imageVector = if (gridColumns == 2) Icons.Default.Grid3x3 else Icons.Default.GridView,
                        contentDescription = "Switch Grid Density",
                        tint = StudioGold,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Title & Description
        Text(
            text = "স্টুডিও ফটো গ্যালারি ও আর্ট আর্কাইভ",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = StudioTextPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Coil লাইব্রেরির মাধ্যমে অপ্টিমাইজড মেমরি ও লো-লেটেন্সি লোডিং সহ স্থানীয় ফটোগুলো ব্রাউজ করুন।",
            style = MaterialTheme.typography.bodyMedium,
            color = StudioTextSecondary,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("ফটো খুঁজুন (টাইটেল, লোকেশন, EXIF)...", color = StudioTextMuted, fontSize = 12.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = StudioGold,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear search",
                            tint = StudioTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("gallery_search_input"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = StudioGold,
                unfocusedBorderColor = StudioCardBorder,
                focusedContainerColor = StudioSurface,
                unfocusedContainerColor = StudioSurface,
                focusedTextColor = StudioTextPrimary,
                unfocusedTextColor = StudioTextPrimary
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

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
                val textCol = if (isSelected) Color.Black else StudioTextSecondary

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(bg)
                        .border(1.dp, border, RoundedCornerShape(16.dp))
                        .clickable { selectedCategory = category }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("gallery_chip_$category")
                ) {
                    Text(
                        text = category,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = textCol
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Grid-Based Gallery Layout
        if (filteredPhotos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(StudioSurface)
                    .border(1.dp, StudioCardBorder, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Collections,
                        contentDescription = null,
                        tint = StudioTextMuted,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "কোনো ফটো পাওয়া যায়নি",
                        color = StudioTextMuted,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            // Adaptive Grid chunked by gridColumns (2 or 3 items per row)
            val chunkedPhotos = filteredPhotos.chunked(gridColumns)
            chunkedPhotos.forEachIndexed { rowIndex, rowPhotos ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowPhotos.forEach { photo ->
                        val isFav = favoriteIds.contains(photo.id)
                        PhotoGridItem(
                            photo = photo,
                            isFavorite = isFav,
                            isCompact = gridColumns == 3,
                            onToggleFavorite = { onToggleFavorite(photo.id) },
                            onSelect = { selectedPhotoForLightbox = photo },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Fill remaining empty columns if uneven
                    val emptySlots = gridColumns - rowPhotos.size
                    if (emptySlots > 0) {
                        for (i in 0 until emptySlots) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }

    // Lightbox / Detail Zoom Dialog
    selectedPhotoForLightbox?.let { photo ->
        PhotoDetailLightboxDialog(
            photo = photo,
            isFavorite = favoriteIds.contains(photo.id),
            onToggleFavorite = { onToggleFavorite(photo.id) },
            onDismiss = { selectedPhotoForLightbox = null }
        )
    }
}

/**
 * Individual Grid Item loaded asynchronously using Coil's SubcomposeAsyncImage / AsyncImage
 */
@Composable
private fun PhotoGridItem(
    photo: LocalPhotoAsset,
    isFavorite: Boolean,
    isCompact: Boolean,
    onToggleFavorite: () -> Unit,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = StudioSurface),
        border = BorderStroke(1.dp, StudioCardBorder),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onSelect)
            .testTag("photo_grid_item_${photo.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(if (isCompact) 1f else 1.25f)
            ) {
                // Coil SubcomposeAsyncImage with crossfade and memory cache configuration
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(photo.resId)
                        .crossfade(true)
                        .crossfade(300)
                        .build(),
                    contentDescription = photo.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize(),
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(StudioDarkBg),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = StudioGold,
                                strokeWidth = 2.dp
                            )
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(StudioDarkBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Error loading image",
                                tint = StudioRose,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                )

                // Top Gradient Vignette
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.5f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.7f)
                                )
                            )
                        )
                )

                // Category pill on top left
                Box(
                    modifier = Modifier
                        .padding(if (isCompact) 4.dp else 6.dp)
                        .align(Alignment.TopStart)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Black.copy(alpha = 0.75f))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = photo.category.uppercase(),
                        fontSize = if (isCompact) 7.sp else 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudioGold,
                        letterSpacing = 0.5.sp
                    )
                }

                // Favorite button on top right
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .padding(if (isCompact) 2.dp else 4.dp)
                        .align(Alignment.TopEnd)
                        .size(if (isCompact) 24.dp else 28.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f))
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) StudioRose else StudioTextSecondary,
                        modifier = Modifier.size(if (isCompact) 12.dp else 14.dp)
                    )
                }

                // Coil Badge on bottom right
                Box(
                    modifier = Modifier
                        .padding(if (isCompact) 4.dp else 6.dp)
                        .align(Alignment.BottomEnd)
                        .clip(RoundedCornerShape(3.dp))
                        .background(StudioEmerald.copy(alpha = 0.85f))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "COIL",
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            // Meta Info
            Column(
                modifier = Modifier.padding(if (isCompact) 6.dp else 10.dp)
            ) {
                Text(
                    text = photo.title,
                    fontSize = if (isCompact) 11.sp else 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = StudioTextPrimary,
                    maxLines = 1
                )
                if (!isCompact) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = photo.location,
                        fontSize = 10.sp,
                        color = StudioCyan,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = photo.exif,
                        fontSize = 9.sp,
                        color = StudioGold,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

/**
 * Detailed Lightbox Dialog displaying high-resolution local photo with full metadata
 */
@Composable
private fun PhotoDetailLightboxDialog(
    photo: LocalPhotoAsset,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(StudioDarkBg)
                .testTag("photo_lightbox_dialog"),
            color = StudioDarkBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Top control bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = StudioGold.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, StudioGold.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "COIL IMAGE VIEWER",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = StudioGold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = photo.category.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = StudioCyan
                        )
                    }

                    Row {
                        IconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(StudioSurfaceElevated)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) StudioRose else StudioTextSecondary
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(StudioSurfaceElevated)
                                .testTag("close_photo_lightbox_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = StudioTextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // High-Res Coil AsyncImage
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = StudioSurface),
                    border = BorderStroke(1.dp, StudioCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 10f)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(photo.resId)
                                .crossfade(true)
                                .crossfade(400)
                                .build(),
                            contentDescription = photo.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize()
                        )

                        // Bottom gradient
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.6f)
                                        )
                                    )
                                )
                        )

                        // Location pin on image
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = StudioCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = photo.location,
                                fontSize = 12.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title and Description
                Text(
                    text = photo.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = StudioTextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = photo.description,
                    fontSize = 13.sp,
                    color = StudioTextSecondary,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Technical EXIF & Asset Specifications Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = StudioSurfaceElevated),
                    border = BorderStroke(1.dp, StudioCardBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = StudioGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ফটোগ্রাফি অপটিক্স ও অ্যাসেট স্পেকস",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = StudioGold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        ExifSpecRow(label = "এক্সপোজার প্যারামিটার", value = photo.exif)
                        ExifSpecRow(label = "ফোকাল লেন্থ ও লেন্স", value = photo.focalLength)
                        ExifSpecRow(label = "রেজোলিউশন ও ফরম্যাট", value = photo.resolution)
                        ExifSpecRow(label = "ক্যাটাগরি ও স্টাইল", value = "${photo.category} (${photo.tag})")
                        ExifSpecRow(label = "ইমেজ লোডার পাইপলাইন", value = "Coil 2.7 • Async Memory Cache")
                    }
                }
            }
        }
    }
}

@Composable
private fun ExifSpecRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = StudioTextMuted
        )
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = StudioTextPrimary
        )
    }
}
