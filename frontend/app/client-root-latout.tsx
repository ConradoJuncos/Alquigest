"use client";
import { useState, useEffect, createContext, ReactNode } from "react";
import ModalLogin from "@/components/modal-login";
import auth from "@/utils/functions/auth-functions/auth";
import HeaderAlquigest from "@/components/header";
import { usePathname } from "next/navigation";
import { Plus } from "lucide-react";

export const AuthContext = createContext({
  username: "",
  setUsername: (user: string) => {},
});

export default function ClientRootLayout({ children }: { children: ReactNode }) {
  const [username, setUsername] = useState("");
  const [showModal, setShowModal] = useState(false);
  const [isDarkMode, setIsDarkMode] = useState(false); 
  const pathname = usePathname(); // Obtener la ruta actual

  // Mapear las rutas a títulos específicos
  const getTituloPagina = (path: string) => {
    if (path.startsWith("/propietarios")) return "Locadores";
    if (path.startsWith("/inmuebles")) return "Inmuebles";
    if (path.startsWith("/inquilinos")) return "Locatarios";
    if (path.startsWith("/alquileres")) return "Alquileres";
    if (path.startsWith("/contratos")) return "Contratos";
    return "Gestiones"; // Título por defecto
  };

  useEffect(() => {
    if (auth.UserEstaLogeado()) {
      const token = auth.getToken();
      if (token) {
        try {
          const payload = JSON.parse(atob(token.split(".")[1]));
          setUsername(payload.sub);
        } catch {
          auth.logout();
          setShowModal(true);
        }
      }
    } else {
      setShowModal(true);
    }

      // Leer la preferencia de tema del localStorage
    const savedTheme = localStorage.getItem("theme");
    if (savedTheme === "dark") {
      setIsDarkMode(true);
      document.documentElement.classList.add("dark");
    } else {
      setIsDarkMode(false);
      document.documentElement.classList.remove("dark");
    }
  }, []);

  const toggleTheme = () => {
    setIsDarkMode((prev) => {
      const newTheme = !prev;
      if (newTheme) {
        document.documentElement.classList.add("dark");
        localStorage.setItem("theme", "dark");
      } else {
        document.documentElement.classList.remove("dark");
        localStorage.setItem("theme", "light");
      }
      return newTheme;
    });
  };

  return (
    <AuthContext.Provider value={{ username, setUsername }}>
        {/* Header visible en todas las páginas */}
        <div className="">
            <HeaderAlquigest tituloPagina={getTituloPagina(pathname)} username={username} toggleTheme={toggleTheme} // Pasar la función para alternar el tema
          isDarkMode={isDarkMode}/>

          <div className="fixed bg-primary bottom-15 right-10 rounded-full p-4 shadow-lg shadow-foreground/40 hover:scale-120 transform transition">
            <Plus className="w-7 h-7 text-background"/>
          </div>
        </div>
      {children}
      {showModal && (
        <ModalLogin
          onClose={(user) => {
            setUsername(user);
            setShowModal(false);
          }}
        />
      )}
    </AuthContext.Provider>
    
  );
}
