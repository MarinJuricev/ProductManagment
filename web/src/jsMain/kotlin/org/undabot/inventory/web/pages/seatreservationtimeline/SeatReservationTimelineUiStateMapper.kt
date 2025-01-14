package org.product.inventory.web.pages.seatreservationtimeline

import org.product.inventory.web.components.AlertMessage
import org.product.inventory.web.components.BreadcrumbItem
import org.product.inventory.web.core.Dictionary
import org.product.inventory.web.core.StringRes
import org.product.inventory.web.pages.Routes
import seatreservation.model.Office
import seatreservation.model.ReservableDate
import user.model.InventoryAppUser

class SeatReservationTimelineUiStateMapper(
    private val dictionary: Dictionary,
    private val reservableDateUiItemMapper: ReservableDateUiItemMapper,
) {

    private data class StateStaticData(
        val breadcrumbItems: List<BreadcrumbItem>,
        val title: String,
    )

    private val stateStaticData by lazy {
        StateStaticData(
            breadcrumbItems = buildBreadcrumbItems(),
            title = dictionary.get(StringRes.seatReservationTimelinePageTitle),
        )
    }

    fun toUiState(
        isLoading: Boolean,
        alertMessage: AlertMessage?,
        selectedOffice: Office?,
        availableOffices: List<Office>,
        reservableDates: List<ReservableDate>,
        currentUser: InventoryAppUser,
    ) = SeatReservationTimelineUiState(
        breadcrumbItems = stateStaticData.breadcrumbItems,
        title = stateStaticData.title,
        isLoading = isLoading,
        alertMessage = alertMessage,
        selectedOffice = selectedOffice,
        availableOffices = availableOffices,
        reservableDateUiItems = reservableDates.map {
            reservableDateUiItemMapper.buildReservableDateItemUi(it, currentUser)
        },
    )

    private fun buildBreadcrumbItems() = listOf(
        BreadcrumbItem(
            text = dictionary.get(StringRes.seatReservationTimelinePath1),
            route = Routes.seatReservation,
        ),
        BreadcrumbItem(
            text = dictionary.get(StringRes.seatReservationTimelinePath2),
            route = Routes.seatReservationTimeline,
        ),
    )
}
