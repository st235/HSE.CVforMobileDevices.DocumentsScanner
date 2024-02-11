package github.com.st235.documentscanner.presentation.screens.composer.editor

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import github.com.st235.documentscanner.R
import github.com.st235.documentscanner.presentation.widgets.LoadingView
import github.com.st235.documentscanner.utils.documents.ImageProcessor
import github.com.st235.documentscanner.utils.stringRes
import st235.com.github.flowlayout.compose.FlowLayout
import st235.com.github.flowlayout.compose.FlowLayoutDirection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentEditor(
    documentUri: Uri,
    viewModel: DocumentsEditorViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val document = state.currentDocument

    LaunchedEffect("preparing_uri_for_editing") {
        viewModel.prepareDocumentForEditing(documentUri)
    }

    if (state.isFinished) {
        LaunchedEffect("closing_view") {
            navController.popBackStack()
        }
    }

    LoadingView(isLoading = state.isLoading) {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    title = {
                        Text(
                            stringResource(id = R.string.document_editor_title),
                            fontWeight = FontWeight.Medium
                        )
                    },
                    navigationIcon = {
                        if (navController.previousBackStackEntry != null) {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_close_24),
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                )
            },
            floatingActionButton = {
                val canUndo = state.isPossibleToUndo

                Column(
                    horizontalAlignment = Alignment.End,
                ) {
                    if (canUndo) {
                        SmallFloatingActionButton(
                            onClick = { viewModel.undo() }
                        ) {
                            Icon(
                                painterResource(R.drawable.ic_undo_24),
                                contentDescription = null
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    ExtendedFloatingActionButton(
                        icon = {
                            Icon(
                                painterResource(R.drawable.ic_done_24),
                                contentDescription = null
                            )
                        },
                        text = { Text(text = stringResource(R.string.document_editor_save)) },
                        onClick = { viewModel.saveDocument() },
                    )
                }
            },
            bottomBar = {
                ControlPanel(
                    onRotateClick = { viewModel.rotate90Clockwise(document) },
                    onBinarisationClick = { viewModel.binarise(document, it) },
                    onFilterClick = { viewModel.filter(document, it) },
                    onContrastClick = { viewModel.contrast(document, it) },
                    onDenoisingClick = { viewModel.denoising(document, it) },
                )
            },
            modifier = modifier
        ) { paddings ->
            if (document != null) {
                Image(
                    bitmap = document.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddings)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
            }
        }
    }
}

enum class ControlPanels {
    BINARIZATION,
    FILTER,
    CONTRAST,
    DENOISING
}

@Composable
fun ControlPanel(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    onRotateClick: () -> Unit = {},
    onBinarisationClick: (mode: ImageProcessor.Binarization) -> Unit = {},
    onFilterClick: (mode: ImageProcessor.Filter) -> Unit = {},
    onContrastClick: (mode: ImageProcessor.Contrast) -> Unit = {},
    onDenoisingClick: (mode: ImageProcessor.Denoising) -> Unit = {},
) {
    var currentlyOpenPanel by remember { mutableStateOf<ControlPanels?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
    ) {
        when (currentlyOpenPanel) {
            ControlPanels.BINARIZATION -> {
                ControlPanelRow(
                    items = ImageProcessor.Binarization.entries.map { it to stringResource(it.stringRes) },
                    onClick = onBinarisationClick
                )
            }
            ControlPanels.FILTER -> {
                ControlPanelRow(
                    items = ImageProcessor.Filter.entries.map { it to stringResource(it.stringRes) },
                    onClick = onFilterClick
                )
            }
            ControlPanels.CONTRAST -> {
                ControlPanelRow(
                    items = ImageProcessor.Contrast.entries.map { it to stringResource(it.stringRes) },
                    onClick = onContrastClick
                )
            }
            ControlPanels.DENOISING -> {
                ControlPanelRow(
                    items = ImageProcessor.Denoising.entries.map { it to stringResource(it.stringRes) },
                    onClick = onDenoisingClick
                )
            }
            else -> {}
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            ControlButton(
                icon = painterResource(R.drawable.ic_rotate_90_degrees_cw_24),
                text = stringResource(R.string.document_editor_rotate_90_cw),
                contentColor = contentColor,
                onClick = { onRotateClick() }
            )
            ControlButton(
                icon = painterResource(R.drawable.ic_invert_colors_24),
                text = stringResource(R.string.document_editor_binarisation),
                contentColor = contentColor,
                onClick = {
                    currentlyOpenPanel = calculateCurrentlyOpenControlPanel(
                        currentlyOpenPanel,
                        ControlPanels.BINARIZATION
                    )
                }
            )
            ControlButton(
                icon = painterResource(R.drawable.ic_contrast_24),
                text = stringResource(R.string.document_editor_contrast),
                contentColor = contentColor,
                onClick = {
                    currentlyOpenPanel = calculateCurrentlyOpenControlPanel(
                        currentlyOpenPanel,
                        ControlPanels.CONTRAST
                    )
                }
            )
            ControlButton(
                icon = painterResource(R.drawable.ic_blur_on_24),
                text = stringResource(R.string.document_editor_filter),
                contentColor = contentColor,
                onClick = {
                    currentlyOpenPanel = calculateCurrentlyOpenControlPanel(
                        currentlyOpenPanel,
                        ControlPanels.FILTER
                    )
                }
            )
            ControlButton(
                icon = painterResource(R.drawable.ic_equalizer_24),
                text = stringResource(R.string.document_editor_denoising),
                contentColor = contentColor,
                onClick = {
                    currentlyOpenPanel = calculateCurrentlyOpenControlPanel(
                        currentlyOpenPanel,
                        ControlPanels.DENOISING
                    )
                }
            )
        }
    }
}

private fun calculateCurrentlyOpenControlPanel(currentState: ControlPanels?,
                                               defaultValue: ControlPanels): ControlPanels? {
    return if (currentState != defaultValue) {
        defaultValue
    } else {
        null
    }
}

@Composable
fun <T> ControlPanelRow(
    items: List<Pair<T, String>>,
    modifier: Modifier = Modifier,
    borderColor: Color = MaterialTheme.colorScheme.primaryContainer,
    textColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    onClick: (item: T) -> Unit = {},
) {
    val cornerRadiusPx = with(LocalDensity.current) { 10.dp.toPx() }

    FlowLayout(
        direction = FlowLayoutDirection.START,
        modifier = modifier.padding(8.dp)
    ) {
        for ((id, localisation) in items) {
            Text(
                text = localisation,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = textColor,
                modifier = Modifier
                    .focusable()
                    .clickable { onClick(id) }
                    .padding(vertical = 2.dp, horizontal = 4.dp)
                    .drawBehind {
                        drawRoundRect(
                            borderColor,
                            cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
                        )
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun ControlButton(
    icon: Painter,
    text: String,
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
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
        Icon(painter = icon, tint = contentColor, contentDescription = null)
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = text,
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(0.dp, 72.dp)
        )
    }
}
