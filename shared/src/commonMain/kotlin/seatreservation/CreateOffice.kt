package seatreservation

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.raise.either
import arrow.core.raise.ensure
import core.utils.UUIDProvider
import seatreservation.model.Office
import seatreservation.model.OfficeError
import seatreservation.model.OfficeError.OfficeAlreadyExists
import seatreservation.repository.OfficeRepository

class CreateOffice(
    private val officeRepository: OfficeRepository,
    private val uuidProvider: UUIDProvider,
) {
    suspend operator fun invoke(
        name: String,
        seats: String,
    ): Either<OfficeError, Unit> = either {
        val seatsNumber = seats.toIntOrNull()
        val trimmedName = name.trim()
        ensure(seatsNumber != null) { OfficeError.InvalidSeatNumber }
        ensure(seatsNumber > 0) { OfficeError.InvalidSeatNumber }
        when (val offices = officeRepository.getOffices()) {
            is Left -> OfficeError.FetchFailed
            is Right -> {
                ensure(offices.value.none { office -> office.title == trimmedName }) { OfficeAlreadyExists }
                officeRepository.saveOffice(
                    Office(
                        id = uuidProvider.generateUUID(),
                        title = trimmedName,
                        numberOfSeats = seatsNumber,
                    ),
                )
            }
        }
    }
}
