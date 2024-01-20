package github.com.st235.documentscanner.utils

import android.content.Context
import android.net.Uri

class TempUriProvider(
    private val context: Context
) {

    fun createRandomUri(folder: String? = null): Uri {
        return context.createTempUri(folder = folder)
    }

}