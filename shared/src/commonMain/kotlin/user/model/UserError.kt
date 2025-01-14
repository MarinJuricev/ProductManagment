package user.model

import core.model.MarinJuricevError

sealed class UserError : MarinJuricevError() {
    data object UnknownError : UserError()

    data class StoreUserError(val message: String) : UserError()

    data object UserNotFound : UserError()

    data object InvalidEmail : UserError()

    data object Unauthorized : UserError()
}
