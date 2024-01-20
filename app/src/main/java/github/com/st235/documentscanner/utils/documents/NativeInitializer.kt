package github.com.st235.documentscanner.utils.documents

object NativeInitializer {

    @Volatile
    private var isInitialised: Boolean = false

    @Synchronized
    fun init() {
        System.loadLibrary("OpenCVDocumentScannerLib")
        isInitialised = true
    }

    @Synchronized
    fun assertNativeIsInitialised() {
        if (!isInitialised) {
            throw IllegalStateException("Native code is not initialised, use init.")
        }
    }
}
