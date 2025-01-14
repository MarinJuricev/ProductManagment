package parking.usersRequests.model

import core.model.MarinJuricevError

sealed class UserRequestsError : MarinJuricevError() {
    data object UnknownError : UserRequestsError()
    data object Unauthorized : UserRequestsError()
}
