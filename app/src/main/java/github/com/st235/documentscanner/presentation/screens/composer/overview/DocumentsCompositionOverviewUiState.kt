package github.com.st235.documentscanner.presentation.screens.composer.overview

import github.com.st235.documentscanner.domain.DocumentsCompositionInteractor

data class DocumentsCompositionOverviewUiState(
    val isLoading: Boolean,
    val isFinished: Boolean,
    val shouldStitch: Boolean,
    val shouldAddMoreDocuments: Boolean,
    val pages: List<DocumentsCompositionInteractor.DocumentPage>
) {
    companion object {
        val EMPTY = DocumentsCompositionOverviewUiState(
            isLoading = false,
            isFinished = false,
            shouldStitch = false,
            shouldAddMoreDocuments = true,
            pages = emptyList()
        )
    }
}
