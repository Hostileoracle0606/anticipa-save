package com.sentinelcloud.mobile.backup

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.sentinelcloud.mobile.metrics.MetricsRepository
import com.sentinelcloud.mobile.risk.RiskSnapshot
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File

class FileBackupManager(
    private val context: Context,
    private val applicationScope: CoroutineScope,
    private val metricsRepository: MetricsRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val preparer: BackupPayloadPreparer = BackupPayloadPreparer(context),
    private val encryption: BackupEncryptionProvider = BackupEncryptionProvider()
) {

    private val storage = Firebase.storage.reference
    private val firestore = Firebase.firestore

    private val _backupEvents = MutableSharedFlow<BackupEvent>(
        extraBufferCapacity = 8,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val backupEvents: SharedFlow<BackupEvent> = _backupEvents.asSharedFlow()

    fun enqueueBackup(snapshot: RiskSnapshot) {
        applicationScope.launch(dispatcher) {
            _backupEvents.emit(BackupEvent.BackupQueued(snapshot))
            runCatching {
                val archive = preparer.createArchive(snapshot)
                val encrypted = encryption.encrypt(archive)
                val path = uploadEncryptedPayload(encrypted, snapshot)
                metricsRepository.recordBackupSuccess(
                    snapshot = snapshot,
                    remotePath = path,
                    bytesUploaded = encrypted.file.length(),
                    encryptionIvBase64 = encrypted.ivBase64
                )
                _backupEvents.emit(
                    BackupEvent.BackupSuccess(
                        snapshot = snapshot,
                        remotePath = path,
                        bytesUploaded = encrypted.file.length()
                    )
                )
                cleanupFiles(listOf(archive, encrypted.file))
            }.onFailure { throwable ->
                Log.e(TAG, "Failed to execute backup", throwable)
                metricsRepository.recordBackupFailure(snapshot, throwable)
                _backupEvents.emit(BackupEvent.BackupFailure(snapshot, throwable))
            }
        }
    }

    private suspend fun uploadEncryptedPayload(
        encrypted: BackupEncryptionProvider.EncryptedPayload,
        snapshot: RiskSnapshot
    ): String {
        val uid = Firebase.auth.currentUser?.uid ?: "anonymous"
        val remotePath = "backups/$uid/${snapshot.timestampMillis}.enc"
        val uri = Uri.fromFile(encrypted.file)
        storage.child(remotePath).putFile(uri).await()

        val metadata = mapOf(
            "riskLevel" to snapshot.riskLevel,
            "temperatureCelsius" to snapshot.temperatureCelsius,
            "motionMagnitude" to snapshot.motionMagnitude,
            "batteryPercent" to snapshot.batteryPercent,
            "timestamp" to snapshot.timestampMillis,
            "encryptionKeyAlias" to encrypted.keyAlias,
            "ivBase64" to encrypted.ivBase64
        )
        firestore.collection("backups").add(metadata).await()
        return remotePath
    }

    private fun cleanupFiles(files: List<File>) {
        files.forEach { file ->
            if (file.exists()) {
                file.delete()
            }
        }
    }

    companion object {
        private const val TAG = "FileBackupManager"
    }
}

