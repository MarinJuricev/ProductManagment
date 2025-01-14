package parking.reservation

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import parking.reservation.model.GarageLevelError
import parking.reservation.model.GarageLevelError.InputFieldTooLong

class CheckInputFieldLength {

    operator fun invoke(
        input: String,
        maxLength: Int = MAX_INPUT_TEXT_LENGTH,
    ): Either<GarageLevelError, Unit> = either {
        ensure(input.length <= maxLength) { InputFieldTooLong }
    }
}

private const val MAX_INPUT_TEXT_LENGTH = 20
