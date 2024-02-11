package github.com.st235.documentscanner.presentation.screens.composer.cropper

import android.graphics.Bitmap
import android.net.Uri
import github.com.st235.documentscanner.utils.documents.DocumentScanner

data class DocumentCropperUiState(
    val isLoading: Boolean,
    val document: Bitmap?,
    val detectedCorners: DocumentScanner.Corners?,
    val preparedUriForEditing: Uri?,
) {
    companion object {
        val EMPTY = DocumentCropperUiState(
            isLoading = true,
            document = null,
            detectedCorners = null,
            preparedUriForEditing = null,
        )
    }
}
