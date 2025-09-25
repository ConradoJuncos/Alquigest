import auth from "@/utils/functions/auth-functions/auth";

export const fetchWithToken = async (url: string, options: RequestInit = {}) => {
  const token = auth.getToken();
  if (!token) throw new Error("No hay token disponible");

  const res = await fetch(url, {
    ...options,
    headers: {
      ...options.headers,
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
    },
  });

  if (!res.ok) {
    if (res.status === 401 || res.status === 403) {
      auth.logout(); // opcional: logout automático si el token expiró
      throw new Error("No autorizado, inicia sesión de nuevo");
    }
  }

  return res.json();
};
