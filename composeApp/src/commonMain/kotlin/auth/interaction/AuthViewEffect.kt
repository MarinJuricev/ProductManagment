package auth.interaction

sealed interface AuthViewEffect {
    data object ShowGoogleLogin : AuthViewEffect
    data object NavigateToHomeScreen : AuthViewEffect
    data class ShowError(val message: String) : AuthViewEffect
}
