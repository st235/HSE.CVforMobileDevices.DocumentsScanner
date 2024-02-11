package github.com.st235.documentscanner.domain

import android.graphics.Bitmap
import android.net.Uri
import github.com.st235.documentscanner.utils.BitmapWriter
import github.com.st235.documentscanner.utils.LocalUriLoader
import github.com.st235.documentscanner.utils.TempUriProvider
import github.com.st235.documentscanner.utils.documents.DocumentScanner

class CropInteractor(
    private val documentScanner: DocumentScanner,
    private val localUriLoader: LocalUriLoader,
    private val tempUriProvider: TempUriProvider,
    private val bitmapWriter: BitmapWriter,
) {

    @Synchronized
    fun prepareBitmap(uri: Uri): Bitmap {
        return localUriLoader.load(uri) ?: throw IllegalStateException("Current bitmap is null")
    }

    fun detectCorners(documentBitmap: Bitmap): DocumentScanner.Corners? {
        return documentScanner.findCorners(documentBitmap)
    }

    fun crop(documentBitmap: Bitmap, corners: DocumentScanner.Corners?): Bitmap {
        if (corners == null) {
            return documentBitmap
        }

        return documentScanner.wrapPerspective(documentBitmap, corners)
    }

    fun save(croppedBitmap: Bitmap): Uri {
        val tempUri = tempUriProvider.createRandomUri()
        bitmapWriter.save(tempUri, croppedBitmap)
        return tempUri
    }

}