package github.com.st235.documentscanner.presentation.feed

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import github.com.st235.documentscanner.R
import github.com.st235.documentscanner.presentation.components.CropView

@Composable
fun DocumentsFeedScreen(modifier: Modifier = Modifier) {
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
        Column(modifier = modifier) {
            Text(text="Hello world", modifier=Modifier.padding(innerPadding))
            CropView(modifier = Modifier.width(300.dp).height(150.dp))
        }


    }
}

@Preview(showBackground = true)
@Composable
fun DocumentsFeedScreenPreview() {
    DocumentsFeedScreen()
}
