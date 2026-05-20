import { fetchWithToken } from "@/utils/functions/auth-functions/fetchWithToken";
import BACKEND_URL from "@/utils/backendURL";

export interface SignupBody {
  username: string;
  email: string;
  password: string;
  role?: string[];
}

const jsonPost = async (path: string, body: unknown): Promise<Response> => {
  return fetch(`${BACKEND_URL}${path}`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify(body),
  });
};

export const authService = {
  signup: async (body: SignupBody): Promise<unknown> => {
    const data = await fetchWithToken(`/auth/signup`, {
      method: "POST",
      body: JSON.stringify(body),
    });
    return data;
  },

  recuperarContrasena: async (email: string): Promise<void> => {
    await jsonPost("/auth/recuperar-contrasena", { email });
  },

  resetearContrasena: async (
    token: string,
    nuevaContrasena: string,
    confirmarContrasena: string
  ): Promise<void> => {
    const res = await jsonPost("/auth/resetear-contrasena", {
      token,
      nuevaContrasena,
      confirmarContrasena,
    });
    if (!res.ok) {
      let mensaje = "Error al cambiar la contraseña";
      try {
        const err = await res.json();
        mensaje = err.message || mensaje;
      } catch {
        // ignore parse error
      }
      throw new Error(mensaje);
    }
  },
};
