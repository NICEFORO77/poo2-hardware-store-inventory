export type Session = {
  token: string;
  username: string;
  nombreCompleto: string;
  rol: string;
};

const SESSION_KEY = "inventario-ferreteria-session";

export function saveSession(session: Session) {
  if (typeof window === "undefined") return;
  localStorage.setItem(SESSION_KEY, JSON.stringify(session));
}

export function getSession(): Session | null {
  if (typeof window === "undefined") return null;
  const value = localStorage.getItem(SESSION_KEY);
  if (!value) return null;

  try {
    return JSON.parse(value) as Session;
  } catch {
    return null;
  }
}

export function clearSession() {
  if (typeof window === "undefined") return;
  localStorage.removeItem(SESSION_KEY);
}
