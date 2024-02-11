package github.com.st235.documentscanner.presentation.screens.composer.stitcher

import android.net.Uri
import github.com.st235.documentscanner.domain.DocumentsStitchingInteractor

data class DocumentsStitcherUiState(
    val isLoading: Boolean,
    val shouldStitch: Boolean,
    val shouldAddMoreDocuments: Boolean,
    val pages: List<DocumentsStitchingInteractor.DocumentPage>,
    val preparedDocumentUri: Uri?
) {
    companion object {
        val EMPTY = DocumentsStitcherUiState(
            isLoading = false,
            shouldStitch = false,
            shouldAddMoreDocuments = true,
            pages = emptyList(),
            preparedDocumentUri = null
        )
    }
}
