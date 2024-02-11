package github.com.st235.documentscanner.presentation.screens.composer.cropper

import android.net.Uri
import github.com.st235.documentscanner.domain.CropInteractor
import github.com.st235.documentscanner.presentation.base.BaseViewModel
import github.com.st235.documentscanner.utils.documents.DocumentScanner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DocumentCropperViewModel(
    private val cropInteractor: CropInteractor,
): BaseViewModel() {

    private val _uiState = MutableStateFlow(
        DocumentCropperUiState.EMPTY
    )

    val uiState = _uiState.asStateFlow()

    fun prepareUriForCropping(documentUri: Uri) {
        _uiState.value = DocumentCropperUiState.EMPTY.copy(isLoading = true)

        backgroundScope.launch {
            val cropBitmap = cropInteractor.prepareBitmap(documentUri)
            val detectedCorners = cropInteractor.detectCorners(cropBitmap)

            _uiState.update {
                _uiState.value.copy(
                    isLoading = false,
                    document = cropBitmap,
                    detectedCorners = detectedCorners
                )
            }
        }
    }

    fun cropAndSave(cropArea: DocumentScanner.Corners?) {
        _uiState.value = _uiState.value.copy(
            isLoading = true
        )

        backgroundScope.launch {
            val defaultDocumentBitmap = _uiState.value.document!!
            val croppedDocumentBitmap = cropInteractor.crop(defaultDocumentBitmap, cropArea)
            val uri = cropInteractor.save(croppedDocumentBitmap)

            _uiState.update {
                _uiState.value.copy(
                    isLoading = false,
                    preparedUriForEditing = uri
                )
            }
        }
    }

}