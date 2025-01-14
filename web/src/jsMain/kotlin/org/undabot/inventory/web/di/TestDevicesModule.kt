package org.product.inventory.web.di

import org.koin.dsl.module
import org.product.inventory.web.pages.testdevices.TestDevicesViewModel

val testDevicesModule = module {
    factory { params ->
        TestDevicesViewModel(
            scope = params.get(),
        )
    }
}
