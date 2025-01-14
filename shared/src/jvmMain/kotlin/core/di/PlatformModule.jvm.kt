package core.di

import core.logger.UbLogger
import core.logger.UbLoggerImpl
import core.networking.buildHttpClient
import core.utils.Base64Crypto
import core.utils.Crypto
import core.utils.UUIDProvider
import core.utils.UUIDProviderImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { buildHttpClient() }
    factory { UbLoggerImpl() } bind UbLogger::class
    factory { UUIDProviderImpl() } bind UUIDProvider::class
    factory { Base64Crypto() } bind Crypto::class
    single { CoroutineScope(SupervisorJob() + Dispatchers.Main) }
}
