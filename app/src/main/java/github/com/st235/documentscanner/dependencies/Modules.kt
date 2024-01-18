package github.com.st235.documentscanner.dependencies

import github.com.st235.documentscanner.domain.DocumentScanner
import github.com.st235.documentscanner.presentation.screens.cropper.DocumentCropperScreen
import github.com.st235.documentscanner.presentation.screens.cropper.DocumentCropperViewModel
import github.com.st235.documentscanner.presentation.utils.UriLoader
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private val viewModelsModule = module {

    viewModel { DocumentCropperViewModel(get(), get()) }

}

private val domainModule = module {

    single { DocumentScanner() }

}

private val utilsModule = module {

    factory { UriLoader(androidContext()) }

}

val appModules = viewModelsModule + domainModule + utilsModule
