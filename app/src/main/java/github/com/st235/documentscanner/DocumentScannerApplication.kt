package github.com.st235.documentscanner

import android.app.Application
import android.util.Log
import github.com.st235.documentscanner.utils.OpenCVHelper

class DocumentScannerApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        OpenCVHelper.load()
        Log.d("HelloWorld", OpenCVHelper().helloWorld())
    }
}