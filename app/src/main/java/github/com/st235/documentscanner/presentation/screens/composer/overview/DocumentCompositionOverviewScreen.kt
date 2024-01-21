package github.com.st235.documentscanner.presentation.screens.composer.overview

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
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
    onCameraClick: () -> Unit = {},
    onGalleryClick: () -> Unit = {},
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .aspectRatio(1f),
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                onClick = { onCameraClick() }
            ) {
                Image(
                    painterResource(R.drawable.ic_photo_camera_24),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = stringResource(R.string.document_overview_screen_open_camera),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                onClick = { onGalleryClick() }
            ) {
                Image(
                    painterResource(R.drawable.ic_perm_media_24),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = stringResource(R.string.document_overview_screen_pick_media),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
        }
    }
}

private fun isCameraPermissionGranted(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
}

