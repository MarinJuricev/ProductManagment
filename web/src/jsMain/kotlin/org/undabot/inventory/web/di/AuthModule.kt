package org.product.inventory.web.di

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.product.inventory.web.pages.auth.AuthUiMapper
import org.product.inventory.web.pages.auth.AuthViewModel

val authModule = module {
    factoryOf(::AuthUiMapper)
    factoryOf(::AuthViewModel)
}
