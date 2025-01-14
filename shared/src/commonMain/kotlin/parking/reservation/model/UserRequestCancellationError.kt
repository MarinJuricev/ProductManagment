package parking.reservation.model

import core.model.MarinJuricevError

sealed class UserRequestCancellationError : MarinJuricevError() {
    data object Unauthorized : UserRequestCancellationError()
    data object GarageLevelDataFetchingFailed : UserRequestCancellationError()
    data object UserRequestsFetchingFailed : UserRequestCancellationError()
}
