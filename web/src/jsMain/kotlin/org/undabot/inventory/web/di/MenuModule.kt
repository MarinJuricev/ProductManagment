package org.product.inventory.web.di

import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.product.inventory.web.pages.menu.MenuItemsUiMapper
import org.product.inventory.web.pages.menu.MenuUiMapper
import org.product.inventory.web.pages.menu.MenuViewModel

val menuModule = module {
    factoryOf(::MenuUiMapper)
    factoryOf(::MenuItemsUiMapper)
    singleOf(::MenuViewModel)
}
