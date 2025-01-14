package user.di

import org.koin.dsl.bind
import org.koin.dsl.module
import user.repository.UserRepository
import user.repository.UserRepositoryImpl
import user.usecase.CreateUser
import user.usecase.GetExistingUser
import user.usecase.GetUsers
import user.usecase.ObserveCurrentUser
import user.usecase.ObserveUsers
import user.usecase.StoreUserIfNotExists
import user.usecase.UpdateUser

fun userModule() = module {
    factory { UserRepositoryImpl(get()) } bind UserRepository::class
    factory { GetExistingUser(get(), get(), get()) }
    factory { StoreUserIfNotExists(get(), get(), get(), get()) }
    factory { UpdateUser(get(), get(), get()) }
    factory { ObserveUsers(get(), get(), get()) }
    factory { ObserveCurrentUser(get(), get(), get()) }
    factory { CreateUser(get(), get(), get()) }
    factory { GetUsers(get(), get(), get()) }
}
