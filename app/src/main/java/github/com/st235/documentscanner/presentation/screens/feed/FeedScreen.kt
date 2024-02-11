package github.com.st235.documentscanner.presentation.screens.feed

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import github.com.st235.documentscanner.R
import github.com.st235.documentscanner.presentation.screens.Screen
import github.com.st235.documentscanner.presentation.widgets.DocumentPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    viewModel: FeedScreenViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val state by viewModel.feedUiState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val writeExternalStoragePermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                viewModel.refreshPages()
            }
        }

    OnLifecycleEvent { owner, event ->
        if (event == Lifecycle.Event.ON_RESUME) {
            if (!isReadMediaImagesPermissionGranted(context)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    writeExternalStoragePermissionLauncher.launch(READ_MEDIA_IMAGES)
                } else {
                    writeExternalStoragePermissionLauncher.launch(READ_EXTERNAL_STORAGE)
                }
            } else {
                viewModel.refreshPages()
            }
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
                        stringResource(id = R.string.feed_screen_title),
                        fontWeight = FontWeight.Medium
                    )
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                icon = {
                    Icon(
                        painterResource(R.drawable.ic_document_scanner_24),
                        contentDescription = null
                    )
                },
                text = { Text(text = stringResource(R.string.feed_screen_start_flow)) },
                onClick = { navController.navigate(Screen.DocumentsFlow.route) }
            )
        }
    ) { paddings ->
        val pages = state.pages
        val hasPermissionToReadGallery = !isReadMediaImagesPermissionGranted(context)
        val shouldShowEmptyView = pages.isEmpty()

        if (hasPermissionToReadGallery) {
            SpecialMessageView(
                icon = R.drawable.ic_hide_image_24,
                headline = stringResource(R.string.feed_screen_no_permission_title),
                description = stringResource(R.string.feed_screen_no_permission_description)
            )
        } else if (shouldShowEmptyView) {
            SpecialMessageView(
                icon = R.drawable.ic_hallway_24,
                headline = stringResource(R.string.feed_screen_empty_gallery_title),
                description = stringResource(R.string.feed_screen_empty_gallery_description)
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .padding(paddings)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(pages) { page ->
                    DocumentPreview(
                        document = page,
                        onClick = {
                            navController.navigate(Screen.DocumentPreview.create(page))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SpecialMessageView(
    @DrawableRes icon: Int,
    headline: String,
    description: String,
    modifier: Modifier = Modifier,
    headlineColor: Color = MaterialTheme.colorScheme.onSurface,
    descriptionColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Icon(
            painterResource(icon),
            contentDescription = null,
            tint = headlineColor,
            modifier = Modifier
                .width(96.dp)
                .height(96.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = headline,
            color = headlineColor,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            fontSize = 26.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = description,
            color = descriptionColor,
            textAlign = TextAlign.Center,
            fontSize = 16.sp
        )
    }
}

@Composable
fun OnLifecycleEvent(onEvent: (owner: LifecycleOwner, event: Lifecycle.Event) -> Unit) {
    val eventHandler = rememberUpdatedState(onEvent)
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

    DisposableEffect(lifecycleOwner.value) {
        val lifecycle = lifecycleOwner.value.lifecycle
        val observer = LifecycleEventObserver { owner, event ->
            eventHandler.value(owner, event)
        }

        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}

private fun isReadMediaImagesPermissionGranted(context: Context): Boolean {
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        READ_MEDIA_IMAGES
    } else {
        READ_EXTERNAL_STORAGE
    }

    return ContextCompat.checkSelfPermission(context, permission) ==
            PackageManager.PERMISSION_GRANTED
}

