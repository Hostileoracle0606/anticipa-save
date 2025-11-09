import { TrendingUp, Zap, Shield, DollarSign } from "lucide-react";

const metrics = [
  {
    icon: Shield,
    value: "91%",
    label: "Prediction Accuracy",
    description: "AI-powered risk detection"
  },
  {
    icon: Zap,
    value: "3.2s",
    label: "Avg Backup Latency",
    description: "Lightning-fast protection"
  },
  {
    icon: TrendingUp,
    value: "99.3%",
    label: "Backup Success Rate",
    description: "Reliable data safety"
  },
  {
    icon: DollarSign,
    value: "$0.01",
    label: "Cost per Month",
    description: "Affordable peace of mind"
  }
];

const MetricsSection = () => {
  return (
    <section className="py-20 bg-muted/30">
      <div className="container mx-auto px-6">
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-8">
          {metrics.map((metric, index) => {
            const Icon = metric.icon;
            return (
              <div 
                key={index}
                className="text-center group animate-in fade-in slide-in-from-bottom-4 duration-700"
                style={{ animationDelay: `${index * 100}ms` }}
              >
                <div className="inline-flex items-center justify-center w-16 h-16 rounded-2xl bg-gradient-to-br from-primary/10 to-secondary/10 mb-4 group-hover:scale-110 transition-transform duration-300">
                  <Icon className="h-8 w-8 text-primary" />
                </div>
                <div className="text-4xl font-bold bg-gradient-to-r from-primary to-secondary bg-clip-text text-transparent mb-2">
                  {metric.value}
                </div>
                <div className="text-lg font-semibold text-foreground mb-1">
                  {metric.label}
                </div>
                <div className="text-sm text-muted-foreground">
                  {metric.description}
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </section>
  );
};

export default MetricsSection;
