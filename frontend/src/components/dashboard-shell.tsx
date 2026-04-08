"use client";

import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { BarChart3, Boxes, ClipboardList, LayoutDashboard, LogOut, PackageSearch, Receipt, ShieldCheck, Truck, Warehouse } from "lucide-react";
import { useEffect, useMemo, useState } from "react";

import { clearSession, getSession, type Session } from "@/lib/auth";
import clsx from "clsx";

const menu = [
  { href: "/dashboard", label: "Resumen", icon: LayoutDashboard },
  { href: "/dashboard/categorias", label: "Categorias", icon: Boxes },
  { href: "/dashboard/proveedores", label: "Proveedores", icon: Truck },
  { href: "/dashboard/productos", label: "Productos", icon: PackageSearch },
  { href: "/dashboard/compras", label: "Compras", icon: ClipboardList },
  { href: "/dashboard/ventas", label: "Ventas", icon: Receipt },
  { href: "/dashboard/movimientos", label: "Movimientos", icon: Warehouse },
  { href: "/dashboard/usuarios", label: "Usuarios", icon: ShieldCheck },
  { href: "/dashboard/reportes", label: "Reportes", icon: BarChart3 }
];

export function DashboardShell({ children }: { children: React.ReactNode }) {
  const router = useRouter();
  const pathname = usePathname();
  const [session, setSession] = useState<Session | null>(null);

  useEffect(() => {
    const current = getSession();
    if (!current) {
      router.replace("/login");
      return;
    }
    setSession(current);
  }, [router]);

  const title = useMemo(() => {
    return menu.find((item) => item.href === pathname)?.label ?? "Inventario";
  }, [pathname]);

  if (!session) {
    return <div className="min-h-screen bg-transparent" />;
  }

  return (
    <div className="min-h-screen px-2 py-2 md:px-3 md:py-3 xl:px-4 xl:py-4">
      <div className="mx-auto grid min-h-[calc(100vh-1rem)] w-full max-w-[1880px] gap-3 lg:grid-cols-[280px_minmax(0,1fr)] xl:gap-4">
        <aside className="glass rounded-[28px] p-5">
          <div className="rounded-[24px] bg-[linear-gradient(135deg,#0f766e,#164e63)] p-5 text-white">
            <p className="text-xs uppercase tracking-[0.35em] text-white/70">Inventario</p>
            <h1 className="mt-3 font-[var(--font-serif)] text-2xl">Ferreteria Central</h1>
            <p className="mt-2 text-sm text-white/80">Control de stock, compras, ventas y reportes en un solo panel.</p>
          </div>

          <div className="mt-6 space-y-2">
            {menu.map((item) => {
              const Icon = item.icon;
              const active = pathname === item.href;
              return (
                <Link
                  key={item.href}
                  href={item.href}
                  className={clsx(
                    "flex items-center gap-3 rounded-2xl px-4 py-3 text-sm font-medium transition-all",
                    active
                      ? "bg-teal-700 text-white shadow-lg shadow-teal-700/20"
                      : "text-slate-700 hover:bg-white/70"
                  )}
                >
                  <Icon className="h-4 w-4" />
                  {item.label}
                </Link>
              );
            })}
          </div>

          <div className="mt-8 rounded-2xl border border-slate-200/70 bg-white/70 p-4">
            <p className="text-xs uppercase tracking-[0.3em] text-slate-500">Sesion</p>
            <p className="mt-2 text-base font-semibold text-slate-900">{session.nombreCompleto}</p>
            <p className="text-sm text-slate-600">
              {session.username} · {session.rol}
            </p>
            <button
              className="mt-4 flex w-full items-center justify-center gap-2 rounded-xl bg-rose-600 px-4 py-2 text-sm font-semibold text-white transition hover:bg-rose-700"
              onClick={() => {
                clearSession();
                router.replace("/login");
              }}
            >
              <LogOut className="h-4 w-4" />
              Cerrar sesion
            </button>
          </div>
        </aside>

        <main className="glass soft-scrollbar overflow-hidden rounded-[28px]">
          <header className="border-b border-slate-200/80 px-6 py-5">
            <p className="text-xs uppercase tracking-[0.4em] text-teal-700">Panel operativo</p>
            <div className="mt-2 flex flex-col gap-1 md:flex-row md:items-end md:justify-between">
              <div>
                <h2 className="text-3xl font-bold text-slate-900">{title}</h2>
                <p className="text-sm text-slate-600">Gestiona tu ferreteria con un flujo rapido y auditable.</p>
              </div>
              <div className="rounded-2xl bg-amber-100 px-4 py-2 text-sm font-medium text-amber-900">
                Estado del sistema: en linea
              </div>
            </div>
          </header>

          <section className="soft-scrollbar h-[calc(100vh-10rem)] overflow-y-auto p-6">{children}</section>
        </main>
      </div>
    </div>
  );
}
