import { Card } from "@/components/ui/card";
import { Activity, HardDrive, Users, TrendingUp } from "lucide-react";

const DashboardSection = () => {
  return (
    <section id="dashboard" className="py-32 bg-gradient-to-b from-muted/20 to-background">
      <div className="container mx-auto px-6">
        <div className="grid lg:grid-cols-2 gap-16 items-center">
          <div className="animate-in fade-in slide-in-from-left-8 duration-700">
            <h2 className="text-4xl md:text-5xl font-bold mb-6">
              Monitor Everything from Your{" "}
              <span className="bg-gradient-to-r from-primary to-secondary bg-clip-text text-transparent">
                Dashboard
              </span>
            </h2>
            <p className="text-xl text-muted-foreground mb-8">
              Get real-time insights into device health, backup performance, and user analytics through a beautiful web interface.
            </p>
            
            <div className="space-y-4">
              <div className="flex items-start gap-4">
                <div className="w-12 h-12 rounded-lg bg-primary/10 flex items-center justify-center flex-shrink-0">
                  <Activity className="h-6 w-6 text-primary" />
                </div>
                <div>
                  <h3 className="font-semibold text-lg mb-1">Device Health Tracking</h3>
                  <p className="text-muted-foreground">Monitor risk levels, sensor data, and device status across all protected phones.</p>
                </div>
              </div>

              <div className="flex items-start gap-4">
                <div className="w-12 h-12 rounded-lg bg-primary/10 flex items-center justify-center flex-shrink-0">
                  <HardDrive className="h-6 w-6 text-primary" />
                </div>
                <div>
                  <h3 className="font-semibold text-lg mb-1">Backup History</h3>
                  <p className="text-muted-foreground">View complete backup logs, success rates, and storage usage over time.</p>
                </div>
              </div>

              <div className="flex items-start gap-4">
                <div className="w-12 h-12 rounded-lg bg-primary/10 flex items-center justify-center flex-shrink-0">
                  <TrendingUp className="h-6 w-6 text-primary" />
                </div>
                <div>
                  <h3 className="font-semibold text-lg mb-1">Analytics & Trends</h3>
                  <p className="text-muted-foreground">BigQuery-powered insights reveal patterns and optimize protection strategies.</p>
                </div>
              </div>
            </div>
          </div>

          <div className="relative animate-in fade-in slide-in-from-right-8 duration-700 delay-200">
            <Card className="p-8 border-border/40 bg-card/50 backdrop-blur-sm shadow-xl">
              <div className="space-y-6">
                <div>
                  <div className="text-sm font-medium text-muted-foreground mb-2">Active Users</div>
                  <div className="text-4xl font-bold text-foreground">1,200</div>
                  <div className="text-sm text-green-600 font-medium mt-1">↑ 23% this month</div>
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <Card className="p-4 bg-primary/5 border-primary/20">
                    <div className="text-sm text-muted-foreground mb-1">Backups Today</div>
                    <div className="text-2xl font-bold text-primary">847</div>
                  </Card>
                  <Card className="p-4 bg-secondary/5 border-secondary/20">
                    <div className="text-sm text-muted-foreground mb-1">Success Rate</div>
                    <div className="text-2xl font-bold text-secondary">99.3%</div>
                  </Card>
                </div>

                <div>
                  <div className="flex justify-between text-sm mb-2">
                    <span className="text-muted-foreground">Storage Used</span>
                    <span className="font-medium">67%</span>
                  </div>
                  <div className="w-full h-2 bg-muted rounded-full overflow-hidden">
                    <div className="h-full w-2/3 bg-gradient-to-r from-primary to-secondary rounded-full" />
                  </div>
                </div>

                <div className="pt-4 border-t border-border/40">
                  <div className="text-sm text-muted-foreground mb-3">Recent Activity</div>
                  <div className="space-y-2">
                    {[1, 2, 3].map((i) => (
                      <div key={i} className="flex items-center gap-3 text-sm">
                        <div className="w-2 h-2 rounded-full bg-green-500" />
                        <span className="text-foreground">Backup completed • Device {i}8A92</span>
                        <span className="text-muted-foreground ml-auto">{i}m ago</span>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            </Card>
          </div>
        </div>
      </div>
    </section>
  );
};

export default DashboardSection;
