package auth

import arrow.core.Either
import auth.interaction.AuthScreenEvent
import auth.interaction.AuthViewEffect
import auth.mapper.AuthScreenMapper
import auth.model.AuthError
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.product.inventory.utils.GoogleCredential

class AuthScreenViewModel(
    screenMapper: AuthScreenMapper,
    private val authentication: Authentication,
) : ScreenModel {

    private val _uiState = MutableStateFlow(screenMapper())
    val uiState = _uiState.asStateFlow()
    private val _viewEffect = Channel<AuthViewEffect>(Channel.BUFFERED)
    val viewEffect: Flow<AuthViewEffect> = _viewEffect.receiveAsFlow()

    init {
        if (authentication.isUserLoggedIn()) {
            screenModelScope.launch {
                _viewEffect.send(AuthViewEffect.NavigateToHomeScreen)
            }
        }
    }

    fun onEvent(event: AuthScreenEvent) {
        when (event) {
            is AuthScreenEvent.GoogleLoginClick -> screenModelScope.launch {
                _viewEffect.send(
                    AuthViewEffect.ShowGoogleLogin,
                )
            }

            is AuthScreenEvent.CredentialsResultReceived -> event.result.handleCredentialsResult()
        }
    }

    private fun Either<AuthError, GoogleCredential>.handleCredentialsResult() {
        when (this) {
            is Either.Left -> screenModelScope.launch {
                this@handleCredentialsResult.value.handle()
            }

            is Either.Right -> signIn(this.value)
        }
    }

    private fun signIn(credential: GoogleCredential) = screenModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        val authResult = authentication.signInWithCredentialToken(
            credentialToken = credential.token,
            email = credential.email,
        )
        _uiState.update { it.copy(isLoading = false) }
        when (authResult) {
            is Either.Left -> authResult.value.handle()
            is Either.Right -> {
                _viewEffect.send(AuthViewEffect.NavigateToHomeScreen)
            }
        }
    }

    private suspend fun AuthError.handle() {
        when (this) {
            is AuthError.StorageError -> _viewEffect.send(AuthViewEffect.ShowError(message))
            is AuthError.InvalidGoogleCredentials -> _viewEffect.send(AuthViewEffect.ShowError(this.message))
            else -> _viewEffect.send(AuthViewEffect.ShowError(this.toString()))
        }
    }
}
