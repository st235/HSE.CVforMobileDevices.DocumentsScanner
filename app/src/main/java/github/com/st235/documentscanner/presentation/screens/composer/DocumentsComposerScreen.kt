package github.com.st235.documentscanner.presentation.screens.composer

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import github.com.st235.documentscanner.R
import github.com.st235.documentscanner.presentation.screens.cropper.launchDocumentCropperScreen
import github.com.st235.documentscanner.presentation.utils.createTempUri

@Composable
fun DocumentsComposerScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var cameraPermissionUri by remember { mutableStateOf(Uri.EMPTY) }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { isSuccessful ->
            if (isSuccessful) {
                launchDocumentCropperScreen(navController,  cameraPermissionUri)
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
                launchDocumentCropperScreen(navController,  uri)
            }
        }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                SmallFloatingActionButton(
                    onClick = {
                        if (!isCameraPermissionGranted(context)) {
                            cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                        } else {
                            cameraPermissionUri = context.createTempUri()
                            cameraLauncher.launch(cameraPermissionUri)
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.secondary,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_photo_camera_24),
                        contentDescription = null
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                ExtendedFloatingActionButton(
                    icon = {
                        Icon(
                            painterResource(R.drawable.ic_perm_media_24),
                            contentDescription = null
                        )
                    },
                    text = { Text(text = stringResource(R.string.document_composer_screen_pick_media)) },
                    onClick = {
                        galleryLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageAndVideo
                            )
                        )
                    }
                )
            }
        }
    ) { paddings ->
        Text("HelloWorld")
    }
}

private fun isCameraPermissionGranted(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
}

