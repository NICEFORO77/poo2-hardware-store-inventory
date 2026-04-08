"use client";

import { EmptyState } from "@/components/ui";

type Row = Record<string, unknown>;

export function DataTable({
  columns,
  rows,
  actions
}: {
  columns: Array<{ key: string; label: string; render?: (row: Row) => React.ReactNode }>;
  rows: Row[];
  actions?: (row: Row) => React.ReactNode;
}) {
  if (!rows.length) {
    return <EmptyState message="No hay registros para mostrar." />;
  }

  return (
    <div className="soft-scrollbar overflow-x-auto">
      <table className="min-w-full border-separate border-spacing-y-2">
        <thead>
          <tr className="text-left text-xs uppercase tracking-[0.25em] text-slate-500">
            {columns.map((column) => (
              <th key={column.key} className="px-3 py-2 font-semibold">
                {column.label}
              </th>
            ))}
            {actions ? <th className="px-3 py-2 font-semibold">Acciones</th> : null}
          </tr>
        </thead>
        <tbody>
          {rows.map((row, index) => (
            <tr key={String(row.id ?? row.codigo ?? index)} className="rounded-2xl bg-slate-50 text-sm text-slate-700">
              {columns.map((column) => (
                <td key={column.key} className="px-3 py-4 align-top">
                  {column.render ? column.render(row) : String(row[column.key] ?? "")}
                </td>
              ))}
              {actions ? <td className="px-3 py-4 align-top">{actions(row)}</td> : null}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
