package github.com.st235.documentscanner.utils.gallery

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.WorkerThread
import java.io.File

class GallerySaver(
    private val contentResolver: ContentResolver
) {

    @WorkerThread
    fun save(
        source: Bitmap,
        title: String,
        album: String,
        description: String? = null
    ): Uri? {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, title)
        values.put(MediaStore.Images.Media.DISPLAY_NAME, title)
        description?.let { values.put(MediaStore.Images.Media.DESCRIPTION, it) }
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
        values.put(MediaStore.MediaColumns.RELATIVE_PATH,
            Environment.DIRECTORY_PICTURES + File.separator + album
        )

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        }

        var uri: Uri? = null

        try {
            uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: return null
            val imageOut = contentResolver.openOutputStream(uri)
            imageOut?.use { _ ->
                source.compress(Bitmap.CompressFormat.JPEG, 100, imageOut)
            }
        } catch (e: Exception) {
            if (uri != null) {
                contentResolver.delete(uri, null, null)
                uri = null
            }
        }

        if (uri != null) {
            return uri
        }

        return null
    }

    @WorkerThread
    fun delete(uri: Uri) {
        contentResolver.delete(uri, null, null)
    }
}
