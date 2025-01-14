package seat.management

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import components.LoadingIndicator
import components.QuestionDialog
import org.product.inventory.shared.MR
import seat.management.components.EditOfficeDialog
import seat.management.components.OfficeInput
import seat.management.components.OfficeItem
import seat.management.components.SeatManagementFormHeader
import seat.management.interaction.SeatManagementScreenEvent
import seat.management.interaction.SeatManagementScreenEvent.AddOfficeClick
import seat.management.interaction.SeatManagementScreenEvent.DeleteOfficeCanceled
import seat.management.interaction.SeatManagementScreenEvent.DeleteOfficeConfirmed
import seat.management.interaction.SeatManagementScreenEvent.EditOfficeCancelClick
import seat.management.interaction.SeatManagementScreenEvent.EditOfficeNameChanged
import seat.management.interaction.SeatManagementScreenEvent.EditOfficeSaveClick
import seat.management.interaction.SeatManagementScreenEvent.EditOfficeSeatsChanged
import seat.management.interaction.SeatManagementScreenEvent.OfficeNameChange
import seat.management.interaction.SeatManagementScreenEvent.SeatsChanged
import seat.management.interaction.SeatManagementScreenState.Content
import seat.management.interaction.SeatManagementScreenState.Loading
import seat.management.interaction.SeatManagementViewEffect.OpenTimeline
import seat.management.interaction.SeatManagementViewEffect.ShowMessage
import seat.timeline.SeatReservationTimelineScreen

class SeatManagementScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel: SeatManagementViewModel = koinScreenModel()
        val navigator = LocalNavigator.currentOrThrow
        val uiState by viewModel.uiState.collectAsState()
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            viewModel.viewEffect.collect {
                when (it) {
                    OpenTimeline -> navigator.replaceAll(SeatReservationTimelineScreen())
                    is ShowMessage -> Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        when (val state = uiState) {
            is Content -> SeatManagementContent(uiState = state, onEvent = viewModel::onEvent)
            is Loading -> LoadingIndicator()
        }
    }
}

@Composable
private fun SeatManagementContent(
    uiState: Content,
    onEvent: (SeatManagementScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    uiState.deleteDialog?.let {
        QuestionDialog(
            questionDialogData = it,
            onDismissRequest = { onEvent(DeleteOfficeCanceled) },
            onPositiveActionClick = { onEvent(DeleteOfficeConfirmed) },
            onNegativeActionClick = { onEvent(DeleteOfficeCanceled) },
        )
    }

    uiState.editDialog?.let {
        EditOfficeDialog(
            data = it,
            officeSeatsChanged = { seats ->
                onEvent(
                    EditOfficeSeatsChanged(
                        office = it.office,
                        seats = seats,
                    ),
                )
            },
            officeNameChanged = { name ->
                onEvent(
                    EditOfficeNameChanged(
                        office = it.office,
                        name = name,
                    ),
                )
            },
            onCancelClick = { onEvent(EditOfficeCancelClick) },
            onSaveClick = { onEvent(EditOfficeSaveClick(it.office)) },
        )
    }

    Box(modifier = modifier.fillMaxSize().padding(16.dp)) {
        LazyColumn(
            modifier = modifier
                .align(Alignment.Center)
                .clip(RoundedCornerShape(23.dp))
                .animateContentSize()
                .background(colorResource(MR.colors.surface.resourceId))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(35.dp),
        ) {
            item {
                SeatManagementFormHeader(
                    officeNameLabel = uiState.screenTexts.officeNameLabel,
                    seatsAmountLabel = uiState.screenTexts.seatsAmountLabel,
                )
            }
            items(items = uiState.offices, key = { it.id }) { office ->
                OfficeItem(
                    office = office,
                    availableOptions = uiState.availableOptions,
                    itemMenuIconResource = uiState.itemMenuIconResource,
                    onOfficeOptionClick = { option, selectedOffice ->
                        onEvent(
                            SeatManagementScreenEvent.OnOfficeOptionClick(option, selectedOffice),
                        )
                    },
                )
            }

            item {
                OfficeInput(
                    officeName = uiState.officeName,
                    seatsAmount = uiState.seatsNumber,
                    enabled = uiState.addButtonEnabled,
                    onAddClick = { name, seats -> onEvent(AddOfficeClick(name, seats)) },
                    onOfficeNameChanged = { onEvent(OfficeNameChange(it)) },
                    onSeatsAmountChanged = { onEvent(SeatsChanged(it)) },
                    officeNamePlaceholder = uiState.officeNamePlaceHolder,
                    seatsPlaceholder = uiState.seatsNumberPlaceholder,
                )
            }
        }
    }
}
