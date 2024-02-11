package github.com.st235.documentscanner.utils

import androidx.annotation.StringRes
import github.com.st235.documentscanner.R
import github.com.st235.documentscanner.utils.documents.ImageProcessor

@get:StringRes
val ImageProcessor.Binarization.stringRes: Int
    get() {
        return when(this) {
            ImageProcessor.Binarization.GLOBAL -> R.string.document_editor_binarisation_global
            ImageProcessor.Binarization.ADAPTIVE_MEAN -> R.string.document_editor_binarisation_adaptive_mean
            ImageProcessor.Binarization.ADAPTIVE_GAUSSIAN -> R.string.document_editor_binarisation_adaptive_gaussian
            ImageProcessor.Binarization.OTSU -> R.string.document_editor_binarisation_otsu
            ImageProcessor.Binarization.TRIANGLE -> R.string.document_editor_binarisation_triangle
            ImageProcessor.Binarization.CHAR -> R.string.document_editor_binarisation_char
        }
    }

val ImageProcessor.Binarization.isFavourite: Boolean
    get() {
        return when(this) {
            ImageProcessor.Binarization.GLOBAL -> false
            ImageProcessor.Binarization.ADAPTIVE_MEAN -> false
            ImageProcessor.Binarization.ADAPTIVE_GAUSSIAN -> false
            ImageProcessor.Binarization.OTSU -> false
            ImageProcessor.Binarization.TRIANGLE -> false
            ImageProcessor.Binarization.CHAR -> true
        }
    }

@get:StringRes
val ImageProcessor.Denoising.stringRes: Int
    get() {
        return when(this) {
            ImageProcessor.Denoising.TVL1 -> R.string.document_editor_denoising_tvl1
            ImageProcessor.Denoising.FAST_NI -> R.string.document_editor_denoising_fastni
        }
    }


@get:StringRes
val ImageProcessor.Filter.stringRes: Int
    get() {
        return when(this) {
            ImageProcessor.Filter.BOX -> R.string.document_editor_filter_box
            ImageProcessor.Filter.GAUSSIAN -> R.string.document_editor_filter_gaussian
            ImageProcessor.Filter.MEDIAN -> R.string.document_editor_filter_median
            ImageProcessor.Filter.BILATERAL -> R.string.document_editor_filter_bilateral
        }
    }


@get:StringRes
val ImageProcessor.Contrast.stringRes: Int
    get() {
        return when(this) {
            ImageProcessor.Contrast.MULT -> R.string.document_editor_contrast_mult
            ImageProcessor.Contrast.HISTOGRAM -> R.string.document_editor_contrast_histogram
            ImageProcessor.Contrast.CLAHE -> R.string.document_editor_contrast_clahe
        }
    }

