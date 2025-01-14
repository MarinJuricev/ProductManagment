package org.product.inventory.web.pages.emailtemplates

import arrow.core.Either
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlHandler
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlParser
import org.product.inventory.web.components.AlertMessage
import org.product.inventory.web.components.BreadcrumbItem
import org.product.inventory.web.core.Dictionary
import org.product.inventory.web.core.ImageRes
import org.product.inventory.web.core.StringRes
import org.product.inventory.web.pages.Routes
import org.product.inventory.web.toRgb
import parking.templates.model.Template
import parking.templates.model.TemplatesError
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class EmailTemplatesUiMapper(
    private val dictionary: Dictionary,
) {

    suspend fun toUiState(
        templatesOptional: Either<TemplatesError, List<Template>>,
        routeToNavigate: String?,
        selectedTemplate: Template?,
        alertMessage: AlertMessage?,
        showAnimation: Boolean,
        loading: Pair<Boolean, Boolean>,
    ): EmailTemplatesUiState {
        val pageLoading = loading.first
        val saveLoading = loading.second

        return EmailTemplatesUiState(
            title = dictionary.get(StringRes.emailTemplatesTitle),
            breadcrumbItems = buildBreadcrumbItems(),
            items = when (templatesOptional) {
                is Either.Left -> emptyList()
                is Either.Right -> templatesOptional.value.map { it.toTemplateItemUi(it.id == selectedTemplate?.id) }
            },
            routeToNavigate = routeToNavigate,
            isLoading = pageLoading,
            selectedTemplate = selectedTemplate?.toTemplateItemUi(isSelected = true),
            saveInProgress = saveLoading,
            infoMessage = when (templatesOptional) {
                is Either.Left -> dictionary.get(StringRes.emailTemplatesFetchErrorMessage)
                is Either.Right -> if (templatesOptional.value.isEmpty()) dictionary.get(StringRes.emailTemplatesEmptyListMessage) else null
            },
            saveButtonText = dictionary.get(StringRes.emailTemplatesSaveButtonText),
            closeButtonText = dictionary.get(StringRes.emailTemplatesCloseButtonText),
            alertMessage = alertMessage,
            showAnimation = showAnimation,
        )
    }

    private fun buildBreadcrumbItems() = listOf(
        BreadcrumbItem(
            text = dictionary.get(StringRes.emailTemplatesPath1),
            route = Routes.home,
        ),
        BreadcrumbItem(
            text = dictionary.get(StringRes.emailTemplatesPath2),
            route = Routes.parkingReservation,
            isNavigable = true,
        ),
        BreadcrumbItem(
            text = dictionary.get(StringRes.emailTemplatesPath3),
            route = Routes.emailTemplates,
        ),
    )

    private suspend fun Template.toTemplateItemUi(
        isSelected: Boolean,
    ) = TemplateItemUi(
        id = id,
        icon = ImageRes.emailTemplateIcon,
        title = title,
        text = getParsedTextFromHtml(text),
        backgroundColor = if (isSelected) emailTemplateSelectedColor else emailTemplateDefaultColor,
        editContent = text,
        isSelected = isSelected,
    )

    /**
     * Parses HTML and returns title and text.
     * Title is the first text node in the HTML.
     * Text is the rest of the text nodes in the HTML.
     */
    private suspend fun getParsedTextFromHtml(html: String): String = suspendCoroutine { continuation ->
        var title = ""
        var text = ""

        val handler = KsoupHtmlHandler.Builder()
            .onText { value ->
                if (title.isEmpty()) {
                    title = value
                } else {
                    text += value
                }
            }
            .onEnd { continuation.resume(text.trim()) }
            .build()

        KsoupHtmlParser(handler = handler).apply {
            write(html)
            end()
        }
    }
}

private val emailTemplateDefaultColor = "EBEBEB".toRgb()
private val emailTemplateSelectedColor = "FFFFFF".toRgb()
