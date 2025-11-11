package com.sentinelcloud.mobile.metrics

import android.util.Log
import com.sentinelcloud.mobile.BuildConfig
import com.sentinelcloud.mobile.risk.RiskSnapshot
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class MetricsService(
    private val baseUrl: String = BuildConfig.METRICS_API_BASE_URL,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun publishRiskEvent(snapshot: RiskSnapshot) {
        val body = JSONObject().apply {
            put("type", "risk")
            put("risk_level", snapshot.riskLevel)
            put("temperature_celsius", snapshot.temperatureCelsius)
            put("motion_magnitude", snapshot.motionMagnitude)
            put("angular_velocity", snapshot.angularVelocity)
            put("battery_percent", snapshot.batteryPercent)
            put("timestamp", snapshot.timestampMillis)
            put("reason", snapshot.reason.name)
        }
        postJson("/metrics/ingest", body)
    }

    suspend fun publishBackupSuccess(
        snapshot: RiskSnapshot,
        remotePath: String,
        bytesUploaded: Long,
        encryptionIvBase64: String
    ) {
        val body = JSONObject().apply {
            put("type", "backup_success")
            put("remote_path", remotePath)
            put("bytes_uploaded", bytesUploaded)
            put("timestamp", snapshot.timestampMillis)
            put("risk_level", snapshot.riskLevel)
            put("encryption_iv_base64", encryptionIvBase64)
        }
        postJson("/metrics/ingest", body)
    }

    suspend fun publishBackupFailure(snapshot: RiskSnapshot, throwable: Throwable?) {
        val body = JSONObject().apply {
            put("type", "backup_failure")
            put("timestamp", snapshot.timestampMillis)
            put("risk_level", snapshot.riskLevel)
            put("error_message", throwable?.message ?: "Unknown error")
        }
        postJson("/metrics/ingest", body)
    }

    private suspend fun postJson(path: String, body: JSONObject) =
        withContext(dispatcher) {
            runCatching {
                val connection = (URL(baseUrl + path).openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    doInput = true
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    connectTimeout = 10_000
                    readTimeout = 10_000
                }

                connection.outputStream.use { output ->
                    OutputStreamWriter(output).use { writer ->
                        writer.write(body.toString())
                        writer.flush()
                    }
                }

                val responseCode = connection.responseCode
                if (responseCode !in 200..299) {
                    val error = connection.errorStream?.let { stream ->
                        BufferedReader(InputStreamReader(stream)).use { it.readText() }
                    }
                    Log.w(TAG, "Metrics API responded with $responseCode : $error")
                }
                connection.disconnect()
            }.onFailure { throwable ->
                Log.w(TAG, "Failed to post metrics", throwable)
            }
        }

    companion object {
        private const val TAG = "MetricsService"
    }
}


