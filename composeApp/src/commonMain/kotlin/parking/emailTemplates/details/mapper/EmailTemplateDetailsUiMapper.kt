package parking.emailTemplates.details.mapper

import parking.emailTemplates.details.interaction.EmailTemplateDetailsScreenState
import parking.templates.model.Template

class EmailTemplateDetailsUiMapper() {
    fun map(template: Template) = EmailTemplateDetailsScreenState.Content(
        template = template,
        saveInProgress = false,
        saveVisible = false,
        shouldAskForSave = false,
    )
}
