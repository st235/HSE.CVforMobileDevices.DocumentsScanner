package github.com.st235.documentscanner.presentation.screens.composer.stitcher

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import github.com.st235.documentscanner.R
import github.com.st235.documentscanner.presentation.screens.Screen
import github.com.st235.documentscanner.presentation.widgets.DocumentPreview
import github.com.st235.documentscanner.presentation.widgets.LoadingView
import github.com.st235.documentscanner.utils.createTempUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentCompositionOverviewScreen(
    viewModel: DocumentsStitcherViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var cameraPermissionUri by remember { mutableStateOf(Uri.EMPTY) }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { isSuccessful ->
            if (isSuccessful) {
                viewModel.addImageToComposition(cameraPermissionUri)
            }
        }

    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                cameraPermissionUri = context.createTempUri()
                cameraLauncher.launch(cameraPermissionUri)
            }
        }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                viewModel.addImageToComposition(uri)
            }
        }

    val uriToCrop = state.preparedDocumentUri
    if (uriToCrop != null) {
        LaunchedEffect(true) {
            navController.navigate(Screen.DocumentsFlow.Cropper.create(uriToCrop)) {
                popUpTo(route = Screen.DocumentsFlow.route) {
                    inclusive = false
                }
            }
        }
    }

    LoadingView(isLoading = state.isLoading) {
        Scaffold(
            modifier = modifier,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    title = {
                        Text(
                            stringResource(id = R.string.documents_stitcher_screen_title),
                            fontWeight = FontWeight.Medium
                        )
                    },
                    navigationIcon = {
                        if (navController.previousBackStackEntry != null) {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_arrow_back_24),
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                )
            },
            floatingActionButton = {
                val shouldStitch = state.shouldStitch
                val canProceedNext = state.pages.isNotEmpty()

                val iconRes = if (shouldStitch) {
                    R.drawable.ic_healing_24
                } else {
                    R.drawable.ic_arrow_forward_24
                }

                val textRes = if (shouldStitch) {
                    R.string.documents_stitcher_screen_stitch_button
                } else {
                    R.string.documents_stitcher_screen_proceed_button
                }

                if (canProceedNext) {
                    ExtendedFloatingActionButton(
                        icon = {
                            Icon(
                                painterResource(iconRes),
                                contentDescription = null
                            )
                        },
                        text = {
                            Text(
                                text = stringResource(textRes),
                                fontWeight = FontWeight.Medium
                            )
                        },
                        onClick = { viewModel.save() }
                    )
                }
            }
        ) { paddings ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .padding(paddings)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    FilePickUpButton(
                        onCameraClick = {
                            if (!isCameraPermissionGranted(context)) {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            } else {
                                cameraPermissionUri = context.createTempUri()
                                cameraLauncher.launch(cameraPermissionUri)
                            }
                        },
                        onGalleryClick = {
                            galleryLauncher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageAndVideo
                                )
                            )
                        }
                    )
                }

                items(state.pages) { page ->
                    DocumentPreview(
                        document = page.uri,
                        title = stringResource(
                            id = R.string.documents_stitcher_screen_document_preview_template,
                            page.id
                        ),
                        onClick = {
                            navController.navigate(Screen.DocumentPreview.create(page.uri))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FilePickUpButton(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    cornerRadius: Dp = 16.dp,
    onCameraClick: () -> Unit = {},
    onGalleryClick: () -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.475f)
                .clip(
                    RoundedCornerShape(
                        cornerRadius,
                        cornerRadius / 2,
                        cornerRadius / 2,
                        cornerRadius
                    )
                )
                .background(backgroundColor)
                .clickable(
                    onClick = onCameraClick,
                    indication = rememberRipple(),
                    interactionSource = interactionSource
                ),
        ) {
           Image(
               painterResource(R.drawable.ic_add_a_photo_24),
               colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
               contentDescription = null,
               modifier = Modifier.size(32.dp)
           )
        }
        Spacer(modifier = Modifier.weight(0.05f))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.475f)
                .clip(
                    RoundedCornerShape(
                        cornerRadius / 2,
                        cornerRadius,
                        cornerRadius,
                        cornerRadius / 2
                    )
                )
                .background(backgroundColor)
                .clickable(
                    onClick = onGalleryClick,
                    indication = rememberRipple(),
                    interactionSource = interactionSource
                ),
        ) {
            Image(
                painterResource(R.drawable.ic_perm_media_24),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

private fun isCameraPermissionGranted(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
}
