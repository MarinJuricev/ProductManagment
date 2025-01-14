package parking.emailTemplates.details

import arrow.core.Either.Left
import arrow.core.Either.Right
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.product.inventory.shared.MR
import org.product.inventory.utils.dictionary.Dictionary
import parking.emailTemplates.details.interaction.EmailTemplateDetailsEvent
import parking.emailTemplates.details.interaction.EmailTemplateDetailsEvent.DialogNegativeButtonClick
import parking.emailTemplates.details.interaction.EmailTemplateDetailsEvent.DialogPositiveButtonClick
import parking.emailTemplates.details.interaction.EmailTemplateDetailsEvent.QuitRequested
import parking.emailTemplates.details.interaction.EmailTemplateDetailsEvent.RetryClick
import parking.emailTemplates.details.interaction.EmailTemplateDetailsEvent.SaveClick
import parking.emailTemplates.details.interaction.EmailTemplateDetailsEvent.ScreenShown
import parking.emailTemplates.details.interaction.EmailTemplateDetailsEvent.TextChanged
import parking.emailTemplates.details.interaction.EmailTemplateDetailsScreenState
import parking.emailTemplates.details.interaction.EmailTemplateDetailsScreenState.Content
import parking.emailTemplates.details.interaction.EmailTemplateDetailsScreenState.Loading
import parking.emailTemplates.details.interaction.EmailTemplateDetailsScreenState.Retry
import parking.emailTemplates.details.interaction.EmailTemplateDetailsViewEffect
import parking.emailTemplates.details.interaction.EmailTemplateDetailsViewEffect.NavigateBack
import parking.emailTemplates.details.interaction.EmailTemplateDetailsViewEffect.ShowMessage
import parking.emailTemplates.details.mapper.DiscardChangesDialogMapper
import parking.emailTemplates.details.mapper.EmailTemplateDetailsUiMapper
import parking.templates.GetTemplateById
import parking.templates.UpdateTemplate

class EmailTemplateDetailsViewModel(
    private val getTemplateById: GetTemplateById,
    private val updateTemplate: UpdateTemplate,
    private val uiMapper: EmailTemplateDetailsUiMapper,
    private val discardDialog: DiscardChangesDialogMapper,
    private val dictionary: Dictionary,
) : ScreenModel {

    private val _uiState = MutableStateFlow<EmailTemplateDetailsScreenState>(Loading)
    val uiState = _uiState.asStateFlow()
    private val _viewEffect = Channel<EmailTemplateDetailsViewEffect>(Channel.BUFFERED)
    val viewEffect: Flow<EmailTemplateDetailsViewEffect> = _viewEffect.receiveAsFlow()

    fun onEvent(event: EmailTemplateDetailsEvent) {
        when (event) {
            is ScreenShown -> fetchTemplate(event.templateId)
            is RetryClick -> fetchTemplate(event.templateId)
            is SaveClick -> handleSave(event.id, event.text)
            is TextChanged -> handleTextChanged(event.text)
            is QuitRequested -> handleQuitRequested()
            is DialogNegativeButtonClick -> handleDialogNegativeButtonClick()
            is DialogPositiveButtonClick -> handleDialogPositiveButtonClick()
        }
    }

    private fun fetchTemplate(templateId: String) = screenModelScope.launch {
        _uiState.update { Loading }
        when (val result = getTemplateById(templateId)) {
            is Left -> _uiState.update { Retry }
            is Right -> _uiState.update { uiMapper.map(result.value) }
        }
    }

    private fun handleSave(
        id: String,
        text: String,
    ) = screenModelScope.launch {
        _uiState.updateContent { it.copy(saveInProgress = true) }
        when (updateTemplate(id = id, text = text)) {
            is Left -> _viewEffect.send(ShowMessage(dictionary.getString(MR.strings.general_update_failed)))
            is Right -> {
                _uiState.updateContent {
                    it.copy(
                        shouldAskForSave = false,
                        saveVisible = false,
                    )
                }
                _viewEffect.send(ShowMessage(dictionary.getString(MR.strings.general_updated)))
            }
        }
        _uiState.updateContent { it.copy(saveInProgress = false) }
    }

    private fun handleTextChanged(text: String) {
        _uiState.updateContent {
            it.copy(
                shouldAskForSave = true,
                saveVisible = true,
                template = it.template.copy(text = text),
            )
        }
    }

    private fun handleQuitRequested() =
        _uiState.updateContent { it.copy(questionDialogData = discardDialog.map()) }

    private fun handleDialogNegativeButtonClick() = screenModelScope.launch {
        _viewEffect.send(NavigateBack)
    }

    private fun handleDialogPositiveButtonClick() {
        _uiState.updateContent { it.copy(questionDialogData = null) }
    }
}

private fun MutableStateFlow<EmailTemplateDetailsScreenState>.updateContent(
    update: (Content) -> Content,
) {
    this.update {
        when (it) {
            is Content -> update(it)
            else -> it
        }
    }
}
