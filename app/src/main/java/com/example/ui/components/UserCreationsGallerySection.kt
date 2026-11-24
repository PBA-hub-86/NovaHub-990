package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.UserCreation
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioCyan
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.StudioEmerald
import com.example.ui.theme.StudioGold
import com.example.ui.theme.StudioGoldLight
import com.example.ui.theme.StudioRose
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceElevated
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UserCreationsGallerySection(
    creations: List<UserCreation>,
    selectedFilter: String,
    onSelectFilter: (String) -> Unit,
    onOpenDetail: (UserCreation) -> Unit,
    onDeleteCreation: (Long) -> Unit,
    onNavigateToAiStudio: () -> Unit,
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val filters = listOf("All", "Images", "Videos")

    val typeFiltered = when (selectedFilter) {
        "Images" -> creations.filter { it.type.equals("IMAGE", ignoreCase = true) }
        "Videos" -> creations.filter { it.type.equals("VIDEO", ignoreCase = true) }
        else -> creations
    }

    val filteredCreations = if (searchQuery.isBlank()) {
        typeFiltered
    } else {
        val q = searchQuery.trim().lowercase()
        typeFiltered.filter { creation ->
            creation.title.lowercase().contains(q) ||
            creation.prompt.lowercase().contains(q) ||
            creation.modelTag.lowercase().contains(q) ||
            creation.generatedOutput.lowercase().contains(q) ||
            creation.type.lowercase().contains(q) ||
            creation.resolution.lowercase().contains(q)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp)
            .testTag("user_creations_gallery_section")
    ) {
        // Section Badge / Index
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "02 / CREATIONS GALLERY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = StudioGold,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(StudioCyan)
                )
            }

            Text(
                text = "${filteredCreations.size} Artifacts",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = StudioTextSecondary
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "User-Generated AI Gallery",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = StudioTextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Browse synthesized text-to-image captures and Veo cinematic video storyboards generated with Gemini 2.5 Flash and Sora.",
            style = MaterialTheme.typography.bodyLarge,
            color = StudioTextSecondary,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Filter chips bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEach { filter ->
                val isSelected = filter == selectedFilter
                val bg = if (isSelected) StudioGold else StudioSurfaceElevated
                val border = if (isSelected) StudioGold else StudioCardBorder
                val textCol = if (isSelected) StudioDarkBg else StudioTextSecondary

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(bg)
                        .border(1.dp, border, RoundedCornerShape(16.dp))
                        .clickable { onSelectFilter(filter) }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                        .testTag("gallery_filter_$filter")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        when (filter) {
                            "Images" -> Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                tint = textCol,
                                modifier = Modifier.size(14.dp)
                            )
                            "Videos" -> Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = null,
                                tint = textCol,
                                modifier = Modifier.size(14.dp)
                            )
                            else -> Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = textCol,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = filter,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = textCol
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // In-Gallery Search input bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = {
                Text(
                    text = "Search gallery images, prompts, models...",
                    color = StudioTextMuted,
                    fontSize = 13.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search gallery",
                    tint = if (searchQuery.isNotEmpty()) StudioGold else StudioTextMuted,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { onSearchQueryChange("") },
                        modifier = Modifier.size(28.dp).testTag("gallery_clear_search_btn")
                    ) {
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
                .testTag("gallery_search_field")
        )

        if (searchQuery.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filtered by: \"$searchQuery\" (${filteredCreations.size} found)",
                    fontSize = 12.sp,
                    color = StudioGold,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Reset",
                    fontSize = 11.sp,
                    color = StudioCyan,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onSearchQueryChange("") }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (filteredCreations.isEmpty()) {
            // Empty state card
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
                    Icon(
                        imageVector = if (searchQuery.isNotEmpty()) Icons.Default.Search else Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = StudioGold,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (searchQuery.isNotEmpty()) "No gallery images match \"$searchQuery\"" else "No $selectedFilter Creations Yet",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudioTextPrimary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (searchQuery.isNotEmpty()) "Try adjusting your query or keywords to find images and artifacts." else "Synthesize high-fidelity prompts using the AI Studio Suite to populate your gallery.",
                        fontSize = 13.sp,
                        color = StudioTextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    if (searchQuery.isNotEmpty()) {
                        OutlinedButton(
                            onClick = { onSearchQueryChange("") },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = StudioGold),
                            border = androidx.compose.foundation.BorderStroke(1.dp, StudioGold),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Clear Search Filter", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(StudioGold)
                                .clickable(onClick = onNavigateToAiStudio)
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Launch AI Studio",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = StudioDarkBg
                            )
                        }
                    }
                }
            }
        } else {
            // 2-Column Responsive Grid
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                maxItemsInEachRow = 2
            ) {
                filteredCreations.forEach { item ->
                    CreationGridCard(
                        creation = item,
                        onClick = { onOpenDetail(item) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun CreationGridCard(
    creation: UserCreation,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isVideo = creation.type.equals("VIDEO", ignoreCase = true)
    val accent = if (isVideo) StudioCyan else StudioGold

    Card(
        colors = CardDefaults.cardColors(containerColor = StudioSurface),
        border = BorderStroke(1.dp, StudioCardBorder),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("creation_card_${creation.id}")
    ) {
        Column {
            // Media Image Thumbnail Container with Overlays
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 10f)
                    .background(StudioDarkBg)
            ) {
                if (creation.imageResId != 0) {
                    Image(
                        painter = painterResource(id = creation.imageResId),
                        contentDescription = creation.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(StudioSurfaceElevated, StudioDarkBg)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isVideo) Icons.Default.Videocam else Icons.Default.Image,
                            contentDescription = null,
                            tint = accent,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Gradient scrim overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    StudioDarkBg.copy(alpha = 0.85f)
                                )
                            )
                        )
                )

                // Top Badges (Media Type & Model Tag)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Type Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(StudioDarkBg.copy(alpha = 0.85f))
                            .border(1.dp, accent.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isVideo) Icons.Default.Videocam else Icons.Default.Image,
                                contentDescription = null,
                                tint = accent,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isVideo) "VIDEO" else "IMAGE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = accent,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    // Resolution Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(StudioDarkBg.copy(alpha = 0.85f))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (isVideo && creation.videoDuration.isNotEmpty()) creation.videoDuration else creation.resolution,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            color = StudioTextSecondary
                        )
                    }
                }

                // Center Play Icon for Video
                if (isVideo) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .align(Alignment.Center)
                            .clip(CircleShape)
                            .background(StudioCyan.copy(alpha = 0.85f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play preview",
                            tint = StudioDarkBg,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Text Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = creation.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = StudioTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = creation.prompt,
                    fontSize = 11.sp,
                    color = StudioTextSecondary,
                    lineHeight = 15.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = creation.modelTag,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = accent
                    )
                    Text(
                        text = "Inspect ↗",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StudioTextMuted
                    )
                }
            }
        }
    }
}

