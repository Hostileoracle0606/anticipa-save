import type { SystemStatus, SyncSnapshot } from "../hooks/useSystemState";
import { motion } from "framer-motion";
import classNames from "classnames";

const stateCopy: Record<SystemStatus["state"], { title: string; subtitle: string }> = {
  stable: { title: "Stable", subtitle: "Device looks healthy." },
  observing: { title: "Monitoring", subtitle: "Watching for risky motion." },
  risk: { title: "Risk detected", subtitle: "We’re securing your data." },
  backing_up: { title: "Backing up", subtitle: "Encrypted vault update in progress." },
  complete: { title: "Protected", subtitle: "Everything is safe in the cloud." }
};

interface Props {
  status: SystemStatus;
  sync: SyncSnapshot | null;
}

export function StatusCard({ status, sync }: Props) {
  const copy = stateCopy[status.state];

  return (
    <motion.article
      layout
      className="flex h-full flex-col justify-between gap-4 rounded-3xl border border-white/10 bg-surface-muted/60 p-6 shadow-lg backdrop-blur-md"
    >
      <div className="flex items-start justify-between gap-4">
        <div>
          <p className="text-xs uppercase tracking-[0.2em] text-white/50">Device status</p>
          <h2 className="mt-2 text-2xl font-semibold text-white">{copy.title}</h2>
          <p className="text-sm text-white/70">{copy.subtitle}</p>
        </div>
        <span
          className={classNames(
            "inline-flex h-10 w-10 items-center justify-center rounded-full border border-white/20",
            {
              "bg-success/20 text-success": status.state === "stable" || status.state === "complete",
              "bg-accent/20 text-accent": status.state === "backing_up" || status.state === "observing",
              "bg-red-500/20 text-red-400": status.state === "risk"
            }
          )}
        >
          <div className="h-3 w-3 rounded-full bg-current" />
        </span>
      </div>

      <dl className="grid grid-cols-2 gap-4 text-sm text-white/70">
        <div>
          <dt className="uppercase tracking-wide text-xs text-white/50">Last backup</dt>
          <dd className="mt-1 text-white">{status.lastBackup ?? "Awaiting first backup"}</dd>
        </div>
        <div>
          <dt className="uppercase tracking-wide text-xs text-white/50">Files protected</dt>
          <dd className="mt-1 text-white">{status.totalSecured.toLocaleString()}</dd>
        </div>
        <div>
          <dt className="uppercase tracking-wide text-xs text-white/50">Sync progress</dt>
          <dd className="mt-1 text-white">
            {sync ? `${Math.round(sync.progress * 100)}% • ${sync.currentPhase}` : "Idle"}
          </dd>
        </div>
        <div>
          <dt className="uppercase tracking-wide text-xs text-white/50">Next check</dt>
          <dd className="mt-1 text-white">{status.nextCheck ?? "Running now"}</dd>
        </div>
      </dl>
    </motion.article>
  );
}


