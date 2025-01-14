package parking.emailTemplates.details

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import components.GeneralRetry
import components.InventorySaveButton
import components.LoadingIndicator
import components.QuestionDialog
import components.RichTextEditor
import parking.emailTemplates.details.components.TemplateTitle
import parking.emailTemplates.details.interaction.EmailTemplateDetailsEvent
import parking.emailTemplates.details.interaction.EmailTemplateDetailsEvent.DialogNegativeButtonClick
import parking.emailTemplates.details.interaction.EmailTemplateDetailsEvent.QuitRequested
import parking.emailTemplates.details.interaction.EmailTemplateDetailsEvent.RetryClick
import parking.emailTemplates.details.interaction.EmailTemplateDetailsEvent.ScreenShown
import parking.emailTemplates.details.interaction.EmailTemplateDetailsScreenState.Content
import parking.emailTemplates.details.interaction.EmailTemplateDetailsScreenState.Loading
import parking.emailTemplates.details.interaction.EmailTemplateDetailsScreenState.Retry
import parking.emailTemplates.details.interaction.EmailTemplateDetailsViewEffect.NavigateBack
import parking.emailTemplates.details.interaction.EmailTemplateDetailsViewEffect.ShowMessage

class EmailTemplateDetailsScreen(
    private val templateId: String,
) : Screen {
    @Composable
    override fun Content() {
        val viewModel: EmailTemplateDetailsViewModel = koinScreenModel()
        val uiState by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            viewModel.onEvent(ScreenShown(templateId))
            viewModel.viewEffect.collect {
                when (it) {
                    is NavigateBack -> navigator.pop()
                    is ShowMessage -> Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        when (val screenState = uiState) {
            is Content -> TemplateDetailsContent(
                uiState = screenState,
                onEvent = viewModel::onEvent,
            )

            is Retry -> GeneralRetry(onRetryClick = { viewModel.onEvent(RetryClick(templateId)) })
            is Loading -> LoadingIndicator()
        }
    }
}

@Composable
fun TemplateDetailsContent(
    uiState: Content,
    modifier: Modifier = Modifier,
    onEvent: (EmailTemplateDetailsEvent) -> Unit,
) {
    BackHandler(enabled = uiState.shouldAskForSave, onBack = { onEvent(QuitRequested) })

    uiState.questionDialogData?.let {
        QuestionDialog(
            questionDialogData = it,
            onNegativeActionClick = { onEvent(DialogNegativeButtonClick) },
            onPositiveActionClick = { onEvent(EmailTemplateDetailsEvent.DialogPositiveButtonClick) },
            onDismissRequest = {},
        )
    }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TemplateTitle(
                modifier = Modifier.fillMaxWidth(0.85f),
                title = uiState.template.title,
            )

            AnimatedVisibility(uiState.saveVisible) {
                InventorySaveButton(
                    saveInProgress = uiState.saveInProgress,
                    onSaveClick = {
                        onEvent(
                            EmailTemplateDetailsEvent.SaveClick(
                                uiState.template.id,
                                uiState.template.text,
                            ),
                        )
                    },
                )
            }
        }
        RichTextEditor(
            value = uiState.template.text,
            onValueChanged = { onEvent(EmailTemplateDetailsEvent.TextChanged(it)) },
        )
    }
}
