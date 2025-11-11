package com.sentinelcloud.mobile.backup

import com.sentinelcloud.mobile.risk.RiskSnapshot

sealed interface BackupEvent {
    val snapshot: RiskSnapshot

    data class BackupQueued(
        override val snapshot: RiskSnapshot
    ) : BackupEvent

    data class BackupSuccess(
        override val snapshot: RiskSnapshot,
        val remotePath: String,
        val bytesUploaded: Long
    ) : BackupEvent

    data class BackupFailure(
        override val snapshot: RiskSnapshot,
        val throwable: Throwable?
    ) : BackupEvent
}


