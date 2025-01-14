package org.product.inventory.server.di

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.product.inventory.server.auth.VerifyToken
import org.product.inventory.server.email.usecase.SendEmailUseCase
import org.product.inventory.server.system.EnvironmentProvider
import kotlin.io.encoding.Base64

private val authModule = module {
    single {
        val firebaseCredentials = get<EnvironmentProvider>().get(FIREBASE_CREDENTIALS)
            ?: error("Firebase credentials not found")

        val decodedCredentials = Base64.decode(firebaseCredentials).inputStream()
        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(decodedCredentials))
            .build()
        FirebaseApp.initializeApp(options)
    }
    single { FirebaseAuth.getInstance(get()) }
}

private val useCaseModule = module {
    singleOf(::VerifyToken)
    singleOf(::SendEmailUseCase)
}

private val systemModule = module {
    single<EnvironmentProvider> {
        EnvironmentProvider { key -> System.getenv(key) }
    }
}

val serverModule = module {
    includes(systemModule, authModule, useCaseModule)
}

private const val FIREBASE_CREDENTIALS = "FIREBASE_CREDENTIALS"
