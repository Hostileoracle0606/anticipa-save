import { Card } from "@/components/ui/card";
import { Brain, Lock, CloudUpload, BarChart3, Bell, Smartphone } from "lucide-react";

const features = [
  {
    icon: Brain,
    title: "AI Risk Detection",
    description: "TensorFlow Lite model analyzes motion, temperature, and sensor data in real-time to predict device damage before it happens."
  },
  {
    icon: CloudUpload,
    title: "Automatic Backup",
    description: "When risk exceeds threshold, your data is instantly compressed, encrypted, and backed up to secure cloud storage."
  },
  {
    icon: Lock,
    title: "AES-256 Encryption",
    description: "Military-grade encryption protects your files before upload. Your data stays private, always."
  },
  {
    icon: BarChart3,
    title: "Analytics Dashboard",
    description: "Track device health, backup history, risk levels, and success metrics through an intuitive web dashboard."
  },
  {
    icon: Bell,
    title: "Smart Notifications",
    description: "Get instant alerts when risk is detected or backup completes. Stay informed without lifting a finger."
  },
  {
    icon: Smartphone,
    title: "Seamless Integration",
    description: "Built with Kotlin & Jetpack Compose for native Android performance. Runs efficiently in the background."
  }
];

const FeaturesSection = () => {
  return (
    <section id="features" className="py-32 bg-gradient-to-b from-background to-muted/20">
      <div className="container mx-auto px-6">
        <div className="text-center max-w-3xl mx-auto mb-16">
          <h2 className="text-4xl md:text-5xl font-bold mb-6">
            Built for <span className="bg-gradient-to-r from-primary to-secondary bg-clip-text text-transparent">Protection</span>
          </h2>
          <p className="text-xl text-muted-foreground">
            Advanced AI and cloud infrastructure combine to safeguard your most important data automatically.
          </p>
        </div>

        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
          {features.map((feature, index) => {
            const Icon = feature.icon;
            return (
              <Card 
                key={index}
                className="p-8 border-border/40 bg-card/50 backdrop-blur-sm hover:shadow-lg hover:shadow-primary/5 transition-all duration-300 hover:-translate-y-1 group animate-in fade-in slide-in-from-bottom-4"
                style={{ animationDelay: `${index * 100}ms` }}
              >
                <div className="inline-flex items-center justify-center w-14 h-14 rounded-xl bg-gradient-to-br from-primary/10 to-secondary/10 mb-5 group-hover:scale-110 transition-transform duration-300">
                  <Icon className="h-7 w-7 text-primary" />
                </div>
                <h3 className="text-xl font-semibold mb-3 text-foreground">
                  {feature.title}
                </h3>
                <p className="text-muted-foreground leading-relaxed">
                  {feature.description}
                </p>
              </Card>
            );
          })}
        </div>
      </div>
    </section>
  );
};

export default FeaturesSection;
