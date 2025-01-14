package core.di

import core.model.Environment
import org.koin.dsl.module

fun environmentModule(environment: Environment) = module {
    single { environment }
}
