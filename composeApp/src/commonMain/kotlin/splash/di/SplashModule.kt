package splash.di

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import splash.SplashScreenViewModel

val splashModule = module {
    factoryOf(::SplashScreenViewModel)
}
