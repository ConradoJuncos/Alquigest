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

  let data: any = null;
  const text = await res.text(); // leer la respuesta como texto
  if (text) {
    try {
      data = JSON.parse(text);
    } catch {
      data = text; // si no es JSON, devolver texto plano
    }
  }

  if (!res.ok) {
    if (res.status === 401 || res.status === 403) {
      auth.logout();
      throw new Error("No autorizado, inicia sesión de nuevo");
    }
    throw new Error((data && data.message) || "Error al procesar la solicitud");
  }

  return data;
};

