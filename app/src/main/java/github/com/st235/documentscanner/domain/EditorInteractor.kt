package github.com.st235.documentscanner.domain

import android.graphics.Bitmap
import github.com.st235.documentscanner.utils.documents.ImageProcessor

class EditorInteractor(
    private val imageProcessor: ImageProcessor
) {

    fun rotate90Clockwise(document: Bitmap): Bitmap {
        return imageProcessor.rotate90DegreesClockwise(document)
    }

    fun binarise(document: Bitmap, mode: ImageProcessor.Binarization): Bitmap {
        return imageProcessor.binarise(document, mode)
    }

}