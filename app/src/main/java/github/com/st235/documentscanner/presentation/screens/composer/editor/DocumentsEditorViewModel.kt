package github.com.st235.documentscanner.presentation.screens.composer.editor

import android.graphics.Bitmap
import android.net.Uri
import github.com.st235.documentscanner.domain.EditorInteractor
import github.com.st235.documentscanner.presentation.base.BaseViewModel
import github.com.st235.documentscanner.utils.documents.ImageProcessor
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DocumentsEditorViewModel(
    private val editorInteractor: EditorInteractor,
): BaseViewModel() {

    private val _uiState = MutableStateFlow(
        DocumentEditorUiState.EMPTY
    )

    val uiState = _uiState.asStateFlow()

    private var editingOperationsJob: Job? = null

    fun prepareDocumentForEditing(documentUri: Uri) {
        _uiState.value = DocumentEditorUiState.EMPTY.copy(isLoading = true)

        backgroundScope.launch {
            val documentBitmap = editorInteractor.prepareBitmap(documentUri)

            _uiState.update {
                _uiState.value.copy(
                    isLoading = false,
                    isFinished = false,
                    previousDocument = null,
                    currentDocument = documentBitmap
                )
            }
        }
    }

    fun undo() {
        if (!uiState.value.isPossibleToUndo) {
            return
        }

        val previousDocument = _uiState.value.previousDocument

        _uiState.value = _uiState.value.copy(
            currentDocument = previousDocument,
            previousDocument = null,
        )
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

    fun filter(document: Bitmap?, mode: ImageProcessor.Filter) {
        applyEditingOperation(document) {
            editorInteractor.filter(it, mode)
        }
    }

    fun contrast(document: Bitmap?, mode: ImageProcessor.Contrast) {
        applyEditingOperation(document) {
            editorInteractor.contrast(it, mode)
        }
    }

    fun denoising(document: Bitmap?, mode: ImageProcessor.Denoising) {
        applyEditingOperation(document) {
            editorInteractor.denoising(it, mode)
        }
    }

    private fun applyEditingOperation(document: Bitmap?, operation: (oldBitmap: Bitmap) -> Bitmap) {
        if (document == null) {
            return
        }

        // Cancel previous operation.
        editingOperationsJob?.cancel()

        _uiState.value = _uiState.value.copy(isLoading = true)

        editingOperationsJob = backgroundScope.launch {
            val newDocument = operation(document)

            _uiState.update {
                _uiState.value.copy(
                    isLoading = false,
                    isFinished = false,
                    previousDocument = document,
                    currentDocument = newDocument,
                )
            }
        }
    }

    fun saveDocument() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        backgroundScope.launch {
            val documentBitmap = _uiState.value.currentDocument!!
            editorInteractor.save(documentBitmap)

            _uiState.update {
                _uiState.value.copy(
                    isLoading = false,
                    isFinished = true,
                )
            }
        }
    }

}