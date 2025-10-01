"use client"
import BACKEND_URL from "@/utils/backendURL";


const auth = {
  login: async (username: string, password: string) => {
    const res = await fetch(`${BACKEND_URL}/auth/signin`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username, password }),
    });

    if (!res.ok) {
      throw new Error("Usuario o contraseña incorrectos");
    }

    const data = await res.json(); 
    // data: { accessToken, username, email, roles, permisos }

    localStorage.setItem("token", data.accessToken); // JWT
    localStorage.setItem("user", JSON.stringify(data)); // info completa usuario

    return { username: data.username }; 
  },

  logout: async () => {
    
    const res = await fetch(`${BACKEND_URL}/auth/signout`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
    });

    if (res.ok){
      console.log("Sesión cerrada en el backend!")
    }

    localStorage.removeItem("token");
  },

  UserEstaLogeado: () => {
    if (typeof window === "undefined") return false;
    const token = localStorage.getItem("token");
    if (!token) return false;

    try {
      const payload = JSON.parse(atob(token.split(".")[1]));
      return payload.exp * 1000 > Date.now();
    } catch {
      return false;
    }
  },

  getUser: () => {
    if (typeof window === "undefined") return null;
    const user = localStorage.getItem("user");
    return user ? JSON.parse(user) : null;
  },

  getUserRoles: (): string[] => {
    const user = auth.getUser();
    return user?.roles || [];
  },

  getUserPermisos: (): Record<string, boolean> => {
    const user = auth.getUser();
    return user?.permisos || {};
  },

  hasRol: (rol: string) => {
    return auth.getUserRoles().includes(rol);
  },

  hasPermiso: (permiso: string) => {
    const permisos = auth.getUserPermisos();
    return permisos[permiso] === true;
  },

  getToken: () => {
    if (typeof window === "undefined") return null;
    return localStorage.getItem("token");
  },
};



export default auth;
