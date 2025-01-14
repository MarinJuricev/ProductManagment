package parking.reservation.model

import core.model.MarinJuricevError

sealed class EmailSendingError : MarinJuricevError() {
    data object Unauthorized : EmailSendingError()
    data object TemplateFetchingError : EmailSendingError()
    data object TemplateNotFound : EmailSendingError()
}
