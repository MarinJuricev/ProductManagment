package org.product.inventory.web.pages.auth

import dev.gitlive.firebase.auth.externals.GoogleAuthProvider
import dev.gitlive.firebase.auth.externals.getAuth
import dev.gitlive.firebase.auth.externals.signInWithPopup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.await
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import user.usecase.StoreUserIfNotExists

class AuthViewModel(
    private val appScope: CoroutineScope,
    private val storeUserIfNotExists: StoreUserIfNotExists,
    uiMapper: AuthUiMapper,
) {

    private val _state = MutableStateFlow(uiMapper.toUiState())
    val state = _state.asStateFlow()

    // use appScope here because scope tied to the screen could be canceled before storeUserIfNotExists() finishes
    // since navigation listener in AppEntry.kt listens for user login status changes and can navigate to another screen
    fun signIn() {
        appScope.launch {
            val signInResult = signInWithPopup(
                auth = getAuth(),
                provider = GoogleAuthProvider(),
            ).await()

            storeUserIfNotExists(
                email = signInResult.user.email,
                profileImageUrl = signInResult.user.photoURL,
            )
        }
    }
}
