package parking.crewManagement

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import parking.crewManagement.interaction.CrewManagementEvent
import parking.crewManagement.interaction.CrewManagementEvent.CreateUserClick
import parking.crewManagement.interaction.CrewManagementEvent.UserSelected
import parking.crewManagement.interaction.CrewManagementScreenState.Loading
import parking.crewManagement.interaction.CrewManagementViewEffect
import parking.crewManagement.interaction.CrewManagementViewEffect.CreateNewUser
import parking.crewManagement.interaction.CrewManagementViewEffect.ShowUserDetails
import parking.crewManagement.mapper.CrewManagementScreenMapper
import user.usecase.ObserveUsers

class CrewManagementViewModel(
    observeUsers: ObserveUsers,
    private val uiMapper: CrewManagementScreenMapper,
) : ScreenModel {

    private val _viewEffect = Channel<CrewManagementViewEffect>(Channel.BUFFERED)
    val viewEffect: Flow<CrewManagementViewEffect> = _viewEffect.receiveAsFlow()
    val uiState = observeUsers()
        .map(uiMapper::map)
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Loading,
        )

    fun onEvent(event: CrewManagementEvent) {
        when (event) {
            is UserSelected -> screenModelScope.launch {
                _viewEffect.send(ShowUserDetails(user = event.user))
            }
            is CreateUserClick -> screenModelScope.launch {
                _viewEffect.send(CreateNewUser)
            }
        }
    }
}
