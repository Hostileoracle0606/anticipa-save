package com.sentinelcloud.mobile

import android.app.Application
import com.google.firebase.FirebaseApp
import com.sentinelcloud.mobile.metrics.MetricsRepository
import com.sentinelcloud.mobile.metrics.MetricsService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class SentinelCloudApp : Application() {

    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        appContainer = AppContainer(
            applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
            metricsRepository = MetricsRepository(
                service = MetricsService()
            )
        )
    }
}

data class AppContainer(
    val applicationScope: CoroutineScope,
    val metricsRepository: MetricsRepository
)


