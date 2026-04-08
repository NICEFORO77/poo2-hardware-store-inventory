"use client";

import { useEffect, useState } from "react";

import { DataTable } from "@/components/data-table";
import { ErrorState, LoadingState, Panel } from "@/components/ui";
import { apiRequest, fetchPdfBlob } from "@/lib/api";
import { Reportes } from "@/lib/types";

const reportLabels: Array<{ key: keyof Reportes; title: string; description: string }> = [
  { key: "stockActual", title: "Stock actual", description: "Inventario disponible por producto." },
  { key: "stockBajo", title: "Stock bajo", description: "Productos que requieren reabastecimiento." },
  { key: "entradasInventario", title: "Entradas", description: "Ingresos al almacen por compras o ajustes." },
  { key: "salidasInventario", title: "Salidas", description: "Ventas, mermas o correcciones de salida." },
  { key: "compras", title: "Compras", description: "Detalle historico de adquisiciones." },
  { key: "ventas", title: "Ventas", description: "Detalle historico de ventas." },
  { key: "productosMasVendidos", title: "Mas vendidos", description: "Productos con mayor rotacion." },
  { key: "valorizacionInventario", title: "Valorizacion", description: "Valor del inventario a costo y venta." },
  { key: "utilidadBasica", title: "Utilidad basica", description: "Margen unitario por producto." }
];

export function ReportsPanel() {
  const [reportes, setReportes] = useState<Reportes | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [pdfError, setPdfError] = useState("");
  const [pdfLoading, setPdfLoading] = useState(false);
  const [pdfUrl, setPdfUrl] = useState("");
  const [selectedReport, setSelectedReport] = useState<(typeof reportLabels)[number] | null>(null);

  async function loadData() {
    try {
      setLoading(true);
      setError("");
      const data = await apiRequest<Reportes>("/reportes");
      setReportes(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : "No se pudieron cargar los reportes");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void loadData();
  }, []);

  useEffect(() => {
    return () => {
      if (pdfUrl) {
        URL.revokeObjectURL(pdfUrl);
      }
    };
  }, [pdfUrl]);

  async function openPdf(report: (typeof reportLabels)[number]) {
    try {
      setPdfLoading(true);
      setPdfError("");
      if (pdfUrl) {
        URL.revokeObjectURL(pdfUrl);
        setPdfUrl("");
      }

      const nextPdfUrl = await fetchPdfBlob(`/reportes/${report.key}/pdf`);
      setSelectedReport(report);
      setPdfUrl(nextPdfUrl);
    } catch (err) {
      setPdfError(err instanceof Error ? err.message : "No se pudo cargar el PDF");
    } finally {
      setPdfLoading(false);
    }
  }

  function closePdfViewer() {
    if (pdfUrl) {
      URL.revokeObjectURL(pdfUrl);
    }
    setPdfUrl("");
    setPdfError("");
    setSelectedReport(null);
  }

  if (loading) return <LoadingState />;
  if (error || !reportes) return <ErrorState message={error || "Sin datos"} onRetry={loadData} />;

  if (selectedReport) {
    return (
      <Panel
        title={`PDF: ${selectedReport.title}`}
        description="Vista previa del reporte dentro del sistema."
      >
        <div className="mb-4 flex flex-wrap items-center justify-between gap-3">
          <p className="text-sm text-slate-500">
            {selectedReport.description}
          </p>
          <div className="flex flex-wrap gap-3">
            <button
              type="button"
              onClick={() => void openPdf(selectedReport)}
              disabled={pdfLoading}
              className="rounded-2xl border border-slate-200 px-4 py-2 text-sm font-semibold text-slate-700 transition hover:border-slate-300 hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-60"
            >
              {pdfLoading ? "Cargando..." : "Recargar PDF"}
            </button>
            <button
              type="button"
              onClick={closePdfViewer}
              className="rounded-2xl bg-teal-700 px-4 py-2 text-sm font-semibold text-white transition hover:bg-teal-800"
            >
              Regresar a reportes
            </button>
          </div>
        </div>

        {pdfLoading ? <LoadingState /> : null}
        {!pdfLoading && pdfError ? (
          <ErrorState message={pdfError} onRetry={() => void openPdf(selectedReport)} />
        ) : null}
        {!pdfLoading && !pdfError && pdfUrl ? (
          <iframe
            src={pdfUrl}
            title={`Vista previa de ${selectedReport.title}`}
            className="h-[calc(100vh-18rem)] min-h-[720px] w-full rounded-2xl border border-slate-200 bg-white"
          />
        ) : null}
      </Panel>
    );
  }

  return (
    <div className="space-y-6">
      {pdfError ? <ErrorState message={pdfError} onRetry={() => setPdfError("")} /> : null}
      {reportLabels.map((report) => {
        const rows = reportes[report.key] ?? [];
        const columns =
          rows[0] == null
            ? []
            : Object.keys(rows[0]).map((key) => ({
                key,
                label: key
                  .replace(/([A-Z])/g, " $1")
                  .replace(/^./, (char) => char.toUpperCase())
              }));

        return (
          <Panel key={report.key} title={report.title} description={report.description}>
            <div className="mb-4 flex justify-end">
              <button
                type="button"
                onClick={() => void openPdf(report)}
                className="rounded-2xl bg-teal-700 px-4 py-2 text-sm font-semibold text-white transition hover:bg-teal-800"
              >
                Ver PDF
              </button>
            </div>
            <DataTable columns={columns} rows={rows} />
          </Panel>
        );
      })}
    </div>
  );
}
