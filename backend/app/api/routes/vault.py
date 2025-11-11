from fastapi import APIRouter, Depends

from ...schemas.vault import VaultItemsResponse
from ...services.vault import VaultService, get_vault_service

router = APIRouter()


@router.get("/vault/items", response_model=VaultItemsResponse)
async def list_vault_items(service: VaultService = Depends(get_vault_service)) -> VaultItemsResponse:
    return await service.list_items()


