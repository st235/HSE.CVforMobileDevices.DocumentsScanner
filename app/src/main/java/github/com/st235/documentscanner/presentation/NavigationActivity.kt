package github.com.st235.documentscanner.presentation

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import github.com.st235.documentscanner.presentation.base.theme.DocumentScannerTheme
import github.com.st235.documentscanner.presentation.screens.Screen
import github.com.st235.documentscanner.presentation.screens.composer.editor.DocumentsEditorViewModel
import github.com.st235.documentscanner.presentation.screens.composer.overview.DocumentCompositionOverviewScreen
import github.com.st235.documentscanner.presentation.screens.composer.cropper.DocumentCropperScreen
import github.com.st235.documentscanner.presentation.screens.composer.cropper.DocumentCropperViewModel
import github.com.st235.documentscanner.presentation.screens.composer.editor.DocumentEditor
import github.com.st235.documentscanner.presentation.screens.composer.overview.DocumentsStitcherViewModel
import github.com.st235.documentscanner.presentation.screens.feed.FeedScreen
import github.com.st235.documentscanner.presentation.screens.feed.FeedScreenViewModel
import org.koin.androidx.compose.koinViewModel

class NavigationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DocumentScannerTheme(
                dynamicColor = false,
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost(
                        startDestination = Screen.DocumentsFeed.route,
                        navController = rememberNavController()
                    )
                }
            }
        }
    }

    @Composable
    fun AppNavHost(
        startDestination: String,
        navController: NavHostController,
        modifier: Modifier = Modifier,
    ) {
        NavHost(
            startDestination = startDestination,
            navController = navController,
            modifier = modifier
        ) {
            composable(Screen.DocumentsFeed.route) {
                val feedScreenViewModel = koinViewModel<FeedScreenViewModel>()

                FeedScreen(
                    viewModel = feedScreenViewModel,
                    navController = navController,
                    modifier = modifier
                )
            }

            navigation(
                route = Screen.DocumentsFlow.route,
                startDestination = Screen.DocumentsFlow.Stitcher.route) {

                composable(Screen.DocumentsFlow.Stitcher.route) {
                    val stitcherViewModel = koinViewModel<DocumentsStitcherViewModel>()

                    DocumentCompositionOverviewScreen(
                        viewModel = stitcherViewModel,
                        navController = navController,
                        modifier = modifier
                    )
                }

                composable(
                    route = Screen.DocumentsFlow.Cropper.route,
                    arguments = listOf(navArgument(Screen.DocumentsFlow.Cropper.URI) {
                        type = NavType.StringType
                    })
                ) { backStackEntry ->
                    val encodedUri = backStackEntry.arguments?.getString(Screen.DocumentsFlow.Cropper.URI) ?: ""
                    val uri = Uri.parse(encodedUri)

                    val cropViewModel = koinViewModel<DocumentCropperViewModel>()

                    DocumentCropperScreen(
                        documentUri = uri,
                        viewModel = cropViewModel,
                        navController = navController,
                        modifier = modifier
                    )
                }

                composable(
                    route = Screen.DocumentsFlow.Editor.route,
                    arguments = listOf(navArgument(Screen.DocumentsFlow.Editor.URI) {
                        type = NavType.StringType
                    })
                    ) { backStackEntry ->
                    val encodedUri = backStackEntry.arguments?.getString(Screen.DocumentsFlow.Cropper.URI) ?: ""
                    val uri = Uri.parse(encodedUri)

                    val documentsEditorViewModel = koinViewModel<DocumentsEditorViewModel>()

                    DocumentEditor(
                        documentUri = uri,
                        viewModel = documentsEditorViewModel,
                        navController = navController,
                        modifier = modifier
                    )
                }
            }
        }
    }
}
