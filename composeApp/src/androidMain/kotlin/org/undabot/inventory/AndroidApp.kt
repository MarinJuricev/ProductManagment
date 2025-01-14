package org.product.inventory

import android.app.Application
import core.di.initKoin
import core.model.Environment
import core.model.Environment.DEVELOPMENT
import org.koin.android.ext.koin.androidContext
import org.product.inventory.di.androidAppModule

class AndroidApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initDI()
    }

    private fun initDI() {
        initKoin(environment = Environment.fromString(BuildConfig.ENVIRONMENT) ?: DEVELOPMENT) {
            androidContext(this@AndroidApp)
            modules(
                androidAppModule(),
            )
        }
    }
}
