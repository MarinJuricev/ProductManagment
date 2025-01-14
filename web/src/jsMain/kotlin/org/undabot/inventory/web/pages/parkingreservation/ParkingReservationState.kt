package org.product.inventory.web.pages.parkingreservation

import org.product.inventory.web.components.BreadcrumbItem
import org.product.inventory.web.pages.Routes

data class ParkingReservationState(
    val breadcrumbItems: List<BreadcrumbItem> = emptyList(),
    val title: String = "",
    val parkingReservationItems: List<ParkingReservationItemUi> = emptyList(),
    val routeToNavigate: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: ErrorMessage? = null,
)

data class ErrorMessage(
    val title: String,
    val message: String,
    val retryText: String,
)

data class ParkingReservationItemUi(
    val icon: String,
    val title: String,
    val text: String,
    val type: ReservationItemType,
)

enum class ReservationItemType(val route: String) {
    NewRequest(Routes.newRequest),
    UserRequests(Routes.userRequests),
    CrewManagement(Routes.crewManagement),
    MyReservations(Routes.myReservations),
    EmailTemplates(Routes.emailTemplates),
    SlotsManagement(Routes.slotsManagement),
}
