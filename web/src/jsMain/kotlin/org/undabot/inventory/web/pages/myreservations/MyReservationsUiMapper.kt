package org.product.inventory.web.pages.myreservations

import org.product.inventory.web.components.BreadcrumbItem
import org.product.inventory.web.components.toDateInputValue
import org.product.inventory.web.components.toProfileInfo
import org.product.inventory.web.core.DateRange
import org.product.inventory.web.core.Dictionary
import org.product.inventory.web.core.StringRes
import org.product.inventory.web.datetime.formatted
import org.product.inventory.web.datetime.isTodayOrAfter
import org.product.inventory.web.datetime.toLocalDate
import org.product.inventory.web.pages.Routes
import parking.reservation.model.ParkingReservation
import user.model.InventoryAppUser

class MyReservationsUiMapper(
    private val dictionary: Dictionary,
) {

    fun buildUiState(
        reservations: List<ParkingReservation>,
        dateRange: DateRange,
        user: InventoryAppUser,
        isLoading: Boolean,
        routeToNavigate: String?,
    ) = MyReservationsState(
        breadcrumbItems = buildBreadcrumbItems(),
        title = dictionary.get(StringRes.myReservationsTitle),
        fromDateLabel = dictionary.get(StringRes.parkingReservationItemFrom),
        toDateLabel = dictionary.get(StringRes.parkingReservationItemTo),
        reservations = reservations.map { item -> item.toParkingReservationUi(user) },
        emptyReservationsMessage = dictionary.get(StringRes.myParkingReservationEmpty),
        fromDate = dateRange.fromDate.toDateInputValue(),
        toDate = dateRange.toDate.toDateInputValue(),
        isLoading = isLoading,
        routeToNavigate = routeToNavigate,
    )

    private fun buildBreadcrumbItems() = listOf(
        BreadcrumbItem(
            text = dictionary.get(StringRes.myReservationsPath1),
            route = Routes.home,
        ),
        BreadcrumbItem(
            text = dictionary.get(StringRes.myReservationsPath2),
            route = Routes.parkingReservation,
            isNavigable = true,
        ),
        BreadcrumbItem(
            text = dictionary.get(StringRes.myReservationsPath3),
            route = Routes.myReservations,
        ),
    )

    private fun ParkingReservation.toParkingReservationUi(
        user: InventoryAppUser,
    ) = ParkingReservationItemUi(
        itemId = id,
        submittedDate = createdAt.toLocalDate().formatted(),
        requestedDate = date.toLocalDate().formatted(),
        requestedDateDateInput = date.toDateInputValue(),
        note = note,
        status = status.toParkingReservationStatusUi(),
        submittedDateLabel = dictionary.get(StringRes.myParkingReservationSubmitted),
        requestDateLabel = dictionary.get(StringRes.parkingReservationItemRequestedDate),
        noteLabel = dictionary.get(StringRes.parkingReservationItemNote),
        requestByLabel = dictionary.get(StringRes.parkingReservationItemRequestedBy),
        additionalNotesLabel = dictionary.get(StringRes.parkingReservationNewRequestAdditionalNotes),
        dateLabel = dictionary.get(StringRes.parkingReservationItemDate),
        profileInfo = user.toProfileInfo(dictionary),
        approveOrRejectNoteLabel = buildApproveOrRejectNoteLabel(status.toParkingReservationStatusUi()),
        approveOrRejectNote = buildApproveOrRejectNote(status.toParkingReservationStatusUi()),
        cancellationStatus = CancellationStatus.Initial,
        cancelRequestText = dictionary.get(StringRes.myParkingReservationCancelRequest),
        confirmCancellationMessage = dictionary.get(
            StringRes.myReservationsCancelReservationDialogMessage,
            date.toLocalDate().formatted(),
        ),
        confirmCancellationPositiveText = dictionary.get(StringRes.myReservationsCancelReservationDialogPositiveText),
        confirmCancellationNegativeText = dictionary.get(StringRes.myReservationsCancelReservationDialogNegativeText),
        garageLevelLabel = dictionary.get(StringRes.userRequestsGarageLevelLabel),
        parkingSpotLabel = dictionary.get(StringRes.userRequestsParkingSpotLabel),
        isActiveReservation = date.toLocalDate().isTodayOrAfter(),
    )

    private fun buildApproveOrRejectNoteLabel(
        status: ParkingReservationStatusUi,
    ) = when (status) {
        is ParkingReservationStatusUi.Rejected -> dictionary.get(StringRes.parkingReservationItemRejectReason)
        else -> dictionary.get(StringRes.parkingReservationItemApproveNote)
    }

    private fun buildApproveOrRejectNote(
        status: ParkingReservationStatusUi,
    ) = when (status) {
        is ParkingReservationStatusUi.Approved -> status.adminNote
        is ParkingReservationStatusUi.Rejected -> status.adminNote
        else -> ""
    }
}
