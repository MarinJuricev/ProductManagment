package parking.myReservations

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinNavigatorScreenModel
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import parking.myReservations.details.MyReservationDetailsScreen
import parking.myReservations.interaction.MyReservationsEvent
import parking.myReservations.interaction.MyReservationsViewEffect.ReservationUpdated
import parking.usersRequests.screenComponent.header.datepicker.DateRangePickerViewModel

class MyReservationsScreen : Screen {
    @Composable
    override fun Content() = BottomSheetNavigator(
        sheetShape = RoundedCornerShape(topStart = 23.dp, topEnd = 23.dp),
    ) {
        val navigator = LocalNavigator.currentOrThrow
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        val viewModel: MyReservationsScreenViewModel = navigator.koinNavigatorScreenModel()
        val dateRangePickerViewModel: DateRangePickerViewModel = koinScreenModel()
        val uiState by viewModel.uiState.collectAsState()
        val scope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            viewModel.viewEffect.collect {
                when (it) {
                    ReservationUpdated -> bottomSheetNavigator.hide()
                }
            }
        }

        MyReservationsContent(
            uiState = uiState,
            onEvent = viewModel::onEvent,
            dateRangePickerViewModel = dateRangePickerViewModel,
            onItemClick = {
                viewModel.onEvent(MyReservationsEvent.ReservationClick(it))
                scope.launch {
                    bottomSheetNavigator.show(MyReservationDetailsScreen())
                }
            },
        )
    }
}
