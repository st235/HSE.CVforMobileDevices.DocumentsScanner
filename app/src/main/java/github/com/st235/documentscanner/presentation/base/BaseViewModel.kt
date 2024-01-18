package github.com.st235.documentscanner.presentation.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

abstract class BaseViewModel: ViewModel() {

    protected val context = Dispatchers.IO
    protected val backgroundScope = CoroutineScope(context)

    override fun onCleared() {
        backgroundScope.cancel()
    }
}