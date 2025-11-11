package com.sentinelcloud.mobile.risk

import android.content.Context
import android.util.Log
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.Interpreter.Options
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class RiskEstimator(
    private val context: Context,
    private val threshold: Float = RiskSnapshot.DEFAULT_THRESHOLD
) {

    private val interpreterLock = ReentrantLock()
    private val interpreter: Interpreter? by lazy {
        loadInterpreter()
    }

    fun estimateRisk(
        motionMagnitude: Float,
        angularVelocity: Float,
        temperatureCelsius: Float,
        batteryPercent: Int
    ): Float {
        val model = interpreter ?: return heuristicRisk(
            motionMagnitude,
            angularVelocity,
            temperatureCelsius
        )

        val input = ByteBuffer.allocateDirect(4 * INPUT_FEATURE_COUNT).apply {
            order(ByteOrder.nativeOrder())
            putFloat(motionMagnitude)
            putFloat(angularVelocity)
            putFloat(temperatureCelsius)
            putFloat(batteryPercent / 100f)
            rewind()
        }

        val output = ByteBuffer.allocateDirect(4).apply {
            order(ByteOrder.nativeOrder())
        }

        interpreterLock.withLock {
            try {
                model.run(input, output)
            } catch (t: Throwable) {
                Log.e(TAG, "TensorFlow Lite inference failed, falling back to heuristic", t)
                return heuristicRisk(motionMagnitude, angularVelocity, temperatureCelsius)
            }
        }

        output.rewind()
        val risk = output.float
        return risk.coerceIn(0f, 1f)
    }

    private fun heuristicRisk(
        motionMagnitude: Float,
        angularVelocity: Float,
        temperatureCelsius: Float
    ): Float {
        val motionRisk = (motionMagnitude / 30f).coerceIn(0f, 1f)
        val angularRisk = (angularVelocity / 20f).coerceIn(0f, 1f)
        val thermalRisk = ((temperatureCelsius - 40f) / 20f).coerceIn(0f, 1f)
        val rawRisk = (motionRisk * 0.5f) + (angularRisk * 0.3f) + (thermalRisk * 0.2f)
        return rawRisk.coerceIn(0f, 1f)
    }

    private fun loadInterpreter(): Interpreter? {
        return try {
            val modelBuffer = context.assets.openFd(MODEL_PATH).use { fd ->
                fd.createInputStream().use { input ->
                    val bytes = input.readBytes()
                    ByteBuffer.allocateDirect(bytes.size).apply {
                        order(ByteOrder.nativeOrder())
                        put(bytes)
                        rewind()
                    }
                }
            }
            Interpreter(modelBuffer, Options().apply {
                setNumThreads(Runtime.getRuntime().availableProcessors().coerceAtMost(4))
            })
        } catch (t: Throwable) {
            Log.w(TAG, "Unable to load TensorFlow Lite model. Using heuristics.", t)
            null
        }
    }

    companion object {
        private const val TAG = "RiskEstimator"
        private const val INPUT_FEATURE_COUNT = 4
        private const val MODEL_PATH = "risk_model.tflite"
    }
}


