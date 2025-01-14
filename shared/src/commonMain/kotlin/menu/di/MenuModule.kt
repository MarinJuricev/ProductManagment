package menu.di

import menu.GetMenuOptions
import org.koin.dsl.module

fun menuModule() = module {
    factory { GetMenuOptions(get()) }
}
