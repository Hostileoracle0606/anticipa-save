package com.sentinelcloud.mobile.metrics

import com.sentinelcloud.mobile.backup.BackupEvent
import com.sentinelcloud.mobile.risk.RiskSnapshot
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class MetricsRepository(
    private val service: MetricsService
) {

    private val _events = MutableSharedFlow<MetricEvent>(
        replay = 0,
        extraBufferCapacity = 8,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events: SharedFlow<MetricEvent> = _events.asSharedFlow()

    suspend fun recordRiskEvent(snapshot: RiskSnapshot) {
        _events.emit(MetricEvent.RiskEvaluated(snapshot))
        service.publishRiskEvent(snapshot)
    }

    suspend fun recordBackupSuccess(
        snapshot: RiskSnapshot,
        remotePath: String,
        bytesUploaded: Long,
        encryptionIvBase64: String
    ) {
        _events.emit(
            MetricEvent.BackupMetric(
                BackupEvent.BackupSuccess(
                    snapshot = snapshot,
                    remotePath = remotePath,
                    bytesUploaded = bytesUploaded
                )
            )
        )
        service.publishBackupSuccess(snapshot, remotePath, bytesUploaded, encryptionIvBase64)
    }

    suspend fun recordBackupFailure(snapshot: RiskSnapshot, throwable: Throwable?) {
        _events.emit(
            MetricEvent.BackupMetric(
                BackupEvent.BackupFailure(snapshot, throwable)
            )
        )
        service.publishBackupFailure(snapshot, throwable)
    }
}

sealed interface MetricEvent {
    data class RiskEvaluated(val snapshot: RiskSnapshot) : MetricEvent
    data class BackupMetric(val event: BackupEvent) : MetricEvent
}


