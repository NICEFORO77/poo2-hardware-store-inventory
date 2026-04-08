import { getSession } from "@/lib/auth";

const API_URL = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080/api";

async function parseResponse(response: Response) {
  if (response.status === 204) return null;
  const contentType = response.headers.get("content-type") ?? "";
  if (contentType.includes("application/json")) {
    return response.json();
  }
  return response.text();
}

export async function apiRequest<T>(
  path: string,
  options: RequestInit = {},
  isFormData = false
): Promise<T> {
  const session = getSession();
  const headers = new Headers(options.headers);

  if (!isFormData) {
    headers.set("Content-Type", "application/json");
  }

  if (session?.token) {
    headers.set("Authorization", `Bearer ${session.token}`);
  }

  const response = await fetch(`${API_URL}${path}`, {
    ...options,
    headers,
    cache: "no-store"
  });

  const data = await parseResponse(response);
  if (!response.ok) {
    const error =
      typeof data === "object" && data && "error" in data
        ? String((data as { error: string }).error)
        : "No se pudo completar la solicitud";
    throw new Error(error);
  }

  return data as T;
}

export async function fetchPdfBlob(path: string) {
  const session = getSession();

  const response = await fetch(`${API_URL}${path}`, {
    method: "GET",
    headers: {
      Authorization: session?.token ? `Bearer ${session.token}` : ""
    },
    cache: "no-store"
  });

  if (!response.ok) {
    const message = await parseResponse(response);
    throw new Error(typeof message === "string" ? message : "No se pudo generar el PDF");
  }

  const blob = await response.blob();
  return URL.createObjectURL(blob);
}

export const money = new Intl.NumberFormat("es-PE", {
  style: "currency",
  currency: "PEN"
});
