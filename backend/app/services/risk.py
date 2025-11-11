from __future__ import annotations

import random
from dataclasses import dataclass
from typing import Optional

from fastapi import Depends


@dataclass
class SyncState:
    progress: float
    phase: str


class RiskEngine:
    def __init__(self) -> None:
        self._risk = 0.1
        self._sync: Optional[SyncState] = None
        self.started = False

    async def current_risk(self) -> float:
        return self._risk

    async def current_sync(self) -> Optional[SyncState]:
        return self._sync

    async def simulate_tick(self) -> None:
        self.started = True
        shift = random.uniform(-0.05, 0.05)
        self._risk = max(0.0, min(1.0, self._risk + shift))

        if self._risk > 0.78 and self._sync is None:
            self._sync = SyncState(progress=0.0, phase="Preparing files")
        if self._sync:
            self._sync.progress = min(1.0, self._sync.progress + random.uniform(0.15, 0.35))
            self._sync.phase = (
                "Encrypting"
                if self._sync.progress < 0.5
                else "Uploading"
                if self._sync.progress < 0.9
                else "Finalizing"
            )
            if self._sync.progress >= 1.0:
                self._risk = random.uniform(0.05, 0.2)
                self._sync = None


_singleton: Optional[RiskEngine] = None


def get_risk_engine() -> RiskEngine:
    global _singleton
    if _singleton is None:
        _singleton = RiskEngine()
    return _singleton

