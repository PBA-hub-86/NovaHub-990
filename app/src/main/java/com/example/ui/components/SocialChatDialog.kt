package com.example.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Videocam
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.ChatMessage
import com.example.data.PlatformSettings
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
import kotlinx.coroutines.delay

@Composable
fun SocialChatDialog(
    messages: List<ChatMessage>,
    inputText: String,
    onInputChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onStartCall: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .height(580.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, StudioCardBorder, RoundedCornerShape(20.dp)),
            color = StudioDarkBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
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
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(StudioEmerald.copy(alpha = 0.2f))
                                .border(1.dp, StudioEmerald, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "💬",
                                fontSize = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "কমিউনিটি লাইভ চ্যাট",
                                fontWeight = FontWeight.Bold,
                                color = StudioTextPrimary,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "সরাসরি একে অপরের সাথে যোগাযোগ করুন",
                                color = StudioEmerald,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { onStartCall("Community Support") },
                            modifier = Modifier.testTag("chat_voice_call_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "ভয়েস কল",
                                tint = StudioGold
                            )
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("chat_close_btn")
                        ) {
                            Text("✕", color = StudioTextMuted, fontSize = 18.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Chat Messages List
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(StudioSurface)
                        .border(1.dp, StudioCardBorderSubtle, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    androidx.compose.foundation.lazy.LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(messages.size) { index ->
                            val msg = messages[index]
                            val isMe = msg.isFromCurrentUser

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                            ) {
                                if (!isMe) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(StudioGold.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(msg.senderAvatar, fontSize = 10.sp, color = StudioGold, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                }

                                Column(
                                    horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(
                                            topStart = 12.dp,
                                            topEnd = 12.dp,
                                            bottomStart = if (isMe) 12.dp else 2.dp,
                                            bottomEnd = if (isMe) 2.dp else 12.dp
                                        ),
                                        color = if (isMe) StudioGold.copy(alpha = 0.2f) else StudioSurfaceElevated,
                                        border = BorderStroke(1.dp, if (isMe) StudioGold.copy(alpha = 0.4f) else StudioCardBorder)
                                    ) {
                                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                            if (!isMe) {
                                                Text(
                                                    text = msg.senderName,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = StudioGoldLight(isMe)
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                            }
                                            Text(
                                                text = msg.messageText,
                                                fontSize = 13.sp,
                                                color = StudioTextPrimary
                                            )
                                        }
                                    }
                                    Text(
                                        text = msg.timeFormatted,
                                        fontSize = 9.sp,
                                        color = StudioTextMuted,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Input Box
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = onInputChange,
                        placeholder = { Text("একটি বার্তা লিখুন...", color = StudioTextMuted, fontSize = 13.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field"),
                        shape = RoundedCornerShape(24.dp),
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
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onSendMessage,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(StudioGold)
                            .testTag("chat_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "পাঠান",
                            tint = Color.Black
                        )
                    }
                }
            }
        }
    }
}

private fun StudioGoldLight(isMe: Boolean): Color = if (isMe) StudioGold else StudioCyan

@Composable
fun VoiceCallDialog(
    peerName: String,
    callSeconds: Int,
    isMuted: Boolean,
    onToggleMute: () -> Unit,
    onSecondTick: () -> Unit,
    onEndCall: () -> Unit
) {
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            onSecondTick()
        }
    }

    val minutes = callSeconds / 60
    val secs = callSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, secs)

    Dialog(
        onDismissRequest = onEndCall,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, StudioCardBorder, RoundedCornerShape(24.dp)),
            color = StudioDarkBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "লাইভ ভয়েস কল",
                    color = StudioEmerald,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(StudioGold.copy(alpha = 0.2f))
                        .border(2.dp, StudioGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = peerName.take(2).uppercase(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudioGold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = peerName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = StudioTextPrimary
                )
                Text(
                    text = timeFormatted,
                    fontSize = 14.sp,
                    color = StudioTextMuted
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mute button
                    IconButton(
                        onClick = onToggleMute,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(if (isMuted) StudioRose.copy(alpha = 0.3f) else StudioSurfaceElevated)
                            .border(1.dp, if (isMuted) StudioRose else StudioCardBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Mute",
                            tint = if (isMuted) StudioRose else StudioTextPrimary
                        )
                    }

                    // End call button
                    IconButton(
                        onClick = onEndCall,
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(StudioRose)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "End Call",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlatformSettingsDialog(
    settings: PlatformSettings,
    onToggleSafeContent: () -> Unit,
    onToggleAutoPlay: () -> Unit,
    onToggleAllowCalls: () -> Unit,
    onToggleAllowGameUploads: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, StudioCardBorder, RoundedCornerShape(20.dp)),
            color = StudioDarkBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = StudioGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "প্ল্যাটফর্ম সেটিংস ও সুরক্ষা",
                            fontWeight = FontWeight.Bold,
                            color = StudioTextPrimary,
                            fontSize = 17.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Text("✕", color = StudioTextMuted, fontSize = 18.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                SettingsItem(
                    title = "🛡️ অপব্যবহার ও স্প্যাম ফিল্টার",
                    subtitle = "অশ্লীল, ক্ষতিকর বা স্প্যাম পোস্ট ও বার্তা স্বয়ংক্রিয়ভাবে ব্লক করে",
                    checked = settings.safeContentFilter,
                    onCheckedChange = { onToggleSafeContent() }
                )

                SettingsItem(
                    title = "📞 ডিরেক্ট কলিং সুবিধা",
                    subtitle = "কমিউনিটি মেম্বারদের সাথে অডিও যোগাযোগ বজায় রাখা",
                    checked = settings.allowDirectCalls,
                    onCheckedChange = { onToggleAllowCalls() }
                )

                SettingsItem(
                    title = "🎮 উন্মুক্ত গেম আপলোড",
                    subtitle = "ব্যবহারকারীরা নিজস্ব কোডেড গেম পাবলিশ করতে পারবে",
                    checked = settings.allowPublicGameUploads,
                    onCheckedChange = { onToggleAllowGameUploads() }
                )

                SettingsItem(
                    title = "🎬 স্বয়ংক্রিয় মিডিয়া প্রিভিউ",
                    subtitle = "ছবি ও ভিডিও গ্যালারিতে সরাসরি অটোপ্লে প্রিভিউ",
                    checked = settings.autoPlayMedia,
                    onCheckedChange = { onToggleAutoPlay() }
                )

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = StudioGold, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("সেটিংস সংরক্ষণ করুন", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun SettingsItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.SemiBold, color = StudioTextPrimary, fontSize = 14.sp)
            Text(text = subtitle, color = StudioTextMuted, fontSize = 11.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = StudioGold,
                checkedTrackColor = StudioGold.copy(alpha = 0.3f),
                uncheckedThumbColor = StudioTextMuted,
                uncheckedTrackColor = StudioSurfaceElevated
            )
        )
    }
}
