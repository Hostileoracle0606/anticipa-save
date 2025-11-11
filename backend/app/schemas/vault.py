from datetime import datetime
from typing import List, Literal

from pydantic import BaseModel, Field

VaultItemType = Literal["image", "video", "message", "document"]


class VaultItem(BaseModel):
    id: str
    name: str
    size_bytes: int
    backed_up_at: datetime
    type: VaultItemType

    class Config:
        json_encoders = {datetime: lambda dt: dt.isoformat()}


class VaultItemsResponse(BaseModel):
    items: List[VaultItem]


