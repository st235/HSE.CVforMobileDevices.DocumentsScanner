package github.com.st235.documentscanner

import android.app.Application
import github.com.st235.documentscanner.dependencies.appModules
import github.com.st235.documentscanner.domain.NativeInitializer
import github.com.st235.documentscanner.presentation.utils.GalleryScanner
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class DocumentScannerApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        NativeInitializer.init()

        GalleryScanner(contentResolver).queryImages(limit = 100, page = 0)

        startKoin {
            androidContext(this@DocumentScannerApplication)
            modules(appModules)
        }
    }
}