package parking.crewManagement

import android.widget.Toast
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import components.LoadingIndicator
import parking.crewManagement.component.CrewManagementScreenContent
import parking.crewManagement.edit.EditOrCreateUserBottomSheet
import parking.crewManagement.interaction.CrewManagementScreenState.Content
import parking.crewManagement.interaction.CrewManagementScreenState.Loading
import parking.crewManagement.interaction.CrewManagementViewEffect.CreateNewUser
import parking.crewManagement.interaction.CrewManagementViewEffect.ShowMessage
import parking.crewManagement.interaction.CrewManagementViewEffect.ShowUserDetails

class CrewManagementScreen : Screen {
    @Composable
    override fun Content() = BottomSheetNavigator(
        sheetShape = RoundedCornerShape(topStart = 23.dp, topEnd = 23.dp),
    ) {
        val viewModel: CrewManagementViewModel = koinScreenModel()
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        val context = LocalContext.current
        LaunchedEffect(Unit) {
            viewModel.viewEffect.collect {
                when (it) {
                    is ShowMessage -> Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    is ShowUserDetails -> bottomSheetNavigator.show(EditOrCreateUserBottomSheet(it.user))
                    is CreateNewUser -> bottomSheetNavigator.show(EditOrCreateUserBottomSheet())
                }
            }
        }

        when (val uiState = viewModel.uiState.collectAsState().value) {
            is Content -> CrewManagementScreenContent(
                uiState = uiState,
                onEvent = viewModel::onEvent,
            )

            is Loading -> LoadingIndicator()
        }
    }
}
