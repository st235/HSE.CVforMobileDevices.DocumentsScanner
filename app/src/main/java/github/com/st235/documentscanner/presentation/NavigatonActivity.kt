package github.com.st235.documentscanner.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import github.com.st235.documentscanner.presentation.base.theme.DocumentScannerTheme
import github.com.st235.documentscanner.presentation.screens.Screen
import github.com.st235.documentscanner.presentation.screens.composer.DocumentsComposerViewModel
import github.com.st235.documentscanner.presentation.screens.composer.overview.DocumentCompositionOverviewScreen
import github.com.st235.documentscanner.presentation.screens.composer.cropper.DocumentCropperScreen
import github.com.st235.documentscanner.presentation.screens.composer.editor.DocumentEditor
import github.com.st235.documentscanner.presentation.screens.feed.FeedScreen
import org.koin.androidx.compose.koinViewModel

class NavigatonActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DocumentScannerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost(
                        startDestination = Screen.DocumentComposer.route,
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
        val viewModel = koinViewModel<DocumentsComposerViewModel>()

        NavHost(
            startDestination = startDestination,
            navController = navController,
            modifier = modifier
        ) {
            composable(Screen.DocumentsFeed.route) {
                FeedScreen(
                    navController = navController,
                    modifier = modifier
                )
            }

            navigation(
                route = Screen.DocumentComposer.route,
                startDestination = Screen.DocumentComposer.Overview.route) {

                composable(Screen.DocumentComposer.Overview.route) {
                    DocumentCompositionOverviewScreen(
                        sharedViewModel = viewModel,
                        navController = navController,
                        modifier = modifier
                    )
                }

                composable(Screen.DocumentComposer.Cropper.route) {
                    DocumentCropperScreen(
                        sharedViewModel = viewModel,
                        navController = navController,
                        modifier = modifier
                    )
                }

                composable(
                    route = Screen.DocumentComposer.Editor.route,
                    arguments = listOf(navArgument(Screen.DocumentComposer.Editor.ID) {
                        type = NavType.IntType
                    })
                    ) { backStackEntry ->
                    val documentId = backStackEntry.arguments?.getInt(Screen.DocumentComposer.Editor.ID) ?: 0

                    DocumentEditor(
                        documentId = documentId,
                        sharedViewModel = viewModel,
                        navController = navController,
                        modifier = modifier
                    )
                }
            }
        }
    }
}
