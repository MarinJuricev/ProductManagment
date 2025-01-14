package seatreservation

import kotlinx.coroutines.flow.Flow
import seatreservation.model.Office
import seatreservation.repository.OfficeRepository

class ObserveOffices(
    private val officeRepository: OfficeRepository,
) {
    operator fun invoke(): Flow<List<Office>> = officeRepository.observeOffices()
}
