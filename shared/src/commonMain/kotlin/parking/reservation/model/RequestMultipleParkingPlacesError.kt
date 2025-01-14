package parking.reservation.model

import core.model.MarinJuricevError

sealed class RequestMultipleParkingPlacesError : MarinJuricevError() {
    data object TooManyParkingRequests : RequestMultipleParkingPlacesError()
}
