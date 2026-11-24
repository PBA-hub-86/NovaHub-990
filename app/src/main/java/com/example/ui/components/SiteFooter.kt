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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioCardBorderSubtle
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.StudioGold
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary

@Composable
fun SiteFooter(
    onScrollToTop: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF050608))
            .border(
                width = 1.dp,
                color = StudioCardBorderSubtle
            )
            .padding(horizontal = 16.dp, vertical = 32.dp)
            .testTag("site_footer"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Back to top button
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(StudioSurface)
                .border(1.dp, StudioCardBorder, CircleShape)
                .clickable(onClick = onScrollToTop)
                .testTag("footer_scroll_to_top_btn"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = "Scroll back to top",
                tint = StudioGold,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Monogram
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(StudioGold),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "NH",
                color = Color(0xFF08090C),
                fontWeight = FontWeight.Black,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "NOVAHUB",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = StudioTextPrimary,
            letterSpacing = 1.5.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Personal studio. Browser games, stills, and a way to write in.",
            fontSize = 12.sp,
            color = StudioTextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Bengali quote
        Text(
            text = "“শিল্প ও সংকেতের সন্ধিক্ষণে”",
            fontSize = 13.sp,
            color = StudioGold,
            fontFamily = FontFamily.Serif
        )
        Text(
            text = "At the nexus of art and code",
            fontSize = 10.sp,
            color = StudioTextMuted,
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(1.dp)
                .background(StudioCardBorderSubtle)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "© 2026 NovaHub Community • All Rights Reserved",
            fontSize = 11.sp,
            color = StudioTextMuted,
            fontFamily = FontFamily.Monospace
        )
    }
}
