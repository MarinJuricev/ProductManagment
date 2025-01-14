package parking.reservation

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.raise.either
import arrow.core.raise.ensure
import auth.Authentication
import core.utils.IsEmailValid
import core.utils.isFutureDate
import email.SendReservationUpdateEmail
import parking.reservation.model.ParkingRequest
import parking.reservation.model.ParkingRequest.Request
import parking.reservation.model.ParkingRequest.Reservation
import parking.reservation.model.ParkingReservationError
import parking.reservation.model.ParkingReservationError.DuplicateReservation
import parking.reservation.model.ParkingReservationError.InvalidEmailFormat
import parking.reservation.model.ParkingReservationError.OnlyFutureDateReservationsAllowed
import parking.reservation.model.ParkingReservationError.Unauthorized
import parking.reservation.model.toTemplateStatus
import parking.reservation.repository.ParkingReservationRepository

class ManageParkingRequest(
    private val requestParkingPlace: RequestParkingPlace,
    private val makeReservation: MakeReservation,
    private val authentication: Authentication,
    private val isEmailValid: IsEmailValid,
    private val repository: ParkingReservationRepository,
    private val sendReservationUpdateEmail: SendReservationUpdateEmail,
) {
    suspend operator fun invoke(request: ParkingRequest): Either<ParkingReservationError, Unit> = either {
        ensure(authentication.isUserLoggedIn()) { Unauthorized }
        ensure(isEmailValid(request.email)) { InvalidEmailFormat }
        ensure(request.date.isFutureDate()) { OnlyFutureDateReservationsAllowed }

        val reservationsForUser = repository.getReservationsForUser(
            email = request.email,
            startDate = request.date,
            endDate = request.date,
        ).getOrElse {
            raise(ParkingReservationError.UnknownError)
        }

        ensure(reservationsForUser.isEmpty()) { DuplicateReservation }
        when (request) {
            is Request -> requestParkingPlace(request = request)
            is Reservation -> makeReservation(reservation = request)
        }.onRight {
            val reservation = request.toDto().toDomain()

            sendReservationUpdateEmail(
                reservation = reservation,
                templateStatus = reservation.status.toTemplateStatus(),
            )
        }.bind()
    }
}
