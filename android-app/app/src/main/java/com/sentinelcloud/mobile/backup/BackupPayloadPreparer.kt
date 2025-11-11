package com.sentinelcloud.mobile.backup

import android.content.Context
import android.util.JsonWriter
import com.sentinelcloud.mobile.risk.RiskSnapshot
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

class BackupPayloadPreparer(
    private val context: Context
) {

    fun createArchive(snapshot: RiskSnapshot): File {
        val backupsDir = File(context.cacheDir, "backups").apply {
            if (!exists()) {
                mkdirs()
            }
        }
        val backupFile = File(backupsDir, "risk-${snapshot.timestampMillis}.json")

        FileOutputStream(backupFile).use { outputStream ->
            OutputStreamWriter(outputStream, Charsets.UTF_8).use { writer ->
                JsonWriter(writer).use { json ->
                    json.beginObject()
                    json.name("timestampMillis").value(snapshot.timestampMillis)
                    json.name("riskLevel").value(snapshot.riskLevel.toDouble())
                    json.name("motionMagnitude").value(snapshot.motionMagnitude.toDouble())
                    json.name("angularVelocity").value(snapshot.angularVelocity.toDouble())
                    json.name("temperatureCelsius").value(snapshot.temperatureCelsius.toDouble())
                    json.name("batteryPercent").value(snapshot.batteryPercent)
                    json.name("reason").value(snapshot.reason.name)
                    json.endObject()
                }
            }
        }

        return backupFile
    }
}


