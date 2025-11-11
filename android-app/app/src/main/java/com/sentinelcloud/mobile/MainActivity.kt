package com.sentinelcloud.mobile

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.sentinelcloud.mobile.backup.FileBackupManager
import com.sentinelcloud.mobile.metrics.MetricsRepository
import com.sentinelcloud.mobile.risk.RiskDetectionModule
import com.sentinelcloud.mobile.risk.RiskSnapshot
import com.sentinelcloud.mobile.ui.DeviceProtectionScreen
import com.sentinelcloud.mobile.ui.theme.SentinelCloudTheme

class MainActivity : ComponentActivity() {

    private lateinit var riskDetectionModule: RiskDetectionModule
    private lateinit var backupManager: FileBackupManager
    private lateinit var metricsRepository: MetricsRepository

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { /* handle result if needed */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val container = (application as SentinelCloudApp).appContainer
        metricsRepository = container.metricsRepository
        backupManager = FileBackupManager(
            context = this,
            applicationScope = container.applicationScope,
            metricsRepository = metricsRepository
        )
        riskDetectionModule = RiskDetectionModule(
            context = this,
            applicationScope = container.applicationScope,
            onRiskThresholdExceeded = { snapshot ->
                backupManager.enqueueBackup(snapshot)
            },
            metricsRepository = metricsRepository
        )

        requestRuntimePermissions()
        riskDetectionModule.start()

        setContent {
            SentinelCloudTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                LaunchedEffect(Unit) {
                    riskDetectionModule.status.collect { status ->
                        when (status) {
                            is RiskDetectionModule.Status.RiskDetected -> {
                                snackbarHostState.showSnackbar(
                                    message = "Risk detected: ${status.snapshot.riskLevel}"
                                )
                            }

                            is RiskDetectionModule.Status.SensorUnavailable -> {
                                snackbarHostState.showSnackbar(
                                    message = "Sensor unavailable: ${status.reason}"
                                )
                            }

                            else -> Unit
                        }
                    }
                }

                val currentRisk by riskDetectionModule.riskSnapshot.collectAsState(
                    initial = RiskSnapshot.idle()
                )
                val lastBackupEvent by backupManager.backupEvents.collectAsState(initial = null)

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
                ) { padding ->
                    DeviceProtectionScreen(
                        modifier = Modifier
                            .fillMaxSize(),
                        riskSnapshot = currentRisk,
                        latestBackupEvent = lastBackupEvent,
                        paddingValues = padding
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        riskDetectionModule.stop()
    }

    private fun requestRuntimePermissions() {
        requestPermissionsLauncher.launch(
            arrayOf(
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.BODY_SENSORS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )
    }
}


