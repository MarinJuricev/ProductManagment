package org.product.inventory.web.pages.seatreservation

import arrow.core.Either
import org.product.inventory.web.components.BreadcrumbItem
import org.product.inventory.web.core.Dictionary
import org.product.inventory.web.core.ImageRes
import org.product.inventory.web.core.StringRes
import org.product.inventory.web.pages.Routes
import seatreservation.dashboard.SeatReservationDashboardType
import seatreservation.dashboard.model.SearReservationDashboardError
import seatreservation.dashboard.model.SeatReservationOption

class SeatReservationUiMapper(
    private val dictionary: Dictionary,
) {

    private val breadcrumbItems = listOf(
        BreadcrumbItem(
            text = dictionary.get(StringRes.seatReservationPath1),
            route = Routes.home,
        ),
        BreadcrumbItem(
            text = dictionary.get(StringRes.seatReservationPath2),
            route = Routes.seatReservation,
        ),
    )

    fun toUiState(
        routeToNavigate: String?,
        dashboardTypeOrError: Either<SearReservationDashboardError, SeatReservationDashboardType>,
    ): SeatReservationUiState = SeatReservationUiState(
        title = dictionary.get(StringRes.seatReservationTitle),
        breadcrumbItems = breadcrumbItems,
        routeToNavigate = routeToNavigate,
        seatReservationItems = buildReservationItems(dashboardTypeOrError),
    )

    // we don't need to check other types since if type is user, next screen should be displayed
    private fun buildReservationItems(
        dashboardTypeOrError: Either<SearReservationDashboardError, SeatReservationDashboardType>,
    ) = dashboardTypeOrError.getOrNull()
        ?.let { it as? SeatReservationDashboardType.Administrator }
        ?.options
        ?.map { it.toSeatReservationItemUi() }
        .orEmpty()

    private fun SeatReservationOption.toSeatReservationItemUi() = when (this) {
        SeatReservationOption.SeatManagement -> SeatReservationItemUi(
            icon = ImageRes.seatReservationSeatManagement,
            title = dictionary.get(StringRes.seatReservationSeatManagementTitle),
            text = dictionary.get(StringRes.seatReservationSeatManagementDescription),
            type = SeatReservationOption.SeatManagement,
        )
        SeatReservationOption.Timeline -> SeatReservationItemUi(
            icon = ImageRes.seatReservationTimeline,
            title = dictionary.get(StringRes.seatReservationTimelineTitle),
            text = dictionary.get(StringRes.seatReservationTimelineDescription),
            type = SeatReservationOption.Timeline,
        )
    }
}
