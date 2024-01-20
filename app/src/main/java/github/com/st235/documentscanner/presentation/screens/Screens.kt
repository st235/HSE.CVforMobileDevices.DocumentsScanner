package github.com.st235.documentscanner.presentation.screens

sealed class Screen(val route: String) {
    data object DocumentsFeed: Screen(route = "documents_feed")

    data object DocumentComposer: Screen(route = "document_composer") {
        data object Overview: Screen(route = "overview")
        data object Cropper: Screen(route = "cropper")
        data object Editor: Screen(route = "editor/{document_id}") {
            const val ID = "document_id"
            fun create(documentId: Int): String {
                return "editor/$documentId"
            }
        }
    }
}
