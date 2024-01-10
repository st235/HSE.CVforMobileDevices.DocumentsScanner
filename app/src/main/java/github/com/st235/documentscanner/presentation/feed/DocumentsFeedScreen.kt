package github.com.st235.documentscanner.presentation.feed

import android.graphics.BitmapFactory
import android.graphics.PointF
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import github.com.st235.documentscanner.R
import github.com.st235.documentscanner.presentation.components.CropView
import github.com.st235.documentscanner.presentation.components.CroppingViewport

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
            CropView(
                image = BitmapFactory.decodeResource(context.resources, R.drawable.cat_horizontal),
                croppingViewport = CroppingViewport(
                    leftTopCorner = PointF(50f, 50f),
                    leftBottomCorner = PointF(50f, 150f),
                    rightTopCorner = PointF(250f, 50f),
                    rightBottomCorner = PointF(170f, 250f),
                ),
                modifier = Modifier.fillMaxWidth().fillMaxHeight()
            )
        }


    }
}

@Preview(showBackground = true)
@Composable
fun DocumentsFeedScreenPreview() {
    DocumentsFeedScreen()
}
