package github.com.st235.documentscanner.presentation.screens.composer.editor

import android.graphics.Bitmap

data class DocumentEditorUiState(
    val isLoading: Boolean,
    val isFinished: Boolean,
    val documentId: Int,
    val previousDocument: Bitmap?,
    val currentDocument: Bitmap?
) {
    companion object {
        val EMPTY = DocumentEditorUiState(
            isLoading = true,
            isFinished = false,
            documentId = 0,
            previousDocument = null,
            currentDocument = null
        )
    }
}

val DocumentEditorUiState.isPossibleToUndo: Boolean
    get() {
        return !isLoading && !isFinished && currentDocument != null && previousDocument != null
    }
