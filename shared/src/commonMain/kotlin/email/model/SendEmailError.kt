package email.model

sealed class SendEmailError() {
    data object Unauthorized : SendEmailError()
    data object FetchingTemplatesError : SendEmailError()
    data object TemplateNotFound : SendEmailError()
}
