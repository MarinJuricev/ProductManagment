package parking.reservation

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import core.model.DatabaseOperationError
import core.model.NetworkError
import core.utils.isNotLateReservation
import parking.reservation.model.ParkingRequest.Request
import parking.reservation.model.ParkingReservationError
import parking.reservation.model.ParkingReservationError.ErrorMessage
import parking.reservation.repository.ParkingReservationRepository

class RequestParkingPlace(
    private val repository: ParkingReservationRepository,
) {
    suspend operator fun invoke(request: Request): Either<ParkingReservationError, Unit> =
        either {
            ensure(request.date.isNotLateReservation()) { ParkingReservationError.LateReservation }

            repository.createRequest(request)
                .mapLeft {
                    when (it) {
                        is DatabaseOperationError.UnknownError -> ErrorMessage(it.message)
                        is NetworkError.BackendError -> ErrorMessage(it.errorMessage.orEmpty())
                        is NetworkError.UnknownNetworkError -> ParkingReservationError.UnknownError
                    }
                }.bind()
        }
}
