package github.com.st235.documentscanner.presentation.screens.feed

import github.com.st235.documentscanner.domain.FeedInteractor
import github.com.st235.documentscanner.presentation.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FeedScreenViewModel(
    private val feedInteractor: FeedInteractor
): BaseViewModel() {

    private val _feedUiState = MutableStateFlow(
        FeedUiState.EMPTY
    )

    val feedUiState = _feedUiState.asStateFlow()

    fun refreshPages() {
        _feedUiState.value = _feedUiState.value.copy(isLoading = true)

        backgroundScope.launch {
            val pages = feedInteractor.loadFeed()

            _feedUiState.value = FeedUiState(
                isLoading = false,
                pages = pages
            )
        }
    }

}