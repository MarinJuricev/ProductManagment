package parking.emailTemplates.interaction

import parking.templates.model.Template

sealed interface EmailTemplatesScreenState {
    data object Loading : EmailTemplatesScreenState
    data object Retry : EmailTemplatesScreenState
    data class Content(val templates: List<Template>) : EmailTemplatesScreenState
}
