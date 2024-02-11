package github.com.st235.documentscanner.presentation.screens.composer.stitcher

import android.net.Uri
import github.com.st235.documentscanner.domain.DocumentsStitchingInteractor
import github.com.st235.documentscanner.presentation.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DocumentsStitcherViewModel(
    private val documentsStitchingInteractor: DocumentsStitchingInteractor,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(
        DocumentsStitcherUiState.EMPTY
    )

    val uiState = _uiState.asStateFlow()

    fun addImageToComposition(documentUri: Uri) {
        _uiState.value = _uiState.value.copy(isLoading = true)

        backgroundScope.launch {
            documentsStitchingInteractor.addPage(documentUri)
            val availablePages = documentsStitchingInteractor.getAllPages()

            _uiState.update {
                _uiState.value.copy(
                    isLoading = false,
                    shouldStitch = availablePages.size > 1,
                    shouldAddMoreDocuments = true,
                    pages = availablePages
                )
            }
        }
    }

    fun save() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        backgroundScope.launch {
            val stitchedUri = documentsStitchingInteractor.save()

            _uiState.update {
                _uiState.value.copy(
                    isLoading = false,
                    preparedDocumentUri = stitchedUri
                )
            }
        }
    }
}
