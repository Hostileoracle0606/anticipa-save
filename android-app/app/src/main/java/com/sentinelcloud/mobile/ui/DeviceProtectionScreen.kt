package com.sentinelcloud.mobile.ui

import android.text.format.Formatter
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.DeviceThermostat
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.Message
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CircularProgressIndicator
import com.sentinelcloud.mobile.backup.BackupEvent
import com.sentinelcloud.mobile.risk.RiskSnapshot
import com.sentinelcloud.mobile.risk.RiskReason
import java.text.DateFormat
import java.util.Date
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DeviceProtectionScreen(
    modifier: Modifier = Modifier,
    riskSnapshot: RiskSnapshot,
    latestBackupEvent: BackupEvent?,
    paddingValues: PaddingValues,
    onEnableProtection: () -> Unit = {},
    onViewDashboard: () -> Unit = {},
    onOpenCloudVault: () -> Unit = {},
    onChangeBackupScope: (String, Boolean) -> Unit = { _, _ -> }
) {
    val colorScheme = MaterialTheme.colorScheme
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            colorScheme.background,
            colorScheme.background,
            colorScheme.surface
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                HeroHeader(
                    riskSnapshot = riskSnapshot,
                    latestBackupEvent = latestBackupEvent,
                    onEnableProtection = onEnableProtection,
                    onViewDashboard = onViewDashboard
                )
            }
            item {
                MetricsSection(riskSnapshot = riskSnapshot)
            }
            item {
                ModuleHighlights()
            }
            item {
                BackupScopeCard(onChangeBackupScope = onChangeBackupScope)
            }
            item {
                BackupTimelineCard(latestBackupEvent = latestBackupEvent)
            }
            item {
                CloudVaultCard(
                    latestBackupEvent = latestBackupEvent,
                    onOpenCloudVault = onOpenCloudVault
                )
            }
            item {
                SensorHealthCard(riskSnapshot = riskSnapshot)
            }
            item {
                RecentBackupsCard(latestBackupEvent = latestBackupEvent)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HeroHeader(
    riskSnapshot: RiskSnapshot,
    latestBackupEvent: BackupEvent?,
    onEnableProtection: () -> Unit,
    onViewDashboard: () -> Unit
) {
    val gradientBrush = Brush.linearGradient(
        listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
            MaterialTheme.colorScheme.secondary
        )
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.linearGradient(
                listOf(
                    Color.White.copy(alpha = 0.35f),
                    Color.White.copy(alpha = 0.15f)
                )
            )
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .background(gradientBrush, shape = RoundedCornerShape(32.dp))
                .padding(26.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                StatusBadge()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                append("Protect your data ")
                                withStyle(SpanStyle(brush = Brush.linearGradient(listOf(Color.White, Color.White.copy(alpha = 0.8f))))) {
                                    append("before it's too late")
                                }
                            },
                            style = MaterialTheme.typography.headlineLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 38.sp
                            )
                        )
                        Text(
                            text = "AI-powered risk forecasting automatically encrypts and syncs your memories the moment danger is detected.",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color.White.copy(alpha = 0.85f),
                                lineHeight = 22.sp
                            )
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatChip(label = "Prediction accuracy", value = "91%")
                            StatChip(label = "Backup success", value = "99.3%")
                            val recent = (latestBackupEvent as? BackupEvent.BackupSuccess)
                            StatChip(
                                label = "Last backup",
                                value = recent?.snapshot?.let { formatRelativeTime(it.timestampMillis) } ?: "Awaiting trigger"
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = onEnableProtection,
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Security,
                                    contentDescription = null,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text("Enable protection")
                            }
                            OutlinedButton(
                                onClick = onViewDashboard,
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.White.copy(alpha = 0.12f),
                                    contentColor = Color.White
                                ),
                                border = null
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Timeline,
                                    contentDescription = null,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text("View dashboard")
                            }
                        }
                        Text(
                            text = "No credit card required • Free 30-day trial • Cancel anytime",
                            style = MaterialTheme.typography.labelMedium.copy(color = Color.White.copy(alpha = 0.7f))
                        )
                    }
                    RiskGauge(
                        modifier = Modifier
                            .weight(0.9f)
                            .wrapContentHeight(),
                        riskSnapshot = riskSnapshot
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusBadge() {
    Surface(
        modifier = Modifier,
        color = Color.White.copy(alpha = 0.18f),
        shape = RoundedCornerShape(50)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PulsingStatusDot()
            Text(
                text = "AI-powered predictive protection",
                style = MaterialTheme.typography.labelLarge.copy(color = Color.White, fontWeight = FontWeight.Medium)
            )
        }
    }
}

