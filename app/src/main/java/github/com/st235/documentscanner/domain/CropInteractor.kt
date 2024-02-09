package github.com.st235.documentscanner.domain

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import github.com.st235.documentscanner.utils.LocalUriLoader
import github.com.st235.documentscanner.utils.UriMimeTypeHandler
import github.com.st235.documentscanner.utils.VideoFramesExtractor
import github.com.st235.documentscanner.utils.documents.DocumentScanner

class CropInteractor(
    private val documentScanner: DocumentScanner,
    private val localUriLoader: LocalUriLoader,
    private val uriMimeTypeHandler: UriMimeTypeHandler,
    private val videoFramesExtractor: VideoFramesExtractor,
) {

    @Volatile
    private var currentBitmap: Bitmap? = null

    @Synchronized
    fun prepareBitmap(uri: Uri) {
        val isVideo = uriMimeTypeHandler.isVideo(uri)
        currentBitmap = if (isVideo) {
            prepareVideoDocument(uri)
        } else {
            prepareImageDocument(uri)
        }
    }

    @Synchronized
    private fun prepareImageDocument(uri: Uri): Bitmap? {
        return localUriLoader.load(uri)
    }

    @Synchronized
    private fun prepareVideoDocument(uri: Uri): Bitmap? {
        val videoIterator = videoFramesExtractor.load(uri)
        for (frame in videoIterator) {
            Log.d("HelloWorld", "frame timestamp: " + frame.timestamp)
        }

        TODO("Implement OpenCV key frame extraction logic.")
    }


    fun detectCorners(): DocumentScanner.Corners? {
        return documentScanner.findCorners(requireBitmap())
    }

    fun crop(corners: DocumentScanner.Corners?): Bitmap {
        if (corners == null) {
            return requireBitmap()
        }

        return documentScanner.wrapPerspective(requireBitmap(), corners)
    }

    @Synchronized
    fun clear() {
        currentBitmap = null
    }

    @Synchronized
    fun requireBitmap(): Bitmap {
        if (currentBitmap == null) {
            throw IllegalStateException("Current bitmap is null")
        }

        return currentBitmap!!
    }

}