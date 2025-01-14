package core.di

import core.model.Environment
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(
    environment: Environment,
    consumerDeclaration: KoinAppDeclaration = {},
) = startKoin {
    consumerDeclaration()
    modules(
        environmentModule(environment),
        platformModule(),
        sharedModule(),
    )
}

fun initKoin(environment: Environment) = initKoin(environment) {}