@Composable
private fun PulsingStatusDot() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val outerScale = infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .graphicsLayer {
                    scaleX = outerScale.value
                    scaleY = outerScale.value
                }
                .background(Color.White.copy(alpha = 0.2f), shape = CircleShape)
        )
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}

@Composable
private fun RiskGauge(
    modifier: Modifier = Modifier,
    riskSnapshot: RiskSnapshot
) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White.copy(alpha = 0.12f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { riskSnapshot.riskLevel.coerceIn(0f, 1f) },
                    modifier = Modifier.size(120.dp),
                    strokeWidth = 8.dp,
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${(riskSnapshot.riskLevel * 100).roundToInt()}%",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = riskSnapshot.reason.toDisplayText(),
                        style = MaterialTheme.typography.labelMedium.copy(color = Color.White.copy(alpha = 0.8f))
                    )
                }
            }
            LinearProgressIndicator(
                progress = { riskSnapshot.motionMagnitude.coerceIn(0f, 20f) / 20f },
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.25f)
            )
            Text(
                text = "Motion activity ${riskSnapshot.motionMagnitude.formatDigits(1)} m/s²",
                style = MaterialTheme.typography.labelMedium.copy(color = Color.White.copy(alpha = 0.85f))
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MetricsSection(riskSnapshot: RiskSnapshot) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Live device insights",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MetricCard(
                title = "Motion stability",
                headline = "${riskSnapshot.motionMagnitude.formatDigits(1)} m/s²",
                caption = if (riskSnapshot.motionMagnitude > 12f) "High movement detected" else "Stable movement envelope",
                icon = Icons.Rounded.Insights,
                tone = if (riskSnapshot.motionMagnitude > 12f) MetricTone.Warning else MetricTone.Success
            )
            MetricCard(
                title = "Thermal status",
                headline = "${riskSnapshot.temperatureCelsius.formatDigits(1)} °C",
                caption = temperatureCaption(riskSnapshot.temperatureCelsius),
                icon = Icons.Rounded.DeviceThermostat,
                tone = when {
                    riskSnapshot.temperatureCelsius > 45 -> MetricTone.Alert
                    riskSnapshot.temperatureCelsius > 38 -> MetricTone.Warning
                    else -> MetricTone.Success
                }
            )
            MetricCard(
                title = "Battery health",
                headline = "${riskSnapshot.batteryPercent}%",
                caption = if (riskSnapshot.batteryPercent < 20) "Consider charging soon" else "Optimal for rapid response",
                icon = Icons.Rounded.BatteryChargingFull,
                tone = when {
                    riskSnapshot.batteryPercent < 20 -> MetricTone.Alert
                    riskSnapshot.batteryPercent < 40 -> MetricTone.Warning
                    else -> MetricTone.Success
                }
            )
        }
    }
}

