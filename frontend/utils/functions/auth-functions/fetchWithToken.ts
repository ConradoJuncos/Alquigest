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

  const contentType = res.headers.get("content-type") || "";

  // Manejo de errores primero con parse según content-type
  if (!res.ok) {
    let message = "Error al procesar la solicitud";
    if (res.status === 401 || res.status === 403) {
      auth.logout();
      message = "No autorizado, inicia sesión de nuevo";
    } else if (contentType.includes("application/json")) {
      try {
        const errJson = await res.json();
        message = errJson.message || errJson.error || message;
      } catch {
        // ignore, use default message
      }
    } else {
      try {
        const errText = await res.text();
        if (errText) message = errText;
      } catch {
        // ignore
      }
    }
    throw new Error(message);
  }

  // Respuesta OK: devolver según tipo
  if (contentType.includes("application/json")) {
    return await res.json();
  }
  if (
    contentType.includes("application/pdf") ||
    contentType.includes("application/octet-stream")
  ) {
    return await res.blob();
  }
  // 204 o sin cuerpo
  if (res.status === 204) {
    return null;
  }
  // Fallback: texto
  return await res.text();
};

