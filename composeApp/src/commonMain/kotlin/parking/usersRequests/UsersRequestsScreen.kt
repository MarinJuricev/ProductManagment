package parking.usersRequests

import android.widget.Toast
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinNavigatorScreenModel
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import parking.usersRequests.components.UsersRequestsContent
import parking.usersRequests.details.ParkingReservationDetailsScreen
import parking.usersRequests.interaction.UsersRequestsEvent.ReservationClick
import parking.usersRequests.interaction.UsersRequestsViewEffect.CloseDetails
import parking.usersRequests.interaction.UsersRequestsViewEffect.ToastMessage
import parking.usersRequests.screenComponent.header.filter.FilterViewModel

class UsersRequestsScreen : Screen {
    @Composable
    override fun Content() = BottomSheetNavigator(
        sheetShape = RoundedCornerShape(topStart = 23.dp, topEnd = 23.dp),
    ) {
        val navigator = LocalNavigator.currentOrThrow
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        val viewModel: UsersRequestsScreenViewModel = navigator.koinNavigatorScreenModel()
        val scope = rememberCoroutineScope()
        val filterViewModel: FilterViewModel = koinScreenModel()
        val context = LocalContext.current
        LaunchedEffect(Unit) {
            viewModel.viewEffect.collect {
                when (it) {
                    is ToastMessage -> Toast.makeText(context, it.message, Toast.LENGTH_SHORT)
                        .show()

                    is CloseDetails -> bottomSheetNavigator.hide()
                }
            }
        }
        UsersRequestsContent(
            uiState = viewModel.state.collectAsState().value,
            onItemClick = {
                scope.launch {
                    viewModel.onEvent(ReservationClick(it))
                    bottomSheetNavigator.show(ParkingReservationDetailsScreen())
                }
            },
            onEvent = viewModel::onEvent,
            filterViewModel = filterViewModel,
        )
    }
}
