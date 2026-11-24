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
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioCardBorderSubtle
import com.example.ui.theme.StudioCyan
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.StudioEmerald
import com.example.ui.theme.StudioGold
import com.example.ui.theme.StudioGoldDark
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceElevated
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary

@Composable
fun HeroSection(
    onPlayGame: () -> Unit,
    onExploreStills: () -> Unit,
    onWriteIn: () -> Unit,
    onOpenSocial: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp)
            .testTag("hero_section")
    ) {
        // Tagline badge with Bengali script
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(30.dp))
                .background(StudioSurface)
                .border(1.dp, StudioCardBorder, RoundedCornerShape(30.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(StudioEmerald)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "PUBLIC SOCIAL & ARCADE HUB",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = StudioEmerald,
                letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "•",
                fontSize = 11.sp,
                color = StudioTextMuted
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "নোভা হাব কমিউনিটি",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = StudioGold
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Big Cinematic Editorial Headline
        Text(
            text = "পাবলিক সোশ্যাল,\nগেমস এবং ক্রিয়েটর হাব।",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = StudioTextPrimary,
            lineHeight = 38.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Bio & Statement
        Text(
            text = "সবাইকে স্বাগতম! এখানে লাইভ ওয়েব গেমস (যেমন Care Survey 89) খেলুন, ছবি ও ভিডিও শেয়ার করুন, ব্লগ পড়ুন এবং কমিউনিটির সবার সাথে লাইভ চ্যাটে যুক্ত হোন। আপনার সব ডেটা নিরাপদে সংরক্ষিত থাকে।",
            style = MaterialTheme.typography.bodyLarge,
            color = StudioTextSecondary,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // CTA buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onOpenSocial,
                colors = ButtonDefaults.buttonColors(
                    containerColor = StudioEmerald,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1.2f)
                    .height(48.dp)
                    .testTag("hero_open_social_btn")
            ) {
                Text(
                    text = "সোশ্যাল ফিড 🔥",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            OutlinedButton(
                onClick = onPlayGame,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = StudioTextPrimary
                ),
                border = BorderStroke(1.dp, StudioCardBorder),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(0.8f)
                    .height(48.dp)
                    .testTag("hero_play_game_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.SportsEsports,
                    contentDescription = null,
                    tint = StudioGold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "গেমস",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Masonry Stills & Photo Archive Banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(StudioSurfaceElevated)
                .border(1.dp, StudioGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .clickable(onClick = onExploreStills)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(StudioGold.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = StudioGold,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "ম্যাসনরি ফটো গ্যালারি • ফাইন আর্ট স্টিলস",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudioTextPrimary
                    )
                    Text(
                        text = "Coil অপ্টিমাইজড ক্যাশিং সহ ব্যক্তিগত ছবি সংরক্ষণ",
                        fontSize = 11.sp,
                        color = StudioTextSecondary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(StudioGold)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "ফটো দেখুন",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF08090C)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Studio Stats / Metrics Grid
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(StudioSurfaceElevated, StudioSurface)
                    )
                )
                .border(1.dp, StudioCardBorder, RoundedCornerShape(14.dp))
                .clickable {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "NovaHub — Games & Social Community")
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "NovaHub-এ যুক্ত হোন! ইন-অ্যাপ HTML5 গেম খেলুন, ছবি শেয়ার করুন এবং কমিউনিটির সাথে লাইভ চ্যাট করুন!"
                        )
                    }
                    try {
                        context.startActivity(Intent.createChooser(shareIntent, "বন্ধুদের সাথে শেয়ার করুন"))
                    } catch (_: Exception) {}
                }
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            StudioStatItem(
                value = "07+",
                label = "GAMES",
                sub = "Live WebView"
            )
            StudioStatDivider()
            StudioStatItem(
                value = "12+",
                label = "STILLS",
                sub = "Fine Art"
            )
            StudioStatDivider()
            StudioStatItem(
                value = "24/7",
                label = "PUBLIC",
                sub = "Open Hub"
            )
            StudioStatDivider()
            StudioStatItem(
                value = "SHARE",
                label = "INVITE",
                sub = "Tap to Share"
            )
        }
    }
}

@Composable
private fun StudioStatItem(
    value: String,
    label: String,
    sub: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = StudioGold,
            fontFamily = FontFamily.Serif
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = StudioTextPrimary,
            letterSpacing = 1.sp
        )
        Text(
            text = sub,
            fontSize = 9.sp,
            color = StudioTextMuted
        )
    }
}

@Composable
private fun StudioStatDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(34.dp)
            .background(StudioCardBorderSubtle)
    )
}
