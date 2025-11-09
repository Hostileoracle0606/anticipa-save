import { useEffect, useState } from "react";
import { supabase } from "@/integrations/supabase/client";
import { Card } from "@/components/ui/card";
import { Activity, HardDrive, TrendingUp, Shield } from "lucide-react";

interface AnalyticsSectionProps {
  userId: string;
}

const AnalyticsSection = ({ userId }: AnalyticsSectionProps) => {
  const [stats, setStats] = useState({
    totalDevices: 0,
    backupsToday: 0,
    successRate: 0,
    avgRiskLevel: 0,
  });

  useEffect(() => {
    const fetchStats = async () => {
      try {
        // Total devices
        const { count: devicesCount } = await supabase
          .from("devices")
          .select("*", { count: "exact", head: true })
          .eq("user_id", userId);

        // Backups today
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        const { count: backupsCount } = await supabase
          .from("backup_logs")
          .select("*", { count: "exact", head: true })
          .eq("user_id", userId)
          .gte("created_at", today.toISOString());

        // Success rate
        const { data: allBackups } = await supabase
          .from("backup_logs")
          .select("success")
          .eq("user_id", userId);

        const successRate = allBackups && allBackups.length > 0
          ? (allBackups.filter(b => b.success).length / allBackups.length) * 100
          : 100;

        // Average risk level
        const { data: devices } = await supabase
          .from("devices")
          .select("current_risk_level")
          .eq("user_id", userId);

        const avgRisk = devices && devices.length > 0
          ? devices.reduce((sum, d) => sum + Number(d.current_risk_level), 0) / devices.length
          : 0;

        setStats({
          totalDevices: devicesCount || 0,
          backupsToday: backupsCount || 0,
          successRate: Number(successRate.toFixed(1)),
          avgRiskLevel: Number(avgRisk.toFixed(2)),
        });
      } catch (error) {
        console.error("Error fetching stats:", error);
      }
    };

    fetchStats();

    // Refresh stats every 30 seconds
    const interval = setInterval(fetchStats, 30000);
    return () => clearInterval(interval);
  }, [userId]);

  const metrics = [
    {
      icon: Shield,
      label: "Protected Devices",
      value: stats.totalDevices,
      color: "text-primary",
      bgColor: "bg-primary/10",
    },
    {
      icon: HardDrive,
      label: "Backups Today",
      value: stats.backupsToday,
      color: "text-secondary",
      bgColor: "bg-secondary/10",
    },
    {
      icon: TrendingUp,
      label: "Success Rate",
      value: `${stats.successRate}%`,
      color: "text-green-600",
      bgColor: "bg-green-100",
    },
    {
      icon: Activity,
      label: "Avg Risk Level",
      value: `${(stats.avgRiskLevel * 100).toFixed(0)}%`,
      color: stats.avgRiskLevel < 0.3 ? "text-green-600" : stats.avgRiskLevel < 0.7 ? "text-yellow-600" : "text-red-600",
      bgColor: stats.avgRiskLevel < 0.3 ? "bg-green-100" : stats.avgRiskLevel < 0.7 ? "bg-yellow-100" : "bg-red-100",
    },
  ];

  return (
    <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-6">
      {metrics.map((metric, index) => {
        const Icon = metric.icon;
        return (
          <Card key={index} className="p-6">
            <div className="flex items-center justify-between mb-4">
              <div className={`w-12 h-12 rounded-lg ${metric.bgColor} flex items-center justify-center`}>
                <Icon className={`h-6 w-6 ${metric.color}`} />
              </div>
            </div>
            <div>
              <p className="text-sm text-muted-foreground mb-1">{metric.label}</p>
              <p className={`text-3xl font-bold ${metric.color}`}>{metric.value}</p>
            </div>
          </Card>
        );
      })}
    </div>
  );
};

export default AnalyticsSection;
