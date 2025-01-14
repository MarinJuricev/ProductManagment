package parking.templates

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import auth.Authentication
import parking.templates.model.Template
import parking.templates.model.TemplatesError
import parking.templates.model.TemplatesError.SavingError
import parking.templates.model.TemplatesError.Unauthorized
import parking.templates.repository.TemplatesRepository

class SaveTemplates(
    private val repository: TemplatesRepository,
    private val authentication: Authentication,
) {

    suspend operator fun invoke(templates: List<Template>): Either<TemplatesError, Unit> = either {
        ensure(authentication.isUserLoggedIn()) { Unauthorized }
        repository
            .saveTemplates(templates)
            .mapLeft { SavingError }
            .bind()
    }
}
