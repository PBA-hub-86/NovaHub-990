package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.zIndex
import com.example.data.StudioGame
import com.example.ui.theme.StudioDarkBg

/**
 * In-App Web Game Launcher Overlay
 * Uses BaseGameWebView to host and execute browser-based HTML5 / WebGL games
 * directly inside the main Activity window for rock-solid WebView surface stability.
 */
@Composable
fun WebGameViewerDialog(
    game: StudioGame,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(20f)
            .background(StudioDarkBg)
            .testTag("web_game_dialog_${game.id}"),
        color = StudioDarkBg
    ) {
        BaseGameWebView(
            gameUrl = game.webGameUrl,
            gameTitle = game.title,
            techStack = game.techStack,
            onClose = onDismiss
        )
    }
}
