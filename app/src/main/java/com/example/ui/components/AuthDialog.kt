package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.R
import com.example.data.StudioUser
import com.example.ui.AuthUiState
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch
import com.example.ui.theme.StudioCardBorder
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

@Composable
fun AuthDialog(
    authUiState: AuthUiState,
    onUsernameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRoleChange: (String) -> Unit,
    onToggleMode: () -> Unit,
    onLoginSubmit: () -> Unit,
    onRegisterSubmit: () -> Unit,
    onGoogleSignInToken: (String) -> Unit = {},
    onAuthLoading: (Boolean) -> Unit = {},
    onAuthError: (String) -> Unit = {},
    onLogout: () -> Unit,
    onDismiss: () -> Unit
) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = StudioSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, StudioGold.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp)
                .testTag("auth_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Top Header Row
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
                                imageVector = if (authUiState.currentUser != null) Icons.Default.AccountCircle else Icons.Default.Security,
                                contentDescription = null,
                                tint = StudioGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (authUiState.currentUser != null) "Studio Profile" else if (authUiState.isRegisterMode) "Create Account / নতুন একাউন্ট" else "Sign In / লগইন",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = StudioTextPrimary
                            )
                            Text(
                                text = if (authUiState.currentUser != null) "Active Member Session" else if (authUiState.isRegisterMode) "Join NovaHub Community" else "Sign in to NovaHub",
                                fontSize = 11.sp,
                                color = StudioTextMuted
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp).testTag("close_auth_dialog_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = StudioTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Logged in User Profile State
                if (authUiState.currentUser != null) {
                    val user = authUiState.currentUser
                    UserProfileCard(user = user, onLogout = onLogout)
                } else {
                    // Login / Registration Form

                    // Mode Switcher Tabs
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(StudioSurfaceElevated)
                            .border(1.dp, StudioCardBorder, RoundedCornerShape(10.dp))
                            .padding(4.dp)
                    ) {
                        // Login Tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (!authUiState.isRegisterMode) StudioGold else Color.Transparent)
                                .clickable { if (authUiState.isRegisterMode) onToggleMode() }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "লগইন (Sign In)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!authUiState.isRegisterMode) Color(0xFF08090C) else StudioTextSecondary
                            )
                        }

                        // Register Tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (authUiState.isRegisterMode) StudioGold else Color.Transparent)
                                .clickable { if (!authUiState.isRegisterMode) onToggleMode() }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "নতুন একাউন্ট (Register)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (authUiState.isRegisterMode) Color(0xFF08090C) else StudioTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Error Banner
                    AnimatedVisibility(visible = authUiState.errorMessage != null) {
                        authUiState.errorMessage?.let { msg ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(StudioRose.copy(alpha = 0.15f))
                                    .border(1.dp, StudioRose.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = msg,
                                    color = StudioRose,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    // Fields
                    if (authUiState.isRegisterMode) {
                        // Username
                        OutlinedTextField(
                            value = authUiState.usernameInput,
                            onValueChange = onUsernameChange,
                            label = { Text("আপনার নাম / ইউজারনেম") },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = StudioGold)
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = StudioGold,
                                unfocusedBorderColor = StudioCardBorder,
                                focusedLabelColor = StudioGold,
                                unfocusedLabelColor = StudioTextMuted,
                                focusedTextColor = StudioTextPrimary,
                                unfocusedTextColor = StudioTextPrimary,
                                focusedContainerColor = StudioDarkBg,
                                unfocusedContainerColor = StudioDarkBg
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("auth_username_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Email Address
                    OutlinedTextField(
                        value = authUiState.emailInput,
                        onValueChange = onEmailChange,
                        label = { Text(if (authUiState.isRegisterMode) "ইমেইল অ্যাড্রেস" else "ইমেইল বা ইউজারনেম") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = StudioGold)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = StudioGold,
                            unfocusedBorderColor = StudioCardBorder,
                            focusedLabelColor = StudioGold,
                            unfocusedLabelColor = StudioTextMuted,
                            focusedTextColor = StudioTextPrimary,
                            unfocusedTextColor = StudioTextPrimary,
                            focusedContainerColor = StudioDarkBg,
                            unfocusedContainerColor = StudioDarkBg
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("auth_email_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Password
                    OutlinedTextField(
                        value = authUiState.passwordInput,
                        onValueChange = onPasswordChange,
                        label = { Text("পাসওয়ার্ড") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = StudioGold)
                        },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle password visibility",
                                    tint = StudioTextSecondary
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (authUiState.isRegisterMode) onRegisterSubmit() else onLoginSubmit()
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = StudioGold,
                            unfocusedBorderColor = StudioCardBorder,
                            focusedLabelColor = StudioGold,
                            unfocusedLabelColor = StudioTextMuted,
                            focusedTextColor = StudioTextPrimary,
                            unfocusedTextColor = StudioTextPrimary,
                            focusedContainerColor = StudioDarkBg,
                            unfocusedContainerColor = StudioDarkBg
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("auth_password_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Google Sign-In via Jetpack Credential Manager
                    androidx.compose.material3.OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                onAuthLoading(true)
                                try {
                                    val credentialManager = CredentialManager.create(context)
                                    val googleOption = GetSignInWithGoogleOption.Builder(
                                        context.getString(R.string.default_web_client_id)
                                    ).build()
                                    val request = GetCredentialRequest.Builder()
                                        .addCredentialOption(googleOption)
                                        .build()
                                    val result = credentialManager.getCredential(
                                        context = context,
                                        request = request
                                    )
                                    val credential = result.credential
                                    if (credential is CustomCredential &&
                                        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                                    ) {
                                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                        onGoogleSignInToken(googleIdTokenCredential.idToken)
                                    } else {
                                        onAuthError("অপ্রত্যাশিত Google ক্রেডেনশিয়াল পাওয়া গেছে")
                                    }
                                } catch (e: GetCredentialCancellationException) {
                                    Log.w("AuthDialog", "Google Sign-In cancelled by user", e)
                                    onAuthLoading(false)
                                } catch (e: GetCredentialException) {
                                    Log.e("AuthDialog", "Google Sign-In failed", e)
                                    onAuthError(e.localizedMessage ?: "Google সাইন-ইন সম্পন্ন করা যায়নি")
                                } catch (e: Exception) {
                                    Log.e("AuthDialog", "Unexpected Google Sign-In error", e)
                                    onAuthError(e.localizedMessage ?: "সাইন-ইন করতে সমস্যা হয়েছে")
                                }
                            }
                        },
                        enabled = !authUiState.isLoading,
                        border = BorderStroke(1.dp, StudioCyan.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("google_sign_in_btn")
                    ) {
                        Text(
                            text = "G  Sign in with Google",
                            color = StudioCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Primary Submit Button
                    Button(
                        onClick = {
                            if (authUiState.isRegisterMode) onRegisterSubmit() else onLoginSubmit()
                        },
                        enabled = !authUiState.isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StudioGold,
                            contentColor = Color(0xFF08090C)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("auth_submit_btn")
                    ) {
                        if (authUiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color(0xFF08090C),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = if (authUiState.isRegisterMode) "একাউন্ট তৈরি সম্পন্ন করুন" else "লগইন করুন",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Bottom helper toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (authUiState.isRegisterMode) "ইতোমধ্যে একাউন্ট আছে?" else "নতুন ইউজার?",
                            fontSize = 12.sp,
                            color = StudioTextSecondary
                        )
                        TextButton(onClick = onToggleMode) {
                            Text(
                                text = if (authUiState.isRegisterMode) "লগইন করুন" else "একাউন্ট খুলুন",
                                color = StudioGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UserProfileCard(
    user: StudioUser,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Large Avatar Monogram
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(StudioGold)
                .border(2.dp, StudioGold.copy(alpha = 0.8f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.avatarInitials,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF08090C)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = user.username,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = StudioTextPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Role Badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(StudioEmerald.copy(alpha = 0.15f))
                .border(1.dp, StudioEmerald.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                .padding(horizontal = 10.dp, vertical = 3.dp)
        ) {
            Text(
                text = user.roleTitle.uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = StudioEmerald,
                letterSpacing = 0.8.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Details Card
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = StudioDarkBg),
            border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                ProfileDetailRow(label = "Email", value = user.email)
                Spacer(modifier = Modifier.height(6.dp))
                ProfileDetailRow(label = "Bio", value = user.bio)
                Spacer(modifier = Modifier.height(6.dp))
                ProfileDetailRow(label = "Vault State", value = "Local SQLite Encrypted Active")
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Logout Button
        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(
                containerColor = StudioSurfaceElevated,
                contentColor = StudioRose
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, StudioRose.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .testTag("auth_logout_btn")
        ) {
            Text(
                text = "সাইন আউট (Sign Out)",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun ProfileDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 11.sp, color = StudioTextMuted)
        Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = StudioTextPrimary)
    }
}
