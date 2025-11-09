import { Button } from "@/components/ui/button";
import { ArrowRight, CheckCircle } from "lucide-react";

const benefits = [
  "30-day free trial",
  "No credit card required",
  "Cancel anytime",
  "24/7 support"
];

const CTASection = () => {
  return (
    <section className="py-32 bg-gradient-to-br from-primary/5 via-background to-secondary/5">
      <div className="container mx-auto px-6">
        <div className="max-w-4xl mx-auto text-center">
          <h2 className="text-4xl md:text-6xl font-bold mb-6 animate-in fade-in slide-in-from-bottom-4 duration-700">
            Protect Your Data <span className="bg-gradient-to-r from-primary to-secondary bg-clip-text text-transparent">Today</span>
          </h2>
          <p className="text-xl text-muted-foreground mb-10 animate-in fade-in slide-in-from-bottom-6 duration-700 delay-100">
            Join 1,200+ users who trust Sentinel Cloud to safeguard their most important memories and files.
          </p>

          <div className="flex flex-col sm:flex-row items-center justify-center gap-4 mb-12 animate-in fade-in slide-in-from-bottom-8 duration-700 delay-200">
            <Button size="lg" className="bg-gradient-to-r from-primary to-secondary hover:opacity-90 text-white font-semibold px-10 h-16 text-lg group">
              Get Started Free
              <ArrowRight className="ml-2 h-5 w-5 group-hover:translate-x-1 transition-transform" />
            </Button>
            <Button size="lg" variant="outline" className="h-16 px-10 text-lg">
              Contact Sales
            </Button>
          </div>

          <div className="flex flex-wrap items-center justify-center gap-6 text-sm animate-in fade-in duration-700 delay-300">
            {benefits.map((benefit, index) => (
              <div key={index} className="flex items-center gap-2 text-muted-foreground">
                <CheckCircle className="h-5 w-5 text-primary" />
                <span>{benefit}</span>
              </div>
            ))}
          </div>
        </div>
      </div>
    </section>
  );
};

export default CTASection;