// Lightbox modal dialog for user-generated artifact
@Composable
fun UserCreationDetailDialog(
    creation: UserCreation,
    onClose: () -> Unit,
    onDelete: (Long) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var copiedNotice by remember { mutableStateOf(false) }
    val isVideo = creation.type.equals("VIDEO", ignoreCase = true)
    val accent = if (isVideo) StudioCyan else StudioGold

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.88f))
                .clickable(onClick = onClose)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = StudioSurface),
                border = BorderStroke(1.dp, StudioCardBorder),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = false) {}
                    .testTag("user_creation_detail_dialog")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Media Preview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 10f)
                            .background(StudioDarkBg)
                    ) {
                        if (creation.imageResId != 0) {
                            Image(
                                painter = painterResource(id = creation.imageResId),
                                contentDescription = creation.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isVideo) Icons.Default.Videocam else Icons.Default.Image,
                                    contentDescription = null,
                                    tint = accent,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }

                        // Close button
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(StudioDarkBg.copy(alpha = 0.7f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = StudioTextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Type & duration badge
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(StudioDarkBg.copy(alpha = 0.85f))
                                    .border(1.dp, accent, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (isVideo) "AI VIDEO STORYBOARD" else "AI IMAGE ARTIFACT",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = accent
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(StudioDarkBg.copy(alpha = 0.85f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = creation.resolution,
                                    fontSize = 10.sp,
                                    color = StudioTextSecondary
                                )
                            }
                        }
                    }

                    // Content Details
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = creation.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = StudioTextPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Synthesized via ${creation.modelTag} • ${creation.aspectRatio}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = accent
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Prompt box
                        Text(
                            text = "ORIGINAL PROMPT DIRECTIVE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = StudioTextMuted
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        SelectionContainer {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(StudioDarkBg)
                                    .border(1.dp, StudioCardBorder, RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = creation.prompt,
                                    fontSize = 12.sp,
                                    color = StudioTextSecondary,
                                    lineHeight = 18.sp
                                )
                            }
                        }

                        // Generated Output (if available)
                        if (creation.generatedOutput.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "SYNTHESIZED SCRIPT & PARAMETERS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = StudioTextMuted
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            SelectionContainer {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(StudioDarkBg)
                                        .border(1.dp, StudioCardBorder, RoundedCornerShape(8.dp))
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = creation.generatedOutput,
                                        fontSize = 12.sp,
                                        color = StudioTextPrimary,
                                        lineHeight = 18.sp,
                                        fontFamily = FontFamily.SansSerif
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Dialog Bottom Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Delete button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(StudioRose.copy(alpha = 0.12f))
                                    .border(1.dp, StudioRose.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .clickable {
                                        onDelete(creation.id)
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .testTag("delete_creation_btn")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = StudioRose,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Delete",
                                        fontSize = 11.sp,
                                        color = StudioRose,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            // Copy prompt button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(accent)
                                    .clickable {
                                        clipboardManager.setText(AnnotatedString(creation.prompt))
                                        copiedNotice = true
                                    }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                                    .testTag("copy_creation_prompt_btn")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Prompt",
                                        tint = StudioDarkBg,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (copiedNotice) "Copied!" else "Copy Prompt",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = StudioDarkBg
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
