package com.example.ui

sealed class SearchResultItem {
    data class GameItem(
        val title: String,
        val subtitle: String,
        val techStack: String,
        val targetSection: String = "games"
    ) : SearchResultItem()

    data class StillItem(
        val title: String,
        val category: String,
        val location: String,
        val stillId: String,
        val targetSection: String = "stills"
    ) : SearchResultItem()

    data class CreationItem(
        val title: String,
        val prompt: String,
        val type: String,
        val creationId: Long,
        val targetSection: String = "creations"
    ) : SearchResultItem()

    data class WritingItem(
        val title: String,
        val category: String,
        val summary: String,
        val writingId: Long,
        val targetSection: String = "writing"
    ) : SearchResultItem()

    data class NavigationItem(
        val title: String,
        val description: String,
        val targetSection: String
    ) : SearchResultItem()
}

data class SearchUiState(
    val isSearchActive: Boolean = false,
    val query: String = "",
    val results: List<SearchResultItem> = emptyList()
)
