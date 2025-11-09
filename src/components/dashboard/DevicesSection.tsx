import { useEffect, useState } from "react";
import { supabase } from "@/integrations/supabase/client";
import { Card } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Smartphone, AlertTriangle, CheckCircle, Trash2, RefreshCw } from "lucide-react";
import { useToast } from "@/hooks/use-toast";

interface Device {
  id: string;
  device_name: string;
  device_model: string | null;
  device_id: string;
  current_risk_level: number;
  last_backup_at: string | null;
  status: string;
  created_at: string;
}

interface DevicesSectionProps {
  userId: string;
}

const DevicesSection = ({ userId }: DevicesSectionProps) => {
  const [devices, setDevices] = useState<Device[]>([]);
  const [loading, setLoading] = useState(true);
  const { toast } = useToast();

  const fetchDevices = async () => {
    try {
      const { data, error } = await supabase
        .from("devices")
        .select("*")
        .eq("user_id", userId)
        .order("created_at", { ascending: false });

      if (error) throw error;
      setDevices(data || []);
    } catch (error) {
      console.error("Error fetching devices:", error);
      toast({
        title: "Error",
        description: "Failed to load devices",
        variant: "destructive",
      });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDevices();

    // Subscribe to realtime changes
    const channel = supabase
      .channel("devices-changes")
      .on(
        "postgres_changes",
        {
          event: "*",
          schema: "public",
          table: "devices",
          filter: `user_id=eq.${userId}`,
        },
        () => {
          fetchDevices();
        }
      )
      .subscribe();

    return () => {
      supabase.removeChannel(channel);
    };
  }, [userId]);

  const handleDelete = async (deviceId: string) => {
    try {
      const { error } = await supabase
        .from("devices")
        .delete()
        .eq("id", deviceId);

      if (error) throw error;

      toast({
        title: "Device Removed",
        description: "The device has been successfully removed.",
      });
    } catch (error) {
      toast({
        title: "Error",
        description: "Failed to remove device",
        variant: "destructive",
      });
    }
  };

  const getRiskColor = (level: number) => {
    if (level < 0.3) return "text-green-600";
    if (level < 0.7) return "text-yellow-600";
    return "text-red-600";
  };

  const getRiskBadge = (level: number) => {
    if (level < 0.3) return <Badge className="bg-green-100 text-green-700 hover:bg-green-100">Low Risk</Badge>;
    if (level < 0.7) return <Badge className="bg-yellow-100 text-yellow-700 hover:bg-yellow-100">Medium Risk</Badge>;
    return <Badge className="bg-red-100 text-red-700 hover:bg-red-100">High Risk</Badge>;
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
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-2xl font-bold">Your Devices</h2>
        <Button variant="ghost" size="sm" onClick={fetchDevices}>
          <RefreshCw className="h-4 w-4 mr-2" />
          Refresh
        </Button>
      </div>

      {devices.length === 0 ? (
        <Card className="p-12">
          <div className="text-center">
            <Smartphone className="h-16 w-16 mx-auto mb-4 text-muted-foreground" />
            <h3 className="text-xl font-semibold mb-2">No Devices Yet</h3>
            <p className="text-muted-foreground mb-6">
              Add your first device to start protecting your data
            </p>
          </div>
        </Card>
      ) : (
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
          {devices.map((device) => (
            <Card key={device.id} className="p-6 hover:shadow-lg transition-shadow">
              <div className="flex items-start justify-between mb-4">
                <div className="flex items-center gap-3">
                  <div className="w-12 h-12 rounded-lg bg-primary/10 flex items-center justify-center">
                    <Smartphone className="h-6 w-6 text-primary" />
                  </div>
                  <div>
                    <h3 className="font-semibold text-lg">{device.device_name}</h3>
                    <p className="text-sm text-muted-foreground">{device.device_model || "Unknown Model"}</p>
                  </div>
                </div>
                <Button
                  variant="ghost"
                  size="sm"
                  onClick={() => handleDelete(device.id)}
                  className="text-destructive hover:text-destructive"
                >
                  <Trash2 className="h-4 w-4" />
                </Button>
              </div>

              <div className="space-y-3">
                <div className="flex items-center justify-between">
                  <span className="text-sm text-muted-foreground">Risk Level</span>
                  {getRiskBadge(Number(device.current_risk_level))}
                </div>

                <div className="flex items-center justify-between">
                  <span className="text-sm text-muted-foreground">Score</span>
                  <span className={`font-bold ${getRiskColor(Number(device.current_risk_level))}`}>
                    {(Number(device.current_risk_level) * 100).toFixed(0)}%
                  </span>
                </div>

                <div className="flex items-center justify-between">
                  <span className="text-sm text-muted-foreground">Status</span>
                  <div className="flex items-center gap-1">
                    {device.status === "active" ? (
                      <>
                        <CheckCircle className="h-4 w-4 text-green-600" />
                        <span className="text-sm font-medium text-green-600">Active</span>
                      </>
                    ) : (
                      <>
                        <AlertTriangle className="h-4 w-4 text-yellow-600" />
                        <span className="text-sm font-medium text-yellow-600">{device.status}</span>
                      </>
                    )}
                  </div>
                </div>

                <div className="pt-3 border-t border-border/40">
                  <span className="text-xs text-muted-foreground">Last Backup</span>
                  <p className="text-sm font-medium">
                    {device.last_backup_at
                      ? new Date(device.last_backup_at).toLocaleDateString()
                      : "No backups yet"}
                  </p>
                </div>
              </div>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
};

export default DevicesSection;
