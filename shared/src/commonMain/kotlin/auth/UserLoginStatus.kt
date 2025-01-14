package auth

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

enum class LoginStatus {
    LOGGED_IN,
    LOGGED_OUT,
    UNKNOWN,
}

class UserLoginStatus(scope: CoroutineScope) {

    val state = Firebase.auth.authStateChanged
        .map { user ->
            when (user) {
                null -> LoginStatus.LOGGED_OUT
                else -> LoginStatus.LOGGED_IN
            }
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.Lazily,
            initialValue = LoginStatus.UNKNOWN,
        )
}
