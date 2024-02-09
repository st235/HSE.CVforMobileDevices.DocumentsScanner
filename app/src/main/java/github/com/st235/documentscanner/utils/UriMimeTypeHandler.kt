package github.com.st235.documentscanner.utils

import android.content.ContentResolver
import android.net.Uri
import android.webkit.MimeTypeMap

class UriMimeTypeHandler(
    private val contentResolver: ContentResolver,
    private val mime: MimeTypeMap = MimeTypeMap.getSingleton(),
) {

    fun isVideo(uri: Uri): Boolean {
        return getFileMimeType(uri)?.lowercase()?.startsWith("video") == true
    }

    fun isImage(uri: Uri): Boolean {
        return getFileMimeType(uri)?.lowercase()?.startsWith("image") == true
    }

    private fun getFileMimeType(uri: Uri): String? {
        return if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            contentResolver.getType(uri)
        } else {
            val extension = MimeTypeMap.getFileExtensionFromUrl(uri.path)
            return mime.getMimeTypeFromExtension(extension)
        }
    }

}
