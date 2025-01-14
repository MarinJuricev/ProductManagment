package auth.di

import auth.Authentication
import auth.AuthenticationImpl
import auth.UserLoginStatus
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import org.koin.dsl.bind
import org.koin.dsl.module

fun authModule() = module {
    single { Firebase.auth }
    single { AuthenticationImpl(get(), get(), get()) } bind Authentication::class
    single { UserLoginStatus(get()) }
}
