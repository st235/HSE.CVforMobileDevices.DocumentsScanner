package github.com.st235.documentscanner.presentation.screens.feed

import android.net.Uri

data class FeedUiState(
    val isLoading: Boolean,
    val pages: List<Uri>
) {
    companion object {
        val EMPTY = FeedUiState(
            isLoading = true,
            pages = emptyList()
        )
    }
}
