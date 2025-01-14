package parking.emailTemplates

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
import parking.emailTemplates.interaction.EmailTemplatesEvent
import parking.emailTemplates.interaction.EmailTemplatesEvent.EmailTemplateClick
import parking.emailTemplates.interaction.EmailTemplatesEvent.RetryClick
import parking.emailTemplates.interaction.EmailTemplatesScreenState
import parking.emailTemplates.interaction.EmailTemplatesScreenState.Content
import parking.emailTemplates.interaction.EmailTemplatesScreenState.Loading
import parking.emailTemplates.interaction.EmailTemplatesScreenState.Retry
import parking.emailTemplates.interaction.EmailTemplatesViewEffect
import parking.emailTemplates.interaction.EmailTemplatesViewEffect.TemplateSelected
import parking.templates.GetTemplates

class EmailTemplatesViewModel(
    private val getTemplates: GetTemplates,
) : ScreenModel {

    private val _uiState = MutableStateFlow<EmailTemplatesScreenState>(Loading)
    val uiState = _uiState.asStateFlow()
    private val _viewEffect = Channel<EmailTemplatesViewEffect>(Channel.BUFFERED)
    val viewEffect: Flow<EmailTemplatesViewEffect> = _viewEffect.receiveAsFlow()

    init {
        fetchEmailTemplates()
    }

    private fun fetchEmailTemplates() = screenModelScope.launch {
        when (val result = getTemplates()) {
            is Left -> _uiState.update { Retry }
            is Right -> _uiState.update { Content(templates = result.value) }
        }
    }

    fun onEvent(event: EmailTemplatesEvent) {
        when (event) {
            is RetryClick -> fetchEmailTemplates()
            is EmailTemplateClick -> event.handle()
        }
    }

    private fun EmailTemplateClick.handle() = screenModelScope.launch {
        _viewEffect.send(TemplateSelected(templateId))
    }
}
