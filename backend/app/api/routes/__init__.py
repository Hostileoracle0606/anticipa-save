"""Route modules."""

from fastapi import APIRouter

from .system import router as system_router
from .vault import router as vault_router
from .preferences import router as preferences_router

api_router = APIRouter(prefix="/api")
api_router.include_router(system_router, tags=["system"])
api_router.include_router(vault_router, tags=["vault"])
api_router.include_router(preferences_router, tags=["preferences"])


