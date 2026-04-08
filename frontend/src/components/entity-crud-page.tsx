"use client";

import { useEffect, useMemo, useState } from "react";

import { DataTable } from "@/components/data-table";
import { ErrorState, LoadingState, Panel } from "@/components/ui";
import { apiRequest } from "@/lib/api";

type Option = {
  label: string;
  value: string;
};

type CrudField = {
  name: string;
  label: string;
  type?: "text" | "email" | "number" | "textarea" | "select" | "password" | "checkbox";
  placeholder?: string;
  options?: Option[];
};

export function EntityCrudPage<T extends Record<string, unknown>>({
  title,
  description,
  endpoint,
  fields,
  columns,
  initialValues
}: {
  title: string;
  description: string;
  endpoint: string;
  fields: CrudField[];
  columns: Array<{ key: string; label: string; render?: (row: T) => React.ReactNode }>;
  initialValues: Record<string, unknown>;
}) {
  const [rows, setRows] = useState<T[]>([]);
  const [form, setForm] = useState<Record<string, unknown>>(initialValues);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [saving, setSaving] = useState(false);

  const submitLabel = useMemo(() => (editingId ? "Actualizar" : "Registrar"), [editingId]);
  const formKeys = useMemo(() => Object.keys(initialValues), [initialValues]);

  async function loadRows() {
    try {
      setLoading(true);
      setError("");
      const data = await apiRequest<T[]>(endpoint);
      setRows(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : "No se pudieron cargar los datos");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void loadRows();
  }, [endpoint]);

  async function onSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    try {
      setSaving(true);
      const payload = Object.fromEntries(formKeys.map((key) => [key, form[key]]));
      const method = editingId ? "PUT" : "POST";
      const path = editingId ? `${endpoint}/${editingId}` : endpoint;
      await apiRequest(path, {
        method,
        body: JSON.stringify(payload)
      });
      setForm(initialValues);
      setEditingId(null);
      await loadRows();
    } catch (err) {
      setError(err instanceof Error ? err.message : "No se pudo guardar");
    } finally {
      setSaving(false);
    }
  }

  async function onDelete(id: number) {
    if (!window.confirm("¿Deseas eliminar este registro?")) return;

    try {
      await apiRequest(`${endpoint}/${id}`, { method: "DELETE" });
      await loadRows();
    } catch (err) {
      setError(err instanceof Error ? err.message : "No se pudo eliminar");
    }
  }

  if (loading) return <LoadingState />;
  if (error && !rows.length) return <ErrorState message={error} onRetry={loadRows} />;

  return (
    <div className="grid gap-6 xl:grid-cols-[0.95fr_1.05fr]">
      <Panel title={title} description={description}>
        <form className="space-y-4" onSubmit={onSubmit}>
          {fields.map((field) => (
            <label key={field.name} className="block">
              <span className="mb-2 block text-sm font-medium text-slate-700">{field.label}</span>
              {field.type === "textarea" ? (
                <textarea
                  className="min-h-28 w-full rounded-2xl border border-slate-200 bg-white px-4 py-3 outline-none ring-0"
                  value={String(form[field.name] ?? "")}
                  placeholder={field.placeholder}
                  onChange={(event) =>
                    setForm((current) => ({ ...current, [field.name]: event.target.value }))
                  }
                />
              ) : field.type === "select" ? (
                <select
                  className="w-full rounded-2xl border border-slate-200 bg-white px-4 py-3 outline-none"
                  value={String(form[field.name] ?? "")}
                  onChange={(event) =>
                    setForm((current) => ({ ...current, [field.name]: event.target.value }))
                  }
                >
                  <option value="">Selecciona una opcion</option>
                  {field.options?.map((option) => (
                    <option key={option.value} value={option.value}>
                      {option.label}
                    </option>
                  ))}
                </select>
              ) : field.type === "checkbox" ? (
                <label className="flex items-center gap-3 rounded-2xl border border-slate-200 bg-white px-4 py-3">
                  <input
                    type="checkbox"
                    checked={Boolean(form[field.name])}
                    onChange={(event) =>
                      setForm((current) => ({ ...current, [field.name]: event.target.checked }))
                    }
                  />
                  <span className="text-sm text-slate-600">Activo</span>
                </label>
              ) : (
                <input
                  type={field.type ?? "text"}
                  className="w-full rounded-2xl border border-slate-200 bg-white px-4 py-3 outline-none"
                  value={String(form[field.name] ?? "")}
                  placeholder={field.placeholder}
                  onChange={(event) =>
                    setForm((current) => ({
                      ...current,
                      [field.name]:
                        field.type === "number" ? Number(event.target.value) : event.target.value
                    }))
                  }
                />
              )}
            </label>
          ))}

          {error ? <p className="rounded-2xl bg-rose-50 px-4 py-3 text-sm text-rose-700">{error}</p> : null}

          <div className="flex gap-3">
            <button
              type="submit"
              disabled={saving}
              className="rounded-2xl bg-teal-700 px-4 py-3 font-semibold text-white transition hover:bg-teal-800 disabled:opacity-70"
            >
              {saving ? "Guardando..." : submitLabel}
            </button>
            <button
              type="button"
              className="rounded-2xl border border-slate-200 px-4 py-3 font-semibold text-slate-700"
              onClick={() => {
                setForm(initialValues);
                setEditingId(null);
              }}
            >
              Limpiar
            </button>
          </div>
        </form>
      </Panel>

      <Panel title="Listado" description="Edita o elimina los registros desde esta tabla.">
        <DataTable
          columns={columns as Array<{ key: string; label: string; render?: (row: Record<string, unknown>) => React.ReactNode }>}
          rows={rows}
          actions={(row) => {
            const record = row as T & { id: number };
            return (
              <div className="flex gap-2">
                <button
                  className="rounded-xl bg-amber-500 px-3 py-2 text-xs font-semibold text-slate-950"
                  onClick={() => {
                    setEditingId(record.id);
                    setForm(Object.fromEntries(formKeys.map((key) => [key, record[key]])));
                  }}
                >
                  Editar
                </button>
                <button
                  className="rounded-xl bg-rose-600 px-3 py-2 text-xs font-semibold text-white"
                  onClick={() => onDelete(record.id)}
                >
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
