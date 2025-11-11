from fastapi import Depends

from ..services.telemetry import TelemetryStore, get_telemetry_store


class PreferenceService:
    def __init__(self, telemetry: TelemetryStore) -> None:
        self._telemetry = telemetry

    async def update_content(self, key: str, enabled: bool) -> None:
        self._telemetry.update_content(key, enabled)


def get_preference_service(telemetry: TelemetryStore = Depends(get_telemetry_store)) -> PreferenceService:
    return PreferenceService(telemetry)


