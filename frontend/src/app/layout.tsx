import type { Metadata } from "next";
import { Merriweather, Poppins } from "next/font/google";

import "./globals.css";

const poppins = Poppins({
  subsets: ["latin"],
  weight: ["400", "500", "600", "700"],
  variable: "--font-sans"
});

const merriweather = Merriweather({
  subsets: ["latin"],
  weight: ["700"],
  variable: "--font-serif"
});

export const metadata: Metadata = {
  title: "Inventario de Ferreteria",
  description: "Sistema profesional de inventario para ferreterias"
};

export default function RootLayout({
  children
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="es" className={`${poppins.variable} ${merriweather.variable}`}>
      <body className="font-[var(--font-sans)] antialiased">{children}</body>
    </html>
  );
}
