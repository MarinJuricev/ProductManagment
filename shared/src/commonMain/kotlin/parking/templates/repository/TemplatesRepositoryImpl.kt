package parking.templates.repository

import arrow.core.Either
import core.model.RepositoryError
import core.utils.safeDatabaseOperation
import database.Database
import database.path.PARKING_TEMPLATES_PATH
import parking.templates.model.Template

class TemplatesRepositoryImpl(
    private val database: Database,
) : TemplatesRepository {

    override suspend fun getTemplates(): Either<RepositoryError, List<Template>> =
        safeDatabaseOperation {
            database.getCollection(path = PARKING_TEMPLATES_PATH)
                .map { it.data<Template>() }
        }

    override suspend fun getTemplateById(templateId: String): Either<RepositoryError, Template> =
        safeDatabaseOperation {
            database.queryCollection(path = PARKING_TEMPLATES_PATH) {
                TEMPLATE_ID equalTo templateId
            }.first().data<Template>()
        }

    override suspend fun saveTemplates(templates: List<Template>): Either<RepositoryError, Unit> =
        safeDatabaseOperation {
            templates.forEach {
                database.save(
                    path = PARKING_TEMPLATES_PATH,
                    data = it,
                    id = it.id,
                )
            }
        }

    override suspend fun updateTemplate(
        id: String,
        text: String,
    ): Either<RepositoryError, Unit> = safeDatabaseOperation {
        database.update(
            id = id,
            path = PARKING_TEMPLATES_PATH,
            fields = mapOf(
                TEMPLATE_TEXT to text,
            ),
        )
    }
}

private const val TEMPLATE_ID = "id"
private const val TEMPLATE_TEXT = "text"
