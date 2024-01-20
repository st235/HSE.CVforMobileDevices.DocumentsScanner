package github.com.st235.documentscanner.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

fun Context.createTempUri(
    provider: String = "$packageName.provider",
    fileName: String = "temp_${System.nanoTime()}",
    folder: String? = null,
    fileExtension: String = ".png"
): Uri {
    val parentDir = if (folder != null) {
        File(cacheDir, folder)
    } else {
        cacheDir
    }

    if (!parentDir.exists()) {
        parentDir.mkdir()
    }

    val tempFile = File.createTempFile(fileName, fileExtension, parentDir)
    tempFile.createNewFile()
    return FileProvider.getUriForFile(applicationContext, provider, tempFile)
}
