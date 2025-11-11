# Sentinel Cloud Android App

The `android-app` module delivers the on-device intelligence and secure backup workflow for Sentinel Cloud. It complements the existing landing page and analytics dashboard by running on Android devices, predicting failure risk, and syncing data to the cloud before damage happens.

## Tech Stack

- Kotlin + Jetpack Compose UI
- TensorFlow Lite (on-device inference with heuristic fallback)
- Firebase Storage, Firestore, and Auth
- FastAPI metrics ingestion via HTTPS
- WorkManager + coroutines for background work

## Project Structure

```
android-app/
├── app/
│   ├── src/main/java/com/sentinelcloud/mobile/
│   │   ├── risk/ … Predictive risk detection + TensorFlow Lite wrapper
│   │   ├── backup/ … AES-GCM encryption + Firebase upload pipeline
│   │   ├── metrics/ … Client for FastAPI metrics service
│   │   ├── ui/ … Compose screens + theming
│   │   └── SentinelCloudApp.kt … Application wiring
│   ├── src/main/res/ … Android resources (theme, icons, strings)
│   └── src/main/assets/ … Placeholder for `risk_model.tflite`
└── build.gradle.kts … Root build configuration
```

## Getting Started

1. **Open in Android Studio (Giraffe or newer)**  
   - `File > Open…` and select `android-app`.
   - Android Studio will download the Android Gradle Plugin (AGP 8.5.2) and Kotlin 1.9.24 automatically.

2. **Add Firebase configuration**  
   - Place your `google-services.json` in `android-app/app/`.
   - Configure Firebase Auth, Firestore, and Storage in the Firebase console.

3. **Provide the TensorFlow Lite model**  
   - Export your trained model as `risk_model.tflite` and drop it into `android-app/app/src/main/assets/`.
   - A heuristic estimator is used automatically if the file is missing.

4. **Configure metrics backend**  
   - Update `BuildConfig.METRICS_API_BASE_URL` in `app/build.gradle.kts` to point to your FastAPI deployment.
   - Ensure CORS allows mobile clients.

5. **Run the app**  
   - Select an Android 13+ device or emulator (API 34 recommended).  
   - Press **Run**. Grant motion/body sensor + storage permissions on first launch.

## Modules

- `RiskDetectionModule` – Streams accelerometer, gyroscope, and temperature data, estimates risk with TensorFlow Lite, and notifies when thresholds are crossed.
- `FileBackupManager` – Compresses & encrypts payloads, uploads to Firebase Storage, and logs results to Firestore & the metrics API.
- `MetricsRepository` – Relay of risk/backup events to FastAPI for dashboards and analytics.
- `DeviceProtectionScreen` – Compose UI that surfaces live telemetry and backup activity to the user.

## Next Steps

- Connect real file inventory & user-selected backup sources.
- Replace heuristic estimator once the trained `.tflite` model is ready.
- Add WorkManager jobs for periodic health checks and offline retries.
- Expand UI with history views, settings, and account management.

For any questions, ping the Sentinel Cloud team on Slack (#sentinel-mobile).


