"use client";

import Link from "next/link";
import { useParams } from "next/navigation";
import { useEffect, useState } from "react";

import { ErrorState, LoadingState } from "@/components/ui";
import { fetchPdfBlob } from "@/lib/api";

const reportTitles: Record<string, string> = {
  stockActual: "Reporte de stock actual",
  stockBajo: "Reporte de productos con stock bajo",
  entradasInventario: "Reporte de entradas de inventario",
  salidasInventario: "Reporte de salidas de inventario",
  compras: "Reporte de compras",
  ventas: "Reporte de ventas",
  productosMasVendidos: "Reporte de productos mas vendidos",
  valorizacionInventario: "Reporte de valorizacion de inventario",
  utilidadBasica: "Reporte de utilidad basica"
};

export default function ReportPdfPage() {
  const params = useParams<{ key: string }>();
  const key = Array.isArray(params.key) ? params.key[0] : params.key;
  const [pdfUrl, setPdfUrl] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let currentUrl = "";

    async function loadPdf() {
      try {
        setLoading(true);
        setError("");
        currentUrl = await fetchPdfBlob(`/reportes/${key}/pdf`);
        setPdfUrl(currentUrl);
      } catch (err) {
        setError(err instanceof Error ? err.message : "No se pudo cargar el PDF");
      } finally {
        setLoading(false);
      }
    }

    void loadPdf();

    return () => {
      if (currentUrl) {
        URL.revokeObjectURL(currentUrl);
      }
    };
  }, [key]);

  return (
    <main className="min-h-screen bg-[linear-gradient(180deg,#f8fafc,#e2e8f0)] p-4">
      <div className="mx-auto flex min-h-[calc(100vh-2rem)] max-w-[1800px] flex-col rounded-[28px] border border-slate-200 bg-white shadow-[0_24px_70px_rgba(15,23,42,0.12)]">
        <header className="flex flex-col gap-4 border-b border-slate-200 px-6 py-5 md:flex-row md:items-center md:justify-between">
          <div>
            <p className="text-xs uppercase tracking-[0.35em] text-teal-700">Visualizador PDF</p>
            <h1 className="mt-2 text-3xl font-bold text-slate-900">
              {reportTitles[key] ?? "Reporte"}
            </h1>
          </div>

          <div className="flex gap-3">
            <Link
              href="/dashboard/reportes"
              className="rounded-2xl border border-slate-200 px-4 py-2 text-sm font-semibold text-slate-700"
            >
              Volver a reportes
            </Link>
            <button
              type="button"
              onClick={() => window.close()}
              className="rounded-2xl bg-teal-700 px-4 py-2 text-sm font-semibold text-white"
            >
              Cerrar pestaña
            </button>
          </div>
        </header>

        <section className="min-h-0 flex-1 p-4">
          {loading ? <LoadingState /> : null}
          {!loading && error ? <ErrorState message={error} /> : null}
          {!loading && !error && pdfUrl ? (
            <iframe
              src={pdfUrl}
              title="Vista previa del reporte PDF"
              className="h-[calc(100vh-10rem)] w-full rounded-2xl border border-slate-200"
            />
          ) : null}
        </section>
      </div>
    </main>
  );
}
