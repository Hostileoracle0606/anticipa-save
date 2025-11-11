from __future__ import annotations

from datetime import datetime, timedelta
from typing import List

from fastapi import Depends

from ..schemas.vault import VaultItem, VaultItemsResponse


class VaultService:
    def __init__(self) -> None:
        now = datetime.utcnow()
        self._items: List[VaultItem] = [
            VaultItem(
                id="photo-1",
                name="Camera roll",
                size_bytes=520 * 1024 * 1024,
                backed_up_at=now - timedelta(hours=8),
                type="image",
            ),
            VaultItem(
                id="video-1",
                name="Vacation videos",
                size_bytes=1024 * 1024 * 1024,
                backed_up_at=now - timedelta(days=1, hours=3),
                type="video",
            ),
            VaultItem(
                id="doc-1",
                name="Documents",
                size_bytes=180 * 1024 * 1024,
                backed_up_at=now - timedelta(days=2),
                type="document",
            ),
        ]

    async def list_items(self) -> VaultItemsResponse:
        return VaultItemsResponse(items=self._items)


_vault_singleton: VaultService | None = None


def get_vault_service() -> VaultService:
    global _vault_singleton
    if _vault_singleton is None:
        _vault_singleton = VaultService()
    return _vault_singleton

