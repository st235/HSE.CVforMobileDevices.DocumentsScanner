package github.com.st235.documentscanner.domain

import android.net.Uri
import github.com.st235.documentscanner.utils.gallery.GalleryScanner

class FeedInteractor(
    private val galleryScanner: GalleryScanner
) {

    fun loadFeed(): List<Uri> {
        return galleryScanner.queryImages(
            album = Config.ALBUM_SCANS
        )
    }
}