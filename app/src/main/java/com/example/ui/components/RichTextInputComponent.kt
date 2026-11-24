package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.FormatStrikethrough
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

/**
 * Rich-Text Input Component for 'A Way to Write In'
 * Includes formatting toolbar (Bold, Italic, Headings, Lists, Quotes, Code, Emojis),
 * word/character counting, Markdown preview toggle, and automatic local state/draft autosave.
 */
@Composable
fun RichTextInputComponent(
    textValue: TextFieldValue,
    onTextChange: (TextFieldValue) -> Unit,
    lastSavedTimestamp: Long = 0L,
    isAutosaving: Boolean = false,
    onClearDraft: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isPreviewMode by remember { mutableStateOf(false) }

    val rawText = textValue.text
    val wordCount = remember(rawText) {
        if (rawText.isBlank()) 0 else rawText.trim().split("\\s+".toRegex()).size
    }
    val charCount = rawText.length

    // Helper to wrap or insert formatting around cursor / selection
    fun applyFormatting(prefix: String, suffix: String = prefix, defaultPlaceholder: String = "") {
        val selection = textValue.selection
        val selectedText = if (selection.collapsed) {
            defaultPlaceholder
        } else {
            rawText.substring(selection.min, selection.max)
        }

        val newText = rawText.substring(0, selection.min) +
                prefix + selectedText + suffix +
                rawText.substring(selection.max)

        val newCursorPos = if (selection.collapsed) {
            selection.min + prefix.length + defaultPlaceholder.length
        } else {
            selection.min + prefix.length + selectedText.length + suffix.length
        }

        onTextChange(
            TextFieldValue(
                text = newText,
                selection = TextRange(newCursorPos)
            )
        )
    }

    // Helper to prepend block tokens to current line
    fun applyBlockFormatting(linePrefix: String) {
        val selection = textValue.selection
        val cursor = selection.start
        val textBefore = rawText.substring(0, cursor)
        val lastNewline = textBefore.lastIndexOf('\n')
        val lineStart = if (lastNewline >= 0) lastNewline + 1 else 0

        val newText = rawText.substring(0, lineStart) +
                linePrefix +
                rawText.substring(lineStart)

        onTextChange(
            TextFieldValue(
                text = newText,
                selection = TextRange(cursor + linePrefix.length)
            )
        )
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = StudioSurfaceElevated),
        border = BorderStroke(1.dp, StudioCardBorder),
        modifier = modifier
            .fillMaxWidth()
            .testTag("rich_text_input_component")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header / Mode & Draft Autosave Status Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Autosave Status Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(if (isAutosaving) StudioGold else StudioEmerald)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAutosaving) "Draft saving..." else "Draft Autosaved (Room DB)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isAutosaving) StudioGold else StudioEmerald
                    )
                }

                // Preview vs Write toggle buttons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (!isPreviewMode) StudioGold.copy(alpha = 0.2f) else StudioSurface,
                        border = BorderStroke(1.dp, if (!isPreviewMode) StudioGold else StudioCardBorderSubtle),
                        modifier = Modifier
                            .clickable { isPreviewMode = false }
                            .testTag("rich_text_write_tab")
                    ) {
                        Text(
                            text = "Write",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!isPreviewMode) StudioGold else StudioTextMuted,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isPreviewMode) StudioCyan.copy(alpha = 0.2f) else StudioSurface,
                        border = BorderStroke(1.dp, if (isPreviewMode) StudioCyan else StudioCardBorderSubtle),
                        modifier = Modifier
                            .clickable { isPreviewMode = true }
                            .testTag("rich_text_preview_tab")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Preview,
                                contentDescription = null,
                                tint = if (isPreviewMode) StudioCyan else StudioTextMuted,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Preview",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isPreviewMode) StudioCyan else StudioTextMuted
                            )
                        }
                    }

                    if (rawText.isNotEmpty()) {
                        IconButton(
                            onClick = onClearDraft,
                            modifier = Modifier
                                .size(26.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(StudioSurface)
                                .testTag("rich_text_clear_draft_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Clear Draft",
                                tint = StudioRose,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Rich-Text Formatting Toolbar
            if (!isPreviewMode) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(StudioSurface)
                        .border(1.dp, StudioCardBorderSubtle, RoundedCornerShape(8.dp))
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RichToolbarButton(
                        icon = Icons.Default.FormatBold,
                        label = "Bold",
                        onClick = { applyFormatting("**", "**", "bold text") }
                    )

                    RichToolbarButton(
                        icon = Icons.Default.FormatItalic,
                        label = "Italic",
                        onClick = { applyFormatting("*", "*", "italic text") }
                    )

                    RichToolbarButton(
                        icon = Icons.Default.FormatStrikethrough,
                        label = "Strikethrough",
                        onClick = { applyFormatting("~~", "~~", "strikethrough") }
                    )

                    Box(
                        modifier = Modifier
                            .height(16.dp)
                            .width(1.dp)
                            .background(StudioCardBorder)
                    )

                    RichToolbarButton(
                        icon = Icons.Default.Title,
                        label = "Header 1",
                        onClick = { applyBlockFormatting("# ") }
                    )

                    RichToolbarButton(
                        icon = Icons.Default.FormatSize,
                        label = "Header 2",
                        onClick = { applyBlockFormatting("## ") }
                    )

                    RichToolbarButton(
                        icon = Icons.Default.FormatListBulleted,
                        label = "Bullet List",
                        onClick = { applyBlockFormatting("• ") }
                    )

                    RichToolbarButton(
                        icon = Icons.Default.FormatQuote,
                        label = "Quote",
                        onClick = { applyBlockFormatting("> ") }
                    )

                    RichToolbarButton(
                        icon = Icons.Default.Code,
                        label = "Code",
                        onClick = { applyFormatting("`", "`", "code") }
                    )

                    Box(
                        modifier = Modifier
                            .height(16.dp)
                            .width(1.dp)
                            .background(StudioCardBorder)
                    )

                    // Quick Studio Stamp Emojis
                    listOf("✨", "🚀", "📷", "🎮", "🖋️", "💡", "🛡️").forEach { emoji ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .clickable {
                                    val sel = textValue.selection
                                    val updated = rawText.substring(0, sel.min) + emoji + rawText.substring(sel.max)
                                    onTextChange(
                                        TextFieldValue(
                                            text = updated,
                                            selection = TextRange(sel.min + emoji.length)
                                        )
                                    )
                                }
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(text = emoji, fontSize = 13.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Main Editor Field or Live Rich Preview
            if (isPreviewMode) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(StudioSurface)
                        .border(1.dp, StudioCardBorder, RoundedCornerShape(8.dp))
                        .padding(14.dp)
                ) {
                    if (rawText.isBlank()) {
                        Text(
                            text = "No content written yet. Switch to 'Write' mode to draft your dispatch.",
                            color = StudioTextMuted,
                            fontSize = 13.sp,
                            fontStyle = FontStyle.Italic
                        )
                    } else {
                        Text(
                            text = parseSimpleMarkdown(rawText),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = StudioTextPrimary,
                                lineHeight = 22.sp
                            )
                        )
                    }
                }
            } else {
                OutlinedTextField(
                    value = textValue,
                    onValueChange = onTextChange,
                    placeholder = {
                        Text(
                            text = "A way to write in: Project proposals, thoughts on games, photography inquiries, or creative dispatches...\n\nSupports **bold**, *italic*, # headings, and • lists. Draft saves automatically while typing.",
                            color = StudioTextMuted,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    },
                    minLines = 5,
                    maxLines = 12,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = StudioSurface,
                        unfocusedContainerColor = StudioSurface,
                        focusedBorderColor = StudioGold,
                        unfocusedBorderColor = StudioCardBorder,
                        focusedTextColor = StudioTextPrimary,
                        unfocusedTextColor = StudioTextPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("rich_text_editor_field")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Footer Metrics (Word count, character count, local persistence badge)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$wordCount words • $charCount chars",
                    fontSize = 11.sp,
                    color = StudioTextMuted,
                    fontFamily = FontFamily.Monospace
                )

                Text(
                    text = "Local Draft Saved • Safe across refresh",
                    fontSize = 11.sp,
                    color = StudioEmerald,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun RichToolbarButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(30.dp)
            .clip(RoundedCornerShape(4.dp))
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = StudioTextSecondary,
            modifier = Modifier.size(16.dp)
        )
    }
}

/**
 * Lightweight Markdown / Rich Text parser for previewing formatted dispatch
 */
fun parseSimpleMarkdown(input: String): AnnotatedString {
    val lines = input.split("\n")
    return buildAnnotatedString {
        lines.forEachIndexed { index, line ->
            when {
                line.startsWith("# ") -> {
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp, color = StudioGold))
                    append(line.removePrefix("# "))
                    pop()
                }
                line.startsWith("## ") -> {
                    pushStyle(SpanStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = StudioCyan))
                    append(line.removePrefix("## "))
                    pop()
                }
                line.startsWith("> ") -> {
                    pushStyle(SpanStyle(fontStyle = FontStyle.Italic, color = StudioTextMuted))
                    append("“" + line.removePrefix("> ") + "”")
                    pop()
                }
                line.startsWith("• ") || line.startsWith("- ") -> {
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold, color = StudioGold))
                    append(" • ")
                    pop()
                    append(line.substring(2))
                }
                else -> {
                    // Inline bold & italic scanning
                    var remaining = line
                    while (remaining.isNotEmpty()) {
                        val boldStart = remaining.indexOf("**")
                        if (boldStart != -1) {
                            val boldEnd = remaining.indexOf("**", boldStart + 2)
                            if (boldEnd != -1) {
                                append(remaining.substring(0, boldStart))
                                pushStyle(SpanStyle(fontWeight = FontWeight.Bold, color = StudioGold))
                                append(remaining.substring(boldStart + 2, boldEnd))
                                pop()
                                remaining = remaining.substring(boldEnd + 2)
                                continue
                            }
                        }

                        val italicStart = remaining.indexOf("*")
                        if (italicStart != -1) {
                            val italicEnd = remaining.indexOf("*", italicStart + 1)
                            if (italicEnd != -1) {
                                append(remaining.substring(0, italicStart))
                                pushStyle(SpanStyle(fontStyle = FontStyle.Italic, color = StudioCyan))
                                append(remaining.substring(italicStart + 1, italicEnd))
                                pop()
                                remaining = remaining.substring(italicEnd + 1)
                                continue
                            }
                        }

                        append(remaining)
                        break
                    }
                }
            }
            if (index < lines.size - 1) {
                append("\n")
            }
        }
    }
}
