import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { supabase } from "@/integrations/supabase/client";
import { Session } from "@supabase/supabase-js";
import { Button } from "@/components/ui/button";
import { Shield, LogOut, Plus } from "lucide-react";
import { useToast } from "@/hooks/use-toast";
import DevicesSection from "@/components/dashboard/DevicesSection";
import AnalyticsSection from "@/components/dashboard/AnalyticsSection";
import RecentActivity from "@/components/dashboard/RecentActivity";
import AddDeviceDialog from "@/components/dashboard/AddDeviceDialog";

const Dashboard = () => {
  const [session, setSession] = useState<Session | null>(null);
  const [loading, setLoading] = useState(true);
  const [showAddDevice, setShowAddDevice] = useState(false);
  const navigate = useNavigate();
  const { toast } = useToast();

  useEffect(() => {
    const { data: { subscription } } = supabase.auth.onAuthStateChange(
      (_event, session) => {
        setSession(session);
        if (!session) {
          navigate("/auth");
        }
        setLoading(false);
      }
    );

    supabase.auth.getSession().then(({ data: { session } }) => {
      setSession(session);
      if (!session) {
        navigate("/auth");
      }
      setLoading(false);
    });

    return () => subscription.unsubscribe();
  }, [navigate]);

  const handleLogout = async () => {
    await supabase.auth.signOut();
    toast({
      title: "Signed out",
      description: "You've been successfully signed out.",
    });
    navigate("/");
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
      </div>
    );
  }

  if (!session) {
    return null;
  }

  return (
    <div className="min-h-screen bg-background">
      {/* Header */}
      <header className="border-b border-border/40 bg-card/50 backdrop-blur-sm sticky top-0 z-50">
        <div className="container mx-auto px-6 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <Shield className="h-8 w-8 text-primary" />
              <div>
                <h1 className="text-2xl font-bold">Sentinel Cloud</h1>
                <p className="text-sm text-muted-foreground">Device Protection Dashboard</p>
              </div>
            </div>

            <div className="flex items-center gap-3">
              <Button onClick={() => setShowAddDevice(true)} size="sm">
                <Plus className="h-4 w-4 mr-2" />
                Add Device
              </Button>
              <Button onClick={handleLogout} variant="ghost" size="sm">
                <LogOut className="h-4 w-4 mr-2" />
                Sign Out
              </Button>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="container mx-auto px-6 py-8">
        <div className="space-y-8">
          {/* Analytics Overview */}
          <AnalyticsSection userId={session.user.id} />

          {/* Devices Section */}
          <DevicesSection userId={session.user.id} />

          {/* Recent Activity */}
          <RecentActivity userId={session.user.id} />
        </div>
      </main>

      {/* Add Device Dialog */}
      <AddDeviceDialog
        open={showAddDevice}
        onOpenChange={setShowAddDevice}
        userId={session.user.id}
      />
    </div>
  );
};

export default Dashboard;
