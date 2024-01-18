package github.com.st235.documentscanner.presentation.screens.cropper

import android.net.Uri
import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import github.com.st235.documentscanner.R
import github.com.st235.documentscanner.domain.DocumentScanner
import github.com.st235.documentscanner.presentation.screens.Screen
import github.com.st235.documentscanner.presentation.widgets.CropArea
import github.com.st235.documentscanner.presentation.widgets.CropView
import org.koin.androidx.compose.koinViewModel

fun launchDocumentCropperScreen(
    navController: NavHostController,
    uri: Uri
) {
    navController.navigate(Screen.DocumentCropper.getRouteFor(uri))
}

@Composable
fun DocumentCropperScreen(
    documentUri: Uri,
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    val documentCropperViewModel = koinViewModel<DocumentCropperViewModel>()
    val state by documentCropperViewModel.documentCropperState.collectAsStateWithLifecycle()

    documentCropperViewModel.load(documentUri)

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                icon = {
                    Icon(
                        painterResource(R.drawable.ic_crop_24),
                        contentDescription = null
                    )
                },
                text = { Text(text = stringResource(R.string.document_cropper_crop_button)) },
                onClick = {  }
            )
        }
    ) { paddings ->
        val document = state.document
        val detectedCorners = state.detectedCorners

        if (document != null && detectedCorners != null) {
            CropView(
                image = document,
                imageCroppedArea = detectedCorners.asCropArea(),
                modifier = Modifier.padding(paddings).fillMaxSize()
            )
        }
    }
}

private fun DocumentScanner.Corners.asCropArea(): CropArea {
    return CropArea(
        topLeft = Offset(topLeft[0], topLeft[1]),
        bottomLeft = Offset(bottomLeft[0], bottomLeft[1]),
        topRight = Offset(topRight[0], topRight[1]),
        bottomRight = Offset(bottomRight[0], bottomRight[1]),
    )
}
