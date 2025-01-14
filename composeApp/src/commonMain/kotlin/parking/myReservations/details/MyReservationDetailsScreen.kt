package parking.myReservations.details

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import components.LoadingIndicator
import components.QuestionDialog
import components.TextWithBorderForm
import org.product.inventory.shared.MR
import parking.myReservations.MyReservationsScreenViewModel
import parking.myReservations.details.interaction.MyReservationDetailsEvent
import parking.myReservations.details.interaction.MyReservationDetailsEvent.CancelReservationConfirmed
import parking.myReservations.details.interaction.MyReservationDetailsEvent.CancelReservationDismissed
import parking.myReservations.details.interaction.MyReservationDetailsUiState
import parking.myReservations.details.interaction.MyReservationDetailsViewEffect.ReservationUpdated
import parking.myReservations.details.interaction.MyReservationDetailsViewEffect.ShowMessage
import parking.myReservations.interaction.MyReservationsEvent
import parking.reservation.components.FormAdditionalNotesForAdmin
import parking.reservation.components.FormDatePicker
import parking.reservation.components.ParkingReservationStatusIndicator

class MyReservationDetailsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current
        val viewModel: MyReservationDetailsViewModel = koinScreenModel()
        val requestsViewModel: MyReservationsScreenViewModel = navigator.koinNavigatorScreenModel()
        val uiState by remember { viewModel.uiState }.collectAsState()
        val selectedReservation by requestsViewModel.selectedReservation.collectAsState()
        LaunchedEffect(Unit) {
            selectedReservation?.let {
                viewModel.onEvent(MyReservationDetailsEvent.ScreenShown(it))
            }
            viewModel.viewEffect.collect {
                when (it) {
                    is ReservationUpdated -> {
                        requestsViewModel.onEvent(MyReservationsEvent.ReservationUpdated)
                    }

                    is ShowMessage -> Toast.makeText(
                        context,
                        it.message,
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
        }
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (val state = uiState) {
                is MyReservationDetailsUiState.Content -> MyParkingDetailsContent(
                    uiState = state,
                    onEvent = viewModel::onEvent,
                )

                MyReservationDetailsUiState.Error -> BodySmallText("Error") // will be replaced with actual message
                MyReservationDetailsUiState.Loading -> LoadingIndicator()
            }
        }
    }
}

@Composable
private fun MyParkingDetailsContent(
    uiState: MyReservationDetailsUiState.Content,
    onEvent: (MyReservationDetailsEvent) -> Unit,
) {
    uiState.cancelRequestDialog?.let {
        QuestionDialog(
            questionDialogData = it,
            onNegativeActionClick = { onEvent(CancelReservationDismissed) },
            onPositiveActionClick = { onEvent(CancelReservationConfirmed(uiState)) },
            onDismissRequest = { onEvent(CancelReservationDismissed) },
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth().animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(23.dp),
    ) {
        ParkingReservationStatusIndicator(parkingReservation = uiState.reservationStatus)
        FormDatePicker(
            title = uiState.dateInfo.dateFormTitle,
            selectedTimestamp = 0,
            displayedDate = uiState.dateInfo.date,
            confirmButtonText = "",
            onDateSelected = {},
            enabled = false,
        )
        FormAdditionalNotesForAdmin(
            title = uiState.adminNotes.notesForAdminFormTitle,
            noteText = uiState.adminNotes.notesForAdmin,
            onNoteChanged = {},
            enabled = false,
            placeholder = {
                BodyMediumText(
                    text = uiState.adminNotes.additionNotesFormPlaceholderText,
                    color = MR.colors.textLight,
                )
            },
        )

        AnimatedVisibility(uiState.adminNotes.adminNoteFormVisible) {
            FormAdditionalNotesForAdmin(
                title = uiState.adminNotes.adminNoteFormTitle,
                noteText = uiState.adminNotes.adminNote,
                onNoteChanged = {},
                enabled = false,
            )
        }

        AnimatedVisibility(uiState.garageInfo.garageLevelVisible) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextWithBorderForm(
                    formTitle = uiState.garageInfo.garageLevelFormTitle,
                    formValue = uiState.garageInfo.garageLevel,
                )

                TextWithBorderForm(
                    formTitle = uiState.garageInfo.parkingSpotFormTitle,
                    formValue = uiState.garageInfo.parkingSpot,
                )
            }
        }

        AnimatedVisibility(uiState.cancelButton.cancelButtonVisible) {
            InventoryBigButton(
                modifier = Modifier.padding(vertical = 8.dp),
                backgroundColor = MR.colors.error,
                onClick = { onEvent(MyReservationDetailsEvent.CancelReservationButtonClick(uiState)) },
                isLoading = uiState.cancelButton.cancelButtonLoading,
                leadingIcon = MR.images.cancel_request_icon,
            ) {
                BodyMediumText(
                    modifier = Modifier.padding(8.dp),
                    text = uiState.cancelButton.cancelButtonText,
                    fontWeight = SemiBold,
                    color = MR.colors.surface,
                )
            }
        }
    }
}
