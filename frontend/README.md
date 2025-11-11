# Sentinel Cloud Frontend

Minimal React + Tailwind interface that keeps Sentinel Cloud’s automation quiet and calm.

## Quick start

```bash
cd frontend
npm install
npm run dev
```

The app expects the FastAPI backend to run on `http://localhost:8000`. You can adjust the URL by setting `VITE_API_BASE_URL`.

## Design principles

- Surface only the essentials: system status, backup progress, data selections, and vault summaries.
- Abstract away ML telemetry behind warm, human-readable copy.
- Responsive layout tuned for iPhone 13 / Pixel 8 breakpoints.
- Framer Motion adds gentle feedback without feeling busy.


