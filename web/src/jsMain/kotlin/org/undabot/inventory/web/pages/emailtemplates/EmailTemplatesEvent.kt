package org.product.inventory.web.pages.emailtemplates

sealed interface EmailTemplatesEvent {

    data class PathClick(val path: String) : EmailTemplatesEvent

    data class TemplateClick(val templateId: String) : EmailTemplatesEvent

    data class TemplateChanged(val templateId: String, val content: String) : EmailTemplatesEvent

    data object UpdateTemplate : EmailTemplatesEvent

    data object CloseButtonClick : EmailTemplatesEvent
}
