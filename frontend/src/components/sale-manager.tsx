"use client";

import { FormEvent, useEffect, useMemo, useState } from "react";

import { DataTable } from "@/components/data-table";
import { ErrorState, LoadingState, Panel } from "@/components/ui";
import { apiRequest, money } from "@/lib/api";
import { Producto, Venta } from "@/lib/types";

type SaleItem = {
  idProducto: string;
  cantidad: string;
  precioVenta: string;
};

const emptyItem = (): SaleItem => ({
  idProducto: "",
  cantidad: "1",
  precioVenta: ""
});

export function SaleManager() {
  const [ventas, setVentas] = useState<Venta[]>([]);
  const [productos, setProductos] = useState<Producto[]>([]);
  const [cliente, setCliente] = useState("");
  const [fecha, setFecha] = useState("");
  const [detalles, setDetalles] = useState<SaleItem[]>([emptyItem()]);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  const total = useMemo(() => {
    return detalles.reduce((sum, item) => sum + Number(item.cantidad || 0) * Number(item.precioVenta || 0), 0);
  }, [detalles]);

  async function loadData() {
    try {
      setLoading(true);
      setError("");
      const [ventasData, productosData] = await Promise.all([
        apiRequest<Venta[]>("/ventas"),
        apiRequest<Producto[]>("/productos")
      ]);
      setVentas(ventasData);
      setProductos(productosData);
    } catch (err) {
      setError(err instanceof Error ? err.message : "No se pudieron cargar las ventas");
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
        fecha: fecha || null,
        cliente,
        detalles: detalles.map((item) => ({
          idProducto: Number(item.idProducto),
          cantidad: Number(item.cantidad),
          precioVenta: Number(item.precioVenta)
        }))
      };
      await apiRequest(editingId ? `/ventas/${editingId}` : "/ventas", {
        method: editingId ? "PUT" : "POST",
        body: JSON.stringify(payload)
      });

      resetForm();
      await loadData();
    } catch (err) {
      setError(err instanceof Error ? err.message : "No se pudo guardar la venta");
    } finally {
      setSaving(false);
    }
  }

  function resetForm() {
    setEditingId(null);
    setCliente("");
    setFecha("");
    setDetalles([emptyItem()]);
  }

  async function onDelete(id: number) {
    if (!window.confirm("¿Eliminar esta venta?")) return;
    try {
      await apiRequest(`/ventas/${id}`, { method: "DELETE" });
      await loadData();
    } catch (err) {
      setError(err instanceof Error ? err.message : "No se pudo eliminar");
    }
  }

  function onEdit(venta: Venta) {
    setEditingId(venta.idVenta);
    setCliente(venta.cliente ?? "");
    setFecha(venta.fecha.slice(0, 16));
    setDetalles(
      venta.detalles.map((detalle) => ({
        idProducto: String(detalle.idProducto),
        cantidad: String(detalle.cantidad),
        precioVenta: String(detalle.precioVenta)
      }))
    );
  }

  if (loading) return <LoadingState />;
  if (error && !ventas.length) return <ErrorState message={error} onRetry={loadData} />;

  return (
    <div className="grid gap-6 xl:grid-cols-[0.95fr_1.05fr]">
      <Panel title={editingId ? "Editar venta" : "Registrar venta"} description="Cada venta descuenta stock y deja trazabilidad inmediata en movimientos.">
        <form className="space-y-4" onSubmit={onSubmit}>
          <div className="grid gap-4 md:grid-cols-2">
            <label className="block">
              <span className="mb-2 block text-sm font-medium text-slate-700">Cliente</span>
              <input
                className="w-full rounded-2xl border border-slate-200 bg-white px-4 py-3"
                value={cliente}
                onChange={(event) => setCliente(event.target.value)}
                placeholder="Cliente mostrador o empresa"
              />
            </label>
            <label className="block">
              <span className="mb-2 block text-sm font-medium text-slate-700">Fecha</span>
              <input
                type="datetime-local"
                className="w-full rounded-2xl border border-slate-200 bg-white px-4 py-3"
                value={fecha}
                onChange={(event) => setFecha(event.target.value)}
              />
            </label>
          </div>

          <div className="space-y-3">
            {detalles.map((item, index) => (
              <div key={`${item.idProducto}-${index}`} className="grid gap-3 rounded-2xl border border-slate-200 bg-slate-50 p-4 md:grid-cols-[1.5fr_0.7fr_0.8fr_auto]">
                <select
                  className="rounded-2xl border border-slate-200 bg-white px-4 py-3"
                  value={item.idProducto}
                  onChange={(event) =>
                    setDetalles((current) =>
                      current.map((detail, position) =>
                        position === index ? { ...detail, idProducto: event.target.value } : detail
                      )
                    )
                  }
                >
                  <option value="">Producto</option>
                  {productos.map((producto) => (
                    <option key={producto.id} value={producto.id}>
                      {producto.nombre} · stock {producto.stockActual}
                    </option>
                  ))}
                </select>
                <input
                  type="number"
                  min="1"
                  className="rounded-2xl border border-slate-200 bg-white px-4 py-3"
                  value={item.cantidad}
                  onChange={(event) =>
                    setDetalles((current) =>
                      current.map((detail, position) =>
                        position === index ? { ...detail, cantidad: event.target.value } : detail
                      )
                    )
                  }
                />
                <input
                  type="number"
                  step="0.01"
                  min="0"
                  className="rounded-2xl border border-slate-200 bg-white px-4 py-3"
                  value={item.precioVenta}
                  onChange={(event) =>
                    setDetalles((current) =>
                      current.map((detail, position) =>
                        position === index ? { ...detail, precioVenta: event.target.value } : detail
                      )
                    )
                  }
                />
                <button
                  type="button"
                  className="rounded-2xl bg-slate-900 px-4 py-3 text-sm font-semibold text-white"
                  onClick={() => setDetalles((current) => current.filter((_, position) => position !== index))}
                >
                  Quitar
                </button>
              </div>
            ))}
          </div>

          <button
            type="button"
            className="rounded-2xl border border-dashed border-teal-600 px-4 py-3 text-sm font-semibold text-teal-700"
            onClick={() => setDetalles((current) => [...current, emptyItem()])}
          >
            Agregar producto
          </button>

          <div className="rounded-2xl bg-teal-700 px-4 py-4 text-sm font-semibold text-white">
            Total estimado: <span className="text-xl">{money.format(total)}</span>
          </div>

          {error ? <p className="rounded-2xl bg-rose-50 px-4 py-3 text-sm text-rose-700">{error}</p> : null}

          <div className="flex gap-3">
            <button className="rounded-2xl bg-rose-600 px-4 py-3 font-semibold text-white" disabled={saving}>
              {saving ? "Guardando..." : editingId ? "Actualizar venta" : "Registrar venta"}
            </button>
            <button type="button" className="rounded-2xl border border-slate-200 px-4 py-3 font-semibold text-slate-700" onClick={resetForm}>
              Limpiar
            </button>
          </div>
        </form>
      </Panel>

      <Panel title="Ventas registradas" description="Revisa historiales, clientes y totales facturados.">
        <DataTable
          columns={[
            { key: "fecha", label: "Fecha" },
            { key: "cliente", label: "Cliente" },
            {
              key: "detalles",
              label: "Detalle",
              render: (row) =>
                (row as Venta).detalles.map((detalle) => `${detalle.producto} x${detalle.cantidad}`).join(", ")
            },
            {
              key: "total",
              label: "Total",
              render: (row) => money.format((row as Venta).total)
            }
          ]}
          rows={ventas}
          actions={(row) => {
            const venta = row as Venta;
            return (
              <div className="flex gap-2">
                <button className="rounded-xl bg-amber-500 px-3 py-2 text-xs font-semibold text-slate-950" onClick={() => onEdit(venta)}>
                  Editar
                </button>
                <button className="rounded-xl bg-rose-600 px-3 py-2 text-xs font-semibold text-white" onClick={() => onDelete(venta.idVenta)}>
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
