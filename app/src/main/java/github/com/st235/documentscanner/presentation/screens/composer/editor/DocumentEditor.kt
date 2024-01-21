package github.com.st235.documentscanner.presentation.screens.composer.editor

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import github.com.st235.documentscanner.R
import github.com.st235.documentscanner.presentation.screens.composer.DocumentsComposerViewModel
import github.com.st235.documentscanner.utils.documents.ImageProcessor
import st235.com.github.flowlayout.compose.FlowLayout
import st235.com.github.flowlayout.compose.FlowLayoutDirection

@Composable
fun DocumentEditor(
    documentId: Int,
    sharedViewModel: DocumentsComposerViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val state by sharedViewModel.documentEditorUiState.collectAsStateWithLifecycle()

    val document = state.currentDocument

    LaunchedEffect(true) {
        sharedViewModel.prepareDocumentForEditing(documentId)
    }

    if (state.isFinished) {
        LaunchedEffect(true) {
            navController.popBackStack()
        }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                icon = {
                    Icon(
                        painterResource(R.drawable.ic_healing_24),
                        contentDescription = null
                    )
                },
                text = { Text(text = stringResource(R.string.document_editor_save)) },
                onClick = { sharedViewModel.modifyDocument(documentId, document) }
            )
        },
        bottomBar = {
            ControlPanel(
                onRotateClick = { sharedViewModel.rotate90Clockwise(document) },
                onBinarisationClick = { sharedViewModel.binarise(document, it) }
            )
        },
        modifier = modifier
    ) { paddings ->
        if (document != null) {
            Image(
                bitmap = document.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun ControlPanel(
    modifier: Modifier = Modifier,
    onRotateClick: () -> Unit = {},
    onBinarisationClick: (mode: ImageProcessor.Binarization) -> Unit = {},
) {
    var currentlyOpenRow by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        if (currentlyOpenRow == "threshold") {
            BinarisationControlRow(onClick = onBinarisationClick)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            ControlButton(
                icon = painterResource(R.drawable.ic_rotate_90_degrees_cw_24),
                text = stringResource(R.string.document_editor_rotate_90_cw),
                onClick = { onRotateClick() }
            )
            ControlButton(
                icon = painterResource(R.drawable.ic_invert_colors_24),
                text = stringResource(R.string.document_editor_binarisation),
                onClick = {
                    currentlyOpenRow = if (currentlyOpenRow == null) {
                        "threshold"
                    } else {
                        null
                    }
                }
            )
        }
    }
}

@Composable
fun BinarisationControlRow(
    modifier: Modifier = Modifier,
    onClick: (mode: ImageProcessor.Binarization) -> Unit = {},
) {
    val scrollState = rememberScrollState()

    FlowLayout(
        direction = FlowLayoutDirection.START,
    ) {
        Text(text = "global", modifier = Modifier
            .focusable()
            .clickable { onClick(ImageProcessor.Binarization.GLOBAL) })
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "otsu", modifier = Modifier
            .focusable()
            .clickable { onClick(ImageProcessor.Binarization.OTSU) })
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "triangle", modifier = Modifier
            .focusable()
            .clickable { onClick(ImageProcessor.Binarization.TRIANGLE) })
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "adaptive gaussian", modifier = Modifier
            .focusable()
            .clickable { onClick(ImageProcessor.Binarization.ADAPTIVE_GAUSSIAN) })
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "adaptive mean", modifier = Modifier
            .focusable()
            .clickable { onClick(ImageProcessor.Binarization.ADAPTIVE_MEAN) })
    }
}



@Composable
fun ControlButton(
    icon: Painter,
    text: String,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    onClick: () -> Unit = {},
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .focusable()
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Icon(painter = icon, contentDescription = null)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = text, textAlign = TextAlign.Center, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.widthIn(0.dp, 72.dp))
    }
}
