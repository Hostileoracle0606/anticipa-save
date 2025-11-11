import { useEffect, useState } from "react";
import { StatusCard } from "./components/StatusCard";
import { DataSelector } from "./components/DataSelector";
import { VaultList, type VaultItem } from "./components/VaultList";
import { useSystemState } from "./hooks/useSystemState";
import { fetchVaultItems, toggleContentType } from "./lib/api";

export type ContentKey = "photos" | "videos" | "messages" | "documents";

const defaultSelections: Record<ContentKey, boolean> = {
  photos: true,
  videos: true,
  messages: false,
  documents: true
};

export default function App() {
  const { status, sync } = useSystemState();
  const [selections, setSelections] = useState<Record<ContentKey, boolean>>(defaultSelections);
  const [vaultItems, setVaultItems] = useState<VaultItem[]>([]);

  useEffect(() => {
    fetchVaultItems()
      .then(setVaultItems)
      .catch((error) => console.error("Failed to fetch vault items", error));
  }, [status.state]);

  const handleToggle = async (key: ContentKey, next: boolean) => {
    setSelections((prev) => ({ ...prev, [key]: next }));
    await toggleContentType(key, next);
  };

  return (
    <main className="min-h-screen w-full px-4 py-8 sm:px-6 lg:px-8">
      <div className="mx-auto flex w-full max-w-4xl flex-col gap-6 sm:gap-8">
        <header className="rounded-3xl bg-gradient-to-br from-primary-500 via-primary-400 to-accent/80 p-6 sm:p-8 shadow-lg">
          <div className="flex flex-col items-start gap-4 text-white">
            <span className="rounded-full bg-white/20 px-3 py-1 text-xs font-medium uppercase tracking-wide text-white/90">
              Sentinel Cloud
            </span>
            <h1 className="text-3xl font-semibold sm:text-4xl">
              Quietly protecting your memories.
            </h1>
            <p className="max-w-xl text-sm text-white/80 sm:text-base">
              Automation stays in the background—if something looks risky, your data is already on
              its way to the vault.
            </p>
          </div>
        </header>

        <section className="grid gap-6 md:grid-cols-2">
          <StatusCard status={status} sync={sync} />
          <DataSelector selections={selections} onToggle={handleToggle} />
        </section>

        <section className="rounded-3xl border border-white/10 bg-surface-muted/80 p-6 shadow-lg backdrop-blur-md">
          <h2 className="text-xl font-semibold text-white mb-4">Cloud vault</h2>
          <VaultList items={vaultItems} />
        </section>
      </div>
    </main>
  );
}

