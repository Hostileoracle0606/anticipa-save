import type { ContentKey } from "../App";
import type { VaultItem } from "../components/VaultList";
import type { SystemStatus, SyncSnapshot } from "../hooks/useSystemState";

const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "/api";

type SystemResponse = {
  status: {
    state: SystemStatus["state"];
    last_backup: string | null;
    total_secured: number;
    next_check: string | null;
  };
  sync: { progress: number; current_phase: string } | null;
};

type VaultResponse = {
  items: Array<{
    id: string;
    name: string;
    size_bytes: number;
    backed_up_at: string;
    type: VaultItem["type"];
  }>;
};

const rtf = new Intl.RelativeTimeFormat("en", { numeric: "auto" });

function timeAgo(iso: string | null): string | null {
  if (!iso) return null;
  const ts = new Date(iso);
  const diff = ts.getTime() - Date.now();
  const minutes = Math.round(diff / 60000);
  if (Math.abs(minutes) < 60) {
    return rtf.format(minutes, "minute");
  }
  const hours = Math.round(minutes / 60);
  if (Math.abs(hours) < 24) {
    return rtf.format(hours, "hour");
  }
  const days = Math.round(hours / 24);
  return rtf.format(days, "day");
}

function formatBytes(value: number): string {
  if (value === 0) return "0 B";
  const units = ["B", "KB", "MB", "GB", "TB"];
  const i = Math.floor(Math.log(value) / Math.log(1024));
  return `${(value / Math.pow(1024, i)).toFixed(1)} ${units[i]}`;
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const res = await fetch(`${BASE_URL}${path}`, {
    headers: { "Content-Type": "application/json" },
    ...init
  });
  if (!res.ok) {
    throw new Error(await res.text());
  }
  return res.json() as Promise<T>;
}

export async function fetchSystemState(): Promise<{ status: SystemStatus; sync: SyncSnapshot | null }> {
  const data = await request<SystemResponse>("/system/state");
  return {
    status: {
      state: data.status.state,
      lastBackup: timeAgo(data.status.last_backup),
      totalSecured: data.status.total_secured,
      nextCheck: timeAgo(data.status.next_check)
    },
    sync: data.sync
      ? {
          progress: data.sync.progress,
          currentPhase: data.sync.current_phase
        }
      : null
  };
}

export async function fetchVaultItems(): Promise<VaultItem[]> {
  const data = await request<VaultResponse>("/vault/items");
  return data.items.map((item) => ({
    id: item.id,
    name: item.name,
    size: formatBytes(item.size_bytes),
    backedUpAt: timeAgo(item.backed_up_at) ?? "Just now",
    type: item.type
  }));
}

export async function toggleContentType(key: ContentKey, enabled: boolean): Promise<void> {
  await request("/preferences/content", {
    method: "POST",
    body: JSON.stringify({ key, enabled })
  });
}

