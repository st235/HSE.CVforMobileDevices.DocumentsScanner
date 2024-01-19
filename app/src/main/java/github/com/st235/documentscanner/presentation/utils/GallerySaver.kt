package github.com.st235.documentscanner.presentation.utils

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
        title: String,
        description: String,
        album: String,
        source: Bitmap
    ): Uri? {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, title)
        values.put(MediaStore.Images.Media.DISPLAY_NAME, title)
        values.put(MediaStore.Images.Media.DESCRIPTION, description)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
        values.put(MediaStore.MediaColumns.RELATIVE_PATH,
            Environment.DIRECTORY_PICTURES + File.separator + album
        )

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        }

        var url: Uri? = null

        try {
            url = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: return null
            val imageOut = contentResolver.openOutputStream(url)
            imageOut?.use { imageOut ->
                source.compress(Bitmap.CompressFormat.JPEG, 100, imageOut)
            }
        } catch (e: Exception) {
            if (url != null) {
                contentResolver.delete(url, null, null)
                url = null
            }
        }

        if (url != null) {
            return url
        }

        return null
    }

    @WorkerThread
    fun delete(uri: Uri) {
        contentResolver.delete(uri, null, null)
    }
}
