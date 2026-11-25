package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StudioMessage
import com.example.data.StudioUser
import com.example.ui.ContactFormState
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioCardBorderSubtle
import com.example.ui.theme.StudioCyan
import com.example.ui.theme.StudioEmerald
import com.example.ui.theme.StudioGold
import com.example.ui.theme.StudioRose
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceElevated
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ContactSection(
    formState: ContactFormState,
    savedDispatches: List<StudioMessage>,
    currentUser: StudioUser? = null,
    onOpenAuth: () -> Unit = {},
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onMessageChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onDeleteDispatch: (Long) -> Unit,
    onDirectEmail: (String) -> Unit,
    onClearDraft: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val categories = listOf("Collaboration", "Game Feedback", "Stills Inquiry", "Say Hello")
    val studioEmail = "hello@novahub.community"

    var richTextFieldValue by remember {
        mutableStateOf(TextFieldValue(formState.message, TextRange(formState.message.length)))
    }

    LaunchedEffect(formState.message) {
        if (richTextFieldValue.text != formState.message) {
            richTextFieldValue = TextFieldValue(formState.message, TextRange(formState.message.length))
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
            .testTag("contact_section")
    ) {
        // Section index
        Text(
            text = "07 / WRITE IN",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = StudioGold,
            letterSpacing = 1.5.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "A way to write in.",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = StudioTextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Whether you are proposing a game collaboration, inquiring about print stills, or simply dropping a line from afar — every dispatch is read.",
            style = MaterialTheme.typography.bodyLarge,
            color = StudioTextSecondary,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Creator Account Status Banner
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (currentUser != null) StudioEmerald.copy(alpha = 0.12f) else StudioSurfaceElevated
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (currentUser != null) StudioEmerald.copy(alpha = 0.4f) else StudioCardBorder
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onOpenAuth)
                .testTag("contact_account_banner")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (currentUser != null) StudioEmerald else StudioGold.copy(alpha = 0.2f))
                            .border(1.dp, if (currentUser != null) StudioEmerald else StudioGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (currentUser != null) {
                            Text(
                                text = currentUser.avatarInitials,
                                color = Color(0xFF08090C),
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = StudioGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = if (currentUser != null) "Signed in as: ${currentUser.username}" else "Community Member Account",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = StudioTextPrimary
                        )
                        Text(
                            text = if (currentUser != null) "${currentUser.roleTitle} • ${currentUser.email}" else "Sign in or create an account to sync across NovaHub",
                            fontSize = 11.sp,
                            color = if (currentUser != null) StudioEmerald else StudioTextMuted
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (currentUser != null) StudioEmerald.copy(alpha = 0.2f) else StudioGold)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (currentUser != null) "Profile" else "Sign In / Join",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (currentUser != null) StudioEmerald else Color(0xFF08090C)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Direct Studio Coordinates Card
        Card(
            colors = CardDefaults.cardColors(containerColor = StudioSurfaceElevated),
            border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "DIRECT INBOX",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = StudioGold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = studioEmail,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = StudioTextPrimary,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Row {
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("NovaHub Email", studioEmail)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Copied $studioEmail to clipboard", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(StudioSurface)
                                .border(1.dp, StudioCardBorder, CircleShape)
                                .size(36.dp)
                                .testTag("copy_email_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Email",
                                tint = StudioGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = { onDirectEmail(studioEmail) },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(StudioGold)
                                .size(36.dp)
                                .testTag("launch_email_client_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Open Email Client",
                                tint = Color(0xFF08090C),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = StudioCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Dhaka, Bangladesh • Global Remote",
                        fontSize = 12.sp,
                        color = StudioTextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Dispatch Form Card
        Card(
            colors = CardDefaults.cardColors(containerColor = StudioSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "DISPATCH FORM",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = StudioGold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Name Field
                OutlinedTextField(
                    value = formState.name,
                    onValueChange = onNameChange,
                    label = { Text("Your Name or Moniker") },
                    placeholder = { Text("e.g. Maya Lin") },
                    singleLine = true,
                    colors = studioTextFieldColors(),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_contact_name")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Email Field
                OutlinedTextField(
                    value = formState.email,
                    onValueChange = onEmailChange,
                    label = { Text("Your Return Email") },
                    placeholder = { Text("e.g. name@domain.com") },
                    singleLine = true,
                    colors = studioTextFieldColors(),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_contact_email")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Category Selector
                Text(
                    text = "PURPOSE OF DISPATCH",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = StudioTextMuted,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = cat == formState.category
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) StudioGold else StudioSurfaceElevated)
                                .border(
                                    1.dp,
                                    if (isSelected) StudioGold else StudioCardBorderSubtle,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { onCategoryChange(cat) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
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

                // Rich-Text Component for 'A Way to Write In'
                RichTextInputComponent(
                    textValue = richTextFieldValue,
                    onTextChange = { newVal ->
                        richTextFieldValue = newVal
                        onMessageChange(newVal.text)
                    },
                    onClearDraft = onClearDraft,
                    modifier = Modifier.fillMaxWidth()
                )

                // Error alert
                if (formState.errorMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = formState.errorMessage,
                        color = StudioRose,
                        fontSize = 12.sp
                    )
                }

                // Success alert
                AnimatedVisibility(visible = formState.submitSuccess) {
                    Column {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(StudioEmerald.copy(alpha = 0.12f))
                                .border(1.dp, StudioEmerald.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = StudioEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Dispatch recorded into studio archives & email draft triggered!",
                                fontSize = 12.sp,
                                color = StudioEmerald,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Submit Button
                Button(
                    onClick = onSubmit,
                    enabled = !formState.isSubmitting,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StudioGold,
                        contentColor = Color(0xFF08090C)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("submit_dispatch_btn")
                ) {
                    if (formState.isSubmitting) {
                        CircularProgressIndicator(
                            color = Color(0xFF08090C),
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Transmit Dispatch",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Local Archive / Sent Dispatches Section
        if (savedDispatches.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "YOUR RECORDED DISPATCHES (${savedDispatches.size})",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = StudioTextMuted,
                letterSpacing = 1.2.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            savedDispatches.forEach { msg ->
                SavedDispatchCard(
                    message = msg,
                    onDelete = { onDeleteDispatch(msg.id) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun SavedDispatchCard(
    message: StudioMessage,
    onDelete: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        .format(Date(message.timestamp))

    Card(
        colors = CardDefaults.cardColors(containerColor = StudioSurfaceElevated),
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorderSubtle),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = message.senderName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = StudioTextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${message.category}",
                        fontSize = 11.sp,
                        color = StudioGold
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Dispatch",
                        tint = StudioTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = message.message,
                fontSize = 12.sp,
                color = StudioTextSecondary,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = dateStr,
                fontSize = 10.sp,
                color = StudioTextMuted,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun studioTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = StudioGold,
    unfocusedBorderColor = StudioCardBorder,
    focusedTextColor = StudioTextPrimary,
    unfocusedTextColor = StudioTextPrimary,
    cursorColor = StudioGold,
    focusedLabelColor = StudioGold,
    unfocusedLabelColor = StudioTextMuted
)
