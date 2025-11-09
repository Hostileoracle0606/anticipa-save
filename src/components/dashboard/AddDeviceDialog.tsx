import { useState } from "react";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { supabase } from "@/integrations/supabase/client";
import { useToast } from "@/hooks/use-toast";
import { z } from "zod";

interface AddDeviceDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  userId: string;
}

const deviceNameSchema = z.string().trim().min(1, { message: "Device name is required" }).max(100, { message: "Device name must be less than 100 characters" });
const deviceModelSchema = z.string().trim().max(100, { message: "Device model must be less than 100 characters" }).optional();
const deviceIdSchema = z.string().trim().min(1, { message: "Device ID is required" }).max(100, { message: "Device ID must be less than 100 characters" });

const AddDeviceDialog = ({ open, onOpenChange, userId }: AddDeviceDialogProps) => {
  const [deviceName, setDeviceName] = useState("");
  const [deviceModel, setDeviceModel] = useState("");
  const [deviceId, setDeviceId] = useState("");
  const [loading, setLoading] = useState(false);
  const { toast } = useToast();

  const validateInputs = () => {
    try {
      deviceNameSchema.parse(deviceName);
      deviceModelSchema.parse(deviceModel || undefined);
      deviceIdSchema.parse(deviceId);
      return true;
    } catch (error) {
      if (error instanceof z.ZodError) {
        toast({
          title: "Validation Error",
          description: error.errors[0].message,
          variant: "destructive",
        });
      }
      return false;
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validateInputs()) return;

    setLoading(true);

    try {
      const { error } = await supabase.from("devices").insert([
        {
          user_id: userId,
          device_name: deviceName.trim(),
          device_model: deviceModel.trim() || null,
          device_id: deviceId.trim(),
          current_risk_level: 0,
          status: "active",
        },
      ]);

      if (error) {
        if (error.code === "23505") {
          toast({
            title: "Device Already Exists",
            description: "A device with this ID is already registered.",
            variant: "destructive",
          });
        } else {
          throw error;
        }
        return;
      }

      toast({
        title: "Device Added",
        description: "Your device has been successfully added.",
      });

      setDeviceName("");
      setDeviceModel("");
      setDeviceId("");
      onOpenChange(false);
    } catch (error) {
      console.error("Error adding device:", error);
      toast({
        title: "Error",
        description: "Failed to add device. Please try again.",
        variant: "destructive",
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Add New Device</DialogTitle>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <Label htmlFor="deviceName">Device Name</Label>
            <Input
              id="deviceName"
              value={deviceName}
              onChange={(e) => setDeviceName(e.target.value)}
              placeholder="My Phone"
              required
              maxLength={100}
            />
          </div>

          <div>
            <Label htmlFor="deviceModel">Device Model (Optional)</Label>
            <Input
              id="deviceModel"
              value={deviceModel}
              onChange={(e) => setDeviceModel(e.target.value)}
              placeholder="Samsung Galaxy S24"
              maxLength={100}
            />
          </div>

          <div>
            <Label htmlFor="deviceId">Device ID</Label>
            <Input
              id="deviceId"
              value={deviceId}
              onChange={(e) => setDeviceId(e.target.value)}
              placeholder="ABC123XYZ"
              required
              maxLength={100}
            />
            <p className="text-xs text-muted-foreground mt-1">
              Unique identifier for your device (e.g., IMEI or serial number)
            </p>
          </div>

          <div className="flex gap-3 justify-end pt-4">
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              Cancel
            </Button>
            <Button type="submit" disabled={loading}>
              {loading ? "Adding..." : "Add Device"}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
};

export default AddDeviceDialog;
