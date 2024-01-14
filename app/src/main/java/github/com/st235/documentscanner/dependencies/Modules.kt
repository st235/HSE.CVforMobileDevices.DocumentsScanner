package github.com.st235.documentscanner.dependencies

import github.com.st235.documentscanner.utils.OpenCVHelper
import org.koin.dsl.module

private val utilsModule = module {

    single { OpenCVHelper() }

}

val appModules = utilsModule
