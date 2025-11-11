from __future__ import annotations

from dataclasses import dataclass, field
from typing import Dict

from fastapi import Depends


@dataclass
class TelemetryStore:
    content_preferences: Dict[str, bool] = field(
        default_factory=lambda: {
            "photos": True,
            "videos": True,
            "messages": False,
            "documents": True,
        }
    )

    def update_content(self, key: str, enabled: bool) -> None:
        self.content_preferences[key] = enabled


_telemetry_singleton: TelemetryStore | None = None


def get_telemetry_store() -> TelemetryStore:
    global _telemetry_singleton
    if _telemetry_singleton is None:
        _telemetry_singleton = TelemetryStore()
    return _telemetry_singleton

