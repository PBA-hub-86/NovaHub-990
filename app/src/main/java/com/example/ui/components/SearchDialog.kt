package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.SearchResultItem
import com.example.ui.SearchUiState
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
fun SearchDialog(
    searchState: SearchUiState,
    onQueryChange: (String) -> Unit,
    onSelectResult: (SearchResultItem) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = StudioSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, StudioGold.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 24.dp)
                .testTag("search_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Header with Search Title and Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(StudioGold.copy(alpha = 0.15f))
                                .border(1.dp, StudioGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = StudioGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Search Studio / সন্ধান করুন",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = StudioTextPrimary
                            )
                            Text(
                                text = "Find games, stills, blog posts, articles & sections",
                                fontSize = 11.sp,
                                color = StudioTextMuted
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp).testTag("close_search_dialog_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Search",
                            tint = StudioTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Search Input Field
                OutlinedTextField(
                    value = searchState.query,
                    onValueChange = onQueryChange,
                    placeholder = { Text("Search games, photos, creations, dispatches...", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = StudioGold)
                    },
                    trailingIcon = {
                        if (searchState.query.isNotEmpty()) {
                            IconButton(onClick = { onQueryChange("") }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = StudioTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = StudioGold,
                        unfocusedBorderColor = StudioCardBorder,
                        focusedTextColor = StudioTextPrimary,
                        unfocusedTextColor = StudioTextPrimary,
                        focusedContainerColor = StudioDarkBg,
                        unfocusedContainerColor = StudioDarkBg,
                        focusedPlaceholderColor = StudioTextMuted,
                        unfocusedPlaceholderColor = StudioTextMuted
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_text_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Results list
                Text(
                    text = if (searchState.query.isBlank()) "QUICK SUGGESTIONS" else "SEARCH RESULTS (${searchState.results.size})",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = StudioGold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (searchState.results.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(StudioDarkBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "কোনো ফলাফল পাওয়া যায়নি",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = StudioTextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Try searching for 'Void Runner', 'stills', 'blog' or 'contact'",
                                fontSize = 11.sp,
                                color = StudioTextMuted
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 380.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(searchState.results) { item ->
                            SearchResultRow(
                                item = item,
                                onClick = { onSelectResult(item) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultRow(
    item: SearchResultItem,
    onClick: () -> Unit
) {
    val (icon, badgeText, badgeColor, title, subtitle) = when (item) {
        is SearchResultItem.GameItem -> {
            SearchRowInfo(
                icon = Icons.Default.SportsEsports,
                badge = "GAME",
                badgeColor = StudioGold,
                title = item.title,
                subtitle = "${item.subtitle} • ${item.techStack}"
            )
        }
        is SearchResultItem.StillItem -> {
            SearchRowInfo(
                icon = Icons.Default.CameraAlt,
                badge = "STILL",
                badgeColor = StudioCyan,
                title = item.title,
                subtitle = "${item.category} • ${item.location}"
            )
        }
        is SearchResultItem.CreationItem -> {
            SearchRowInfo(
                icon = Icons.Default.AutoAwesome,
                badge = "VAULT",
                badgeColor = StudioEmerald,
                title = item.title,
                subtitle = "${item.type} • ${item.prompt}"
            )
        }
        is SearchResultItem.NavigationItem -> {
            SearchRowInfo(
                icon = Icons.Default.Explore,
                badge = "SECTION",
                badgeColor = StudioGold,
                title = item.title,
                subtitle = item.description
            )
        }
        is SearchResultItem.WritingItem -> {
            SearchRowInfo(
                icon = Icons.Default.MenuBook,
                badge = "ESSAY",
                badgeColor = StudioGold,
                title = item.title,
                subtitle = "${item.category} • ${item.summary}"
            )
        }
    }

    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = StudioDarkBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("search_result_item")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(badgeColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = badgeColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = StudioTextPrimary,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(badgeColor.copy(alpha = 0.15f))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badgeText,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = badgeColor
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = StudioTextMuted,
                        maxLines = 1
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = StudioTextSecondary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

private data class SearchRowInfo(
    val icon: ImageVector,
    val badge: String,
    val badgeColor: Color,
    val title: String,
    val subtitle: String
)
