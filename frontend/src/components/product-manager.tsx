"use client";

import { ChangeEvent, FormEvent, useEffect, useRef, useState } from "react";

import { DataTable } from "@/components/data-table";
import { ErrorState, LoadingState, Panel } from "@/components/ui";
import { apiRequest, money } from "@/lib/api";
import { Categoria, Producto, Proveedor } from "@/lib/types";

const MAX_PHOTO_SIZE_BYTES = 15 * 1024 * 1024;

type ProductFormState = {
  codigo: string;
  nombre: string;
  descripcion: string;
  unidadMedida: string;
  precioCompra: string;
  precioVenta: string;
  stockActual: string;
  stockMinimo: string;
  estado: "ACTIVO" | "INACTIVO";
  idCategoria: string;
  idProveedorPrincipal: string;
  foto: File | null;
};

const initialForm: ProductFormState = {
  codigo: "",
  nombre: "",
  descripcion: "",
  unidadMedida: "unidad",
  precioCompra: "",
  precioVenta: "",
  stockActual: "0",
  stockMinimo: "",
  estado: "ACTIVO",
  idCategoria: "",
  idProveedorPrincipal: "",
  foto: null
};

export function ProductManager() {
  const [productos, setProductos] = useState<Producto[]>([]);
  const [categorias, setCategorias] = useState<Categoria[]>([]);
  const [proveedores, setProveedores] = useState<Proveedor[]>([]);
  const [form, setForm] = useState<ProductFormState>(initialForm);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const fileInputRef = useRef<HTMLInputElement | null>(null);

  async function loadData() {
    try {
      setLoading(true);
      setError("");
      const [productosData, categoriasData, proveedoresData] = await Promise.all([
        apiRequest<Producto[]>("/productos"),
        apiRequest<Categoria[]>("/categorias"),
        apiRequest<Proveedor[]>("/proveedores")
      ]);
      setProductos(productosData);
      setCategorias(categoriasData);
      setProveedores(proveedoresData);
    } catch (err) {
      setError(err instanceof Error ? err.message : "No se pudieron cargar los productos");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void loadData();
  }, []);

  function readFileAsDataUrl(file: File) {
    return new Promise<string>((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = () => {
        if (typeof reader.result === "string") {
          resolve(reader.result);
          return;
        }
        reject(new Error("No se pudo procesar la imagen"));
      };
      reader.onerror = () => reject(new Error("No se pudo procesar la imagen"));
      reader.readAsDataURL(file);
    });
  }

  async function buildJsonPayload() {
    const payload = {
      codigo: form.codigo,
      nombre: form.nombre,
      descripcion: form.descripcion,
      unidadMedida: form.unidadMedida,
      precioCompra: Number(form.precioCompra),
      precioVenta: Number(form.precioVenta),
      stockActual: Number(form.stockActual),
      stockMinimo: Number(form.stockMinimo),
      estado: form.estado,
      idCategoria: Number(form.idCategoria),
      idProveedorPrincipal: form.idProveedorPrincipal ? Number(form.idProveedorPrincipal) : null
    };

    if (!form.foto) {
      return payload;
    }

    return {
      ...payload,
      fotoNombre: form.foto.name,
      fotoBase64: await readFileAsDataUrl(form.foto)
    };
  }

  function formatFileSize(bytes: number) {
    return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
  }

  async function onSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    try {
      setSaving(true);
      setError("");
      const method = editingId ? "PUT" : "POST";
      const path = editingId ? `/productos/${editingId}` : "/productos";
      await apiRequest(path, {
        method,
        body: JSON.stringify(await buildJsonPayload())
      });
      setForm(initialForm);
      setEditingId(null);
      if (fileInputRef.current) {
        fileInputRef.current.value = "";
      }
      await loadData();
    } catch (err) {
      setError(err instanceof Error ? err.message : "No se pudo guardar el producto");
    } finally {
      setSaving(false);
    }
  }

  async function onDelete(id: number) {
    if (!window.confirm("¿Eliminar este producto?")) return;
    try {
      await apiRequest(`/productos/${id}`, { method: "DELETE" });
      await loadData();
    } catch (err) {
      setError(err instanceof Error ? err.message : "No se pudo eliminar");
    }
  }

  function onEdit(producto: Producto) {
    setEditingId(producto.id);
    setError("");
    setForm({
      codigo: producto.codigo,
      nombre: producto.nombre,
      descripcion: producto.descripcion,
      unidadMedida: producto.unidadMedida,
      precioCompra: String(producto.precioCompra),
      precioVenta: String(producto.precioVenta),
      stockActual: String(producto.stockActual),
      stockMinimo: String(producto.stockMinimo),
      estado: producto.estado,
      idCategoria: String(producto.categoria.id),
      idProveedorPrincipal: producto.proveedorPrincipal ? String(producto.proveedorPrincipal.id) : "",
      foto: null
    });
    if (fileInputRef.current) {
      fileInputRef.current.value = "";
    }
  }

  if (loading) return <LoadingState />;
  if (error && !productos.length) return <ErrorState message={error} onRetry={loadData} />;

  return (
    <div className="grid gap-6 xl:grid-cols-[0.92fr_1.08fr]">
      <Panel title={editingId ? "Editar producto" : "Nuevo producto"} description="Registra productos con foto, categoria, proveedor principal y parametros de stock.">
        <form className="space-y-4" onSubmit={onSubmit}>
          <div className="grid gap-4 md:grid-cols-2">
            {[
              ["codigo", "Codigo"],
              ["nombre", "Nombre"],
              ["unidadMedida", "Unidad de medida"],
              ["stockMinimo", "Stock minimo"],
              ["precioCompra", "Precio compra"],
              ["precioVenta", "Precio venta"],
              ["stockActual", "Stock actual"]
            ].map(([key, label]) => (
              <label key={key} className="block">
                <span className="mb-2 block text-sm font-medium text-slate-700">{label}</span>
                <input
                  className="w-full rounded-2xl border border-slate-200 bg-white px-4 py-3 outline-none"
                  value={form[key as keyof ProductFormState] as string}
                  onChange={(event) =>
                    setForm((current) => ({ ...current, [key]: event.target.value }))
                  }
                />
              </label>
            ))}
          </div>

          <label className="block">
            <span className="mb-2 block text-sm font-medium text-slate-700">Descripcion</span>
            <textarea
              className="min-h-28 w-full rounded-2xl border border-slate-200 bg-white px-4 py-3 outline-none"
              value={form.descripcion}
              onChange={(event) => setForm((current) => ({ ...current, descripcion: event.target.value }))}
            />
          </label>

          <div className="grid gap-4 md:grid-cols-3">
            <label className="block">
              <span className="mb-2 block text-sm font-medium text-slate-700">Categoria</span>
              <select
                className="w-full rounded-2xl border border-slate-200 bg-white px-4 py-3 outline-none"
                value={form.idCategoria}
                onChange={(event) => setForm((current) => ({ ...current, idCategoria: event.target.value }))}
              >
                <option value="">Selecciona</option>
                {categorias.map((categoria) => (
                  <option key={categoria.id} value={categoria.id}>
                    {categoria.nombre}
                  </option>
                ))}
              </select>
            </label>

            <label className="block">
              <span className="mb-2 block text-sm font-medium text-slate-700">Proveedor principal</span>
              <select
                className="w-full rounded-2xl border border-slate-200 bg-white px-4 py-3 outline-none"
                value={form.idProveedorPrincipal}
                onChange={(event) => setForm((current) => ({ ...current, idProveedorPrincipal: event.target.value }))}
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
              <span className="mb-2 block text-sm font-medium text-slate-700">Estado</span>
              <select
                className="w-full rounded-2xl border border-slate-200 bg-white px-4 py-3 outline-none"
                value={form.estado}
                onChange={(event) =>
                  setForm((current) => ({ ...current, estado: event.target.value as ProductFormState["estado"] }))
                }
              >
                <option value="ACTIVO">Activo</option>
                <option value="INACTIVO">Inactivo</option>
              </select>
            </label>
          </div>

          <label className="block">
            <span className="mb-2 block text-sm font-medium text-slate-700">Foto del producto</span>
            <input
              ref={fileInputRef}
              type="file"
              accept=".jpg,.jpeg,.png,.webp"
              className="w-full rounded-2xl border border-slate-200 bg-white px-4 py-3"
              onChange={(event: ChangeEvent<HTMLInputElement>) => {
                const selectedFile = event.target.files?.[0] ?? null;
                if (selectedFile && selectedFile.size > MAX_PHOTO_SIZE_BYTES) {
                  setError(`La imagen supera el limite de ${formatFileSize(MAX_PHOTO_SIZE_BYTES)}`);
                  event.target.value = "";
                  setForm((current) => ({ ...current, foto: null }));
                  return;
                }

                setError("");
                setForm((current) => ({ ...current, foto: selectedFile }));
              }}
            />
            <p className="mt-2 text-xs text-slate-500">
              Formatos permitidos: JPG, JPEG, PNG o WEBP. Tamano maximo: {formatFileSize(MAX_PHOTO_SIZE_BYTES)}.
            </p>
            {editingId ? (
              <p className="mt-1 text-xs text-slate-500">
                Deja este campo vacio si no quieres cambiar la imagen actual.
              </p>
            ) : null}
          </label>

          {error ? <p className="rounded-2xl bg-rose-50 px-4 py-3 text-sm text-rose-700">{error}</p> : null}

          <div className="flex gap-3">
            <button className="rounded-2xl bg-teal-700 px-4 py-3 font-semibold text-white" disabled={saving}>
              {saving ? "Guardando..." : editingId ? "Actualizar producto" : "Registrar producto"}
            </button>
            <button
              type="button"
              className="rounded-2xl border border-slate-200 px-4 py-3 font-semibold text-slate-700"
              onClick={() => {
                setEditingId(null);
                setError("");
                setForm(initialForm);
                if (fileInputRef.current) {
                  fileInputRef.current.value = "";
                }
              }}
            >
              Limpiar
            </button>
          </div>
        </form>
      </Panel>

      <Panel title="Catalogo de productos" description="Consulta stock, categoria, proveedor y fotografia registrada.">
        <DataTable
          columns={[
            {
              key: "fotoUrl",
              label: "Foto",
              render: (row) => {
                const producto = row as Producto;
                return producto.fotoUrl ? (
                  <img
                    src={`${process.env.NEXT_PUBLIC_API_URL?.replace("/api", "") ?? "http://localhost:8080"}${producto.fotoUrl}`}
                    alt={producto.nombre}
                    className="h-14 w-14 rounded-2xl object-cover"
                  />
                ) : (
                  <div className="grid h-14 w-14 place-items-center rounded-2xl bg-slate-200 text-xs text-slate-500">
                    Sin foto
                  </div>
                );
              }
            },
            { key: "codigo", label: "Codigo" },
            { key: "nombre", label: "Producto" },
            {
              key: "categoria",
              label: "Categoria",
              render: (row) => (row as Producto).categoria.nombre
            },
            { key: "stockActual", label: "Stock" },
            {
              key: "precioVenta",
              label: "Precio venta",
              render: (row) => money.format((row as Producto).precioVenta)
            }
          ]}
          rows={productos}
          actions={(row) => {
            const producto = row as Producto;
            return (
              <div className="flex gap-2">
                <button className="rounded-xl bg-amber-500 px-3 py-2 text-xs font-semibold text-slate-950" onClick={() => onEdit(producto)}>
                  Editar
                </button>
                <button className="rounded-xl bg-rose-600 px-3 py-2 text-xs font-semibold text-white" onClick={() => onDelete(producto.id)}>
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
