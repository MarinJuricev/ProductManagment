package org.product.inventory.web.di

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.product.inventory.web.pages.seatreservation.SeatReservationUiMapper
import org.product.inventory.web.pages.seatreservation.SeatReservationViewModel
import org.product.inventory.web.pages.seatreservationtimeline.ReservableDateUiItemMapper
import org.product.inventory.web.pages.seatreservationtimeline.SeatReservationTimelineUiStateMapper
import org.product.inventory.web.pages.seatreservationtimeline.SeatReservationTimelineViewModel
import org.product.inventory.web.pages.seatreservationtmanagement.SeatReservationManagementUiMapper
import org.product.inventory.web.pages.seatreservationtmanagement.SeatReservationManagementViewModel

private val seatReservationManagementModule = module {
    factoryOf(::SeatReservationManagementViewModel)
    factoryOf(::SeatReservationManagementUiMapper)
}

private val seatReservationTimelineModule = module {
    factoryOf(::SeatReservationTimelineViewModel)
    factoryOf(::SeatReservationTimelineUiStateMapper)
    factoryOf(::ReservableDateUiItemMapper)
}

val seatReservationModule = module {
    factoryOf(::SeatReservationViewModel)
    factoryOf(::SeatReservationUiMapper)

    includes(seatReservationManagementModule)
    includes(seatReservationTimelineModule)
}
