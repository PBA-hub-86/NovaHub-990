package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioCardBorderSubtle
import com.example.ui.theme.StudioCyan
import com.example.ui.theme.StudioEmerald
import com.example.ui.theme.StudioGold
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceElevated
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AboutSection(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
            .testTag("about_section")
    ) {
        // Section index
        Text(
            text = "01 / ABOUT",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = StudioGold,
            letterSpacing = 1.5.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Crafting at the intersection of game systems & atmospheric imagery.",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = StudioTextPrimary
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Welcome to NovaHub — an open community hub for interactive HTML5 browser games, atmospheric photography stills, creative writing, and live social interaction. Built for public visitors from Facebook and beyond, everything runs smoothly in a clean, secure mobile experience.",
            style = MaterialTheme.typography.bodyLarge,
            color = StudioTextSecondary,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Three Core Studio Pillars
        PillarCard(
            icon = Icons.Default.SportsEsports,
            iconTint = StudioGold,
            title = "Browser & Kinetic Games",
            description = "Building arcade-inspired, responsive gameplay systems with zero bloat. Focusing on satisfying physics loops, procedural obstacles, and tight input feedback."
        )

        Spacer(modifier = Modifier.height(12.dp))

        PillarCard(
            icon = Icons.Default.CameraAlt,
            iconTint = StudioCyan,
            title = "Stills & Nocturnal Photography",
            description = "Documenting the quiet, rain-drenched streets of Dhaka and serene riverbanks of Bengal. 35mm composition with heavy emphasis on chiaroscuro and atmospheric reflections."
        )

        Spacer(modifier = Modifier.height(12.dp))

        PillarCard(
            icon = Icons.Default.Code,
            iconTint = StudioEmerald,
            title = "Creative Code & UI Architecture",
            description = "Engineering clean, accessible interfaces across Jetpack Compose, React, and Canvas. Balancing high typographical restraint with snappy 60fps performance."
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Tooling & Skills Chips
        Text(
            text = "STACK & METHODOLOGY",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = StudioTextMuted,
            letterSpacing = 1.2.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        val skills = listOf(
            "Kotlin & Jetpack Compose",
            "Room Database",
            "TypeScript & React",
            "HTML5 Canvas 2D / WebGL",
            "Game Physics Loops",
            "35mm Fine Art Stills",
            "Chiptune & Ambient Audio",
            "Responsive Design"
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            skills.forEach { skill ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(StudioSurfaceElevated)
                        .border(1.dp, StudioCardBorderSubtle, RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = skill,
                        fontSize = 11.sp,
                        color = StudioTextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun PillarCard(
    icon: ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    title: String,
    description: String
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = StudioSurface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconTint.copy(alpha = 0.12f))
                    .border(1.dp, iconTint.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = StudioTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = StudioTextSecondary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
