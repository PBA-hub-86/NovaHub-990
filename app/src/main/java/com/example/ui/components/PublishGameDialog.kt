package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.rememberCoroutineScope
import com.example.data.ai.FirebaseAiService
import kotlinx.coroutines.launch
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
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

/**
 * Dialog for publishing community-created browser games.
 * Users can enter title, description, play URL (HTML5, Phaser, itch.io, PlayCanvas, MakeCode),
 * tech stack tags, and pick a custom cover photo.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PublishGameDialog(
    onDismiss: () -> Unit,
    onPublish: (title: String, subtitle: String, description: String, webGameUrl: String, techStack: String, authorName: String, imageUri: String) -> Unit,
    initialAuthor: String = "Community Creator"
) {
    var title by remember { mutableStateOf("") }
    var subtitle by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var webGameUrl by remember { mutableStateOf("") }
    var techStack by remember { mutableStateOf("HTML5 Canvas, JavaScript") }
    var authorName by remember { mutableStateOf(initialAuthor) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var validationError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    // Android Photo Picker for game cover image
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {}
            selectedImageUri = uri
        }
    }

    // Quick Game / Web App template suggestions
    val gamePresets = listOf(
        Pair("Phaser 3 Game", "https://phaser.io/examples"),
        Pair("Itch.io Game", "https://itch.io"),
        Pair("MakeCode Arcade", "https://arcade.makecode.com/"),
        Pair("HTML5 2048", "https://play2048.co/"),
        Pair("Custom Web App", "https://github.com/")
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF060709).copy(alpha = 0.88f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = StudioSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .testTag("publish_game_dialog")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(StudioGold.copy(alpha = 0.15f))
                                    .border(1.dp, StudioGold, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SportsEsports,
                                    contentDescription = null,
                                    tint = StudioGold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Publish Game or Web App",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StudioTextPrimary
                                )
                                Text(
                                    text = "Add Your Web App or Browser Game",
                                    fontSize = 11.sp,
                                    color = StudioGold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(32.dp).testTag("close_publish_dialog_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = StudioTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Submit any HTML5 browser game or web app URL (itch.io, Vercel, Netlify, GitHub Pages, or Cloudflare Workers) to play directly inside NovaHub.",
                        fontSize = 12.sp,
                        color = StudioTextSecondary,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Cover image picker
                    Text(
                        text = "Game Cover Artwork",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StudioTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(StudioSurfaceElevated)
                            .border(1.dp, StudioCardBorder, RoundedCornerShape(10.dp))
                            .clickable {
                                photoPickerLauncher.launch(
                                    androidx.activity.result.PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            }
                            .testTag("pick_game_cover_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedImageUri != null) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Game cover preview",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(8.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(StudioDarkBg.copy(alpha = 0.8f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Change Image",
                                    fontSize = 10.sp,
                                    color = StudioGold,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = null,
                                    tint = StudioCyan,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Pick Cover Image from Gallery",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = StudioTextPrimary
                                )
                                Text(
                                    text = "Tap to select photo from device",
                                    fontSize = 10.sp,
                                    color = StudioTextMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val coroutineScope = rememberCoroutineScope()
                    var isAiGenerating by remember { mutableStateOf(false) }

                    // Header with Gemini AI Brainstorm Action
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Game / App Details",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = StudioTextPrimary
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(StudioGold.copy(alpha = 0.12f))
                                .border(1.dp, StudioGold.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .clickable(enabled = !isAiGenerating) {
                                    coroutineScope.launch {
                                        isAiGenerating = true
                                        try {
                                            val genre = if (subtitle.isNotBlank()) subtitle else "Retro Arcade"
                                            val res = FirebaseAiService.generateGameIdea(genre)
                                            res.onSuccess { idea ->
                                                description = idea.take(450)
                                                if (title.isBlank()) {
                                                    title = "Neon " + genre.take(12)
                                                }
                                            }
                                        } finally {
                                            isAiGenerating = false
                                        }
                                    }
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("gemini_idea_btn")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isAiGenerating) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(11.dp),
                                        color = StudioGold,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = StudioGold,
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isAiGenerating) "Gemini thinking..." else "Gemini Idea Assist",
                                    fontSize = 10.sp,
                                    color = StudioGold,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Title
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Game Title") },
                        placeholder = { Text("e.g., Cyber Neon Matrix") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = StudioGold,
                            unfocusedBorderColor = StudioCardBorder,
                            focusedTextColor = StudioTextPrimary,
                            unfocusedTextColor = StudioTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("publish_game_title_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Subtitle / Genre
                    OutlinedTextField(
                        value = subtitle,
                        onValueChange = { subtitle = it },
                        label = { Text("Genre / Subtitle") },
                        placeholder = { Text("e.g., 2D Kinetic Dodger / Arcade") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = StudioGold,
                            unfocusedBorderColor = StudioCardBorder,
                            focusedTextColor = StudioTextPrimary,
                            unfocusedTextColor = StudioTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("publish_game_subtitle_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Web Game URL
                    OutlinedTextField(
                        value = webGameUrl,
                        onValueChange = {
                            webGameUrl = it
                            validationError = null
                        },
                        label = { Text("Web Playable URL") },
                        placeholder = { Text("https://mygame.itch.io/game or https://...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Link,
                                contentDescription = null,
                                tint = StudioCyan
                            )
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = StudioGold,
                            unfocusedBorderColor = StudioCardBorder,
                            focusedTextColor = StudioTextPrimary,
                            unfocusedTextColor = StudioTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("publish_game_url_input")
                    )

                    // Presets chips
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Quick Demo Templates:",
                        fontSize = 11.sp,
                        color = StudioTextMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        gamePresets.forEach { (name, url) ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(StudioSurfaceElevated)
                                    .border(1.dp, StudioCardBorder, RoundedCornerShape(6.dp))
                                    .clickable {
                                        webGameUrl = url
                                        if (title.isBlank()) title = name
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = name,
                                    fontSize = 10.sp,
                                    color = StudioCyan,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tech Stack
                    OutlinedTextField(
                        value = techStack,
                        onValueChange = { techStack = it },
                        label = { Text("Tech Stack (comma separated)") },
                        placeholder = { Text("HTML5 Canvas, WebGL, Phaser, Audio API") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = StudioGold,
                            unfocusedBorderColor = StudioCardBorder,
                            focusedTextColor = StudioTextPrimary,
                            unfocusedTextColor = StudioTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("publish_game_tech_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Author Name
                    OutlinedTextField(
                        value = authorName,
                        onValueChange = { authorName = it },
                        label = { Text("Creator Name") },
                        placeholder = { Text("e.g. Nova Creator") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = StudioGold
                            )
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = StudioGold,
                            unfocusedBorderColor = StudioCardBorder,
                            focusedTextColor = StudioTextPrimary,
                            unfocusedTextColor = StudioTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("publish_game_author_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Description
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Gameplay Description") },
                        placeholder = { Text("How to play, controls, or key features...") },
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = StudioGold,
                            unfocusedBorderColor = StudioCardBorder,
                            focusedTextColor = StudioTextPrimary,
                            unfocusedTextColor = StudioTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("publish_game_desc_input")
                    )

                    if (validationError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = validationError ?: "",
                            fontSize = 12.sp,
                            color = StudioRose,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Submit Button
                    Button(
                        onClick = {
                            if (title.isBlank()) {
                                validationError = "Please enter a game title"
                                return@Button
                            }
                            val formattedUrl = if (webGameUrl.startsWith("http://") || webGameUrl.startsWith("https://")) {
                                webGameUrl.trim()
                            } else {
                                "https://${webGameUrl.trim()}"
                            }

                            if (formattedUrl.isBlank() || !formattedUrl.contains(".")) {
                                validationError = "Please enter a valid web URL (e.g. https://play2048.co)"
                                return@Button
                            }

                            onPublish(
                                title.trim(),
                                subtitle.trim(),
                                description.trim(),
                                formattedUrl,
                                techStack.trim(),
                                authorName.trim(),
                                selectedImageUri?.toString() ?: ""
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StudioGold,
                            contentColor = Color(0xFF08090C)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("submit_publish_game_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Publish,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Publish to Game Portal",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
