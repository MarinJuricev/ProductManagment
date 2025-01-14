package seatreservation

import arrow.core.Either
import arrow.core.raise.either
import seatreservation.model.Office
import seatreservation.model.OfficeError
import seatreservation.repository.OfficeRepository

class UpdateOffice(
    private val officeRepository: OfficeRepository,
) {
    suspend operator fun invoke(
        office: Office,
    ): Either<OfficeError, Unit> = either {
        officeRepository.saveOffice(office)
    }
}
