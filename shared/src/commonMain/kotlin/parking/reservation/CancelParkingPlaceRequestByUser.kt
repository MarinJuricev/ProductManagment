package parking.reservation

import arrow.core.Either
import arrow.core.raise.either
import email.SendReservationUpdateEmail
import parking.reservation.model.ParkingReservation
import parking.reservation.model.ParkingReservationError
import parking.templates.model.TemplateStatus.Canceled

class CancelParkingPlaceRequestByUser(
    private val cancelParkingPlaceRequest: CancelParkingPlaceRequest,
    private val sendReservationUpdateEmail: SendReservationUpdateEmail,
) {
    suspend operator fun invoke(parkingReservation: ParkingReservation): Either<ParkingReservationError, Unit> =
        either {
            cancelParkingPlaceRequest(parkingReservation = parkingReservation)
                .onRight {
                    sendReservationUpdateEmail(
                        parkingReservation,
                        templateStatus = Canceled,
                    )
                }
                .bind()
        }
}
