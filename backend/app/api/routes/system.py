from fastapi import APIRouter, Depends

from ...services.state import StateService, get_state_service
from ...schemas.system import SystemStateResponse

router = APIRouter()


@router.get("/system/state", response_model=SystemStateResponse)
async def read_system_state(service: StateService = Depends(get_state_service)) -> SystemStateResponse:
    return await service.current_state()


