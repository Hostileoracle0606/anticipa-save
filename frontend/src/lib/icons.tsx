import type { ComponentType } from "react";
import {
  MessageCircle,
  FileText,
  Image as ImageIcon,
  Video,
  CloudUpload,
  Cloud,
  CheckCircle
} from "lucide-react";

export type IconType =
  | "message"
  | "document"
  | "image"
  | "video"
  | "cloud"
  | "upload"
  | "success";

const map: Record<IconType, ComponentType<{ className?: string }>> = {
  message: MessageCircle,
  document: FileText,
  image: ImageIcon,
  video: Video,
  cloud: Cloud,
  upload: CloudUpload,
  success: CheckCircle
};

export function iconFor(type: IconType) {
  return map[type];
}

