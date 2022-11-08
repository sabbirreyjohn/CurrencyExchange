package com.androidrey.currencyexchange

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class TheApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    /**
     * Work Manager Configuration
     */
    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()
    }

    companion object {

        private var appInstance: TheApplication? = null

        val instance: TheApplication
            @Synchronized get() {
                if (appInstance == null) {
                    appInstance =
                        TheApplication()
                }
                return appInstance!!
            }
    }

    override fun onCreate() {
        appInstance = this
        super.onCreate()
    }
}