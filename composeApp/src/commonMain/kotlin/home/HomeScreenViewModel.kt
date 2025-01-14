package home

import auth.Authentication
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import home.interaction.HomeScreenEvent
import home.interaction.HomeScreenEvent.OnLogoutCanceled
import home.interaction.HomeScreenEvent.OnLogoutClick
import home.interaction.HomeScreenEvent.OnLogoutConfirmed
import home.interaction.HomeScreenState
import home.interaction.HomeScreenState.Loading
import home.mapper.HomeScreenMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import menu.GetMenuOptions
import user.usecase.ObserveCurrentUser

class HomeScreenViewModel(
    private val screenMapper: HomeScreenMapper,
    private val authentication: Authentication,
    private val observeCurrentUser: ObserveCurrentUser,
    private val getMenuOptions: GetMenuOptions,
) : ScreenModel {

    private val _uiState = MutableStateFlow<HomeScreenState>(Loading)
    val uiState = _uiState.asStateFlow()

    init {
        screenModelScope.launch {
            observeCurrentUser().collect { user ->
                _uiState.update { screenMapper(user, getMenuOptions(user)) }
            }
        }
    }

    fun onEvent(event: HomeScreenEvent) {
        when (event) {
            is OnLogoutCanceled -> dialogVisible(false)
            is OnLogoutClick -> dialogVisible(true)
            is OnLogoutConfirmed -> screenModelScope.launch {
                authentication.signOut()
                dialogVisible(false)
            }
        }
    }

    private fun dialogVisible(visible: Boolean) = _uiState.updateContent {
        it.copy(
            dialogData = it.dialogData.copy(
                isVisible = visible,
            ),
        )
    }

    private fun MutableStateFlow<HomeScreenState>.updateContent(
        update: (HomeScreenState.Content) -> HomeScreenState.Content,
    ) {
        this.update {
            when (it) {
                is HomeScreenState.Content -> update(it)
                else -> it
            }
        }
    }
}
