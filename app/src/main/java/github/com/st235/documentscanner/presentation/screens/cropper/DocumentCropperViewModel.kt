package github.com.st235.documentscanner.presentation.screens.cropper

import android.net.Uri
import github.com.st235.documentscanner.domain.DocumentScanner
import github.com.st235.documentscanner.presentation.base.BaseViewModel
import github.com.st235.documentscanner.presentation.utils.UriLoader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DocumentCropperViewModel(
    private val documentScanner: DocumentScanner,
    private val uriLoader: UriLoader
): BaseViewModel() {

    private val _documentCropperState = MutableStateFlow<DocumentCropperUiState>(
        DocumentCropperUiState.EMPTY)

    val documentCropperState: StateFlow<DocumentCropperUiState> = _documentCropperState

    fun load(documentUri: Uri) {
        backgroundScope.launch {
            val document = uriLoader.load(documentUri)
            val detectedCorners = document?.let { documentScanner.findCorners(it) }

            _documentCropperState.update {
                DocumentCropperUiState(
                    document = document,
                    detectedCorners = detectedCorners
                )
            }
        }
    }



}