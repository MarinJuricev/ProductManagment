package parking.reservation.model

import core.model.MarinJuricevError

sealed class GarageLevelError : MarinJuricevError() {
    data object Unauthorized : GarageLevelError()
    data object ParkingReservationStorageError : GarageLevelError()
    data object StorageError : GarageLevelError()
    data object DuplicatedLevelName : GarageLevelError()
    data object DuplicatedSpotName : GarageLevelError()
    data object InputFieldTooLong : GarageLevelError()
}
