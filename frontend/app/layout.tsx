import type React from "react";
import type { Metadata } from "next";
import { Nunito } from "next/font/google";
import "./globals.css";
import ClientRootLayout from "./client-root-latout";

const nunito = Nunito({
  subsets: ["latin"],
  display: "swap",
  variable: "--font-nunito",
});

export const metadata: Metadata = {
  title: "AlquiGest",
  description: "Sistema de gestión de alquileres para estudio jurídico",
  icons: {
    icon: "/alquigest-circulo.png",
  },
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="es" className={`${nunito.className}`}>
      <body>
        <ClientRootLayout>{children}</ClientRootLayout>
      </body>
    </html>
  );
}
