package github.com.st235.documentscanner.presentation.screens.composer.editor

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import github.com.st235.documentscanner.presentation.screens.composer.DocumentsComposerViewModel

@Composable
fun DocumentEditor(
    documentId: Int,
    sharedViewModel: DocumentsComposerViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val state by sharedViewModel.documentEditorUiState.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        sharedViewModel.prepareDocumentForEditing(documentId)
    }

    Scaffold(
        modifier = modifier
    ) { paddings ->
        val documentBitmap = state.documentBitmap

        if (documentBitmap != null) {
            Image(
                bitmap = documentBitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
