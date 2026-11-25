package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.R
import com.example.data.StudioStill
import com.example.data.UserCreation
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
 * Unified Still/Photo item for the Masonry Gallery
 */
data class MasonryPhotoItem(
    val id: String,
    val title: String,
    val location: String,
    val category: String,
    val focalDescription: String,
    val exif: String,
    val imageResId: Int = 0,
    val imageUri: String = "",
    val aspectRatio: Float = 1.0f, // Natural aspect ratio for staggered masonry heights
    val isPersonalUpload: Boolean = false,
    val year: String = "2025"
)

/**
 * Production-ready Masonry-style Stills & Photography Gallery
 * Employs Coil for asynchronous image decoding, crossfade animations, disk/memory caching,
 * and displays personal stills/photos in a staggered 2-column aesthetic layout.
 */
@Composable
fun MasonryStillsGalleryComponent(
    curatedStills: List<StudioStill>,
    userUploads: List<UserCreation>,
    favoriteIds: Set<String>,
    onToggleFavorite: (String) -> Unit,
    onAddPersonalPhoto: (title: String, location: String, uri: String) -> Unit,
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedPhotoForLightbox by remember { mutableStateOf<MasonryPhotoItem?>(null) }
    var isAddPhotoDialogOpen by remember { mutableStateOf(false) }

    // Convert curated stills and user creations into Masonry items with varied aspect ratios
    val allMasonryPhotos = remember(curatedStills, userUploads) {
        val aspectRatios = listOf(0.75f, 1.25f, 0.8f, 1.0f, 1.4f, 0.7f)
        val curated = curatedStills.mapIndexed { index, still ->
            MasonryPhotoItem(
                id = still.id,
                title = still.title,
                location = still.location,
                category = still.category,
                focalDescription = still.focalDescription,
                exif = still.exif,
                imageResId = still.imageResId,
                aspectRatio = aspectRatios[index % aspectRatios.size],
                isPersonalUpload = false,
                year = still.year
            )
        }

        val userPhotos = userUploads
            .filter { it.type.equals("IMAGE", ignoreCase = true) }
            .mapIndexed { index, creation ->
                MasonryPhotoItem(
                    id = "user_${creation.id}",
                    title = creation.title,
                    location = if (creation.modelTag.isNotBlank()) creation.modelTag else "Personal Studio",
                    category = "Personal Uploads",
                    focalDescription = creation.prompt.ifBlank { "Personal photography upload preserved locally in Room database." },
                    exif = if (creation.resolution.isNotBlank()) "${creation.resolution} • ${creation.aspectRatio}" else "Fine Art RAW",
                    imageResId = creation.imageResId,
                    imageUri = creation.mediaUri,
                    aspectRatio = if (index % 2 == 0) 0.85f else 1.2f,
                    isPersonalUpload = true,
                    year = "2026"
                )
            }

        userPhotos + curated
    }

    // Filter items based on active category & search query
    val filteredPhotos = remember(allMasonryPhotos, selectedCategory, searchQuery) {
        allMasonryPhotos.filter { photo ->
            val matchesCategory = when (selectedCategory) {
                "All" -> true
                "Personal" -> photo.isPersonalUpload
                "Favorites" -> favoriteIds.contains(photo.id)
                else -> photo.category.equals(selectedCategory, ignoreCase = true)
            }
            val q = searchQuery.trim().lowercase()
            val matchesQuery = q.isBlank() ||
                    photo.title.lowercase().contains(q) ||
                    photo.location.lowercase().contains(q) ||
                    photo.category.lowercase().contains(q) ||
                    photo.exif.lowercase().contains(q) ||
                    photo.focalDescription.lowercase().contains(q)

            matchesCategory && matchesQuery
        }
    }

    // Distribute into 2 columns for true staggered masonry layout
    val column1Items = remember(filteredPhotos) {
        filteredPhotos.filterIndexed { index, _ -> index % 2 == 0 }
    }
    val column2Items = remember(filteredPhotos) {
        filteredPhotos.filterIndexed { index, _ -> index % 2 != 0 }
    }

    val categories = listOf("All", "Personal", "Favorites", "Urban Night", "Golden Dawn", "Brutalist Shadow")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp)
            .testTag("masonry_stills_gallery_section")
    ) {
        // Section Badge Header
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
                    text = "04 / MASONRY STILLS & DARKROOM ARCHIVE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = StudioGold,
                    letterSpacing = 1.4.sp
                )
            }

            // Add Personal Photo Button (Triggers Room SQLite persistence)
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = StudioGold,
                modifier = Modifier
                    .clickable { isAddPhotoDialogOpen = true }
                    .testTag("add_personal_still_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = null,
                        tint = StudioDarkBg,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Add Photo",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudioDarkBg
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Personal Stills & Nocturnes",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = StudioTextPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Fine art photography archive rendered via staggered masonry cards. Fully optimized with Coil memory & disk caching. Any added photos persist permanently in Room.",
            style = MaterialTheme.typography.bodyMedium,
            color = StudioTextSecondary,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // In-Gallery Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Search photos by title, EXIF, location...", color = StudioTextMuted, fontSize = 12.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = if (searchQuery.isNotEmpty()) StudioGold else StudioTextMuted,
                    modifier = Modifier.size(16.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = StudioGold,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
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
                .testTag("masonry_gallery_search_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category Filter Chips (Horizontal Scroll)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = cat == selectedCategory
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) StudioGold else StudioSurfaceElevated)
                        .border(
                            1.dp,
                            if (isSelected) StudioGold else StudioCardBorderSubtle,
                            RoundedCornerShape(6.dp)
                        )
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("masonry_cat_$cat")
                ) {
                    Text(
                        text = cat,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color(0xFF08090C) else StudioTextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // True Staggered 2-Column Masonry Grid
        if (filteredPhotos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(StudioSurface)
                    .border(1.dp, StudioCardBorder, RoundedCornerShape(12.dp))
                    .padding(32.dp),
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
                        text = "No stills found matching \"$searchQuery\"",
                        color = StudioTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap 'Add Photo' to upload and persist your personal photo archive.",
                        color = StudioTextMuted,
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Column 1
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    column1Items.forEach { photo ->
                        MasonryPhotoCard(
                            photo = photo,
                            isFavorite = favoriteIds.contains(photo.id),
                            onToggleFavorite = { onToggleFavorite(photo.id) },
                            onClick = { selectedPhotoForLightbox = photo }
                        )
                    }
                }

                // Column 2
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    column2Items.forEach { photo ->
                        MasonryPhotoCard(
                            photo = photo,
                            isFavorite = favoriteIds.contains(photo.id),
                            onToggleFavorite = { onToggleFavorite(photo.id) },
                            onClick = { selectedPhotoForLightbox = photo }
                        )
                    }
                }
            }
        }
    }

    // Fullscreen Lightbox Zoom Modal
    selectedPhotoForLightbox?.let { photo ->
        MasonryLightboxDialog(
            photo = photo,
            isFavorite = favoriteIds.contains(photo.id),
            onToggleFavorite = { onToggleFavorite(photo.id) },
            onDismiss = { selectedPhotoForLightbox = null }
        )
    }

    // Add Personal Still Dialog (Photo Picker + Room SQLite persistence)
    if (isAddPhotoDialogOpen) {
        AddPersonalStillDialog(
            onSave = { title, location, uri ->
                onAddPersonalPhoto(title, location, uri)
                isAddPhotoDialogOpen = false
            },
            onDismiss = { isAddPhotoDialogOpen = false }
        )
    }
}

