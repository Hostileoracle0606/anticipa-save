# Sentinel Cloud – Quietly Intelligent Data Protection

Sentinel Cloud is an end‑to‑end proof of concept for an AI‑driven, quietly intelligent backup assistant.  
It predicts when a mobile device is at risk, encrypts personal content pre‑emptively, and keeps the user informed with calm, minimal UI copy rather than noisy telemetry.

The repository contains three coordinated surfaces:

| Surface | Purpose | Tech Stack |
| --- | --- | --- |
| `android-app/` | On-device risk detection + encrypted backup trigger | Kotlin · Jetpack Compose · TensorFlow Lite · Firebase |
| `frontend/` | Minimal user dashboard for status, toggles, and vault access | React 18 · Vite · Tailwind · Framer Motion |
| `backend/` | API façade + simulated ML pipeline + vault metadata | FastAPI · Python 3.11 · Firebase client libs (simulated) |

---

## 1. System Overview

### Risk-sensitive backup flow
1. **Device Monitoring** – the Android app streams motion, thermal, and battery signals to a TFLite model (`RiskDetectionModule`).
2. **Risk Classification** – when the risk score crosses a threshold, the app:
   - encrypts the selected content set (photos, videos, docs, messages),
   - pushes the payload to Firebase Storage,
   - posts summary metrics to the FastAPI backend.
3. **Vault Synchronisation** – the backend logs metadata, exposes status summaries, and keeps the React dashboard updated with short, reassuring copy (“Stable”, “Backing up”, “Protected”).
4. **User Awareness** – the frontend never shows raw data; it narrates high‑level status, lets users toggle which content types are mirrored, and offers restore affordances.

---

## 2. Repository Structure

```
anticipa-save/
├── android-app/          # Android Studio project (Kotlin + Compose)
│   ├── app/src/main/     # Activities, UI, risk & backup modules
│   └── README.md         # Firebase + build instructions
├── frontend/             # Vite + React dashboard
│   ├── src/components/   # StatusCard, DataSelector, VaultList, etc.
│   └── README.md         # Frontend quick-start
├── backend/              # FastAPI service simulating TFLite + Firebase
│   ├── app/              # API routes / services / schemas
│   └── README.md         # Backend quick-start
├── .gitignore            # ignores for build artifacts & secrets
└── README.md             # (this file)
```

---

## 3. How Everything Works

### Android mobile client (`android-app/`)
* `MainActivity` wires together risk detection, backup manager, and MetricsService.
* `RiskDetectionModule` reads sensors, feeds the TFLite model (`RiskEstimator`), and raises callbacks when `riskLevel ≥ 0.8`.
* `FileBackupManager` compresses + AES‑GCM encrypts the payload, uploads to Firebase Storage, and records metadata.
* `DeviceProtectionScreen` in Jetpack Compose mirrors the brand language of the landing page while keeping copy minimal.
* Configuration notes:
  - Provide a real `google-services.json` (ignored in Git) for Firebase.
  - Drop your TFLite model inside `app/src/main/assets/risk_model.tflite`.

### FastAPI backend (`backend/`)
* `services/risk.py` simulates sensor inference and sync progress.
* `services/state.py` orchestrates overall status, last-backup timestamps, and sync snapshots.
* `services/vault.py` returns mock vault entries (type, size, relative time).
* REST endpoints (all under `/api`):
  - `GET /system/state` → `{ status, sync }`
  - `GET /vault/items` → vault summary
  - `POST /preferences/content` → enable/disable content types
* Designed so a real ML pipeline can replace the stubs without changing the frontend contract.

### React dashboard (`frontend/`)
* Provides **three panels only**:
  - `StatusCard` – “Stable”, “Backing up”, etc., with last backup time, files protected, and sync progress.
  - `DataSelector` – filter chips for Photos / Videos / Messages / Documents.
  - `VaultList` – minimal list with restore buttons.
* `useSystemState` hook polls `/api/system/state` and converts timestamps into relative copy.
* Tailored for mobile breakpoints (iPhone 13 / Pixel 8 sizing) with a blue gradient hero header that mirrors the marketing site.

---

## 4. Getting Started

### Prerequisites
* Node.js 18+ and npm
* Python 3.11 (for the backend)
* Android Studio (Hedgehog or newer) + Android SDK 34

### Backend (FastAPI)
```bash
cd backend
python -m venv .venv
.venv\Scripts\activate        # Windows
pip install -e .[dev]
uvicorn app.main:app --reload --port 8000
```

### Frontend (React dashboard)
```bash
cd frontend
npm install
npm run dev                   # proxies API requests to http://localhost:8000
```

### Android app
1. Open `android-app/` in Android Studio.
2. Supply `app/google-services.json`.
3. Add `risk_model.tflite` to `app/src/main/assets/`.
4. Sync, build, and deploy (`Run > Run 'app'`).

---

## 5. Development Workflow

1. Run the backend (`uvicorn …`).
2. Start the frontend (`npm run dev`).
3. Install or run the Android app on an emulator/physical device.
4. Adjust risk simulation parameters in `backend/app/services/risk.py` or wire up real telemetry.
5. Keep build outputs out of Git—`.gitignore` already excludes `android-app/app/build/`, `gradle-8.7-bin/`, `frontend/node_modules`, and `backend/.venv`.

---

## 6. Contributing / Next Steps

* Replace the mock TFLite estimator with your trained model.
* Wire Firebase Auth to personalise vault content.
* Swap the simulated FastAPI services with calls into real cloud storage and analytics (Cloud Run, BigQuery).
* Expand the Compose UI to include backup history, device health trends, or push notification controls—while preserving the “quiet intelligence” tone.

Feel free to open issues or pull requests if you build on top of Sentinel Cloud. The goal is to keep the product calm, trustworthy, and proactive—no flashing warnings, just reassurance that your data is already safe.***