package github.com.st235.documentscanner.presentation.feed

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import github.com.st235.documentscanner.R
import github.com.st235.documentscanner.presentation.components.CropView
import github.com.st235.documentscanner.presentation.components.CropArea
import github.com.st235.documentscanner.utils.OpenCVHelper

@Composable
fun DocumentsFeedScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        Log.d("HelloWorld", uri.toString())
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                icon = { Icon(painterResource(R.drawable.ic_perm_media_24px), contentDescription = stringResource(R.string.documents_feed_screen_pick_media)) },
                text = { Text(text = stringResource(R.string.documents_feed_screen_pick_media)) },
                onClick = { galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)) }
            )
        }
    ) { innerPadding ->
        Column(modifier = modifier.padding(innerPadding)) {
            val openCvHelper by remember { mutableStateOf(OpenCVHelper()) }
            val image by remember {
                mutableStateOf(BitmapFactory.decodeResource(context.resources, R.drawable.document))
            }
            var cropArea by remember { mutableStateOf<CropArea?>(null) }
            var croppedImage by remember { mutableStateOf<Bitmap?>(null) }

            if (croppedImage == null) {
                Button(onClick = {
                    val vprt = cropArea!!
                    croppedImage = openCvHelper.wrapPerspective(
                        image = image,
                        OpenCVHelper.Corners(
                            topLeft = vprt.topLeft.asFloatArray(),
                            topRight = vprt.topRight.asFloatArray(),
                            bottomLeft = vprt.bottomLeft.asFloatArray(),
                            bottomRight = vprt.bottomRight.asFloatArray(),
                        )
                    )
                }) {
                    Text("Crop")
                }
                val foundCorners = openCvHelper.findCorners(image)
                cropArea = foundCorners.asCroppingViewport()

                CropView(
                    image = image,
                    imageCroppedArea = foundCorners.asCroppingViewport(),
                    onCropAreaChanged = { viewport ->
                        cropArea = viewport
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                )
            } else {
                Image(bitmap = croppedImage!!.asImageBitmap(), contentDescription = null)
            }
        }


    }
}

fun OpenCVHelper.Corners?.asCroppingViewport(): CropArea {
    return if (this == null) {
        CropArea(
            topLeft = Offset(50f, 50f),
            bottomLeft = Offset(50f, 150f),
            topRight = Offset(250f, 50f),
            bottomRight = Offset(170f, 250f),
        )
    } else {
        CropArea(
            topLeft = Offset(topLeft[0], topLeft[1]),
            bottomLeft = Offset(bottomLeft[0], bottomLeft[1]),
            topRight = Offset(topRight[0], topRight[1]),
            bottomRight = Offset(bottomRight[0], bottomRight[1]),
        )
    }
}

fun Offset.asFloatArray(): FloatArray {
    return floatArrayOf(x, y)
}

@Preview(showBackground = true)
@Composable
fun DocumentsFeedScreenPreview() {
    DocumentsFeedScreen()
}
