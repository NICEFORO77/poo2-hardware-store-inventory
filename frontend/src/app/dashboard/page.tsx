"use client";

import { useEffect, useState } from "react";

import { DataTable } from "@/components/data-table";
import { ErrorState, LoadingState, MetricCard, Panel } from "@/components/ui";
import { apiRequest, money } from "@/lib/api";
import { DashboardSummary } from "@/lib/types";

export default function DashboardPage() {
  const [summary, setSummary] = useState<DashboardSummary | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  async function loadSummary() {
    try {
      setLoading(true);
      setError("");
      const response = await apiRequest<DashboardSummary>("/dashboard/resumen");
      setSummary(response);
    } catch (err) {
      setError(err instanceof Error ? err.message : "No se pudo cargar el dashboard");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void loadSummary();
  }, []);

  if (loading) return <LoadingState />;
  if (error || !summary) return <ErrorState message={error || "Sin datos"} onRetry={loadSummary} />;

  return (
    <div className="space-y-6">
      <section className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        <MetricCard label="Productos registrados" value={String(summary.totalProductos)} />
        <MetricCard label="Productos bajo stock" value={String(summary.productosBajoStock)} accent="amber" />
        <MetricCard label="Compras registradas" value={String(summary.totalCompras)} accent="slate" />
        <MetricCard label="Ventas registradas" value={String(summary.totalVentas)} accent="rose" />
      </section>

      <section className="grid gap-6 xl:grid-cols-[1.2fr_0.8fr]">
        <Panel title="Valor del inventario" description="Lectura rapida del capital inmovilizado y valor potencial de venta.">
          <div className="grid gap-4 md:grid-cols-2">
            <div className="rounded-3xl bg-teal-700 p-5 text-white">
              <p className="text-sm text-white/80">Valorizacion a costo</p>
              <p className="mt-3 text-3xl font-bold">{money.format(summary.valorInventarioCosto)}</p>
            </div>
            <div className="rounded-3xl bg-amber-500 p-5 text-slate-950">
              <p className="text-sm text-slate-900/70">Valorizacion a venta</p>
              <p className="mt-3 text-3xl font-bold">{money.format(summary.valorInventarioVenta)}</p>
            </div>
          </div>
        </Panel>

        <Panel title="Cobertura operativa" description="Indicadores clave para el equipo.">
          <div className="space-y-4">
            {[
              ["Categorias", summary.totalCategorias],
              ["Proveedores", summary.totalProveedores],
              ["Productos activos", summary.productosActivos]
            ].map(([label, value]) => (
              <div key={String(label)} className="flex items-center justify-between rounded-2xl border border-slate-200 bg-slate-50 px-4 py-4">
                <span className="text-sm text-slate-600">{label}</span>
                <span className="text-xl font-bold text-slate-900">{String(value)}</span>
              </div>
            ))}
          </div>
        </Panel>
      </section>

      <Panel title="Movimientos recientes" description="Ultimos cambios que afectaron el inventario.">
        <DataTable
          columns={[
            { key: "fecha", label: "Fecha" },
            { key: "producto", label: "Producto" },
            { key: "tipo", label: "Tipo" },
            { key: "cantidad", label: "Cantidad" },
            { key: "motivo", label: "Motivo" }
          ]}
          rows={summary.movimientosRecientes}
        />
      </Panel>
    </div>
  );
}
