package github.com.st235.documentscanner.domain

import android.graphics.Bitmap
import android.net.Uri
import github.com.st235.documentscanner.utils.LocalUriLoader
import github.com.st235.documentscanner.utils.documents.DocumentScanner

class CropInteractor(
    private val documentScanner: DocumentScanner,
    private val localUriLoader: LocalUriLoader
) {

    @Volatile
    private var currentBitmap: Bitmap? = null

    @Synchronized
    fun prepareBitmap(uri: Uri) {
        currentBitmap = localUriLoader.load(uri)
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