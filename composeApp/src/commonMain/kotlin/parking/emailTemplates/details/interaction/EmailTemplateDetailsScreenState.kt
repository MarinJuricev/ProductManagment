package parking.emailTemplates.details.interaction

import components.QuestionDialogData
import parking.templates.model.Template

sealed interface EmailTemplateDetailsScreenState {
    data object Loading : EmailTemplateDetailsScreenState
    data object Retry : EmailTemplateDetailsScreenState
    data class Content(
        val template: Template,
        val saveInProgress: Boolean,
        val saveVisible: Boolean,
        val shouldAskForSave: Boolean,
        val questionDialogData: QuestionDialogData? = null,
    ) : EmailTemplateDetailsScreenState
}
