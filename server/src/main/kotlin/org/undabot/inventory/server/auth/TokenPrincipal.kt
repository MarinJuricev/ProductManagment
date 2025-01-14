package org.product.inventory.server.auth

import com.google.firebase.auth.FirebaseToken
import io.ktor.server.auth.Principal

data class TokenPrincipal(
    val token: FirebaseToken,
) : Principal
