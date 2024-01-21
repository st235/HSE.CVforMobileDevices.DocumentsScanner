package github.com.st235.documentscanner.presentation.screens.composer

import android.graphics.Bitmap
import android.net.Uri
import github.com.st235.documentscanner.domain.CropInteractor
import github.com.st235.documentscanner.domain.DocumentsCompositionInteractor
import github.com.st235.documentscanner.domain.EditorInteractor
import github.com.st235.documentscanner.utils.documents.DocumentScanner
import github.com.st235.documentscanner.presentation.base.BaseViewModel
import github.com.st235.documentscanner.presentation.screens.composer.cropper.DocumentCropperUiState
import github.com.st235.documentscanner.presentation.screens.composer.editor.DocumentEditorUiState
import github.com.st235.documentscanner.presentation.screens.composer.overview.DocumentsCompositionOverviewUiState
import github.com.st235.documentscanner.utils.documents.ImageProcessor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DocumentsComposerViewModel(
    private val cropInteractor: CropInteractor,
    private val documentsCompositionInteractor: DocumentsCompositionInteractor,
    private val editorInteractor: EditorInteractor,
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
                    isLoading = false,
                    isFinished = false,
                    shouldStitch = availablePages.size > 1,
                    shouldAddMoreDocuments = true,
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
                    isFinished = false,
                    documentId = documentId,
                    previousDocument = null,
                    currentDocument = documentBitmap
                )
            }
        }
    }

    fun rotate90Clockwise(document: Bitmap?) {
        applyEditingOperation(document) {
            editorInteractor.rotate90Clockwise(it)
        }
    }

    fun binarise(document: Bitmap?, mode: ImageProcessor.Binarization) {
        applyEditingOperation(document) {
            editorInteractor.binarise(it, mode)
        }
    }

    private fun applyEditingOperation(document: Bitmap?, operation: (oldBitmap: Bitmap) -> Bitmap) {
        if (document == null) {
            return
        }

        val documentId = _documentEditorUiState.value.documentId
        _documentEditorUiState.value = _documentEditorUiState.value.copy(isLoading = true)

        backgroundScope.launch {
            val newDocument = operation(document)

            _documentEditorUiState.update {
                DocumentEditorUiState(
                    isLoading = false,
                    isFinished = false,
                    documentId = documentId,
                    previousDocument = document,
                    currentDocument = newDocument,
                )
            }
        }
    }

    fun modifyDocument(documentId: Int, document: Bitmap?) {
        if (document == null) {
            return
        }

        _documentEditorUiState.value = _documentEditorUiState.value.copy(isLoading = true)

        backgroundScope.launch {
            documentsCompositionInteractor.updatePage(documentId, document)

            val availablePages = documentsCompositionInteractor.getAllPages()

            _documentCompositionOverviewState.update {
                DocumentsCompositionOverviewUiState(
                    isLoading = false,
                    isFinished = false,
                    shouldStitch = availablePages.size > 1,
                    shouldAddMoreDocuments = true,
                    pages = availablePages
                )
            }

            _documentEditorUiState.value = _documentEditorUiState.value.copy(
                isLoading = false,
                isFinished = true
            )
        }
    }

    fun save() {
        _documentCompositionOverviewState.value = _documentCompositionOverviewState.value.copy(isLoading = true)

        backgroundScope.launch {
            documentsCompositionInteractor.save()

            _documentCompositionOverviewState.value =
                _documentCompositionOverviewState.value.copy(
                    isLoading = false,
                    isFinished = true
                )
        }
    }

}