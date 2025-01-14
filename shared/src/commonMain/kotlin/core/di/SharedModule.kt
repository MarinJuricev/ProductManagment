package core.di

import auth.di.authModule
import core.logger.UbLogger
import core.logger.UbLoggerImpl
import core.networking.buildHttpClient
import core.utils.Base64Crypto
import core.utils.Crypto
import core.utils.CurrentDateProvider
import core.utils.DateProvider
import core.utils.IsEmailValid
import core.utils.UUIDProvider
import core.utils.UUIDProviderImpl
import database.Database
import database.DatabaseImpl
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.firestore.firestore
import email.EmailService
import email.EmailServiceImpl
import email.SendReservationUpdateEmail
import email.UpdateTemplateEmailWithData
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.plugin
import io.ktor.client.request.header
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import menu.di.menuModule
import org.koin.dsl.bind
import org.koin.dsl.module
import parking.di.parkingModule
import parking.role.ObserveScreenUnavailability
import seatreservation.di.seatReservationModule
import user.di.userModule

fun sharedModule() = module {
    includes(coreModule())
    includes(parkingModule())
    includes(userModule())
    includes(networkModule())
    includes(seatReservationModule())
    includes(menuModule())
    includes(authModule())
    includes(databaseModule())
}

private fun databaseModule() = module {
    single { Firebase.firestore }
    single { DatabaseImpl(get(), get()) } bind Database::class
}

private fun coreModule() = module {
    factory { UbLoggerImpl() } bind UbLogger::class
    factory { IsEmailValid() }
    factory { Base64Crypto() } bind Crypto::class
    single { CoroutineScope(SupervisorJob() + Dispatchers.Main) }
    factory { EmailServiceImpl(get()) } bind EmailService::class
    factory { UUIDProviderImpl() } bind UUIDProvider::class
    factory { SendReservationUpdateEmail(get(), get(), get(), get()) }
    factory { UpdateTemplateEmailWithData() }
    factory { ObserveScreenUnavailability(get()) }
    factory { CurrentDateProvider() } bind DateProvider::class
}

private fun networkModule() = module {
    single { buildHttpClient().configureToken(get()) }
}

private fun HttpClient.configureToken(auth: FirebaseAuth): HttpClient = apply {
    plugin(HttpSend).intercept { request ->
        val token = auth.currentUser?.getIdToken(true)
        request.header(TOKEN_HEADER, token)
        execute(request)
    }
}

private const val TOKEN_HEADER = "token"
