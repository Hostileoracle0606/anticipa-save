import { iconFor, IconType } from "../lib/icons";

export interface VaultItem {
  id: string;
  name: string;
  size: string;
  backedUpAt: string;
  type: IconType;
}

type Props = {
  items: VaultItem[];
};

export function VaultList({ items }: Props) {
  if (!items.length) {
    return (
      <div className="rounded-2xl border border-white/10 bg-white/5 p-6 text-sm text-white/60">
        Nothing in your vault yet. We’ll sync automatically when Sentinel detects risk.
      </div>
    );
  }

  return (
    <ul className="space-y-3">
      {items.map((item) => {
        const Icon = iconFor(item.type);
        return (
          <li
            key={item.id}
            className="flex items-center justify-between rounded-2xl border border-white/10 bg-white/5 px-4 py-3"
          >
            <div className="flex items-center gap-3">
              <span className="flex h-10 w-10 items-center justify-center rounded-xl border border-white/10 bg-white/10">
                <Icon className="h-5 w-5 text-accent" />
              </span>
              <div>
                <p className="text-sm font-medium text-white">{item.name}</p>
                <p className="text-xs text-white/60">
                  {item.size} • {item.backedUpAt}
                </p>
              </div>
            </div>
            <button className="text-sm font-medium text-accent hover:text-white transition-colors">
              Restore
            </button>
          </li>
        );
      })}
    </ul>
  );
}


