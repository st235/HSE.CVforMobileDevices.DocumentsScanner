package github.com.st235.documentscanner.presentation.screens.composer.cropper

import android.graphics.Bitmap
import github.com.st235.documentscanner.utils.documents.DocumentScanner

data class DocumentCropperUiState(
    val isLoading: Boolean,
    val isFinished: Boolean,
    val document: Bitmap?,
    val detectedCorners: DocumentScanner.Corners?
) {
    companion object {
        val EMPTY = DocumentCropperUiState(
            isLoading = true,
            isFinished = false,
            document = null,
            detectedCorners = null
        )
    }
}
