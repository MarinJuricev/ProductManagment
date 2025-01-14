package parking.emailTemplates

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import components.BodyMediumText
import components.GeneralRetry
import components.Image
import components.ImageType.Resource
import components.LoadingIndicator
import org.product.inventory.shared.MR
import parking.emailTemplates.details.EmailTemplateDetailsScreen
import parking.emailTemplates.interaction.EmailTemplatesEvent
import parking.emailTemplates.interaction.EmailTemplatesEvent.EmailTemplateClick
import parking.emailTemplates.interaction.EmailTemplatesEvent.RetryClick
import parking.emailTemplates.interaction.EmailTemplatesScreenState.Content
import parking.emailTemplates.interaction.EmailTemplatesScreenState.Loading
import parking.emailTemplates.interaction.EmailTemplatesScreenState.Retry
import parking.emailTemplates.interaction.EmailTemplatesViewEffect.TemplateSelected
import parking.templates.model.Template

class EmailTemplatesScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel: EmailTemplatesViewModel = koinScreenModel()
        val navigator = LocalNavigator.currentOrThrow
        val uiState by viewModel.uiState.collectAsState()
        LaunchedEffect(Unit) {
            viewModel.viewEffect.collect {
                when (it) {
                    is TemplateSelected -> navigator.push(EmailTemplateDetailsScreen(it.templateId))
                }
            }
        }

        when (val state = uiState) {
            is Content -> EmailTemplatesList(onEvent = viewModel::onEvent, uiState = state)
            is Loading -> LoadingIndicator()
            is Retry -> GeneralRetry(onRetryClick = { viewModel.onEvent(RetryClick) })
        }
    }
}

@Composable
private fun EmailTemplatesList(
    uiState: Content,
    onEvent: (EmailTemplatesEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(
            items = uiState.templates,
            key = { template -> template.id },
        ) { template ->
            EmailTemplateListItem(
                template = template,
                onTemplateClick = { selectedTemplate -> onEvent(EmailTemplateClick(selectedTemplate.id)) },
            )
        }
    }
}

@Composable
private fun EmailTemplateListItem(
    template: Template,
    modifier: Modifier = Modifier,
    onTemplateClick: (Template) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(23.dp))
            .clickable { onTemplateClick(template) }
            .background(colorResource(MR.colors.surface.resourceId)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(colorResource(MR.colors.secondary.resourceId))
                .clip(RoundedCornerShape(topStart = 23.dp, bottomStart = 23.dp)),
        ) {
            Image(
                modifier = Modifier
                    .align(Alignment.Center)
                    .width(32.dp),
                imageType = Resource(resource = MR.images.email_templates_icon),
            )
        }

        BodyMediumText(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            text = template.title,
            fontWeight = SemiBold,
            textAlign = TextAlign.Center,
        )
    }
}
