package parking.emailTemplates.details.interaction

sealed interface EmailTemplateDetailsViewEffect {
    data object NavigateBack : EmailTemplateDetailsViewEffect
    data class ShowMessage(val message: String) : EmailTemplateDetailsViewEffect
}
