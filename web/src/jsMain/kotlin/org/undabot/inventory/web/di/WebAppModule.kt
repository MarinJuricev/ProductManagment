package org.product.inventory.web.di

import org.koin.core.module.Module
import org.koin.dsl.module

fun webAppModule(
    additionalModules: List<Module> = emptyList(),
) = module {
    includes(
        listOf(
            coreModule,
            authModule,
            parkingReservationModule,
            seatReservationModule,
            testDevicesModule,
            menuModule,
        ) + additionalModules,
    )
}
