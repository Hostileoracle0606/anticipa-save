import type { Config } from "tailwindcss";

export default {
  content: ["./index.html", "./src/**/*.{ts,tsx}"],
  theme: {
    extend: {
      colors: {
        surface: "#0F172A",
        "surface-muted": "#1E2A44",
        primary: {
          DEFAULT: "#1F5BFF",
          100: "#E8F0FF",
          200: "#C5D6FF",
          300: "#9FBAFF",
          400: "#7296FF",
          500: "#1F5BFF",
          600: "#1440C7"
        },
        accent: "#1CD8FF",
        success: "#1BB982"
      }
    }
  },
  plugins: []
} satisfies Config;


