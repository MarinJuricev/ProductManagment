package org.product.inventory.web.pages.emailtemplates

import arrow.core.Either
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.product.inventory.web.components.AlertMessage
import org.product.inventory.web.components.clearAfterDelay
import org.product.inventory.web.core.AlertMessageMapper
import org.product.inventory.web.core.Dictionary
import org.product.inventory.web.core.StringRes
import parking.templates.GetTemplates
import parking.templates.SaveTemplates
import parking.templates.model.Template
import parking.templates.model.TemplatesError

class EmailTemplatesViewModel(
    private val scope: CoroutineScope,
    private val uiMapper: EmailTemplatesUiMapper,
    private val getTemplates: GetTemplates,
    private val saveTemplates: SaveTemplates,
    private val alertMessageMapper: AlertMessageMapper,
    private val dictionary: Dictionary,
) {

    private val _routeToNavigate = MutableStateFlow<String?>(null)
    private val _isLoading = MutableStateFlow(true)

    private val _selectedTemplate = MutableStateFlow<Template?>(null)
    private val _updatedTemplates = MutableStateFlow<List<Template>>(emptyList())
    private val _saveInProgress = MutableStateFlow(false)
    private val _alertMessage = MutableStateFlow<AlertMessage?>(null)
    private val _showAnimation = MutableStateFlow(false)

    private val templates = combine(
        flow { emit(getTemplates()) }.onEach { _isLoading.update { false } },
        _updatedTemplates,
        ::combineTemplates,
    ).stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = Either.Right(emptyList()),
    )

    private fun combineTemplates(
        originalListOptional: Either<TemplatesError, List<Template>>,
        updatedList: List<Template>,
    ): Either<TemplatesError, List<Template>> = originalListOptional.map { originalList ->
        originalList.map { originalTemplate ->
            updatedList.firstOrNull { it.id == originalTemplate.id } ?: originalTemplate
        }
    }

    private val loadingFlow = combine(
        _isLoading,
        _saveInProgress,
    ) { pageLoading, saveLoading -> pageLoading to saveLoading }

    val state = core.utils.combine(
        templates,
        _routeToNavigate,
        _selectedTemplate,
        _alertMessage,
        _showAnimation,
        loadingFlow,
        uiMapper::toUiState,
    ).stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = EmailTemplatesUiState(),
    )

    fun onEvent(event: EmailTemplatesEvent) {
        when (event) {
            is EmailTemplatesEvent.PathClick -> _routeToNavigate.update { event.path }
            is EmailTemplatesEvent.TemplateClick -> selectTemplate(event.templateId)
            is EmailTemplatesEvent.TemplateChanged -> templateChanged(event.templateId, event.content)
            EmailTemplatesEvent.UpdateTemplate -> scope.launch { updateTemplate() }
            EmailTemplatesEvent.CloseButtonClick -> _selectedTemplate.update { null }
        }
    }

    private suspend fun updateTemplate() {
        val updatedTemplate = _selectedTemplate.value ?: return
        val updatedTemplates = templates.value.getOrNull()?.map {
            if (it.id == updatedTemplate.id) {
                updatedTemplate
            } else {
                it
            }
        } ?: return

        _saveInProgress.update { true }

        val result = saveTemplates(updatedTemplates)

        _saveInProgress.update { false }

        result.onRight {
            _updatedTemplates.update { updatedTemplates }
            _showAnimation.update { true }
            _selectedTemplate.update { null }
        }

        handleMessage(isSuccess = result.isRight())
    }

    private suspend fun handleMessage(isSuccess: Boolean) {
        _alertMessage.update {
            alertMessageMapper.map(
                isSuccess = isSuccess,
                successMessage = dictionary.get(StringRes.emailTemplatesRequestEditSuccess),
                failureMessage = dictionary.get(StringRes.emailTemplatesRequestEditFailure),
            )
        }
        _alertMessage.clearAfterDelay()
    }

    private fun selectTemplate(templateId: String) {
        val templateFromList = templates.value.getOrNull()
            ?.firstOrNull { it.id == templateId }
            ?.takeIf { _selectedTemplate.value?.id != templateId } // if the same template is selected, do nothing
            ?: return

        val showAnimation = _selectedTemplate.value == null
        _showAnimation.update { showAnimation }
        _selectedTemplate.update { templateFromList }
    }

    private fun templateChanged(
        templateId: String,
        content: String,
    ) {
        val template = _selectedTemplate.value
            ?.takeIf { selectedTemplate -> selectedTemplate.id == templateId }
            ?: return

        _selectedTemplate.update {
            template.copy(text = content)
        }
    }
}
