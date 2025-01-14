package org.product.inventory.web.pages.parkingreservation

import arrow.core.Either
import org.product.inventory.web.components.BreadcrumbItem
import org.product.inventory.web.core.Dictionary
import org.product.inventory.web.core.ImageRes
import org.product.inventory.web.core.StringRes
import org.product.inventory.web.pages.Routes
import parking.dashboard.model.ParkingDashboardError
import parking.dashboard.model.ParkingDashboardOption

class ParkingReservationUiMapper(
    private val dictionary: Dictionary,
) {

    fun buildUiState(
        isLoading: Boolean,
        routeToNavigate: String?,
        dashboardOptions: Either<ParkingDashboardError, List<ParkingDashboardOption>>,
    ) = ParkingReservationState(
        breadcrumbItems = buildBreadcrumbItems(),
        title = dictionary.get(StringRes.parkingReservationTitle),
        routeToNavigate = routeToNavigate,
        isLoading = isLoading,
        parkingReservationItems = when (dashboardOptions) {
            is Either.Left -> emptyList()
            is Either.Right -> dashboardOptions.value.map { it.toReservationItem() }
        },
        errorMessage = when (dashboardOptions) {
            is Either.Left -> ErrorMessage(
                title = dictionary.get(StringRes.parkingReservationRetryScreenTitle),
                message = dictionary.get(StringRes.parkingReservationRetryScreenDescription),
                retryText = dictionary.get(StringRes.parkingReservationRetryScreenButtonText),
            )
            is Either.Right -> null
        },
    )

    private fun buildBreadcrumbItems() = listOf(
        BreadcrumbItem(
            text = dictionary.get(StringRes.parkingReservationPath1),
            route = Routes.home,
        ),
        BreadcrumbItem(
            text = dictionary.get(StringRes.parkingReservationPath2),
            route = Routes.parkingReservation,
        ),
    )

    private fun ParkingDashboardOption.toReservationItem() = when (this) {
        ParkingDashboardOption.EmailTemplatesOption -> ParkingReservationItemUi(
            icon = ImageRes.emailTemplates,
            title = dictionary.get(StringRes.reservationItemEmailTemplatesTitle),
            text = dictionary.get(StringRes.reservationItemEmailTemplatesDescription),
            type = ReservationItemType.EmailTemplates,
        )

        ParkingDashboardOption.MyReservationsOption -> ParkingReservationItemUi(
            icon = ImageRes.myReservations,
            title = dictionary.get(StringRes.reservationItemMyReservationsTitle),
            text = dictionary.get(StringRes.reservationItemMyReservationsDescription),
            type = ReservationItemType.MyReservations,
        )

        ParkingDashboardOption.NewRequestOption -> ParkingReservationItemUi(
            icon = ImageRes.newRequest,
            title = dictionary.get(StringRes.reservationItemNewRequestTitle),
            text = dictionary.get(StringRes.reservationItemNewRequestDescription),
            type = ReservationItemType.NewRequest,
        )

        ParkingDashboardOption.SlotsManagementOption -> ParkingReservationItemUi(
            icon = ImageRes.slotsManagement,
            title = dictionary.get(StringRes.reservationItemSlotsManagementTitle),
            text = dictionary.get(StringRes.reservationItemSlotsManagementDescription),
            type = ReservationItemType.SlotsManagement,
        )

        ParkingDashboardOption.UserRequestsOption -> ParkingReservationItemUi(
            icon = ImageRes.usersRequests,
            title = dictionary.get(StringRes.reservationItemUserRequestsTitle),
            text = dictionary.get(StringRes.reservationItemUserRequestsDescription),
            type = ReservationItemType.UserRequests,
        )
    }
}
