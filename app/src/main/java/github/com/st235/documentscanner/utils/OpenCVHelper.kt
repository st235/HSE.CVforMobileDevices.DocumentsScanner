package github.com.st235.documentscanner.utils

class OpenCVHelper {

    companion object {
        fun load() {
            System.loadLibrary("OpenCVDocumentScannerLib")
        }
    }

    external fun helloWorld(): String
}