/**
 * Individual Staggered Masonry Photo Card with Coil Loading & Caching
 */
@Composable
private fun MasonryPhotoCard(
    photo: MasonryPhotoItem,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = StudioSurface),
        border = BorderStroke(1.dp, StudioCardBorder),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag("masonry_card_${photo.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(photo.aspectRatio)
            ) {
                // Optimized Coil SubcomposeAsyncImage with crossfade and memory/disk cache policy
                val imageModel = if (photo.imageUri.isNotBlank()) {
                    photo.imageUri
                } else if (photo.imageResId != 0) {
                    photo.imageResId
                } else {
                    R.drawable.still_dhaka_night_1789660149613
                }

                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imageModel)
                        .crossfade(true)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .build(),
                    contentDescription = photo.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(StudioSurfaceElevated),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = StudioGold,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(StudioSurfaceElevated),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Collections,
                                contentDescription = null,
                                tint = StudioTextMuted,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                )

                // Subtle gradient scrim at bottom
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xCC000000))
                            )
                        )
                )

                // Top badges: Category / Personal label & Favorite Heart
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xAA08090C)
                    ) {
                        Text(
                            text = if (photo.isPersonalUpload) "PERSONAL" else photo.category.uppercase(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (photo.isPersonalUpload) StudioEmerald else StudioGold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0x88000000))
                            .clickable { onToggleFavorite() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) StudioRose else Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            // Metadata info container below image
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(
                    text = photo.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = StudioTextPrimary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = StudioCyan,
                        modifier = Modifier.size(11.dp)
                    )
                    Text(
                        text = photo.location,
                        fontSize = 10.sp,
                        color = StudioTextMuted,
                        maxLines = 1
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = photo.exif,
                    fontSize = 9.sp,
                    color = StudioGold.copy(alpha = 0.9f),
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1
                )
            }
        }
    }
}

