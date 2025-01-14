package parking.usersRequests.details

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinNavigatorScreenModel
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import components.BodyMediumText
import components.BodySmallText
import components.InventoryBigButton
import components.InventoryDropDownPicker
import components.LoadingIndicator
import components.SwitchForm
import org.product.inventory.shared.MR
import parking.reservation.components.FormAdditionalNotesForAdmin
import parking.reservation.components.FormDatePicker
import parking.reservation.components.ParkingReservationStatusIndicator
import parking.usersRequests.UsersRequestsScreenViewModel
import parking.usersRequests.components.FormGarageLevelPicker
import parking.usersRequests.components.FormGarageSpotPicker
import parking.usersRequests.components.HasGarageAccessUiRetry
import parking.usersRequests.components.NoFreeSpacesError
import parking.usersRequests.details.interaction.ParkingReservationDetailsEvent
import parking.usersRequests.details.interaction.ParkingReservationDetailsEvent.AdminNoteChanged
import parking.usersRequests.details.interaction.ParkingReservationDetailsEvent.GarageLevelChanged
import parking.usersRequests.details.interaction.ParkingReservationDetailsEvent.GarageSpotChanged
import parking.usersRequests.details.interaction.ParkingReservationDetailsEvent.HasGarageAccessChanged
import parking.usersRequests.details.interaction.ParkingReservationDetailsEvent.RetryClick
import parking.usersRequests.details.interaction.ParkingReservationDetailsEvent.SaveButtonClick
import parking.usersRequests.details.interaction.ParkingReservationDetailsEvent.StatusChanged
import parking.usersRequests.details.interaction.ParkingReservationDetailsScreenState.Content
import parking.usersRequests.details.interaction.ParkingReservationDetailsScreenState.Error
import parking.usersRequests.details.interaction.ParkingReservationDetailsScreenState.Loading
import parking.usersRequests.details.interaction.ParkingReservationDetailsViewEffect.ReservationUpdated
import parking.usersRequests.details.interaction.ParkingReservationDetailsViewEffect.ShowMessage
import parking.usersRequests.details.model.HasGarageAccessUi
import parking.usersRequests.details.model.HasGarageAccessUi.Retry
import parking.usersRequests.interaction.UsersRequestsEvent.ParkingReservationUpdate

class ParkingReservationDetailsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current
        val viewModel: ParkingReservationDetailsViewModel = koinScreenModel()
        val requestsViewModel: UsersRequestsScreenViewModel = navigator.koinNavigatorScreenModel()
        val uiState = viewModel.uiState.collectAsState().value
        val selectedReservation = requestsViewModel.selectedReservation.collectAsState().value
        LaunchedEffect(Unit) {
            selectedReservation?.let {
                viewModel.onEvent(ParkingReservationDetailsEvent.ScreenShown(selectedReservation))
            }
            viewModel.viewEffect.collect {
                when (it) {
                    is ShowMessage -> Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    is ReservationUpdated -> requestsViewModel.onEvent(ParkingReservationUpdate)
                }
            }
        }
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (uiState) {
                is Content -> ParkingDetailsContent(uiState = uiState, onEvent = viewModel::onEvent)
                is Loading -> LoadingIndicator()
                is Error -> BodySmallText("Error") // will be replaced with actual message
            }
        }
    }
}

@Composable
private fun ParkingDetailsContent(
    uiState: Content,
    onEvent: (ParkingReservationDetailsEvent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().animateContentSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(23.dp),
    ) {
        InventoryDropDownPicker(
            selectedItem = uiState.currentStatus,
            menuItems = uiState.availableStatusOptions,
            onItemClick = { it?.let { status -> onEvent(StatusChanged(status)) } },
            trailingIcon = MR.images.drop_down_icon,
        ) {
            ParkingReservationStatusIndicator(parkingReservation = it)
        }
        FormDatePicker(
            title = uiState.screenTexts.dateFormTitle,
            selectedTimestamp = 0,
            displayedDate = uiState.selectedReservation.date,
            confirmButtonText = "",
            onDateSelected = {},
            enabled = false,
        )
        FormAdditionalNotesForAdmin(
            title = uiState.screenTexts.additionalNotesFormTitle,
            noteText = uiState.selectedReservation.note,
            onNoteChanged = {},
            enabled = false,
            placeholder = {
                BodyMediumText(
                    text = uiState.screenTexts.additionNotesFormPlaceholderText,
                    color = MR.colors.textLight,
                )
            },
        )

        AnimatedVisibility(uiState.adminNoteVisible) {
            FormAdditionalNotesForAdmin(
                title = uiState.adminNoteFormTitle,
                noteText = uiState.adminNote,
                onNoteChanged = { onEvent(AdminNoteChanged(it)) },
                enabled = true,
            )
        }

        AnimatedVisibility(uiState.garageAccessSwitchVisible) {
            when (val garageAccessUi = uiState.hasGarageAccessUi) {
                is HasGarageAccessUi.Loading -> LoadingIndicator()
                is Retry -> HasGarageAccessUiRetry(uiState = garageAccessUi) {
                    onEvent(RetryClick(uiState.selectedReservation))
                }

                is HasGarageAccessUi.UiData -> SwitchForm(
                    formTitle = garageAccessUi.formTitle,
                    switchActive = garageAccessUi.switchActive,
                    onCheckedChange = { onEvent(HasGarageAccessChanged(it)) },
                )
            }
        }

        AnimatedVisibility(uiState.garageLevelPickersVisible) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FormGarageLevelPicker(
                    levelPickerTitle = uiState.screenTexts.garageLevelPickerTitle,
                    availableGarageLevels = uiState.availableGarageLevels,
                    onLevelSelected = { onEvent(GarageLevelChanged(it)) },
                    currentGarageLevel = uiState.currentGarageLevel,
                )

                FormGarageSpotPicker(
                    spotPickerTitle = uiState.screenTexts.garageParkingSpotPickerTitle,
                    availableGarageSpots = uiState.availableGarageSpots,
                    onSpotSelected = { onEvent(GarageSpotChanged(it)) },
                    currentGarageSpot = uiState.currentGarageSpot,
                )
            }
        }

        AnimatedVisibility(uiState.noSpaceLeftErrorVisible) {
            NoFreeSpacesError(message = uiState.screenTexts.noSpaceLeftMessage)
        }

        AnimatedVisibility(uiState.saveButtonVisible) {
            InventoryBigButton(
                onClick = { onEvent(SaveButtonClick(uiState)) },
                isLoading = uiState.saveButtonLoading,
            ) {
                BodyMediumText(
                    modifier = Modifier.padding(8.dp),
                    text = uiState.screenTexts.saveButtonText,
                    fontWeight = SemiBold,
                    color = MR.colors.surface,
                )
            }
        }
    }
}
