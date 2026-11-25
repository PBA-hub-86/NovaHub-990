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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.StudioUser
import com.example.data.StudioWriting
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WritingSection(
    writings: List<StudioWriting>,
    selectedCategory: String,
    searchQuery: String,
    onSelectCategory: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onReadWriting: (StudioWriting) -> Unit,
    onTogglePin: (StudioWriting) -> Unit,
    onDeleteWriting: (Long) -> Unit,
    onOpenCompose: () -> Unit,
    currentUser: StudioUser? = null,
    noteText: String = "",
    onNoteTextChange: (String) -> Unit = {},
    onSaveNote: () -> Unit = {},
    noteSaveSuccess: Boolean = false,
    modifier: Modifier = Modifier
) {
    val categories = listOf("All", "Game Design", "Nocturne & Stills", "Creative Code", "Field Notes")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
            .testTag("writing_section")
    ) {
        // Section Header tag
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "05 / BLOG & JOURNAL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = StudioGold,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(StudioGold.copy(alpha = 0.15f))
                        .border(1.dp, StudioGold.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "/blog",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudioGold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Compose New Essay Button
            Button(
                onClick = onOpenCompose,
                colors = ButtonDefaults.buttonColors(
                    containerColor = StudioGold,
                    contentColor = Color(0xFF08090C)
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier
                    .height(34.dp)
                    .testTag("compose_writing_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Write Article",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Articles, devlogs, and date-stamped journal entries.",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = StudioTextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Dedicated /blog section for long-form articles, devlogs on HTML5 game loops, and date-stamped journaling entries. Persisted reliably in local Room SQLite database.",
            style = MaterialTheme.typography.bodyLarge,
            color = StudioTextSecondary,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Date-Stamped Journaling & Quick Note Composer Card (TextField for writing notes and Button to save to Room database)
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = StudioSurface),
            border = BorderStroke(1.dp, StudioGold.copy(alpha = 0.35f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("quick_note_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(StudioGold.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = StudioGold,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "DATE-STAMPED JOURNALING",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StudioGold,
                                    letterSpacing = 1.2.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(StudioEmerald.copy(alpha = 0.15f))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "ROOM DB",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black,
                                        color = StudioEmerald
                                    )
                                }
                            }
                            Text(
                                text = "Record daily journal entries with automatic date and time stamps",
                                fontSize = 12.sp,
                                color = StudioTextSecondary
                            )
                        }
                    }

                    if (noteSaveSuccess) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(StudioEmerald.copy(alpha = 0.18f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = StudioEmerald,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Saved to Room",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = StudioEmerald
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // TextField for writing notes / journal entries
                OutlinedTextField(
                    value = noteText,
                    onValueChange = onNoteTextChange,
                    placeholder = {
                        Text(
                            text = "Write your journal entry, game observation, or thought for today...",
                            color = StudioTextMuted,
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp)
                        .testTag("note_text_field"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = StudioDarkBg.copy(alpha = 0.65f),
                        unfocusedContainerColor = StudioDarkBg.copy(alpha = 0.45f),
                        focusedBorderColor = StudioGold,
                        unfocusedBorderColor = StudioCardBorder,
                        focusedTextColor = StudioTextPrimary,
                        unfocusedTextColor = StudioTextPrimary
                    ),
                    maxLines = 8
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = StudioTextMuted,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (noteText.isBlank()) "Auto-date-stamped & saved to Room" else "${noteText.length} characters",
                            fontSize = 11.sp,
                            color = StudioTextMuted
                        )
                    }

                    // Button to save to Room database
                    Button(
                        onClick = onSaveNote,
                        enabled = noteText.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StudioGold,
                            contentColor = Color(0xFF08090C),
                            disabledContainerColor = StudioSurfaceElevated,
                            disabledContentColor = StudioTextMuted
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier
                            .height(38.dp)
                            .testTag("save_note_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Save Journal Entry",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Search in Writings
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = {
                Text("Search essays, tags, or concepts...", color = StudioTextMuted, fontSize = 13.sp)
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = StudioGold,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear search",
                            tint = StudioTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = StudioSurface,
                unfocusedContainerColor = StudioSurface,
                focusedBorderColor = StudioGold,
                unfocusedBorderColor = StudioCardBorder,
                focusedTextColor = StudioTextPrimary,
                unfocusedTextColor = StudioTextPrimary
            ),
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("writing_search_field")
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Category Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                val isSelected = selectedCategory == category
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) StudioGold else StudioSurface)
                        .border(
                            1.dp,
                            if (isSelected) StudioGold else StudioCardBorder,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { onSelectCategory(category) }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                        .testTag("writing_cat_${category.lowercase().replace(" ", "_")}")
                ) {
                    Text(
                        text = category,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color(0xFF08090C) else StudioTextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Writings count badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${writings.size} PUBLISHED PIECES",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = StudioTextMuted,
                letterSpacing = 1.2.sp
            )
            if (selectedCategory != "All" || searchQuery.isNotEmpty()) {
                Text(
                    text = "Filtered by: ${if (selectedCategory != "All") selectedCategory else ""} ${if (searchQuery.isNotEmpty()) "\"$searchQuery\"" else ""}",
                    fontSize = 11.sp,
                    color = StudioGold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (writings.isEmpty()) {
            // Empty state
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = StudioSurface),
                border = BorderStroke(1.dp, StudioCardBorderSubtle),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = StudioTextMuted,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No essays found matching criteria",
                        style = MaterialTheme.typography.titleMedium,
                        color = StudioTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Try clearing the search filter or compose a new studio entry.",
                        style = MaterialTheme.typography.bodySmall,
                        color = StudioTextMuted
                    )
                }
            }
        } else {
            // List of writing cards
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                writings.forEach { writing ->
                    WritingCard(
                        writing = writing,
                        onRead = { onReadWriting(writing) },
                        onTogglePin = { onTogglePin(writing) },
                        onDelete = { onDeleteWriting(writing.id) },
                        isAuthorOrAdmin = currentUser != null
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WritingCard(
    writing: StudioWriting,
    onRead: () -> Unit,
    onTogglePin: () -> Unit,
    onDelete: () -> Unit,
    isAuthorOrAdmin: Boolean
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = StudioSurface),
        border = BorderStroke(
            if (writing.isPinned) 1.5.dp else 1.dp,
            if (writing.isPinned) StudioGold.copy(alpha = 0.8f) else StudioCardBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onRead)
            .testTag("writing_card_${writing.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Top metadata row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Category Badge
                    val badgeColor = when (writing.category) {
                        "Game Design" -> StudioGold
                        "Nocturne & Stills" -> StudioCyan
                        "Creative Code" -> StudioEmerald
                        else -> StudioRose
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(badgeColor.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = writing.category.uppercase(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor,
                            letterSpacing = 0.8.sp
                        )
                    }

                    if (writing.isPinned) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(StudioGold.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.PushPin,
                                    contentDescription = null,
                                    tint = StudioGold,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "PINNED",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    color = StudioGold
                                )
                            }
                        }
                    }
                }

                // Date & Read time
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = StudioTextMuted,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "${writing.dateFormatted} • ${writing.readTimeMinutes} min read",
                        fontSize = 11.sp,
                        color = StudioTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title
            Text(
                text = writing.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = StudioTextPrimary,
                lineHeight = 26.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Summary
            Text(
                text = writing.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = StudioTextSecondary,
                lineHeight = 20.sp,
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Tags row
            if (writing.tags.isNotBlank()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    writing.tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }.take(4).forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(StudioDarkBg)
                                .border(1.dp, StudioCardBorderSubtle, RoundedCornerShape(6.dp))
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "#$tag",
                                fontSize = 10.sp,
                                color = StudioTextMuted,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Bottom bar with author and interactive read link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "By ${writing.author}",
                    fontSize = 11.sp,
                    color = StudioTextMuted,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Pin button
                    IconButton(
                        onClick = onTogglePin,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (writing.isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                            contentDescription = "Toggle pin",
                            tint = if (writing.isPinned) StudioGold else StudioTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Delete button if admin/author
                    if (isAuthorOrAdmin) {
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete writing",
                                tint = StudioRose,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Read article action
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(StudioGold.copy(alpha = 0.12f))
                            .clickable(onClick = onRead)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "Read Full",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = StudioGold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = StudioGold,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WritingReaderDialog(
    writing: StudioWriting,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            color = StudioDarkBg,
            modifier = Modifier
                .fillMaxSize()
                .testTag("writing_reader_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Top close action row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(StudioGold.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = writing.category.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = StudioGold,
                            letterSpacing = 1.sp
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(StudioSurface)
                            .testTag("close_writing_reader_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close reader",
                            tint = StudioTextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Title
                Text(
                    text = writing.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = StudioTextPrimary,
                    lineHeight = 32.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Author, date, read time metadata
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "By ${writing.author}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StudioGold
                    )
                    Text(
                        text = "•",
                        fontSize = 12.sp,
                        color = StudioTextMuted
                    )
                    Text(
                        text = writing.dateFormatted,
                        fontSize = 12.sp,
                        color = StudioTextMuted
                    )
                    Text(
                        text = "•",
                        fontSize = 12.sp,
                        color = StudioTextMuted
                    )
                    Text(
                        text = "${writing.readTimeMinutes} min read",
                        fontSize = 12.sp,
                        color = StudioTextMuted
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Summary Blockquote
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(StudioSurface)
                        .border(BorderStroke(1.dp, StudioCardBorder), RoundedCornerShape(8.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = writing.summary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = StudioTextSecondary,
                        lineHeight = 22.sp,
                        fontFamily = FontFamily.Serif
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Main Article Body
                writing.content.split("\n\n").forEach { paragraph ->
                    if (paragraph.isNotBlank()) {
                        Text(
                            text = paragraph.trim(),
                            style = MaterialTheme.typography.bodyLarge,
                            color = StudioTextPrimary,
                            lineHeight = 26.sp,
                            fontFamily = FontFamily.Default
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tags section
                if (writing.tags.isNotBlank()) {
                    Text(
                        text = "TOPICS & INDEX",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudioTextMuted,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        writing.tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }.forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(StudioSurface)
                                    .border(1.dp, StudioCardBorder, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "#$tag",
                                    fontSize = 11.sp,
                                    color = StudioGold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Bottom Close Button
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StudioSurface,
                        contentColor = StudioTextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, StudioCardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Close Essay")
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun ComposeWritingDialog(
    onSaveWriting: (title: String, category: String, summary: String, content: String, tags: String, readTime: Int, author: String, isPinned: Boolean) -> Unit,
    onDismiss: () -> Unit,
    currentUser: StudioUser? = null
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Game Design") }
    var summary by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    var author by remember { mutableStateOf(currentUser?.username ?: "NovaHub Editorial") }
    var readTimeMinutes by remember { mutableStateOf(3) }
    var isPinned by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val categories = listOf("Game Design", "Nocturne & Stills", "Creative Code", "Field Notes", "Essays")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            color = StudioDarkBg,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, StudioCardBorder),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .testTag("compose_writing_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "NEW BLOG ARTICLE & ESSAY (/blog)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = StudioGold,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "Compose & Publish to Room Database",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = StudioTextPrimary
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = StudioTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Error alert
                if (errorMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(StudioRose.copy(alpha = 0.15f))
                            .border(1.dp, StudioRose.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = StudioRose,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Title Input
                Text("Essay Title", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = StudioTextSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; errorMessage = null },
                    placeholder = { Text("e.g., Tactical Pacing in Top-Down Physics Games", color = StudioTextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = StudioSurface,
                        unfocusedContainerColor = StudioSurface,
                        focusedBorderColor = StudioGold,
                        unfocusedBorderColor = StudioCardBorder,
                        focusedTextColor = StudioTextPrimary,
                        unfocusedTextColor = StudioTextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("compose_title_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Category selection
                Text("Category", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = StudioTextSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = category == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) StudioGold else StudioSurface)
                                .border(1.dp, if (isSelected) StudioGold else StudioCardBorder, RoundedCornerShape(20.dp))
                                .clickable { category = cat }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
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

                Spacer(modifier = Modifier.height(14.dp))

                // Summary Input
                Text("Short Summary (1-2 sentences)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = StudioTextSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = summary,
                    onValueChange = { summary = it },
                    placeholder = { Text("Brief synthesis of the central argument or concept...", color = StudioTextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = StudioSurface,
                        unfocusedContainerColor = StudioSurface,
                        focusedBorderColor = StudioGold,
                        unfocusedBorderColor = StudioCardBorder,
                        focusedTextColor = StudioTextPrimary,
                        unfocusedTextColor = StudioTextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("compose_summary_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Main Content Input
                Text("Full Essay Content", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = StudioTextSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it; errorMessage = null },
                    placeholder = { Text("Write your reflections, dev notes, or architectural decisions...", color = StudioTextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = StudioSurface,
                        unfocusedContainerColor = StudioSurface,
                        focusedBorderColor = StudioGold,
                        unfocusedBorderColor = StudioCardBorder,
                        focusedTextColor = StudioTextPrimary,
                        unfocusedTextColor = StudioTextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    minLines = 8,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("compose_content_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Tags & Author Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Tags (comma separated)", fontSize = 12.sp, color = StudioTextSecondary)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = tags,
                            onValueChange = { tags = it },
                            placeholder = { Text("Physics, Canvas", color = StudioTextMuted, fontSize = 12.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = StudioSurface,
                                unfocusedContainerColor = StudioSurface,
                                focusedBorderColor = StudioGold,
                                unfocusedBorderColor = StudioCardBorder,
                                focusedTextColor = StudioTextPrimary,
                                unfocusedTextColor = StudioTextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Author Name", fontSize = 12.sp, color = StudioTextSecondary)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = author,
                            onValueChange = { author = it },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = StudioSurface,
                                unfocusedContainerColor = StudioSurface,
                                focusedBorderColor = StudioGold,
                                unfocusedBorderColor = StudioCardBorder,
                                focusedTextColor = StudioTextPrimary,
                                unfocusedTextColor = StudioTextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Pin to top checkbox row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { isPinned = !isPinned }
                        .background(StudioSurface)
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Icon(
                        imageVector = if (isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                        contentDescription = null,
                        tint = if (isPinned) StudioGold else StudioTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Pin to Top of Studio Writings",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = StudioTextPrimary
                        )
                        Text(
                            text = "Features this essay prominently at the top of the collection",
                            fontSize = 11.sp,
                            color = StudioTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Submit Button
                Button(
                    onClick = {
                        if (title.isBlank()) {
                            errorMessage = "Please enter an essay title."
                            return@Button
                        }
                        if (content.isBlank() || content.length < 20) {
                            errorMessage = "Essay content is too short. Please write at least 20 characters."
                            return@Button
                        }
                        val calculatedReadTime = maxOf(1, content.split("\\s+".toRegex()).size / 150)
                        onSaveWriting(
                            title.trim(),
                            category.trim(),
                            summary.trim().ifBlank { content.take(120).trim() + "..." },
                            content.trim(),
                            tags.trim(),
                            calculatedReadTime,
                            author.trim(),
                            isPinned
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StudioGold,
                        contentColor = Color(0xFF08090C)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_essay_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Publish to Room Database",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
