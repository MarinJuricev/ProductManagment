package org.product.inventory.web.di

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.product.inventory.web.core.AlertMessageMapper
import org.product.inventory.web.pages.crewmanagement.CrewManagementUiMapper
import org.product.inventory.web.pages.crewmanagement.CrewManagementViewModel
import org.product.inventory.web.pages.emailtemplates.EmailTemplatesUiMapper
import org.product.inventory.web.pages.emailtemplates.EmailTemplatesViewModel
import org.product.inventory.web.pages.myreservations.MyReservationsUiMapper
import org.product.inventory.web.pages.myreservations.MyReservationsViewModel
import org.product.inventory.web.pages.newrequest.MultiDateSelectionStateMapper
import org.product.inventory.web.pages.newrequest.NewRequestStaticDataMapper
import org.product.inventory.web.pages.newrequest.NewRequestUiMapper
import org.product.inventory.web.pages.newrequest.NewRequestViewModel
import org.product.inventory.web.pages.newrequest.ParkingSelectionDataMapper
import org.product.inventory.web.pages.parkingreservation.ParkingReservationUiMapper
import org.product.inventory.web.pages.parkingreservation.ParkingReservationViewModel
import org.product.inventory.web.pages.slotsmanagement.SlotsManagementUiMapper
import org.product.inventory.web.pages.slotsmanagement.SlotsManagementViewModel
import org.product.inventory.web.pages.usersrequests.UserRequestsUiMapper
import org.product.inventory.web.pages.usersrequests.UserRequestsViewModel

private val newRequestModule = module {
    factoryOf(::NewRequestUiMapper)
    factoryOf(::NewRequestStaticDataMapper)
    factoryOf(::ParkingSelectionDataMapper)
    factoryOf(::MultiDateSelectionStateMapper)
    factoryOf(::AlertMessageMapper)
    factoryOf(::NewRequestViewModel)
}

private val myReservationsModule = module {
    factoryOf(::MyReservationsUiMapper)
    factoryOf(::MyReservationsViewModel)
}

private val userRequestsModule = module {
    factoryOf(::UserRequestsUiMapper)
    factoryOf(::UserRequestsViewModel)
}

private val crewManagementModule = module {
    factoryOf(::CrewManagementUiMapper)
    factoryOf(::CrewManagementViewModel)
}

private val slotsManagementModule = module {
    factoryOf(::SlotsManagementUiMapper)
    factoryOf(::SlotsManagementViewModel)
}

private val emailTemplatesModule = module {
    factoryOf(::EmailTemplatesUiMapper)
    factoryOf(::EmailTemplatesViewModel)
}

val parkingReservationModule = module {
    factoryOf(::ParkingReservationUiMapper)
    factoryOf(::ParkingReservationViewModel)

    includes(
        newRequestModule,
        myReservationsModule,
        userRequestsModule,
        crewManagementModule,
        slotsManagementModule,
        emailTemplatesModule,
    )
}
