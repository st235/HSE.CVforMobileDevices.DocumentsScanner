package github.com.st235.documentscanner.utils

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri

class BitmapWriter(
    private val contentResolver: ContentResolver
) {
    fun save(uri: Uri, bitmap: Bitmap) {
        val imageOut = contentResolver.openOutputStream(uri)
        imageOut?.use { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
    }
}