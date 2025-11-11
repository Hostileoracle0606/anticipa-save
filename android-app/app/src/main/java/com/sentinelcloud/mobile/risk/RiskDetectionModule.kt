package com.sentinelcloud.mobile.risk

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.BatteryManager
import androidx.annotation.VisibleForTesting
import com.sentinelcloud.mobile.metrics.MetricsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.math.sqrt

class RiskDetectionModule(
    private val context: Context,
    private val applicationScope: CoroutineScope,
    private val onRiskThresholdExceeded: (RiskSnapshot) -> Unit,
    private val metricsRepository: MetricsRepository,
    private val threshold: Float = RiskSnapshot.DEFAULT_THRESHOLD
) {

    sealed interface Status {
        data object Idle : Status
        data class RiskDetected(val snapshot: RiskSnapshot) : Status
        data class SensorUnavailable(val reason: String) : Status
    }

    private val _status = MutableSharedFlow<Status>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val status: SharedFlow<Status> = _status.asSharedFlow()

    private val _riskSnapshot = MutableStateFlow(defaultSnapshot())
    val riskSnapshot: StateFlow<RiskSnapshot> = _riskSnapshot.asStateFlow()

    private val sensorManager: SensorManager? =
        context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager

    private val linearAccelerationSensor: Sensor? =
        sensorManager?.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
    private val gyroscopeSensor: Sensor? =
        sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    private val temperatureSensor: Sensor? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            sensorManager?.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        } else {
            sensorManager?.getDefaultSensor(Sensor.TYPE_TEMPERATURE)
        }

    private var riskJob: Job? = null
    private val listener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            onSensorEvent(event)
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
    }

    fun start() {
        _status.tryEmit(Status.Idle)
        if (sensorManager == null || linearAccelerationSensor == null || gyroscopeSensor == null) {
            _status.tryEmit(Status.SensorUnavailable("Required motion sensors missing"))
            return
        }

        sensorManager.registerListener(
            listener,
            linearAccelerationSensor,
            SensorManager.SENSOR_DELAY_GAME
        )
        sensorManager.registerListener(
            listener,
            gyroscopeSensor,
            SensorManager.SENSOR_DELAY_GAME
        )
        temperatureSensor?.let {
            sensorManager.registerListener(
                listener,
                it,
                SensorManager.SENSOR_DELAY_UI
            )
        }

        riskJob?.cancel()
        riskJob = riskSnapshot
            .filter { it.riskLevel >= threshold }
            .onEach { snapshot ->
                metricsRepository.recordRiskEvent(snapshot)
                onRiskThresholdExceeded(snapshot)
                _status.emit(Status.RiskDetected(snapshot))
            }
            .launchIn(applicationScope)
    }

    fun stop() {
        riskJob?.cancel()
        sensorManager?.unregisterListener(listener)
        _status.tryEmit(Status.Idle)
    }

    private fun onSensorEvent(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_LINEAR_ACCELERATION -> updateWithMotion(event.values)
            Sensor.TYPE_GYROSCOPE -> updateWithGyroscope(event.values)
            Sensor.TYPE_AMBIENT_TEMPERATURE,
            Sensor.TYPE_TEMPERATURE -> updateWithTemperature(event.values.firstOrNull())
        }
    }

    private fun updateWithMotion(values: FloatArray) {
        val magnitude = values.vectorMagnitude()
        updateSnapshot { current ->
            current.copy(
                motionMagnitude = magnitude,
                riskLevel = riskEstimator.estimateRisk(
                    motionMagnitude = magnitude,
                    angularVelocity = current.angularVelocity,
                    temperatureCelsius = current.temperatureCelsius,
                    batteryPercent = current.batteryPercent
                ),
                reason = if (magnitude > MOTION_THRESHOLD) RiskReason.POTENTIAL_DROP else current.reason
            )
        }
    }

    private fun updateWithGyroscope(values: FloatArray) {
        val velocity = values.vectorMagnitude()
        updateSnapshot { current ->
            current.copy(
                angularVelocity = velocity,
                riskLevel = riskEstimator.estimateRisk(
                    motionMagnitude = current.motionMagnitude,
                    angularVelocity = velocity,
                    temperatureCelsius = current.temperatureCelsius,
                    batteryPercent = current.batteryPercent
                )
            )
        }
    }

    private fun updateWithTemperature(value: Float?) {
        val temperatureCelsius = value ?: return
        updateSnapshot { current ->
            current.copy(
                temperatureCelsius = temperatureCelsius,
                riskLevel = riskEstimator.estimateRisk(
                    motionMagnitude = current.motionMagnitude,
                    angularVelocity = current.angularVelocity,
                    temperatureCelsius = temperatureCelsius,
                    batteryPercent = current.batteryPercent
                ),
                reason = if (temperatureCelsius > TEMPERATURE_THRESHOLD) {
                    RiskReason.OVERHEAT
                } else {
                    current.reason
                }
            )
        }
    }

    private fun updateSnapshot(block: (RiskSnapshot) -> RiskSnapshot) {
        val snapshot = block(_riskSnapshot.value).copy(
            timestampMillis = System.currentTimeMillis(),
            batteryPercent = fetchBatteryPercent()
        )
        _riskSnapshot.value = snapshot
    }

    private val riskEstimator by lazy {
        RiskEstimator(context)
    }

    private fun FloatArray.vectorMagnitude(): Float {
        if (isEmpty()) return 0f
        val squared = fold(0f) { acc, component ->
            acc + component * component
        }
        return sqrt(squared)
    }

    companion object {
        private const val MOTION_THRESHOLD = 15f
        private const val TEMPERATURE_THRESHOLD = 45f
    }

    @VisibleForTesting
    internal fun fetchBatteryPercent(): Int {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
        return batteryManager?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) ?: 100
    }

    private fun defaultSnapshot(): RiskSnapshot =
        RiskSnapshot.idle().copy(batteryPercent = fetchBatteryPercent())
}

