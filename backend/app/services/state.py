from __future__ import annotations

import asyncio
import random
from datetime import datetime, timedelta
from typing import Optional

from fastapi import Depends

from ..schemas.system import SystemStateLiteral, SystemStateResponse, SystemStatus, SyncSnapshot
from .risk import RiskEngine, SyncState, get_risk_engine
from .telemetry import TelemetryStore, get_telemetry_store


class StateService:
    def __init__(self, risk_engine: RiskEngine, telemetry: TelemetryStore) -> None:
        self._risk_engine = risk_engine
        self._telemetry = telemetry
        self._last_backup: Optional[datetime] = None
        self._total_secured = 12_432  # fake baseline
        self._next_check = datetime.utcnow() + timedelta(minutes=2)

    async def current_state(self) -> SystemStateResponse:
        risk_level = await self._risk_engine.current_risk()
        sync = await self._risk_engine.current_sync()

        state = self._map_state(risk_level, sync)
        if state == "complete":
            self._last_backup = datetime.utcnow()
            self._total_secured += random.randint(5, 60)
            self._next_check = datetime.utcnow() + timedelta(minutes=2)

        status = SystemStatus(
            state=state,
            last_backup=self._last_backup,
            total_secured=self._total_secured,
            next_check=self._next_check,
        )
        snapshot = (
            SyncSnapshot(progress=sync.progress, current_phase=sync.phase) if sync else None
        )
        return SystemStateResponse(status=status, sync=snapshot)

    def _map_state(self, risk: float, sync: Optional[SyncState]) -> SystemStateLiteral:
        if sync and sync.progress < 1.0:
            return "backing_up"
        if sync and sync.progress >= 1.0:
            return "complete"
        if risk > 0.75:
            return "risk"
        if risk > 0.35:
            return "observing"
        return "stable"


async def _poll_risk(risk_engine: RiskEngine):
    while True:
        await risk_engine.simulate_tick()
        await asyncio.sleep(3)


def get_state_service(
    risk_engine: RiskEngine = Depends(get_risk_engine),
    telemetry: TelemetryStore = Depends(get_telemetry_store),
) -> StateService:
    # start background risk updates once
    if not risk_engine.started:
        asyncio.create_task(_poll_risk(risk_engine))
    return StateService(risk_engine=risk_engine, telemetry=telemetry)

