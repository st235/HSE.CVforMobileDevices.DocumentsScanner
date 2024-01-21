package github.com.st235.documentscanner.utils

import android.content.Context
import android.os.Build
import github.com.st235.documentscanner.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DocumentsNameGenerator(
    private val context: Context
) {

    private val currentLocale: Locale
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context.resources.configuration.locales[0]
            } else {
                context.resources.configuration.locale
            }
        }

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("HH:mm:ss a", currentLocale)

    fun generateName(): String {
        val origin = dateFormat.format(calendar.time)
        return context.resources.getString(R.string.document_overview_document_save_template, origin)
    }

}