package github.com.st235.documentscanner.dependencies

import android.content.Context
import github.com.st235.documentscanner.domain.CropInteractor
import github.com.st235.documentscanner.domain.DocumentsCompositionInteractor
import github.com.st235.documentscanner.utils.documents.DocumentScanner
import github.com.st235.documentscanner.presentation.screens.composer.DocumentsComposerViewModel
import github.com.st235.documentscanner.utils.BitmapWriter
import github.com.st235.documentscanner.utils.LocalUriLoader
import github.com.st235.documentscanner.utils.TempUriProvider
import github.com.st235.documentscanner.utils.gallery.GallerySaver
import github.com.st235.documentscanner.utils.gallery.GalleryScanner
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private val viewModelsModule = module {

    viewModel { DocumentsComposerViewModel(get(), get()) }

}

private val domainModule = module {

    factory { CropInteractor(get(), get()) }

    factory { DocumentsCompositionInteractor(get(), get(), get(), get()) }

}

private val utilsModule = module {

    single { get<Context>().contentResolver }

    single { BitmapWriter(get()) }

    single { DocumentScanner() }

    factory { LocalUriLoader(get()) }

    factory { GallerySaver(get()) }

    factory { GalleryScanner(get()) }

    factory { TempUriProvider(androidContext()) }

}

val appModules = viewModelsModule + domainModule + utilsModule
