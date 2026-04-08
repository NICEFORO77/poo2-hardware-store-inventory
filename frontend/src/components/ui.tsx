"use client";

import clsx from "clsx";

export function Panel({
  title,
  description,
  children,
  className
}: {
  title: string;
  description?: string;
  children: React.ReactNode;
  className?: string;
}) {
  return (
    <section className={clsx("rounded-[26px] border border-slate-200/80 bg-white/80 p-5 shadow-sm", className)}>
      <div className="mb-5 flex flex-col gap-1">
        <h3 className="panel-title">{title}</h3>
        {description ? <p className="text-sm text-slate-600">{description}</p> : null}
      </div>
      {children}
    </section>
  );
}

export function MetricCard({
  label,
  value,
  accent = "teal"
}: {
  label: string;
  value: string;
  accent?: "teal" | "amber" | "rose" | "slate";
}) {
  const colors = {
    teal: "from-teal-700 to-teal-500",
    amber: "from-amber-600 to-amber-400",
    rose: "from-rose-700 to-rose-500",
    slate: "from-slate-700 to-slate-500"
  };

  return (
    <div className="overflow-hidden rounded-[24px] border border-slate-200 bg-white">
      <div className={clsx("h-1.5 bg-gradient-to-r", colors[accent])} />
      <div className="p-5">
        <p className="text-sm text-slate-500">{label}</p>
        <p className="mt-3 text-3xl font-bold text-slate-900">{value}</p>
      </div>
    </div>
  );
}

export function EmptyState({ message }: { message: string }) {
  return (
    <div className="rounded-2xl border border-dashed border-slate-300 bg-slate-50 px-4 py-8 text-center text-sm text-slate-500">
      {message}
    </div>
  );
}

export function LoadingState() {
  return (
    <div className="rounded-2xl border border-slate-200 bg-white px-4 py-8 text-center text-sm text-slate-500">
      Cargando informacion...
    </div>
  );
}

export function ErrorState({
  message,
  onRetry
}: {
  message: string;
  onRetry?: () => void;
}) {
  return (
    <div className="rounded-2xl border border-rose-200 bg-rose-50 px-4 py-6 text-sm text-rose-700">
      <p>{message}</p>
      {onRetry ? (
        <button
          onClick={onRetry}
          className="mt-3 rounded-xl bg-rose-600 px-3 py-2 font-medium text-white"
        >
          Reintentar
        </button>
      ) : null}
    </div>
  );
}
