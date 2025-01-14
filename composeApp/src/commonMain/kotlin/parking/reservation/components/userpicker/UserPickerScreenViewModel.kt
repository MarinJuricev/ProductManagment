package parking.reservation.components.userpicker

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import parking.reservation.components.userpicker.interaction.UserPickerEffect
import parking.reservation.components.userpicker.interaction.UserPickerEffect.ClosePicker
import parking.reservation.components.userpicker.interaction.UserPickerEffect.SelectUser
import parking.reservation.components.userpicker.interaction.UserPickerEvent
import parking.reservation.components.userpicker.interaction.UserPickerEvent.CancelClick
import parking.reservation.components.userpicker.interaction.UserPickerEvent.SaveClick
import parking.reservation.components.userpicker.interaction.UserPickerEvent.SearchValueChange
import parking.reservation.components.userpicker.interaction.UserPickerEvent.UserClick
import parking.reservation.components.userpicker.interaction.UserPickerEvent.UserPreselected
import parking.reservation.components.userpicker.interaction.UserPickerState.Loading
import parking.reservation.components.userpicker.mapper.UserPickerUiMapper
import user.model.InventoryAppUser
import user.usecase.ObserveUsers

class UserPickerScreenViewModel(
    private val observeUsers: ObserveUsers,
    private val userPickerUiMapper: UserPickerUiMapper,
) : ScreenModel {
    private val preselectedUser = MutableStateFlow<InventoryAppUser?>(null)
    private val _searchValue = MutableStateFlow("")
    private val _viewEffect = Channel<UserPickerEffect>(Channel.BUFFERED)
    val viewEffect: Flow<UserPickerEffect> = _viewEffect.receiveAsFlow()

    val uiState = combine(
        observeUsers(),
        preselectedUser.filterNotNull(),
        _searchValue,
        userPickerUiMapper::map,
    ).stateIn(
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = Loading,
    )

    fun onEvent(event: UserPickerEvent) {
        when (event) {
            is UserPreselected -> preselectedUser.update { event.user }
            is SearchValueChange -> _searchValue.update { event.search }
            is UserClick -> preselectedUser.update { event.user }
            is CancelClick -> screenModelScope.launch { _viewEffect.send(ClosePicker) }
            is SaveClick -> screenModelScope.launch { _viewEffect.send(SelectUser(event.user)) }
        }
    }
}
