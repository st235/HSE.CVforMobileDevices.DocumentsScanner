package github.com.st235.documentscanner.presentation.feed

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PointF
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
import github.com.st235.documentscanner.presentation.components.CroppingViewport
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
//            val openCvHelper by remember { mutableStateOf(get<OpenCVHelper>()) }
            var croppingViewport by remember { mutableStateOf<CroppingViewport?>(null) }
            var croppedImage by remember { mutableStateOf<Bitmap?>(null) }

            if (croppedImage == null) {
                Button(onClick = {
                    val vprt = croppingViewport!!
//                    croppedImage = openCvHelper.wrapPerspective(
//                        image = BitmapFactory.decodeResource(context.resources, R.drawable.document),
//                        topLeft = vprt.leftTop.asFloatArray(),
//                        topRight = vprt.rightTop.asFloatArray(),
//                        bottomLeft = vprt.leftBottom.asFloatArray(),
//                        bottomRight = vprt.rightBottom.asFloatArray(),
//                    )
                }) {
                    Text("Crop")
                }
                CropView(
                    image = BitmapFactory.decodeResource(context.resources, R.drawable.document),
                    croppingViewport = CroppingViewport(
                        leftTop = Offset(50f, 50f),
                        leftBottom = Offset(50f, 150f),
                        rightTop = Offset(250f, 50f),
                        rightBottom = Offset(170f, 250f),
                    ),
                    onCornersChanged = { viewport ->
                        Log.d("HelloWorld", "image cropping viewport: $viewport")
                        croppingViewport = viewport
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

fun Offset.asFloatArray(): FloatArray {
    return floatArrayOf(x, y)
}

@Preview(showBackground = true)
@Composable
fun DocumentsFeedScreenPreview() {
    DocumentsFeedScreen()
}
