"use client";

import { LockKeyhole, UserRound } from "lucide-react";
import { useRouter } from "next/navigation";
import { FormEvent, useEffect, useState } from "react";

import { apiRequest } from "@/lib/api";
import { getSession, saveSession } from "@/lib/auth";

type LoginResponse = {
  token: string;
  username: string;
  nombreCompleto: string;
  rol: string;
};

export default function LoginPage() {
  const router = useRouter();
  const [username, setUsername] = useState("admin");
  const [password, setPassword] = useState("admin123");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    if (getSession()) {
      router.replace("/dashboard");
    }
  }, [router]);

  async function onSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setLoading(true);
    setError("");

    try {
      const response = await apiRequest<LoginResponse>("/auth/login", {
        method: "POST",
        body: JSON.stringify({ username, password })
      });

      saveSession({
        token: response.token,
        username: response.username,
        nombreCompleto: response.nombreCompleto,
        rol: response.rol
      });
      router.replace("/dashboard");
    } catch (err) {
      setError(err instanceof Error ? err.message : "No se pudo iniciar sesion");
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="grid min-h-screen place-items-center px-4 py-10">
      <div className="grid w-full max-w-6xl overflow-hidden rounded-[32px] border border-white/50 bg-white/80 shadow-[0_24px_80px_rgba(15,23,42,0.18)] backdrop-blur xl:grid-cols-[1.1fr_0.9fr]">
        <section className="relative hidden overflow-hidden bg-[linear-gradient(135deg,#0f766e,#164e63)] p-10 text-white xl:block">
          <div className="absolute inset-0 bg-[radial-gradient(circle_at_top_left,rgba(255,255,255,0.16),transparent_28%),radial-gradient(circle_at_bottom_right,rgba(192,138,34,0.35),transparent_25%)]" />
          <div className="relative">
            <p className="text-xs uppercase tracking-[0.45em] text-white/70">Inventario profesional</p>
            <h1 className="mt-5 max-w-md font-[var(--font-serif)] text-5xl leading-tight">
              Control total para una ferreteria que no puede perder ritmo.
            </h1>
            <p className="mt-6 max-w-xl text-lg text-white/84">
              Registra productos, compras, ventas, movimientos y reportes desde un dashboard limpio y moderno.
            </p>

            <div className="mt-10 grid gap-4">
              {[
                "Stock auditado con movimientos de entrada, salida y ajuste",
                "Reportes esenciales para reabastecimiento, ventas y valorizacion",
                "Usuarios con roles de administrador y operador"
              ].map((item) => (
                <div key={item} className="rounded-2xl border border-white/15 bg-white/10 px-5 py-4 text-sm text-white/88">
                  {item}
                </div>
              ))}
            </div>
          </div>
        </section>

        <section className="p-6 sm:p-10">
          <div className="mx-auto max-w-md">
            <div className="rounded-3xl border border-slate-200 bg-[var(--surface-strong)] p-8 shadow-[0_18px_50px_rgba(15,23,42,0.08)]">
              <p className="text-sm font-semibold uppercase tracking-[0.35em] text-teal-700">Acceso</p>
              <h2 className="mt-4 text-4xl font-bold text-slate-900">Bienvenido</h2>
              <p className="mt-3 text-sm text-slate-600">
                Usa los usuarios de prueba `admin / admin123` o `operador / operador123`.
              </p>

              <form className="mt-8 space-y-4" onSubmit={onSubmit}>
                <label className="block">
                  <span className="mb-2 block text-sm font-medium text-slate-700">Usuario</span>
                  <div className="flex items-center gap-3 rounded-2xl border border-slate-200 bg-white px-4 py-3">
                    <UserRound className="h-4 w-4 text-teal-700" />
                    <input
                      className="w-full bg-transparent outline-none"
                      value={username}
                      onChange={(event) => setUsername(event.target.value)}
                    />
                  </div>
                </label>

                <label className="block">
                  <span className="mb-2 block text-sm font-medium text-slate-700">Contrasena</span>
                  <div className="flex items-center gap-3 rounded-2xl border border-slate-200 bg-white px-4 py-3">
                    <LockKeyhole className="h-4 w-4 text-teal-700" />
                    <input
                      type="password"
                      className="w-full bg-transparent outline-none"
                      value={password}
                      onChange={(event) => setPassword(event.target.value)}
                    />
                  </div>
                </label>

                {error ? <p className="rounded-2xl bg-rose-50 px-4 py-3 text-sm text-rose-700">{error}</p> : null}

                <button
                  type="submit"
                  disabled={loading}
                  className="w-full rounded-2xl bg-[linear-gradient(135deg,#0f766e,#0f5c57)] px-4 py-3 font-semibold text-white transition hover:brightness-110 disabled:opacity-70"
                >
                  {loading ? "Ingresando..." : "Entrar al dashboard"}
                </button>
              </form>
            </div>
          </div>
        </section>
      </div>
    </main>
  );
}