@Composable
private fun ModuleHighlights() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Smart protection stack",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
        )
        HighlightCard(
            icon = Icons.Rounded.Insights,
            title = "Risk Detection Engine",
            description = "TensorFlow Lite classifies motion and thermal anomalies in real time, orchestrating proactive responses before impact.",
            bulletPoints = listOf(
                "Live acceleration + gyroscope fusion",
                "Custom risk thresholds per device",
                "Context-aware alerting & haptics"
            ),
            accentBrush = Brush.linearGradient(
                listOf(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)
                )
            ),
            actionLabel = "Tune sensitivity"
        )
        HighlightCard(
            icon = Icons.Rounded.CloudDone,
            title = "Backup Orchestration",
            description = "Encrypted payloads stream to Firebase Storage with per-event metrics logged for your Sentinel dashboard.",
            bulletPoints = listOf(
                "AES-256 encryption via Android Keystore",
                "Firestore metadata + FastAPI analytics",
                "Automatic retries on flaky networks"
            ),
            accentBrush = Brush.linearGradient(
                listOf(
                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.18f),
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                )
            ),
            actionLabel = "Review backups"
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BackupScopeCard(
    onChangeBackupScope: (String, Boolean) -> Unit
) {
    val scopeOptions = listOf(
        "Photos" to Icons.Rounded.Image,
        "Videos" to Icons.Rounded.PlayCircle,
        "Messages" to Icons.Rounded.Message,
        "Documents" to Icons.Rounded.Description
    )
    val selections = remember {
        mutableStateMapOf(
            "Photos" to true,
            "Videos" to true,
            "Messages" to false,
            "Documents" to true
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                text = "What Sentinel protects",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
            )
            Text(
                text = "Toggle the collections you want ready in the cloud vault.",
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                scopeOptions.forEach { (label, icon) ->
                    val selected = selections[label] == true
                    FilterChip(
                        selected = selected,
                        onClick = {
                            val next = !selected
                            selections[label] = next
                            onChangeBackupScope(label, next)
                        },
                        label = { Text(label) },
                        leadingIcon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = null
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
                            selectedLabelColor = MaterialTheme.colorScheme.primary,
                            selectedLeadingIconColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
            AssistChip(
                onClick = { onChangeBackupScope("Custom folder", true) },
                label = { Text("Add folder") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Folder,
                        contentDescription = null
                    )
                }
            )
        }
    }
}

@Composable
private fun HighlightCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    bulletPoints: List<String>,
    accentBrush: Brush,
    actionLabel: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
    ) {
        Box(
            modifier = Modifier
                .background(accentBrush, shape = RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.padding(10.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    bulletPoints.forEach { point ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = point,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                TextButton(onClick = { /* TODO: Hook into app navigation */ }) {
                    Text(actionLabel)
                }
            }
        }
    }
}

@Composable
private fun BackupTimelineCard(
    latestBackupEvent: BackupEvent?
) {
    val context = LocalContext.current
    val timelineItems = remember(latestBackupEvent) {
        buildTimeline(latestBackupEvent, context = context)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.CloudUpload,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Column {
                    Text(
                        text = "Backup activity",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = "Every event is logged for your Sentinel dashboard analytics.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }
            timelineItems.forEachIndexed { index, item ->
                TimelineRow(item)
                if (index != timelineItems.lastIndex) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                }
            }
        }
    }
}

@Composable
private fun TimelineRow(item: TimelineItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = item.status.containerColor(MaterialTheme.colorScheme),
            modifier = Modifier.size(42.dp)
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = item.status.contentColor(MaterialTheme.colorScheme),
                modifier = Modifier.padding(10.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
            )
            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        AssistChip(
            onClick = {},
            label = { Text(item.timestamp) },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                labelColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        )
    }
}

@Composable
private fun CloudVaultCard(
    latestBackupEvent: BackupEvent?,
    onOpenCloudVault: () -> Unit
) {
    val statusLine = when (latestBackupEvent) {
        is BackupEvent.BackupSuccess -> "Vault updated ${formatRelativeTime(latestBackupEvent.snapshot.timestampMillis)}."
        is BackupEvent.BackupQueued -> "Encryption in progress — upload will begin once packaged."
        is BackupEvent.BackupFailure -> "Retry scheduled once conditions are healthy."
        null -> "Standing by for your first automated backup."
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.22f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Cloud,
                        contentDescription = null,
                        modifier = Modifier.padding(12.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Column {
                    Text(
                        text = "Cloud vault",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = statusLine,
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(onClick = onOpenCloudVault) {
                    Icon(
                        imageVector = Icons.Rounded.CloudUpload,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Open vault")
                }
                OutlinedButton(onClick = onOpenCloudVault) {
                    Icon(
                        imageVector = Icons.Rounded.Upload,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Upload from phone")
                }
            }
            TextButton(onClick = onOpenCloudVault) {
                Text("Access on sentinelcloud.app")
            }
        }
    }
}

@Composable
private fun RecentBackupsCard(
    latestBackupEvent: BackupEvent?
) {
    val context = LocalContext.current
    val items = remember(latestBackupEvent) {
        buildRecentBackups(latestBackupEvent, context)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Recent backups",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
            )
            items.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                modifier = Modifier.padding(10.dp),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        Column {
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
                            )
                            Text(
                                text = "${item.sizeLabel} • ${item.relativeTime}",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }
                    AssistChip(
                        onClick = {},
                        label = { Text("Restore") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.CloudDone,
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SensorHealthCard(
    riskSnapshot: RiskSnapshot
) {
    val sensors = remember(riskSnapshot) {
        listOf(
            SensorHealth(
                title = "Motion sensors",
                detail = "${riskSnapshot.motionMagnitude.formatDigits(1)} m/s² • ${riskSnapshot.angularVelocity.formatDigits(1)} °/s",
                tone = when {
                    riskSnapshot.motionMagnitude > 12f || riskSnapshot.angularVelocity > 12f -> SensorTone.Alert
                    riskSnapshot.motionMagnitude > 8f || riskSnapshot.angularVelocity > 8f -> SensorTone.Elevated
                    else -> SensorTone.Stable
                }
            ),
            SensorHealth(
                title = "Thermal monitor",
                detail = "${riskSnapshot.temperatureCelsius.formatDigits(1)} °C core temperature",
                tone = when {
                    riskSnapshot.temperatureCelsius > 45 -> SensorTone.Alert
                    riskSnapshot.temperatureCelsius > 38 -> SensorTone.Elevated
                    else -> SensorTone.Stable
                }
            ),
            SensorHealth(
                title = "Battery health",
                detail = "${riskSnapshot.batteryPercent}% available capacity",
                tone = when {
                    riskSnapshot.batteryPercent < 20 -> SensorTone.Alert
                    riskSnapshot.batteryPercent < 40 -> SensorTone.Elevated
                    else -> SensorTone.Stable
                }
            )
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.28f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                text = "Sensor health",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
            )
            sensors.forEach { sensor ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = sensor.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
                        )
                        Text(
                            text = sensor.detail,
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                    SensorStatusPill(sensor.tone)
                }
            }
        }
    }
}

@Composable
private fun SensorStatusPill(tone: SensorTone) {
    val colors = tone.colors(MaterialTheme.colorScheme)
    Surface(
        color = colors.first,
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = tone.label,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium.copy(color = colors.second, fontWeight = FontWeight.Medium)
        )
    }
}

@Composable
private fun StatChip(label: String, value: String) {
    Surface(
        color = Color.White.copy(alpha = 0.12f),
        shape = RoundedCornerShape(40)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.75f))
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge.copy(color = Color.White, fontWeight = FontWeight.SemiBold)
            )
        }
    }
}

