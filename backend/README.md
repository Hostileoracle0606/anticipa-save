# Sentinel Cloud Backend

FastAPI service that simulates Sentinel Cloud's quietly intelligent automation layer.

## Features

- Mock TensorFlow Lite risk engine that drives state transitions.
- Backup orchestration stubs with encrypted vault summaries.
- Content preference API for selecting which data types are mirrored.
- CORS-enabled REST interface consumed by the React frontend.

## Running Locally

```bash
cd backend
python -m venv .venv
.venv\Scripts\activate  # or `source .venv/bin/activate` on macOS/Linux
pip install -e .[dev]
uvicorn app.main:app --reload --port 8000
```

Run tests:

```bash
pytest
```


