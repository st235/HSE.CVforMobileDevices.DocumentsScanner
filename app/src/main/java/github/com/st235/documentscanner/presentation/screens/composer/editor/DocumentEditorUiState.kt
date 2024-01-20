package github.com.st235.documentscanner.presentation.screens.composer.editor

import android.graphics.Bitmap

data class DocumentEditorUiState(
    val isLoading: Boolean,
    val documentId: Int,
    val documentBitmap: Bitmap?
) {
    companion object {
        val EMPTY = DocumentEditorUiState(
            isLoading = true,
            documentId = 0,
            documentBitmap = null
        )
    }
}