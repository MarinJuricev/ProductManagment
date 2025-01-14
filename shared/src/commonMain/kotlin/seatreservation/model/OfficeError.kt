package seatreservation.model

import core.model.MarinJuricevError

sealed class OfficeError : MarinJuricevError() {
    data object OfficeNotFound : OfficeError()
    data object FetchFailed : OfficeError()
    data object InvalidSeatNumber : OfficeError()
    data object OfficeAlreadyExists : OfficeError()
}
