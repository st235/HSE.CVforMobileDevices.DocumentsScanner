package github.com.st235.documentscanner.presentation.screens.composer.overview

import github.com.st235.documentscanner.domain.DocumentsCompositionInteractor

data class DocumentsCompositionOverviewUiState(
    val pages: List<DocumentsCompositionInteractor.DocumentPage>
) {
    companion object {
        val EMPTY = DocumentsCompositionOverviewUiState(
            pages = emptyList()
        )
    }
}
