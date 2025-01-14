package parking.reservation

import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import components.BodyMediumText
import components.BodySmallText
import components.GeneralRetry
import components.InventoryBigButton
import components.LoadingIndicator
import components.MultiDatePickerForm
import components.SwitchForm
import org.product.inventory.shared.MR
import parking.reservation.components.CreateReservationFormHeader
import parking.reservation.components.FormAdditionalNotesForAdmin
import parking.reservation.components.FormDatePicker
import parking.reservation.components.userpicker.UserPickerScreen
import parking.reservation.interaction.CreateParkingReservationEvent
import parking.reservation.interaction.CreateParkingReservationEvent.ChangeUserClick
import parking.reservation.interaction.CreateParkingReservationEvent.DateSelected
import parking.reservation.interaction.CreateParkingReservationEvent.GarageLevelChanged
import parking.reservation.interaction.CreateParkingReservationEvent.GarageSpotChanged
import parking.reservation.interaction.CreateParkingReservationEvent.HasGarageAccessChanged
import parking.reservation.interaction.CreateParkingReservationEvent.MultiDateSelectionAdded
import parking.reservation.interaction.CreateParkingReservationEvent.MultiDateSelectionRemoved
import parking.reservation.interaction.CreateParkingReservationEvent.NoteChanged
import parking.reservation.interaction.CreateParkingReservationEvent.RetryFetchGarageData
import parking.reservation.interaction.CreateParkingReservationEvent.SubmitButtonClick
import parking.reservation.interaction.CreateParkingReservationEvent.UserChanged
import parking.reservation.interaction.CreateParkingReservationScreenState.Content
import parking.reservation.interaction.CreateParkingReservationScreenState.Loading
import parking.reservation.interaction.CreateParkingReservationViewEffect.NavigateToDashboard
import parking.reservation.interaction.CreateParkingReservationViewEffect.OpenUserPicker
import parking.reservation.interaction.CreateParkingReservationViewEffect.ShowMessage
import parking.reservation.interaction.GarageDataUI
import parking.reservation.model.RequestMode
import parking.usersRequests.components.FormGarageLevelPicker
import parking.usersRequests.components.FormGarageSpotPicker
import parking.usersRequests.components.NoFreeSpacesError

class CreateParkingReservationScreen : Screen {
    @Composable
    override fun Content() = BottomSheetNavigator(
        hideOnBackPress = false,
        sheetGesturesEnabled = false,
        sheetShape = RoundedCornerShape(topStart = 23.dp, topEnd = 23.dp),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            val viewModel: CreateParkingReservationViewModel = koinScreenModel()
            val navigator = LocalNavigator.currentOrThrow
            val context = LocalContext.current
            val bottomSheetNavigator = LocalBottomSheetNavigator.current

            LaunchedEffect(Unit) {
                viewModel.viewEffect.collect {
                    when (it) {
                        is NavigateToDashboard -> navigator.popUntilRoot()
                        is ShowMessage -> Toast.makeText(context, it.message, LENGTH_SHORT).show()
                        is OpenUserPicker -> bottomSheetNavigator.show(
                            UserPickerScreen(
                                preselectedUser = it.preselectedUser,
                                onUserSelected = { user ->
                                    bottomSheetNavigator.hide()
                                    viewModel.onEvent(UserChanged(user))
                                },
                                onDismiss = { bottomSheetNavigator.hide() },
                            ),
                        )
                    }
                }
            }

            when (val state = viewModel.uiState.collectAsState().value) {
                is Content -> CrateParkingReservationScreenContent(
                    uiState = state,
                    onEvent = viewModel::onEvent,
                    bottomSheetNavigator = bottomSheetNavigator,
                )

                is Loading -> LoadingIndicator()
            }
        }
    }
}

