package github.com.st235.documentscanner.presentation.screens.composer.overview

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import github.com.st235.documentscanner.R
import github.com.st235.documentscanner.presentation.screens.Screen
import github.com.st235.documentscanner.presentation.screens.composer.DocumentsComposerViewModel
import github.com.st235.documentscanner.presentation.widgets.DocumentPreview
import github.com.st235.documentscanner.utils.createTempUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentCompositionOverviewScreen(
    sharedViewModel: DocumentsComposerViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val state by sharedViewModel.documentsCompositionOverviewUiState.collectAsStateWithLifecycle()

    if (state.isFinished) {
        LaunchedEffect(true) {
            navController.popBackStack()
        }
    }

    var cameraPermissionUri by remember { mutableStateOf(Uri.EMPTY) }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { isSuccessful ->
            if (isSuccessful) {
                sharedViewModel.prepareUriForCropping(cameraPermissionUri)
                navController.navigate(Screen.DocumentComposer.Cropper.route)
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
                sharedViewModel.prepareUriForCropping(uri)
                navController.navigate(Screen.DocumentComposer.Cropper.route)
            }
        }

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
                        stringResource(id = R.string.document_overview_screen_title),
                        fontWeight = FontWeight.Medium
                    )
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                icon = {
                    Icon(
                        painterResource(R.drawable.ic_save_24),
                        contentDescription = null
                    )
                },
                text = { Text(text = stringResource(R.string.document_overview_save_button)) },
                onClick = { sharedViewModel.save() }
            )
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
                            cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
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
                        id = R.string.document_overview_document_preview_template,
                        page.id
                    ),
                    onClick = {
                        navController.navigate(Screen.DocumentComposer.Editor.create(page.id))
                    }
                )
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

private fun isCameraPermissionGranted(context: Context): Boolean =
    isPermissionGranted(android.Manifest.permission.CAMERA, context)

private fun isWriteExternalStoragePermissionGranted(context: Context): Boolean =
    isPermissionGranted(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, context)

private fun isPermissionGranted(permission: String, context: Context): Boolean {
    return ContextCompat.checkSelfPermission(context, permission) ==
            PackageManager.PERMISSION_GRANTED
}

