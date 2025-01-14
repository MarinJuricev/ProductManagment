package home.interaction

sealed interface HomeScreenEvent {
    data object OnLogoutClick : HomeScreenEvent
    data object OnLogoutConfirmed : HomeScreenEvent
    data object OnLogoutCanceled : HomeScreenEvent
}
