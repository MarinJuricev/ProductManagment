package seatreservation

import kotlinx.coroutines.flow.Flow
import seatreservation.model.Office
import seatreservation.repository.OfficeRepository

class ObserveOffice(
    private val officeRepository: OfficeRepository,
) {
    operator fun invoke(
        id: String,
    ): Flow<Office> = officeRepository.observeOffice(id = id)
}
