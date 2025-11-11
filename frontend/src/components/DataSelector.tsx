import type { ContentKey } from "../App";
import classNames from "classnames";
import { motion } from "framer-motion";
import { IconType, iconFor } from "../lib/icons";

type Props = {
  selections: Record<ContentKey, boolean>;
  onToggle: (key: ContentKey, next: boolean) => void;
};

const labels: Record<ContentKey, { title: string; copy: string; icon: IconType }> = {
  photos: { title: "Photos", copy: "Camera roll & screenshots", icon: "image" },
  videos: { title: "Videos", copy: "Movies & clips", icon: "video" },
  messages: { title: "Messages", copy: "SMS & chat history", icon: "message" },
  documents: { title: "Documents", copy: "Downloads & files", icon: "document" }
};

export function DataSelector({ selections, onToggle }: Props) {
  return (
    <section className="rounded-3xl border border-white/10 bg-surface-muted/60 p-6 shadow-lg backdrop-blur-md">
      <header className="mb-4">
        <p className="text-xs uppercase tracking-[0.2em] text-white/50">Manage data</p>
        <h2 className="mt-2 text-lg font-semibold text-white">Pick what Sentinel protects</h2>
        <p className="text-sm text-white/65">
          Changes take effect immediately. Everything is encrypted before it leaves your device.
        </p>
      </header>

      <ul className="space-y-3">
        {(Object.keys(selections) as ContentKey[]).map((key) => {
          const selected = selections[key];
          const label = labels[key];
          const Icon = iconFor(label.icon);
          return (
            <li key={key}>
              <button
                onClick={() => onToggle(key, !selected)}
                className={classNames(
                  "flex w-full items-center justify-between rounded-2xl border px-4 py-3 transition-colors focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2",
                  selected
                    ? "border-accent/40 bg-white/10 text-white focus-visible:outline-accent"
                    : "border-white/10 bg-transparent text-white/70 hover:border-white/20 focus-visible:outline-white"
                )}
              >
                <div className="flex items-center gap-3 text-left">
                  <span
                    className={classNames(
                      "flex h-10 w-10 items-center justify-center rounded-xl border",
                      selected ? "border-accent/50 bg-accent/20" : "border-white/10 bg-white/5"
                    )}
                  >
                    <Icon className="h-5 w-5" />
                  </span>
                  <div>
                    <p className="font-medium">{label.title}</p>
                    <p className="text-xs text-white/60">{label.copy}</p>
                  </div>
                </div>
                <motion.span
                  layout
                  className={classNames(
                    "inline-flex h-6 w-12 rounded-full border transition-colors",
                    selected ? "border-accent bg-accent/40" : "border-white/10 bg-white/10"
                  )}
                >
                  <motion.span
                    layout
                    className={classNames(
                      "m-0.5 h-5 w-5 rounded-full bg-white transition-transform",
                      selected ? "translate-x-6" : "translate-x-0"
                    )}
                  />
                </motion.span>
              </button>
            </li>
          );
        })}
      </ul>
    </section>
  );
}


