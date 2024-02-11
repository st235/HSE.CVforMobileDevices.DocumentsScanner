package github.com.st235.documentscanner.domain

import android.graphics.Bitmap
import android.net.Uri
import github.com.st235.documentscanner.utils.DocumentsNameGenerator
import github.com.st235.documentscanner.utils.LocalUriLoader
import github.com.st235.documentscanner.utils.documents.ImageProcessor
import github.com.st235.documentscanner.utils.gallery.GallerySaver

class EditorInteractor(
    private val imageProcessor: ImageProcessor,
    private val localUriLoader: LocalUriLoader,
    private val documentsNameGenerator: DocumentsNameGenerator,
    private val gallerySaver: GallerySaver,
) {

    fun prepareBitmap(uri: Uri): Bitmap {
        return localUriLoader.load(uri) ?: throw IllegalStateException("Cannot load $uri")
    }

    fun rotate90Clockwise(document: Bitmap): Bitmap {
        return imageProcessor.rotate90DegreesClockwise(document)
    }

    fun binarise(document: Bitmap, mode: ImageProcessor.Binarization): Bitmap {
        return imageProcessor.binarise(document, mode)
    }

    fun filter(document: Bitmap, mode: ImageProcessor.Filter): Bitmap {
        return imageProcessor.filter(document, mode)
    }

    fun contrast(document: Bitmap, mode: ImageProcessor.Contrast): Bitmap {
        return imageProcessor.enhanceContrast(document, mode)
    }

    fun denoising(document: Bitmap, mode: ImageProcessor.Denoising): Bitmap {
        return imageProcessor.denoise(document, mode)
    }

    fun save(documentBitmap: Bitmap) {
        val documentName = documentsNameGenerator.generateName()
        gallerySaver.save(
            source = documentBitmap,
            title = documentName,
            album = Config.ALBUM_SCANS
        )
    }

}