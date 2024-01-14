package github.com.st235.documentscanner

import android.app.Application
import android.util.Log
import github.com.st235.documentscanner.dependencies.appModules
import github.com.st235.documentscanner.utils.OpenCVHelper
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class DocumentScannerApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@DocumentScannerApplication)
            modules(appModules)
        }
    }
}