package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioCyan
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.StudioGold
import com.example.ui.theme.StudioRose
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceElevated
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary

/**
 * UploadMediaDialog enables users to upload photos or videos from their phone
 * directly into the User Creations Gallery.
 */
@Composable
fun UploadMediaDialog(
    onDismiss: () -> Unit,
    onUpload: (title: String, type: String, prompt: String, modelTag: String, uriString: String, duration: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var noteOrPrompt by remember { mutableStateOf("") }
    var selectedMediaType by remember { mutableStateOf("IMAGE") } // "IMAGE" or "VIDEO"
    var selectedMediaUri by remember { mutableStateOf<Uri?>(null) }
    var videoDuration by remember { mutableStateOf("0:15") }
    var validationError by remember { mutableStateOf<String?>(null) }

    // Android Safe Photo Picker launcher
    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedMediaUri = uri
        }
    }

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
                    .testTag("upload_media_dialog")
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
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = null,
                                    tint = StudioGold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Upload Photo or Video",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StudioTextPrimary
                                )
                                Text(
                                    text = "Save directly to Creations Gallery",
                                    fontSize = 11.sp,
                                    color = StudioGold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(32.dp).testTag("close_upload_dialog_btn")
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
                        text = "Choose any photo or video from your device using Android Photo Picker and archive it in your NovaHub Gallery.",
                        fontSize = 12.sp,
                        color = StudioTextSecondary,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Type Selector Tabs: Image vs Video
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Image Option
                        val isImage = selectedMediaType == "IMAGE"
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isImage) StudioGold else StudioSurfaceElevated)
                                .border(1.dp, if (isImage) StudioGold else StudioCardBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    selectedMediaType = "IMAGE"
                                    selectedMediaUri = null
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = null,
                                    tint = if (isImage) StudioDarkBg else StudioTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Photo",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isImage) StudioDarkBg else StudioTextSecondary
                                )
                            }
                        }

                        // Video Option
                        val isVideo = selectedMediaType == "VIDEO"
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isVideo) StudioCyan else StudioSurfaceElevated)
                                .border(1.dp, if (isVideo) StudioCyan else StudioCardBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    selectedMediaType = "VIDEO"
                                    selectedMediaUri = null
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Videocam,
                                    contentDescription = null,
                                    tint = if (isVideo) StudioDarkBg else StudioTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Video",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isVideo) StudioDarkBg else StudioTextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Media File Picker Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(StudioSurfaceElevated)
                            .border(1.dp, StudioCardBorder, RoundedCornerShape(10.dp))
                            .clickable {
                                if (selectedMediaType == "IMAGE") {
                                    pickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                } else {
                                    pickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                    )
                                }
                            }
                            .testTag("pick_media_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedMediaUri != null) {
                            if (selectedMediaType == "IMAGE") {
                                AsyncImage(
                                    model = selectedMediaUri,
                                    contentDescription = "Selected media preview",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.Videocam,
                                        contentDescription = null,
                                        tint = StudioCyan,
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Video File Selected",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = StudioCyan
                                    )
                                    Text(
                                        text = selectedMediaUri?.lastPathSegment ?: "Video selected",
                                        fontSize = 11.sp,
                                        color = StudioTextMuted
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(8.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(StudioDarkBg.copy(alpha = 0.8f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Change File",
                                    fontSize = 10.sp,
                                    color = StudioGold,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = if (selectedMediaType == "IMAGE") Icons.Default.Image else Icons.Default.Videocam,
                                    contentDescription = null,
                                    tint = if (selectedMediaType == "IMAGE") StudioGold else StudioCyan,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (selectedMediaType == "IMAGE") "Choose Photo from Device" else "Choose Video from Device",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = StudioTextPrimary
                                )
                                Text(
                                    text = "Tap to open system photo/video picker",
                                    fontSize = 11.sp,
                                    color = StudioTextMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Title
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        placeholder = { Text("e.g. My Cinematic Capture") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = StudioGold,
                            unfocusedBorderColor = StudioCardBorder,
                            focusedTextColor = StudioTextPrimary,
                            unfocusedTextColor = StudioTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("upload_title_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Prompt or Description
                    OutlinedTextField(
                        value = noteOrPrompt,
                        onValueChange = { noteOrPrompt = it },
                        label = { Text("Description / Notes") },
                        placeholder = { Text("e.g. Beautiful portrait, 85mm f/1.4 lens or scene description...") },
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = StudioGold,
                            unfocusedBorderColor = StudioCardBorder,
                            focusedTextColor = StudioTextPrimary,
                            unfocusedTextColor = StudioTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("upload_prompt_input")
                    )

                    if (selectedMediaType == "VIDEO") {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = videoDuration,
                            onValueChange = { videoDuration = it },
                            label = { Text("Video Duration") },
                            placeholder = { Text("e.g. 0:15 or 0:30") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = StudioGold,
                                unfocusedBorderColor = StudioCardBorder,
                                focusedTextColor = StudioTextPrimary,
                                unfocusedTextColor = StudioTextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("upload_duration_input")
                        )
                    }

                    if (validationError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = validationError ?: "",
                            fontSize = 12.sp,
                            color = StudioRose,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Submit
                    Button(
                        onClick = {
                            if (title.isBlank()) {
                                validationError = "Please enter a title"
                                return@Button
                            }
                            if (selectedMediaUri == null) {
                                validationError = "Please select a photo or video file"
                                return@Button
                            }

                            onUpload(
                                title,
                                selectedMediaType,
                                noteOrPrompt.ifBlank { "User uploaded visual media capture." },
                                "User Upload",
                                selectedMediaUri.toString(),
                                if (selectedMediaType == "VIDEO") videoDuration.ifBlank { "0:15" } else ""
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
                            .testTag("submit_upload_media_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Upload,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Save to Gallery",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
