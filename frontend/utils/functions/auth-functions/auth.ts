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

    const data = await res.json(); // { token: "...", username: "lucas" }
    localStorage.setItem("token", data.accessToken); // guardamos JWT
    return { username: data.username }; // retornamos username
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
    const token = localStorage.getItem("token");
    if (!token) return false;

    try {
      const payload = JSON.parse(atob(token.split(".")[1])); // decodifica payload
      return payload.exp * 1000 > Date.now();
    } catch {
      return false;
    }
  },

  getToken: () => localStorage.getItem("token"),
};

export default auth;
