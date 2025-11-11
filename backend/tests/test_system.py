import pytest
from httpx import AsyncClient

from app.main import app


@pytest.mark.anyio
async def test_state_endpoint():
    async with AsyncClient(app=app, base_url="http://testserver") as client:
        response = await client.get("/api/system/state")
    assert response.status_code == 200
    payload = response.json()
    assert "status" in payload
    assert payload["status"]["state"] in {"stable", "observing", "risk", "backing_up", "complete"}


