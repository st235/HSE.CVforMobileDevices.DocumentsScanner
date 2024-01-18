package github.com.st235.documentscanner.presentation.screens.cropper

import android.graphics.Bitmap
import github.com.st235.documentscanner.domain.DocumentScanner

data class DocumentCropperUiState(
    val document: Bitmap?,
    val detectedCorners: DocumentScanner.Corners?
) {
    companion object {
        val EMPTY = DocumentCropperUiState(
            document = null,
            detectedCorners = null
        )
    }
}
