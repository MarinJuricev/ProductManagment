package parking.emailTemplates.interaction

sealed interface EmailTemplatesEvent {

    data object RetryClick : EmailTemplatesEvent
    data class EmailTemplateClick(val templateId: String) : EmailTemplatesEvent
}
