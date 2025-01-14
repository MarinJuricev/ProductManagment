package parking.usersRequests.interaction

sealed interface UsersRequestsViewEffect {
    data class ToastMessage(val message: String) : UsersRequestsViewEffect
    data object CloseDetails : UsersRequestsViewEffect
}
