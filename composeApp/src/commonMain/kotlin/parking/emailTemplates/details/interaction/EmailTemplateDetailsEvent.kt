package parking.emailTemplates.details.interaction

sealed interface EmailTemplateDetailsEvent {
    data class ScreenShown(val templateId: String) : EmailTemplateDetailsEvent
    data class RetryClick(val templateId: String) : EmailTemplateDetailsEvent
    data class TextChanged(val text: String) : EmailTemplateDetailsEvent
    data class SaveClick(val id: String, val text: String) : EmailTemplateDetailsEvent
    data object QuitRequested : EmailTemplateDetailsEvent
    data object DialogNegativeButtonClick : EmailTemplateDetailsEvent
    data object DialogPositiveButtonClick : EmailTemplateDetailsEvent
}