private fun formatRelativeTime(timestampMillis: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestampMillis
    val minutes = diff / (60 * 1000)
    val hours = diff / (60 * 60 * 1000)
    return when {
        diff < 60_000 -> "Just now"
        diff < 3_600_000 -> "$minutes min ago"
        diff < 2 * 3_600_000 -> "1 hour ago"
        hours < 24 -> "$hours hrs ago"
        else -> DateFormat.getDateInstance(DateFormat.SHORT).format(Date(timestampMillis))
    }
}

private fun temperatureCaption(temp: Float): String = when {
    temp > 45 -> "Device overheating — throttling enabled"
    temp > 38 -> "Elevated temperature — monitoring closely"
    else -> "Thermals within optimal range"
}

private fun Float.formatDigits(digits: Int): String =
    "%.${digits}f".format(this)

private fun RiskReason.toDisplayText(): String = when (this) {
    RiskReason.IDLE -> "Stable"
    RiskReason.POTENTIAL_DROP -> "Drop risk"
    RiskReason.OVERHEAT -> "Thermal spike"
    RiskReason.BATTERY_FAILURE -> "Battery fault"
    RiskReason.UNKNOWN -> "Monitoring"
}

private data class TimelineItem(
    val title: String,
    val subtitle: String,
    val timestamp: String,
    val status: TimelineStatus,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

private enum class TimelineStatus {
    Success, Pending, Warning;

    fun containerColor(colorScheme: ColorScheme): Color = when (this) {
        Success -> colorScheme.primary.copy(alpha = 0.12f)
        Pending -> colorScheme.secondaryContainer.copy(alpha = 0.6f)
        Warning -> colorScheme.error.copy(alpha = 0.15f)
    }

    fun contentColor(colorScheme: ColorScheme): Color = when (this) {
        Success -> colorScheme.primary
        Pending -> colorScheme.onSecondaryContainer
        Warning -> colorScheme.error
    }
}

private data class SensorHealth(
    val title: String,
    val detail: String,
    val tone: SensorTone
)

private enum class SensorTone(val label: String) {
    Stable("Stable"),
    Elevated("Elevated"),
    Alert("Critical");

    fun colors(colorScheme: ColorScheme): Pair<Color, Color> = when (this) {
        Stable -> colorScheme.secondaryContainer.copy(alpha = 0.6f) to colorScheme.onSecondaryContainer
        Elevated -> colorScheme.primary.copy(alpha = 0.16f) to colorScheme.primary
        Alert -> colorScheme.error.copy(alpha = 0.18f) to colorScheme.error
    }
}

private fun buildTimeline(
    latestBackupEvent: BackupEvent?,
    context: android.content.Context
): List<TimelineItem> {
    val formatter = DateFormat.getTimeInstance(DateFormat.SHORT)
    return when (latestBackupEvent) {
        is BackupEvent.BackupSuccess -> {
            val bytes = Formatter.formatShortFileSize(context, latestBackupEvent.bytesUploaded)
            listOf(
                TimelineItem(
                    title = "Backup completed",
                    subtitle = "Uploaded $bytes to ${latestBackupEvent.remotePath}",
                    timestamp = formatter.format(Date(latestBackupEvent.snapshot.timestampMillis)),
                    status = TimelineStatus.Success,
                    icon = Icons.Rounded.CloudDone
                ),
                TimelineItem(
                    title = "Risk threshold exceeded",
                    subtitle = "Risk score ${(latestBackupEvent.snapshot.riskLevel * 100).roundToInt()}% triggered automated backup.",
                    timestamp = formatter.format(Date(latestBackupEvent.snapshot.timestampMillis)),
                    status = TimelineStatus.Pending,
                    icon = Icons.Rounded.Security
                )
            )
        }

        is BackupEvent.BackupFailure -> {
            listOf(
                TimelineItem(
                    title = "Backup failed",
                    subtitle = latestBackupEvent.throwable?.message ?: "Unknown error. Will retry automatically.",
                    timestamp = formatter.format(Date(latestBackupEvent.snapshot.timestampMillis)),
                    status = TimelineStatus.Warning,
                    icon = Icons.Rounded.CloudUpload
                ),
                TimelineItem(
                    title = "Retry scheduled",
                    subtitle = "We will retry once connectivity and thermals stabilise.",
                    timestamp = "Queued",
                    status = TimelineStatus.Pending,
                    icon = Icons.Rounded.Timeline
                )
            )
        }

        is BackupEvent.BackupQueued -> {
            listOf(
                TimelineItem(
                    title = "Backup queued",
                    subtitle = "Encryption in progress. Upload will begin once files are packaged.",
                    timestamp = "Preparing",
                    status = TimelineStatus.Pending,
                    icon = Icons.Rounded.CloudUpload
                )
            )
        }

        null -> listOf(
            TimelineItem(
                title = "Standing by",
                subtitle = "Risk below threshold. Sentinel Cloud will react instantly when danger is detected.",
                timestamp = "Live",
                status = TimelineStatus.Pending,
                icon = Icons.Rounded.Security
            )
        )
    }
}

private data class BackedUpItem(
    val name: String,
    val sizeLabel: String,
    val relativeTime: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

private fun buildRecentBackups(
    latestBackupEvent: BackupEvent?,
    context: android.content.Context
): List<BackedUpItem> {
    return when (latestBackupEvent) {
        is BackupEvent.BackupSuccess -> listOf(
            BackedUpItem(
                name = "Risk snapshot",
                sizeLabel = Formatter.formatShortFileSize(context, latestBackupEvent.bytesUploaded),
                relativeTime = formatRelativeTime(latestBackupEvent.snapshot.timestampMillis),
                icon = Icons.Rounded.Description
            )
        )
        else -> listOf(
            BackedUpItem(
                name = "Camera roll",
                sizeLabel = Formatter.formatShortFileSize(context, 520L * 1024 * 1024),
                relativeTime = "Synced yesterday",
                icon = Icons.Rounded.Image
            ),
            BackedUpItem(
                name = "Videos",
                sizeLabel = Formatter.formatShortFileSize(context, 1_024L * 1024 * 1024),
                relativeTime = "Synced 2 days ago",
                icon = Icons.Rounded.PlayCircle
            ),
            BackedUpItem(
                name = "Documents",
                sizeLabel = Formatter.formatShortFileSize(context, 180L * 1024 * 1024),
                relativeTime = "Synced this week",
                icon = Icons.Rounded.Description
            )
        )
    }
}

private enum class MetricTone {
    Success, Warning, Alert;

    fun container(colorScheme: ColorScheme): Color = when (this) {
        Success -> colorScheme.secondaryContainer.copy(alpha = 0.7f)
        Warning -> colorScheme.primary.copy(alpha = 0.12f)
        Alert -> colorScheme.error.copy(alpha = 0.18f)
    }

    fun content(colorScheme: ColorScheme): Color = when (this) {
        Success -> colorScheme.onSecondaryContainer
        Warning -> colorScheme.primary
        Alert -> colorScheme.error
    }
}

@Composable
private fun MetricCard(
    title: String,
    headline: String,
    caption: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tone: MetricTone
) {
    val container = tone.container(MaterialTheme.colorScheme)
    val content = tone.content(MaterialTheme.colorScheme)
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier
                .widthIn(min = 220.dp)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = container
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp),
                    tint = content
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
            )
            Text(
                text = headline,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = caption,
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }
    }
}
