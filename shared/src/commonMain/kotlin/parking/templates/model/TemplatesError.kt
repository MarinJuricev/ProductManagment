package parking.templates.model

import core.model.MarinJuricevError

sealed class TemplatesError : MarinJuricevError() {
    data object Unauthorized : TemplatesError()
    data object FetchingError : TemplatesError()
    data object SavingError : TemplatesError()
}
