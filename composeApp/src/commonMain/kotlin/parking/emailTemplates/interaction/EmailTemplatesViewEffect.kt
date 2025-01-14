package parking.emailTemplates.interaction

sealed interface EmailTemplatesViewEffect {
    data class TemplateSelected(val templateId: String) : EmailTemplatesViewEffect
}
