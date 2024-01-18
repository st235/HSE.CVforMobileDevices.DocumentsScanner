package github.com.st235.documentscanner.presentation.base

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
import github.com.st235.documentscanner.presentation.screens.Screen
import github.com.st235.documentscanner.presentation.base.theme.DocumentScannerTheme
import github.com.st235.documentscanner.presentation.screens.composer.DocumentsComposerScreen
import github.com.st235.documentscanner.presentation.screens.cropper.DocumentCropperScreen
import github.com.st235.documentscanner.presentation.screens.feed.FeedScreen

class MainActivity : ComponentActivity() {
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
            composable(Screen.DocumentComposer.route) {
                DocumentsComposerScreen(
                    navController = navController,
                    modifier = modifier
                )
            }
            composable(
                Screen.DocumentCropper.route,
                arguments = listOf(
                    navArgument(name = Screen.DocumentCropper.ARG_URI) {
                        type = NavType.StringType
                    },
                )
            ) { navBackStackEntry ->
                val uri = Uri.parse(navBackStackEntry.arguments?.getString(Screen.DocumentCropper.ARG_URI))

                DocumentCropperScreen(
                    documentUri = uri,
                    navController = navController,
                    modifier = modifier
                )
            }
            composable(Screen.DocumentEditor.route) {
                DocumentsComposerScreen(
                    navController = navController,
                    modifier = modifier
                )
            }
        }
    }
}
