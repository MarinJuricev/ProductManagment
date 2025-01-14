package parking.reservation.model

import kotlinx.serialization.Serializable
import parking.templates.model.TemplateStatus

@Serializable(with = ParkingReservationStatusSerializer::class)
sealed class ParkingReservationStatus {
    @Serializable
    data object Submitted : ParkingReservationStatus()

    @Serializable
    data class Approved(val adminNote: String, val parkingCoordinate: ParkingCoordinate) : ParkingReservationStatus()

    @Serializable
    data class Declined(val adminNote: String) : ParkingReservationStatus()

    @Serializable
    data object Canceled : ParkingReservationStatus()
}

fun ParkingReservationStatus.toTemplateStatus(): TemplateStatus = when (this) {
    is ParkingReservationStatus.Approved -> TemplateStatus.Approved
    is ParkingReservationStatus.Declined -> TemplateStatus.Declined
    ParkingReservationStatus.Canceled -> TemplateStatus.Canceled
    ParkingReservationStatus.Submitted -> TemplateStatus.Submitted
}
