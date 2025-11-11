from fastapi import APIRouter, Depends

from ...schemas.preferences import ContentPreferenceRequest, ContentPreferenceResponse
from ...services.preferences import PreferenceService, get_preference_service

router = APIRouter()


@router.post("/preferences/content", response_model=ContentPreferenceResponse)
async def update_content_preferences(
    payload: ContentPreferenceRequest,
    service: PreferenceService = Depends(get_preference_service),
) -> ContentPreferenceResponse:
    await service.update_content(payload.key, payload.enabled)
    return ContentPreferenceResponse(success=True)


