package com.sentinelcloud.mobile.risk

data class RiskSnapshot(
    val timestampMillis: Long,
    val riskLevel: Float,
    val motionMagnitude: Float,
    val angularVelocity: Float,
    val temperatureCelsius: Float,
    val batteryPercent: Int,
    val reason: RiskReason
) {
    val isThresholdExceeded: Boolean
        get() = riskLevel >= DEFAULT_THRESHOLD

    companion object {
        const val DEFAULT_THRESHOLD: Float = 0.8f

        fun idle(): RiskSnapshot = RiskSnapshot(
            timestampMillis = System.currentTimeMillis(),
            riskLevel = 0f,
            motionMagnitude = 0f,
            angularVelocity = 0f,
            temperatureCelsius = 25f,
            batteryPercent = 100,
            reason = RiskReason.IDLE
        )
    }
}

enum class RiskReason {
    IDLE,
    POTENTIAL_DROP,
    OVERHEAT,
    BATTERY_FAILURE,
    UNKNOWN
}


