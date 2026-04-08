"use client";

import { FormEvent, useEffect, useMemo, useState } from "react";

import { DataTable } from "@/components/data-table";
import { ErrorState, LoadingState, Panel } from "@/components/ui";
import { apiRequest, money } from "@/lib/api";
import { Compra, Producto, Proveedor } from "@/lib/types";

type PurchaseItem = {
  idProducto: string;
  cantidad: string;
  precioCompra: string;
};

const emptyItem = (): PurchaseItem => ({
  idProducto: "",
  cantidad: "1",
  precioCompra: ""
});

export function PurchaseManager() {
  const [compras, setCompras] = useState<Compra[]>([]);
  const [proveedores, setProveedores] = useState<Proveedor[]>([]);
  const [productos, setProductos] = useState<Producto[]>([]);
  const [idProveedor, setIdProveedor] = useState("");
  const [fecha, setFecha] = useState("");
  const [detalles, setDetalles] = useState<PurchaseItem[]>([emptyItem()]);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  const total = useMemo(() => {
    return detalles.reduce((sum, item) => sum + Number(item.cantidad || 0) * Number(item.precioCompra || 0), 0);
  }, [detalles]);

  async function loadData() {
    try {
      setLoading(true);
      setError("");
      const [comprasData, proveedoresData, productosData] = await Promise.all([
        apiRequest<Compra[]>("/compras"),
        apiRequest<Proveedor[]>("/proveedores"),
        apiRequest<Producto[]>("/productos")
      ]);
      setCompras(comprasData);
      setProveedores(proveedoresData);
      setProductos(productosData);
    } catch (err) {
      setError(err instanceof Error ? err.message : "No se pudieron cargar las compras");
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
        idProveedor: Number(idProveedor),
        detalles: detalles.map((item) => ({
          idProducto: Number(item.idProducto),
          cantidad: Number(item.cantidad),
          precioCompra: Number(item.precioCompra)
        }))
      };

      await apiRequest(editingId ? `/compras/${editingId}` : "/compras", {
        method: editingId ? "PUT" : "POST",
        body: JSON.stringify(payload)
      });

      resetForm();
      await loadData();
    } catch (err) {
      setError(err instanceof Error ? err.message : "No se pudo guardar la compra");
    } finally {
      setSaving(false);
    }
  }

  function resetForm() {
    setEditingId(null);
    setIdProveedor("");
    setFecha("");
    setDetalles([emptyItem()]);
  }

  async function onDelete(id: number) {
    if (!window.confirm("¿Eliminar esta compra?")) return;
    try {
      await apiRequest(`/compras/${id}`, { method: "DELETE" });
      await loadData();
    } catch (err) {
      setError(err instanceof Error ? err.message : "No se pudo eliminar");
    }
  }

  function onEdit(compra: Compra) {
    setEditingId(compra.idCompra);
    setIdProveedor(String(compra.idProveedor));
    setFecha(compra.fecha.slice(0, 16));
    setDetalles(
      compra.detalles.map((detalle) => ({
        idProducto: String(detalle.idProducto),
        cantidad: String(detalle.cantidad),
        precioCompra: String(detalle.precioCompra)
      }))
    );
  }

  if (loading) return <LoadingState />;
  if (error && !compras.length) return <ErrorState message={error} onRetry={loadData} />;

  return (
    <div className="grid gap-6 xl:grid-cols-[0.95fr_1.05fr]">
      <Panel title={editingId ? "Editar compra" : "Registrar compra"} description="Cada compra incrementa stock y genera movimientos de entrada automaticamente.">
        <form className="space-y-4" onSubmit={onSubmit}>
          <div className="grid gap-4 md:grid-cols-2">
            <label className="block">
              <span className="mb-2 block text-sm font-medium text-slate-700">Proveedor</span>
              <select
                className="w-full rounded-2xl border border-slate-200 bg-white px-4 py-3"
                value={idProveedor}
                onChange={(event) => setIdProveedor(event.target.value)}
              >
                <option value="">Selecciona</option>
                {proveedores.map((proveedor) => (
                  <option key={proveedor.id} value={proveedor.id}>
                    {proveedor.nombre}
                  </option>
                ))}
              </select>
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
                      {producto.nombre}
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
                  value={item.precioCompra}
                  onChange={(event) =>
                    setDetalles((current) =>
                      current.map((detail, position) =>
                        position === index ? { ...detail, precioCompra: event.target.value } : detail
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

          <div className="rounded-2xl bg-amber-100 px-4 py-4 text-sm font-semibold text-amber-950">
            Total estimado: <span className="text-xl">{money.format(total)}</span>
          </div>

          {error ? <p className="rounded-2xl bg-rose-50 px-4 py-3 text-sm text-rose-700">{error}</p> : null}

          <div className="flex gap-3">
            <button className="rounded-2xl bg-teal-700 px-4 py-3 font-semibold text-white" disabled={saving}>
              {saving ? "Guardando..." : editingId ? "Actualizar compra" : "Registrar compra"}
            </button>
            <button type="button" className="rounded-2xl border border-slate-200 px-4 py-3 font-semibold text-slate-700" onClick={resetForm}>
              Limpiar
            </button>
          </div>
        </form>
      </Panel>

      <Panel title="Compras registradas" description="Consulta el historial y modifica transacciones cuando sea necesario.">
        <DataTable
          columns={[
            { key: "fecha", label: "Fecha" },
            { key: "proveedor", label: "Proveedor" },
            {
              key: "detalles",
              label: "Detalle",
              render: (row) =>
                (row as Compra).detalles.map((detalle) => `${detalle.producto} x${detalle.cantidad}`).join(", ")
            },
            {
              key: "total",
              label: "Total",
              render: (row) => money.format((row as Compra).total)
            }
          ]}
          rows={compras}
          actions={(row) => {
            const compra = row as Compra;
            return (
              <div className="flex gap-2">
                <button className="rounded-xl bg-amber-500 px-3 py-2 text-xs font-semibold text-slate-950" onClick={() => onEdit(compra)}>
                  Editar
                </button>
                <button className="rounded-xl bg-rose-600 px-3 py-2 text-xs font-semibold text-white" onClick={() => onDelete(compra.idCompra)}>
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