/**
 * Fullscreen Lightbox Zoom Dialog for viewing a fine art photo
 */
@Composable
private fun MasonryLightboxDialog(
    photo: MasonryPhotoItem,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val imageModel = if (photo.imageUri.isNotBlank()) {
        photo.imageUri
    } else if (photo.imageResId != 0) {
        photo.imageResId
    } else {
        R.drawable.still_dhaka_night_1789660149613
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xF508090C))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(StudioSurface)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = StudioTextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(StudioSurface)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) StudioRose else StudioTextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // High-fidelity Coil Image view
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = StudioSurfaceElevated),
                    border = BorderStroke(1.dp, StudioCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(imageModel)
                            .crossfade(true)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .build(),
                        contentDescription = photo.title,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(380.dp),
                        loading = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = StudioGold)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Metadata Details Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = StudioSurface),
                    border = BorderStroke(1.dp, StudioCardBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = photo.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = StudioTextPrimary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = StudioCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = photo.location,
                                fontSize = 12.sp,
                                color = StudioCyan,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "•  ${photo.category} (${photo.year})",
                                fontSize = 12.sp,
                                color = StudioTextMuted
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = photo.focalDescription,
                            fontSize = 13.sp,
                            color = StudioTextSecondary,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // EXIF Specs
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(StudioSurfaceElevated)
                                .border(1.dp, StudioCardBorder, RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = StudioGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = photo.exif,
                                fontSize = 11.sp,
                                color = StudioGold,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Dialog to add a personal photo/still with zero-permission picker and Room persistence
 */
@Composable
private fun AddPersonalStillDialog(
    onSave: (title: String, location: String, uri: String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {}
            selectedUri = uri
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = StudioDarkBg),
            border = BorderStroke(1.dp, StudioCardBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Add Personal Still",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudioTextPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = StudioTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Photo Selection Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(StudioSurface)
                        .border(1.dp, StudioCardBorder, RoundedCornerShape(10.dp))
                        .clickable {
                            photoPickerLauncher.launch(
                                androidx.activity.result.PickVisualMediaRequest(
                                    androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedUri != null) {
                        AsyncImage(
                            model = selectedUri,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                tint = StudioGold,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Tap to choose photo from device",
                                fontSize = 12.sp,
                                color = StudioTextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Photo Title") },
                    placeholder = { Text("e.g. Riverbank Dawn Reflections") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = StudioSurface,
                        unfocusedContainerColor = StudioSurface,
                        focusedBorderColor = StudioGold,
                        unfocusedBorderColor = StudioCardBorder,
                        focusedTextColor = StudioTextPrimary,
                        unfocusedTextColor = StudioTextPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location / Camera Notes") },
                    placeholder = { Text("e.g. Hatirjheel • 35mm Prime") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = StudioSurface,
                        unfocusedContainerColor = StudioSurface,
                        focusedBorderColor = StudioGold,
                        unfocusedBorderColor = StudioCardBorder,
                        focusedTextColor = StudioTextPrimary,
                        unfocusedTextColor = StudioTextPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                errorMessage?.let { err ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = err, color = StudioRose, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (title.isBlank()) {
                            errorMessage = "Please enter a photo title"
                        } else if (selectedUri == null) {
                            errorMessage = "Please select a photo"
                        } else {
                            onSave(title.trim(), location.trim(), selectedUri.toString())
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StudioGold,
                        contentColor = StudioDarkBg
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Text("Save to Gallery (Room DB)", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
