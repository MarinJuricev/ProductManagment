package org.product.inventory.web.pages.emailtemplates

import org.jetbrains.compose.web.css.CSSColorValue
import org.product.inventory.web.components.AlertMessage
import org.product.inventory.web.components.BreadcrumbItem

data class EmailTemplatesUiState(
    val breadcrumbItems: List<BreadcrumbItem> = emptyList(),
    val title: String = "",
    val items: List<TemplateItemUi> = emptyList(),
    val routeToNavigate: String? = null,
    val isLoading: Boolean = true,
    val selectedTemplate: TemplateItemUi? = null,
    val saveInProgress: Boolean = false,
    val infoMessage: String? = null,
    val saveButtonText: String = "",
    val closeButtonText: String = "",
    val alertMessage: AlertMessage? = null,
    val showAnimation: Boolean = false,
)

data class TemplateItemUi(
    val id: String,
    val icon: String,
    val title: String,
    val text: String,
    val isSelected: Boolean,
    val backgroundColor: CSSColorValue,
    val editContent: String,
)
