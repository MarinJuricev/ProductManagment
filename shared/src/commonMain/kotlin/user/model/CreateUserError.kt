package user.model

import core.model.MarinJuricevError

sealed class CreateUserError : MarinJuricevError() {
    data object DuplicatedUser : CreateUserError()
    data object InvalidEmail : CreateUserError()
    data object CreateUserFailed : CreateUserError()
}
