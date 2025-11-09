import { useEffect, useState } from "react";
import { supabase } from "@/integrations/supabase/client";
import { Card } from "@/components/ui/card";
import { CheckCircle, XCircle, AlertTriangle, RefreshCw } from "lucide-react";

interface BackupLog {
  id: string;
  device_id: string;
  success: boolean;
  backup_latency_seconds: number | null;
  created_at: string;
  devices: {
    device_name: string;
  };
}

interface RecentActivityProps {
  userId: string;
}

const RecentActivity = ({ userId }: RecentActivityProps) => {
  const [logs, setLogs] = useState<BackupLog[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchLogs = async () => {
      try {
        const { data, error } = await supabase
          .from("backup_logs")
          .select(`
            id,
            device_id,
            success,
            backup_latency_seconds,
            created_at,
            devices (
              device_name
            )
          `)
          .eq("user_id", userId)
          .order("created_at", { ascending: false })
          .limit(10);

        if (error) throw error;
        setLogs(data || []);
      } catch (error) {
        console.error("Error fetching logs:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchLogs();

    // Subscribe to realtime changes
    const channel = supabase
      .channel("backup-logs-changes")
      .on(
        "postgres_changes",
        {
          event: "INSERT",
          schema: "public",
          table: "backup_logs",
          filter: `user_id=eq.${userId}`,
        },
        () => {
          fetchLogs();
        }
      )
      .subscribe();

    return () => {
      supabase.removeChannel(channel);
    };
  }, [userId]);

  const getTimeAgo = (date: string) => {
    const seconds = Math.floor((new Date().getTime() - new Date(date).getTime()) / 1000);
    if (seconds < 60) return `${seconds}s ago`;
    const minutes = Math.floor(seconds / 60);
    if (minutes < 60) return `${minutes}m ago`;
    const hours = Math.floor(minutes / 60);
    if (hours < 24) return `${hours}h ago`;
    const days = Math.floor(hours / 24);
    return `${days}d ago`;
  };

  if (loading) {
    return (
      <Card className="p-8">
        <div className="flex items-center justify-center">
          <RefreshCw className="h-6 w-6 animate-spin text-primary" />
        </div>
      </Card>
    );
  }

  return (
    <div>
      <h2 className="text-2xl font-bold mb-6">Recent Activity</h2>
      
      <Card className="p-6">
        {logs.length === 0 ? (
          <div className="text-center py-8">
            <AlertTriangle className="h-12 w-12 mx-auto mb-3 text-muted-foreground" />
            <p className="text-muted-foreground">No backup activity yet</p>
          </div>
        ) : (
          <div className="space-y-4">
            {logs.map((log) => (
              <div key={log.id} className="flex items-center gap-4 p-4 rounded-lg hover:bg-muted/30 transition-colors">
                <div className={`w-10 h-10 rounded-full flex items-center justify-center ${log.success ? 'bg-green-100' : 'bg-red-100'}`}>
                  {log.success ? (
                    <CheckCircle className="h-5 w-5 text-green-600" />
                  ) : (
                    <XCircle className="h-5 w-5 text-red-600" />
                  )}
                </div>
                
                <div className="flex-1">
                  <p className="font-medium">
                    Backup {log.success ? "completed" : "failed"} • {log.devices.device_name}
                  </p>
                  <p className="text-sm text-muted-foreground">
                    {log.backup_latency_seconds ? `${log.backup_latency_seconds.toFixed(1)}s latency` : "No latency data"}
                  </p>
                </div>
                
                <span className="text-sm text-muted-foreground">
                  {getTimeAgo(log.created_at)}
                </span>
              </div>
            ))}
          </div>
        )}
      </Card>
    </div>
  );
};

export default RecentActivity;
