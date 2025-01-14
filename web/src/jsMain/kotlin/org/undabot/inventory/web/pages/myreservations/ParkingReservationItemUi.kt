package org.product.inventory.web.pages.myreservations

import com.varabyte.kobweb.compose.ui.graphics.Color
import org.product.inventory.shared.MR
import org.product.inventory.web.components.DateInputValue
import org.product.inventory.web.components.ParkingOption
import org.product.inventory.web.components.ProfileInfo
import org.product.inventory.web.core.Dictionary
import org.product.inventory.web.core.StringRes
import org.product.inventory.web.di.DiJs
import org.product.inventory.web.toCssColor
import parking.reservation.model.GarageLevel
import parking.reservation.model.ParkingCoordinate
import parking.reservation.model.ParkingReservationStatus
import parking.reservation.model.ParkingSpot

data class ParkingReservationItemUi(
    val itemId: String,
    val submittedDateLabel: String,
    val submittedDate: String,
    val requestDateLabel: String,
    val requestedDateDateInput: DateInputValue,
    val requestedDate: String,
    val noteLabel: String,
    val note: String,
    val status: ParkingReservationStatusUi,
    val requestByLabel: String,
    val additionalNotesLabel: String,
    val approveOrRejectNoteLabel: String,
    val approveOrRejectNote: String,
    val dateLabel: String,
    val profileInfo: ProfileInfo,
    val cancellationStatus: CancellationStatus,
    val cancelRequestText: String,
    val confirmCancellationMessage: String,
    val confirmCancellationPositiveText: String,
    val confirmCancellationNegativeText: String,
    val garageLevelLabel: String,
    val parkingSpotLabel: String,
    val isActiveReservation: Boolean,
)

enum class CancellationStatus {
    Initial,
    Confirmation,
}

sealed class ParkingReservationStatusUi(
    open val text: String,
    open val color: Color,
) {

    data class Submitted(
        override val text: String,
        override val color: Color.Rgb,
    ) : ParkingReservationStatusUi(text, color)

    data class Approved(
        override val text: String,
        override val color: Color.Rgb,
        val adminNote: String,
        val garageLevel: ParkingOption,
        val parkingSpot: ParkingOption,
    ) : ParkingReservationStatusUi(text, color)

    data class Rejected(
        override val text: String,
        override val color: Color.Rgb,
        val adminNote: String,
    ) : ParkingReservationStatusUi(text, color)

    data class Canceled(
        override val text: String,
        override val color: Color.Rgb,
    ) : ParkingReservationStatusUi(text, color)
}

fun ParkingReservationStatusUi.toParkingReservationStatus() = when (this) {
    is ParkingReservationStatusUi.Submitted -> ParkingReservationStatus.Submitted
    is ParkingReservationStatusUi.Canceled -> ParkingReservationStatus.Canceled
    is ParkingReservationStatusUi.Rejected -> ParkingReservationStatus.Declined(adminNote)
    is ParkingReservationStatusUi.Approved -> ParkingReservationStatus.Approved(
        adminNote = adminNote,
        parkingCoordinate = ParkingCoordinate(
            level = GarageLevel(
                id = garageLevel.id,
                title = garageLevel.value,
            ),
            spot = ParkingSpot(
                id = parkingSpot.id,
                title = parkingSpot.value,
            ),
        ),
    )
}

fun ParkingReservationStatus.toParkingReservationStatusUi(
    dictionary: Dictionary = DiJs.get(),
) = when (this) {
    is ParkingReservationStatus.Approved -> ParkingReservationStatusUi.Approved(
        text = dictionary.get(StringRes.parkingReservationStatusApprovedLabel),
        color = MR.colors.approvedStatus.toCssColor(),
        adminNote = adminNote,
        garageLevel = ParkingOption(
            id = parkingCoordinate.level.id,
            value = parkingCoordinate.level.title,
        ),
        parkingSpot = ParkingOption(
            id = parkingCoordinate.spot.id,
            value = parkingCoordinate.spot.title,
        ),
    )

    is ParkingReservationStatus.Canceled -> ParkingReservationStatusUi.Canceled(
        text = dictionary.get(StringRes.parkingReservationStatusCanceledLabel),
        color = MR.colors.textLight.toCssColor(),
    )

    is ParkingReservationStatus.Declined -> ParkingReservationStatusUi.Rejected(
        text = dictionary.get(StringRes.parkingReservationStatusRejectedLabel),
        color = MR.colors.rejectedStatus.toCssColor(),
        adminNote = adminNote,
    )

    ParkingReservationStatus.Submitted -> ParkingReservationStatusUi.Submitted(
        text = dictionary.get(StringRes.parkingReservationStatusSubmittedLabel),
        color = MR.colors.submittedStatus.toCssColor(),
    )
}
