package github.com.st235.documentscanner.domain

import android.graphics.Bitmap
import android.net.Uri
import github.com.st235.documentscanner.utils.BitmapWriter
import github.com.st235.documentscanner.utils.DocumentsNameGenerator
import github.com.st235.documentscanner.utils.LocalUriLoader
import github.com.st235.documentscanner.utils.TempUriProvider
import github.com.st235.documentscanner.utils.documents.ImageStitcher
import github.com.st235.documentscanner.utils.gallery.GallerySaver

class DocumentsCompositionInteractor(
    private val nameGenerator: DocumentsNameGenerator,
    private val imageStitcher: ImageStitcher,
    private val uriLoader: LocalUriLoader,
    private val tempUriProvider: TempUriProvider,
    private val bitmapWriter: BitmapWriter,
    private val gallerySaver: GallerySaver
) {

    data class DocumentPage(
        val id: Int,
        val uri: Uri
    )

    @Volatile
    private var documentId: Int = 1
    private val documentPages = mutableMapOf<Int, DocumentPage>()

    fun getDocumentById(documentId: Int): Bitmap? {
        return uriLoader.load(getDocumentPageById(documentId).uri)
    }

    @Synchronized
    fun getDocumentPageById(documentId: Int): DocumentPage {
        return documentPages.getValue(documentId)
    }

    @Synchronized
    fun getAllPages(): List<DocumentPage> {
        return ArrayList(documentPages.values)
    }

    private fun loadAllPages(): Array<Bitmap> {
        val pages = getAllPages()
        return pages.mapNotNull { uriLoader.load(it.uri) }.toTypedArray()
    }

    @Synchronized
    fun addPage(bitmap: Bitmap) {
        val tempId = documentId
        val tempUri = tempUriProvider.createRandomUri()

        bitmapWriter.save(tempUri, bitmap)

        documentPages[tempId] = DocumentPage(tempId, tempUri)
        documentId += 1
    }

    @Synchronized
    fun updatePage(documentId: Int, bitmap: Bitmap) {
        val oldUri = getDocumentPageById(documentId).uri
        val tempUri = tempUriProvider.createRandomUri()

        bitmapWriter.delete(oldUri)
        bitmapWriter.save(tempUri, bitmap)

        documentPages[documentId] = DocumentPage(documentId, tempUri)
    }

    fun save() {
        val documentsBitmaps = loadAllPages()

        if (documentsBitmaps.isEmpty()) {
            throw IllegalStateException("Documents list is empty")
        }

        val finalDocument = if (documentsBitmaps.size > 1) {
            imageStitcher.stitch(documentsBitmaps)
        } else {
            documentsBitmaps[0]
        }

        gallerySaver.save(
            title = nameGenerator.generateName(),
            album = Config.ALBUM_SCANS,
            source = finalDocument
        )
    }

}