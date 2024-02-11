package github.com.st235.documentscanner.presentation.screens.composer.cropper

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import github.com.st235.documentscanner.R
import github.com.st235.documentscanner.presentation.base.theme.globalOverlayColor
import github.com.st235.documentscanner.presentation.screens.Screen
import github.com.st235.documentscanner.presentation.widgets.CropArea
import github.com.st235.documentscanner.presentation.widgets.CropView
import github.com.st235.documentscanner.presentation.widgets.LoadingView
import github.com.st235.documentscanner.utils.documents.DocumentScanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentCropperScreen(
    documentUri: Uri,
    viewModel: DocumentCropperViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var currentlySelectedCropArea by remember { mutableStateOf(CropArea.ALL) }

    LaunchedEffect("loading_uri") {
        viewModel.prepareUriForCropping(documentUri)
    }

    val uriToEdit = state.preparedUriForEditing
    if (uriToEdit != null) {
        LaunchedEffect("launching_uri_for_editing") {
            navController.navigate(Screen.DocumentsFlow.Editor.create(uriToEdit)) {
                popUpTo(route = Screen.DocumentsFlow.route) {
                    inclusive = false
                }
            }
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
                            stringResource(id = R.string.document_cropper_title),
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
                ExtendedFloatingActionButton(
                    icon = {
                        Icon(
                            painterResource(R.drawable.ic_crop_24),
                            contentDescription = null
                        )
                    },
                    text = { Text(text = stringResource(R.string.document_cropper_crop_button)) },
                    onClick = { viewModel.cropAndSave(currentlySelectedCropArea.asCorners()) }
                )
            },
            modifier = modifier
        ) { paddings ->
            val document = state.document
            val corners = state.detectedCorners.asCropArea()

            if (document != null) {
                CropView(
                    image = document,
                    imageCroppedArea = corners,
                    overlayColor = globalOverlayColor,
                    modifier = Modifier
                        .padding(paddings)
                        .fillMaxSize()
                        .systemGestureExclusion(),
                    onCropAreaChanged = {
                        currentlySelectedCropArea = it
                    }
                )
            }
        }
    }
}

private fun CropArea.asCorners(): DocumentScanner.Corners? {
    if (this == CropArea.ALL) {
        return null
    }

    return DocumentScanner.Corners(
        topLeft = floatArrayOf(topLeft.x, topLeft.y),
        topRight = floatArrayOf(topRight.x, topRight.y),
        bottomRight = floatArrayOf(bottomRight.x, bottomRight.y),
        bottomLeft = floatArrayOf(bottomLeft.x, bottomLeft.y),
    )
}

private fun DocumentScanner.Corners?.asCropArea(): CropArea {
    if (this == null) {
        return CropArea.ALL
    }

    return CropArea(
        topLeft = Offset(topLeft[0], topLeft[1]),
        bottomLeft = Offset(bottomLeft[0], bottomLeft[1]),
        topRight = Offset(topRight[0], topRight[1]),
        bottomRight = Offset(bottomRight[0], bottomRight[1]),
    )
}
