from datetime import datetime
from typing import Literal, Optional

from pydantic import BaseModel, Field

SystemStateLiteral = Literal["stable", "observing", "risk", "backing_up", "complete"]


class SyncSnapshot(BaseModel):
    progress: float = Field(ge=0.0, le=1.0)
    current_phase: str


class SystemStatus(BaseModel):
    state: SystemStateLiteral
    last_backup: Optional[datetime]
    total_secured: int
    next_check: Optional[datetime]

    class Config:
        json_encoders = {datetime: lambda dt: dt.isoformat()}


class SystemStateResponse(BaseModel):
    status: SystemStatus
    sync: Optional[SyncSnapshot]


