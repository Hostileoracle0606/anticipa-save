from typing import Literal

from pydantic import BaseModel

ContentKey = Literal["photos", "videos", "messages", "documents"]


class ContentPreferenceRequest(BaseModel):
    key: ContentKey
    enabled: bool


class ContentPreferenceResponse(BaseModel):
    success: bool


