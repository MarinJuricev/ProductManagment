package org.product.inventory.web.pages.usersrequests

import org.product.inventory.web.components.AlertMessage
import org.product.inventory.web.components.BreadcrumbItem
import org.product.inventory.web.components.DateInputValue
import org.product.inventory.web.pages.myreservations.ParkingReservationStatusUi

data class UserRequestsState(
    val breadcrumbItems: List<BreadcrumbItem> = emptyList(),
    val title: String = "",
    val requests: List<UserRequestItemUi> = emptyList(),
    val fromDateLabel: String = "",
    val toDateLabel: String = "",
    val fromDate: DateInputValue = DateInputValue(),
    val toDate: DateInputValue = DateInputValue(),
    val userEmailFilter: String = "",
    val userEmailFilterPlaceholder: String = "",
    val userEmailFilterLabel: String = "",
    val statusFilterEntries: List<ParkingReservationStatusUi?> = emptyList(),
    val statusFilter: ParkingReservationStatusUi? = null,
    val statusFilterAllOptionText: String = "",
    val emptyRequestsMessage: String = "",
    val closeDialog: Boolean = false,
    val isLoading: Boolean = false,
    val alertMessage: AlertMessage? = null,
    val routeToNavigate: String? = null,
)