@Composable
fun CrateParkingReservationScreenContent(
    uiState: Content,
    onEvent: (CreateParkingReservationEvent) -> Unit,
    bottomSheetNavigator: BottomSheetNavigator,
) {
    if (uiState.multiDatePickerState.submitStatusSheetVisible) {
        bottomSheetNavigator.show(
            SubmitRequestsStatusScreen(uiState = uiState.multiDatePickerState),
        )
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.Center)
                .clip(RoundedCornerShape(23.dp))
                .background(
                    colorResource(MR.colors.surface.resourceId),
                )
                .padding(horizontal = 8.dp, vertical = 28.dp)
                .animateContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            CreateReservationFormHeader(
                title = uiState.headerData.headerTitle,
                user = uiState.headerData.user,
                enableUserSelect = uiState.headerData.selectAnotherUserEnabled,
                onUserSelectClick = { onEvent(ChangeUserClick(currentlySelectedUser = uiState.headerData.user)) },
            )
            if (uiState.requestMode is RequestMode.Reservation) {
                FormDatePicker(
                    title = uiState.datePickerData.datePickerFormTitle,
                    confirmButtonText = uiState.datePickerData.datePickerConfirmButtonText,
                    displayedDate = uiState.datePickerData.displayedDate,
                    selectedTimestamp = uiState.datePickerData.selectedTimestamp,
                    onDateSelected = { onEvent(DateSelected(it)) },
                    lowerDateLimit = uiState.datePickerData.lowerDateLimit,
                )
            } else {
                MultiDatePickerForm(
                    uiState = uiState.multiDatePickerState,
                    onDateSelected = { onEvent(MultiDateSelectionAdded(it)) },
                    onDateRemoved = { onEvent(MultiDateSelectionRemoved(it)) },
                )
            }

            FormAdditionalNotesForAdmin(
                title = uiState.notesFormData.notesFormTitle,
                noteText = uiState.notesFormData.notes,
                onNoteChanged = { onEvent(NoteChanged(it)) },
                placeholder = {
                    BodySmallText(
                        text = uiState.notesFormData.notesPlaceholderText,
                        color = MR.colors.textLight,
                    )
                },
            )

            if (uiState.requestMode is RequestMode.Reservation) {
                when (uiState.garageData) {
                    is GarageDataUI.Content -> {
                        AnimatedVisibility(
                            uiState.garageData.garageAccessSwitchVisible &&
                                !uiState.garageData.noSpaceLeftErrorVisible,
                        ) {
                            SwitchForm(
                                formTitle = uiState.garageData.switchFormTitle,
                                switchActive = uiState.garageData.hasGarageAccess,
                                onCheckedChange = { onEvent(HasGarageAccessChanged(it)) },
                            )
                        }

                        AnimatedVisibility(!uiState.garageData.noSpaceLeftErrorVisible) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                FormGarageLevelPicker(
                                    levelPickerTitle = uiState.garageData.garageLevelPickerTitle,
                                    availableGarageLevels = uiState.garageData.availableGarageLevels,
                                    onLevelSelected = { onEvent(GarageLevelChanged(it)) },
                                    currentGarageLevel = uiState.garageData.currentGarageLevel,
                                )

                                FormGarageSpotPicker(
                                    spotPickerTitle = uiState.garageData.garageSpotPickerTitle,
                                    availableGarageSpots = uiState.garageData.availableGarageSpots,
                                    onSpotSelected = { onEvent(GarageSpotChanged(it)) },
                                    currentGarageSpot = uiState.garageData.currentGarageSpot,
                                )
                            }
                        }

                        AnimatedVisibility(uiState.garageData.noSpaceLeftErrorVisible) {
                            NoFreeSpacesError(message = uiState.garageData.noFreeSpaceMessage)
                        }
                    }

                    is GarageDataUI.Retry -> GeneralRetry(
                        onRetryClick = { onEvent(RetryFetchGarageData) },
                    )

                    is GarageDataUI.Loading -> LoadingIndicator(modifier = Modifier.size(100.dp))
                }
            }

            AnimatedVisibility(uiState.submitButtonData.buttonEnabled) {
                InventoryBigButton(
                    modifier = Modifier.padding(top = 15.dp)
                        .alpha(if (uiState.submitButtonData.submitButtonLoading) 0.5f else 1f),
                    onClick = { onEvent(SubmitButtonClick) },
                    isLoading = uiState.submitButtonData.submitButtonLoading,
                ) {
                    BodyMediumText(
                        modifier = Modifier.padding(8.dp),
                        text = uiState.submitButtonData.submitButtonText,
                        fontWeight = SemiBold,
                        color = MR.colors.surface,
                    )
                }
            }
        }
    }
}
