package github.com.st235.documentscanner.presentation.screens

import android.net.Uri
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screen(val route: String) {
    data object DocumentsFeed: Screen(route = "documents_feed")
    data object DocumentCropper: Screen(route = "document_cropper/{uri}") {
        const val ARG_URI = "uri"

        fun getRouteFor(uri: Uri): String {
            val encodedUrl = URLEncoder.encode(uri.toString(), StandardCharsets.UTF_8.toString())
            return "document_cropper/$encodedUrl"
        }
    }
    data object DocumentComposer: Screen(route = "document_composer")
    data object DocumentEditor: Screen(route = "document_editor")
}
