package github.com.st235.documentscanner.utils.gallery

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.annotation.WorkerThread


class GalleryScanner(
    private val contentResolver: ContentResolver
) {

    private companion object {
        val DEFAULT_PROJECTION = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATE_TAKEN
        )
    }

    @WorkerThread
    fun queryImages(
        album: String = "%",
        page: Int,
        limit: Int
    ): List<Uri> {
        val selection = "${MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME} LIKE ?"
        val selectionArgs = arrayOf(album)

        val offset = page * limit

        val sortByColumn = MediaStore.Images.ImageColumns.DATE_TAKEN

        val cursor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val bundle = Bundle().apply {
                putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection)
                putStringArray(
                    ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS,
                    selectionArgs
                )

                putString(
                    ContentResolver.QUERY_ARG_SORT_COLUMNS,
                    sortByColumn
                )
                putInt(
                    ContentResolver.QUERY_ARG_SORT_DIRECTION,
                    ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
                )

                putInt(ContentResolver.QUERY_ARG_LIMIT, limit)
                putInt(ContentResolver.QUERY_ARG_OFFSET, offset)
            }
            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                DEFAULT_PROJECTION,
                bundle,
                null
            )
        } else {
            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                DEFAULT_PROJECTION,
                selection,
                selectionArgs,
                "$sortByColumn DESC LIMIT $limit OFFSET $offset"
            )
        }

        val imagesResult = mutableListOf<Uri>()

        while (cursor?.moveToNext() == true) {
            val idColumnIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID)

            val imageUri = ContentUris
                .withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    cursor.getInt(idColumnIndex).toLong()
                )

            imagesResult.add(imageUri)
        }

        cursor?.close()
        return imagesResult
    }

}