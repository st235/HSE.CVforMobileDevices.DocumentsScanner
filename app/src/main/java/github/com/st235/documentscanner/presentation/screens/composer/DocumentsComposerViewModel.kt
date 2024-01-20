package github.com.st235.documentscanner.presentation.screens.composer

import android.net.Uri
import androidx.lifecycle.viewModelScope
import github.com.st235.documentscanner.domain.CropInteractor
import github.com.st235.documentscanner.domain.DocumentsCompositionInteractor
import github.com.st235.documentscanner.utils.documents.DocumentScanner
import github.com.st235.documentscanner.presentation.base.BaseViewModel
import github.com.st235.documentscanner.presentation.screens.composer.cropper.DocumentCropperUiState
import github.com.st235.documentscanner.presentation.screens.composer.editor.DocumentEditorUiState
import github.com.st235.documentscanner.presentation.screens.composer.overview.DocumentsCompositionOverviewUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DocumentsComposerViewModel(
    private val cropInteractor: CropInteractor,
    private val documentsCompositionInteractor: DocumentsCompositionInteractor
): BaseViewModel() {

    private val _documentCropperState = MutableStateFlow(
        DocumentCropperUiState.EMPTY
    )

    val documentCropperState = _documentCropperState.asStateFlow()

    private val _documentCompositionOverviewState = MutableStateFlow(
        DocumentsCompositionOverviewUiState.EMPTY
    )

    val documentsCompositionOverviewUiState = _documentCompositionOverviewState.asStateFlow()

    private val _documentEditorUiState = MutableStateFlow(
        DocumentEditorUiState.EMPTY
    )

    val documentEditorUiState = _documentEditorUiState.asStateFlow()

    fun prepareUriForCropping(documentUri: Uri) {
        _documentCropperState.value = DocumentCropperUiState.EMPTY

        backgroundScope.launch {
            cropInteractor.prepareBitmap(documentUri)

            val document = cropInteractor.requireBitmap()
            val detectedCorners = cropInteractor.detectCorners()

            _documentCropperState.update {
                DocumentCropperUiState(
                    isLoading = false,
                    isFinished = false,
                    document = document,
                    detectedCorners = detectedCorners
                )
            }
        }
    }

    fun cropAndSave(cropArea: DocumentScanner.Corners?) {
        _documentCropperState.value = _documentCropperState.value.copy(
            isLoading = true
        )

        backgroundScope.launch {
            val croppedDocumentBitmap = cropInteractor.crop(cropArea)
            documentsCompositionInteractor.addPage(croppedDocumentBitmap)

            val availablePages = documentsCompositionInteractor.getAllPages()

            _documentCompositionOverviewState.update {
                DocumentsCompositionOverviewUiState(
                    pages = availablePages
                )
            }

            _documentCropperState.value = _documentCropperState.value.copy(
                isLoading = false,
                isFinished = true
            )
        }
    }

    fun prepareDocumentForEditing(documentId: Int) {
        _documentEditorUiState.value = DocumentEditorUiState.EMPTY

        backgroundScope.launch {
            val documentBitmap = documentsCompositionInteractor.getDocumentById(documentId)

            _documentEditorUiState.update {
                DocumentEditorUiState(
                    isLoading = false,
                    documentId = documentId,
                    documentBitmap = documentBitmap
                )
            }
        }
    }

}