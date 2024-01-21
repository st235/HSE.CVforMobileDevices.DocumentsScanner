package github.com.st235.documentscanner.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import java.io.FileNotFoundException
import java.io.IOException


class LocalUriLoader(
    private val contentResolver: ContentResolver
) {

    private companion object {
        const val TAG = "UriLoader"
        const val MAX_DIMENSION = 1920

        private fun calculateInSampleSize(
            options: BitmapFactory.Options,
            maxWidth: Int,
            maxHeight: Int
        ): Int {
            val rawHeight = options.outHeight
            val rawWidth = options.outWidth

            var inSampleSize = 1
            if (rawHeight > maxHeight || rawWidth > maxWidth) {
                val halfHeight = rawHeight / 2
                val halfWidth = rawWidth / 2

                while (halfHeight / inSampleSize >= maxHeight
                    && halfWidth / inSampleSize >= maxWidth) {
                    inSampleSize *= 2
                }
            }
            return inSampleSize
        }
    }

    fun load(uri: Uri): Bitmap? {
        try {
            val options = BitmapFactory.Options()
            var inputStream = contentResolver.openInputStream(uri)

            options.inJustDecodeBounds = true
            options.inPreferredConfig = Bitmap.Config.RGB_565

            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()

            inputStream = contentResolver.openInputStream(uri)

            options.inSampleSize = calculateInSampleSize(options,
                maxWidth = MAX_DIMENSION,
                maxHeight = MAX_DIMENSION
            )

            options.inJustDecodeBounds = false
            val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()

            if (bitmap == null) {
                return null
            }

            val rotationMatrix = getCorrectBitmapRotation(uri)
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, rotationMatrix, true)
        } catch (e: FileNotFoundException) {
            Log.e(TAG, "File that corresponds to $uri was not found.", e)
            return null
        } catch (e: IOException) {
            Log.e(TAG, "Cannot open $uri.", e)
            return null
        }
    }

    private fun getCorrectBitmapRotation(uri: Uri): Matrix {
        val inputStream = contentResolver.openInputStream(uri) ?: return Matrix()

        val exifInterface = ExifInterface(inputStream)
        val orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL)

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> Matrix().apply { postRotate(90f) }
            ExifInterface.ORIENTATION_ROTATE_180 -> Matrix().apply { postRotate(180f) }
            ExifInterface.ORIENTATION_ROTATE_270 -> Matrix().apply { postRotate(270f) }
            else -> Matrix()
        }

    }
}