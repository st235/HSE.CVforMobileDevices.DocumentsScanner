package github.com.st235.documentscanner.presentation.screens

import android.net.Uri
import java.net.URLEncoder

sealed class Screen(val route: String) {
    data object DocumentsFeed: Screen(route = "documents_feed")

    data object DocumentPreview: Screen(route = "preview/{document_uri}") {
        const val URI = "document_uri"
        fun create(documentUri: Uri): String {
            val encodedUri = URLEncoder.encode(documentUri.toString(), Charsets.UTF_8.name())
            return "preview/$encodedUri"
        }
    }

    data object DocumentsFlow: Screen(route = "document_composer") {
        data object Stitcher: Screen(route = "overview")
        data object Cropper: Screen(route = "cropper/{document_uri}") {
            const val URI = "document_uri"
            fun create(documentUri: Uri): String {
                val encodedUri = URLEncoder.encode(documentUri.toString(), Charsets.UTF_8.name())
                return "cropper/$encodedUri"
            }
        }

        data object Editor: Screen(route = "editor/{document_uri}") {
            const val URI = "document_uri"
            fun create(documentUri: Uri): String {
                val encodedUri = URLEncoder.encode(documentUri.toString(), Charsets.UTF_8.name())
                return "editor/$encodedUri"
            }
        }
    }
}
