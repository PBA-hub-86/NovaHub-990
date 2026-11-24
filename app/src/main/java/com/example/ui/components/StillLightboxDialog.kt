package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.StudioStill
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioCyan
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.StudioGold
import com.example.ui.theme.StudioRose
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary

@Composable
fun StillLightboxDialog(
    still: StudioStill,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onNextStill: (() -> Unit)? = null,
    onPreviousStill: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF060709).copy(alpha = 0.98f))
                .padding(16.dp)
                .testTag("still_lightbox_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top control bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = still.category.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = StudioGold,
                            letterSpacing = 1.2.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "• ${still.year}",
                            fontSize = 11.sp,
                            color = StudioTextMuted
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(StudioSurface)
                                .border(1.dp, StudioCardBorder, CircleShape)
                                .testTag("lightbox_fav_btn")
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite Still",
                                tint = if (isFavorite) StudioRose else StudioTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(StudioSurface)
                                .border(1.dp, StudioCardBorder, CircleShape)
                                .testTag("lightbox_close_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Lightbox",
                                tint = StudioTextPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // High Res Image with Navigation Overlay
                Box(modifier = Modifier.fillMaxWidth()) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
                        colors = CardDefaults.cardColors(containerColor = StudioSurface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(id = still.imageResId),
                            contentDescription = still.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 10f)
                        )
                    }

                    // Previous Still Button
                    if (onPreviousStill != null) {
                        IconButton(
                            onClick = onPreviousStill,
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF08090C).copy(alpha = 0.75f))
                                .border(1.dp, StudioCardBorder, CircleShape)
                                .size(36.dp)
                                .testTag("lightbox_prev_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = "Previous Photography",
                                tint = StudioGold,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    // Next Still Button
                    if (onNextStill != null) {
                        IconButton(
                            onClick = onNextStill,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF08090C).copy(alpha = 0.75f))
                                .border(1.dp, StudioCardBorder, CircleShape)
                                .size(36.dp)
                                .testTag("lightbox_next_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Next Photography",
                                tint = StudioGold,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title & Location
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = still.title,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = StudioTextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = StudioCyan,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = still.location,
                            fontSize = 13.sp,
                            color = StudioCyan,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Curatorial narrative
                    Text(
                        text = still.focalDescription,
                        style = MaterialTheme.typography.bodyLarge,
                        color = StudioTextSecondary,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // EXIF Metadata Pill
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(StudioSurface)
                            .border(1.dp, StudioCardBorder, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "CAMERA & OPTICS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = StudioTextMuted,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = still.exif,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = StudioGold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}
