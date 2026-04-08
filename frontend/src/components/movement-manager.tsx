"use client";

import { FormEvent, useEffect, useState } from "react";

import { DataTable } from "@/components/data-table";
import { ErrorState, LoadingState, Panel } from "@/components/ui";
import { apiRequest } from "@/lib/api";
import { Movimiento, Producto } from "@/lib/types";

const initialForm = {
  fecha: "",
  idProducto: "",
  tipoMovimiento: "AJUSTE",
  cantidad: "",
  motivo: "",
  referencia: ""
};

export function MovementManager() {
  const [movimientos, setMovimientos] = useState<Movimiento[]>([]);
  const [productos, setProductos] = useState<Producto[]>([]);
  const [form, setForm] = useState(initialForm);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  async function loadData() {
    try {
      setLoading(true);
      setError("");
      const [movimientosData, productosData] = await Promise.all([
        apiRequest<Movimiento[]>("/movimientos"),
        apiRequest<Producto[]>("/productos")
      ]);
      setMovimientos(movimientosData);
      setProductos(productosData);
    } catch (err) {
      setError(err instanceof Error ? err.message : "No se pudieron cargar los movimientos");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void loadData();
  }, []);

  async function onSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    try {
      setSaving(true);
      setError("");
      const payload = {
        ...form,
        fecha: form.fecha || null,
        idProducto: Number(form.idProducto),
        cantidad: Number(form.cantidad)
      };
      await apiRequest(editingId ? `/movimientos/${editingId}` : "/movimientos", {
        method: editingId ? "PUT" : "POST",
        body: JSON.stringify(payload)
      });
      setEditingId(null);
      setForm(initialForm);
      await loadData();
    } catch (err) {
      setError(err instanceof Error ? err.message : "No se pudo guardar el movimiento");
    } finally {
      setSaving(false);
    }
  }

  function onEdit(movimiento: Movimiento) {
    setEditingId(movimiento.id);
    setForm({
      fecha: movimiento.fecha.slice(0, 16),
      idProducto: String(movimiento.producto.id),
      tipoMovimiento: movimiento.tipoMovimiento,
      cantidad: String(movimiento.cantidad),
      motivo: movimiento.motivo,
      referencia: movimiento.referencia ?? ""
    });
  }

  async function onDelete(id: number) {
    if (!window.confirm("¿Eliminar este movimiento?")) return;
    try {
      await apiRequest(`/movimientos/${id}`, { method: "DELETE" });
      await loadData();
    } catch (err) {
      setError(err instanceof Error ? err.message : "No se pudo eliminar");
    }
  }

  if (loading) return <LoadingState />;
  if (error && !movimientos.length) return <ErrorState message={error} onRetry={loadData} />;

  return (
    <div className="grid gap-6 xl:grid-cols-[0.9fr_1.1fr]">
      <Panel title={editingId ? "Editar movimiento" : "Nuevo movimiento"} description="Usa ajustes para corregir diferencias de inventario sin editar stock manualmente.">
        <form className="space-y-4" onSubmit={onSubmit}>
          <label className="block">
            <span className="mb-2 block text-sm font-medium text-slate-700">Producto</span>
            <select
              className="w-full rounded-2xl border border-slate-200 bg-white px-4 py-3"
              value={form.idProducto}
              onChange={(event) => setForm((current) => ({ ...current, idProducto: event.target.value }))}
            >
              <option value="">Selecciona</option>
              {productos.map((producto) => (
                <option key={producto.id} value={producto.id}>
                  {producto.nombre}
                </option>
              ))}
            </select>
          </label>

          <div className="grid gap-4 md:grid-cols-2">
            <label className="block">
              <span className="mb-2 block text-sm font-medium text-slate-700">Tipo</span>
              <select
                className="w-full rounded-2xl border border-slate-200 bg-white px-4 py-3"
                value={form.tipoMovimiento}
                onChange={(event) => setForm((current) => ({ ...current, tipoMovimiento: event.target.value }))}
              >
                <option value="ENTRADA">Entrada</option>
                <option value="SALIDA">Salida</option>
                <option value="AJUSTE">Ajuste</option>
              </select>
            </label>
            <label className="block">
              <span className="mb-2 block text-sm font-medium text-slate-700">Cantidad</span>
              <input
                type="number"
                className="w-full rounded-2xl border border-slate-200 bg-white px-4 py-3"
                value={form.cantidad}
                onChange={(event) => setForm((current) => ({ ...current, cantidad: event.target.value }))}
              />
            </label>
          </div>

          <label className="block">
            <span className="mb-2 block text-sm font-medium text-slate-700">Fecha</span>
            <input
              type="datetime-local"
              className="w-full rounded-2xl border border-slate-200 bg-white px-4 py-3"
              value={form.fecha}
              onChange={(event) => setForm((current) => ({ ...current, fecha: event.target.value }))}
            />
          </label>

          <label className="block">
            <span className="mb-2 block text-sm font-medium text-slate-700">Motivo</span>
            <textarea
              className="min-h-28 w-full rounded-2xl border border-slate-200 bg-white px-4 py-3"
              value={form.motivo}
              onChange={(event) => setForm((current) => ({ ...current, motivo: event.target.value }))}
            />
          </label>

          <label className="block">
            <span className="mb-2 block text-sm font-medium text-slate-700">Referencia</span>
            <input
              className="w-full rounded-2xl border border-slate-200 bg-white px-4 py-3"
              value={form.referencia}
              onChange={(event) => setForm((current) => ({ ...current, referencia: event.target.value }))}
            />
          </label>

          {error ? <p className="rounded-2xl bg-rose-50 px-4 py-3 text-sm text-rose-700">{error}</p> : null}

          <div className="flex gap-3">
            <button className="rounded-2xl bg-teal-700 px-4 py-3 font-semibold text-white" disabled={saving}>
              {saving ? "Guardando..." : editingId ? "Actualizar movimiento" : "Registrar movimiento"}
            </button>
            <button type="button" className="rounded-2xl border border-slate-200 px-4 py-3 font-semibold text-slate-700" onClick={() => {
              setEditingId(null);
              setForm(initialForm);
            }}>
              Limpiar
            </button>
          </div>
        </form>
      </Panel>

      <Panel title="Bitacora de movimientos" description="Toda entrada, salida o ajuste queda documentado para auditoria.">
        <DataTable
          columns={[
            { key: "fecha", label: "Fecha" },
            {
              key: "producto",
              label: "Producto",
              render: (row) => (row as Movimiento).producto.nombre
            },
            { key: "tipoMovimiento", label: "Tipo" },
            { key: "cantidad", label: "Cantidad" },
            { key: "motivo", label: "Motivo" }
          ]}
          rows={movimientos}
          actions={(row) => {
            const movimiento = row as Movimiento;
            return (
              <div className="flex gap-2">
                <button className="rounded-xl bg-amber-500 px-3 py-2 text-xs font-semibold text-slate-950" onClick={() => onEdit(movimiento)}>
                  Editar
                </button>
                <button className="rounded-xl bg-rose-600 px-3 py-2 text-xs font-semibold text-white" onClick={() => onDelete(movimiento.id)}>
                  Eliminar
                </button>
              </div>
            );
          }}
        />
      </Panel>
    </div>
  );
}
