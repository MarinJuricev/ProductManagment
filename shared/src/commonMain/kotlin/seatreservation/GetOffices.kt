package seatreservation

import arrow.core.Either
import arrow.core.raise.either
import seatreservation.model.Office
import seatreservation.model.OfficeError
import seatreservation.repository.OfficeRepository

class GetOffices(
    private val officeRepository: OfficeRepository,
) {
    suspend operator fun invoke(): Either<OfficeError, List<Office>> = either {
        officeRepository.getOffices()
            .mapLeft { OfficeError.FetchFailed }
            .bind()
    }
}
