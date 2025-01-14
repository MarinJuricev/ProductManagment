package parking.templates.repository

import arrow.core.Either
import core.model.RepositoryError
import parking.templates.model.Template

interface TemplatesRepository {

    suspend fun getTemplates(): Either<RepositoryError, List<Template>>
    suspend fun getTemplateById(templateId: String): Either<RepositoryError, Template>

    suspend fun saveTemplates(templates: List<Template>): Either<RepositoryError, Unit>
    suspend fun updateTemplate(
        id: String,
        text: String,
    ): Either<RepositoryError, Unit>
}
