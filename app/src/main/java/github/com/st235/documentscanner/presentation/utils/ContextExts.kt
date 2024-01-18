package github.com.st235.documentscanner.presentation.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

fun Context.createTempUri(
    provider: String = "$packageName.provider",
    fileName: String = "temp_${System.nanoTime()}",
    fileExtension: String = ".png"
): Uri {
    val tempFile = File.createTempFile(fileName, fileExtension, cacheDir)
    tempFile.createNewFile()
    return FileProvider.getUriForFile(applicationContext, provider, tempFile)
}
