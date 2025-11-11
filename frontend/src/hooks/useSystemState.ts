import { useEffect, useState } from "react";
import { fetchSystemState } from "../lib/api";

export type SystemStatusState = "stable" | "observing" | "risk" | "backing_up" | "complete";

export interface SystemStatus {
  state: SystemStatusState;
  lastBackup: string | null;
  totalSecured: number;
  nextCheck: string | null;
}

export interface SyncSnapshot {
  progress: number;
  currentPhase: string;
}

export function useSystemState() {
  const [status, setStatus] = useState<SystemStatus>({
    state: "observing",
    lastBackup: null,
    totalSecured: 0,
    nextCheck: "In 2 min"
  });
  const [sync, setSync] = useState<SyncSnapshot | null>(null);

  useEffect(() => {
    let mounted = true;

    const poll = async () => {
      try {
        const data = await fetchSystemState();
        if (!mounted) return;
        setStatus(data.status);
        setSync(data.sync);
      } catch (error) {
        console.error("Failed to fetch system state", error);
      }
    };

    poll();
    const interval = window.setInterval(poll, 5000);

    return () => {
      mounted = false;
      window.clearInterval(interval);
    };
  }, []);

  return { status, sync };
}


