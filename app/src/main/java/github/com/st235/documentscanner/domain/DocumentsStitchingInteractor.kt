package github.com.st235.documentscanner.domain

import android.graphics.Bitmap
import android.net.Uri
import github.com.st235.documentscanner.utils.BitmapWriter
import github.com.st235.documentscanner.utils.LocalUriLoader
import github.com.st235.documentscanner.utils.TempUriProvider
import github.com.st235.documentscanner.utils.UriMimeTypeHandler
import github.com.st235.documentscanner.utils.documents.ImageStitcher
import github.com.st235.documentscanner.utils.documents.KeyFrameDetector

class DocumentsStitchingInteractor(
    private val localUriLoader: LocalUriLoader,
    private val keyFrameDetector: KeyFrameDetector,
    private val tempUriProvider: TempUriProvider,
    private val uriMimeTypeHandler: UriMimeTypeHandler,
    private val imageStitcher: ImageStitcher,
    private val bitmapWriter: BitmapWriter
) {

    data class DocumentPage(
        val id: Int,
        val uri: Uri
    )

    @Volatile
    private var documentId: Int = 1
    private val documentPages = mutableMapOf<Int, DocumentPage>()

    @Synchronized
    fun addPage(uri: Uri) {
        val tempId = documentId
        val tempUri = tempUriProvider.createRandomUri()

        val tempBitmap = prepareBitmap(uri)

        if (tempBitmap == null) {
            return
        }

        bitmapWriter.save(tempUri, tempBitmap)

        documentPages[tempId] = DocumentPage(tempId, tempUri)
        documentId += 1
    }

    @Synchronized
    private fun prepareBitmap(uri: Uri): Bitmap? {
        val isVideo = uriMimeTypeHandler.isVideo(uri)
        return if (isVideo) {
            prepareVideoDocument(uri)
        } else {
            prepareImageDocument(uri)
        }
    }

    private fun prepareImageDocument(uri: Uri): Bitmap? {
        return localUriLoader.load(uri)
    }


    private fun prepareVideoDocument(uri: Uri): Bitmap? {
        return keyFrameDetector.getKeyFrame(uri)
    }

    @Synchronized
    fun save(): Uri {
        if (documentPages.isEmpty()) {
            throw IllegalStateException("Documents list is empty")
        }

        if (documentPages.size <= 1) {
            return documentPages.values.first().uri
        }

        val documentsBitmaps = loadAllPages()
        val documentToCrop = imageStitcher.stitch(documentsBitmaps)
        val tempUri = tempUriProvider.createRandomUri()
        bitmapWriter.save(tempUri, documentToCrop)
        return tempUri
    }

    private fun loadAllPages(): Array<Bitmap> {
        val pages = getAllPages()
        return pages.mapNotNull { localUriLoader.load(it.uri) }.toTypedArray()
    }

    @Synchronized
    fun getAllPages(): List<DocumentPage> {
        return ArrayList(documentPages.values)
    }

    @Deprecated("")
    fun getDocumentById(documentId: Int): Bitmap? {
        return localUriLoader.load(getDocumentPageById(documentId).uri)
    }

    @Synchronized
    @Deprecated("")
    fun getDocumentPageById(documentId: Int): DocumentPage {
        return documentPages.getValue(documentId)
    }

    @Synchronized
    @Deprecated("")
    fun updatePage(documentId: Int, bitmap: Bitmap) {
        val oldUri = getDocumentPageById(documentId).uri
        val tempUri = tempUriProvider.createRandomUri()

        bitmapWriter.delete(oldUri)
        bitmapWriter.save(tempUri, bitmap)

        documentPages[documentId] = DocumentPage(documentId, tempUri)
    }

}