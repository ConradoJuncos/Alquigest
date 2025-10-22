import auth from "@/utils/functions/auth-functions/auth";

export const fetchWithToken = async (url: string, options: RequestInit = {}) => {
  const token = auth.getToken();
  if (!token) throw new Error("No hay token disponible");

  // Construir headers dinámicamente: no forzar Content-Type si el body es FormData
  const headers: Record<string, string> = {
    ...(options.headers as Record<string, string>),
    Authorization: `Bearer ${token}`,
  };

  // Solo establecer application/json cuando NO se esté enviando FormData y el caller no definió Content-Type
  const isFormData =
    typeof FormData !== "undefined" && options.body instanceof FormData;
  const hasContentTypeHeader = Object.keys(headers).some(
    (k) => k.toLowerCase() === "content-type"
  );
  if (!isFormData && !hasContentTypeHeader) {
    headers["Content-Type"] = "application/json";
  }

  const res = await fetch(url, {
    ...options,
    headers,
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

