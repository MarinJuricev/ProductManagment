package org.product.inventory.web.di

import com.varabyte.kobweb.core.init.InitKobwebContext
import org.koin.core.module.dsl.factoryOf
import org.koin.core.scope.Scope
import org.koin.dsl.bind
import org.koin.dsl.module
import org.product.inventory.web.core.Dictionary
import org.product.inventory.web.core.DictionaryImpl
import org.product.inventory.web.core.Navigator
import org.product.inventory.web.core.NavigatorImpl

val coreModule = module {
    factoryOf(::DictionaryImpl) bind Dictionary::class
    factory { kobwebContext().router }
    factoryOf(::NavigatorImpl) bind Navigator::class
}

fun Scope.kobwebContext(): InitKobwebContext = get()
