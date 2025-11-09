import { Waves, Brain, Shield, Cloud } from "lucide-react";

const steps = [
  {
    icon: Waves,
    title: "Continuous Monitoring",
    description: "Sensors track motion, temperature, and device health in real-time using minimal battery."
  },
  {
    icon: Brain,
    title: "AI Risk Analysis",
    description: "TensorFlow Lite model processes sensor data to calculate risk score (0-1 scale)."
  },
  {
    icon: Shield,
    title: "Automatic Trigger",
    description: "When risk exceeds 0.8, backup process initiates immediately without user intervention."
  },
  {
    icon: Cloud,
    title: "Secure Upload",
    description: "Files are compressed, AES-256 encrypted, and synced to Firebase cloud storage."
  }
];

const HowItWorksSection = () => {
  return (
    <section id="how-it-works" className="py-32 bg-background">
      <div className="container mx-auto px-6">
        <div className="text-center max-w-3xl mx-auto mb-20">
          <h2 className="text-4xl md:text-5xl font-bold mb-6">
            How <span className="bg-gradient-to-r from-primary to-secondary bg-clip-text text-transparent">It Works</span>
          </h2>
          <p className="text-xl text-muted-foreground">
            Four seamless steps to protect your data from drops, overheating, and device failure.
          </p>
        </div>

        <div className="relative max-w-5xl mx-auto">
          {/* Connection line */}
          <div className="hidden lg:block absolute top-20 left-0 right-0 h-0.5 bg-gradient-to-r from-primary/20 via-primary/40 to-secondary/20" />
          
          <div className="grid lg:grid-cols-4 gap-12 lg:gap-8">
            {steps.map((step, index) => {
              const Icon = step.icon;
              return (
                <div 
                  key={index}
                  className="relative text-center animate-in fade-in slide-in-from-bottom-6 duration-700"
                  style={{ animationDelay: `${index * 150}ms` }}
                >
                  <div className="inline-flex items-center justify-center w-20 h-20 rounded-2xl bg-gradient-to-br from-primary to-secondary mb-6 relative z-10 shadow-lg shadow-primary/20">
                    <Icon className="h-10 w-10 text-white" />
                  </div>
                  <div className="absolute top-8 left-1/2 -translate-x-1/2 w-10 h-10 rounded-full bg-primary/10 animate-pulse -z-10" />
                  
                  <div className="text-sm font-bold text-primary mb-3">STEP {index + 1}</div>
                  <h3 className="text-xl font-semibold mb-3 text-foreground">
                    {step.title}
                  </h3>
                  <p className="text-muted-foreground">
                    {step.description}
                  </p>
                </div>
              );
            })}
          </div>
        </div>
      </div>
    </section>
  );
};

export default HowItWorksSection;
