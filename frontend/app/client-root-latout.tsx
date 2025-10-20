"use client"
import { useState, useEffect, createContext, ReactNode } from "react";
import ModalLogin from "@/components/modal-login";
import auth from "@/utils/functions/auth-functions/auth";
import HeaderAlquigest from "@/components/header";
import { usePathname } from "next/navigation";
import { Home, Plus } from "lucide-react";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu";
import NuevoPropietarioModal from "./propietarios/nuevoPropietarioModal";
import NuevoInquilinoModal from "./inquilinos/nuevoInquilinoModal";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import Footer from "@/components/footer";
import QuickActions from "@/components/quick-actions";
import ModalNotificacionesInicio from "@/components/notifications/modal-notificaciones-inicio";

export const AuthContext = createContext({
  username: "",
  setUsername: (user: string) => {},
});

export default function ClientRootLayout({ children }: { children: ReactNode }) {
  const [username, setUsername] = useState("");
  const [showModal, setShowModal] = useState(false);
  const [showNotificaciones, setShowNotificaciones] = useState(false);
  const [isDarkMode, setIsDarkMode] = useState(false);
  const [notificationDot, setNotificationDot] = useState(false); // para el punto de notificación
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
      <div>
        <HeaderAlquigest
          tituloPagina={getTituloPagina(pathname)}
          username={username}
          toggleTheme={toggleTheme}
          isDarkMode={isDarkMode}
          onBellClick={() => setShowNotificaciones(true)}
          showNotificationDot={notificationDot}
        />
        <QuickActions />
        {children}
        <Footer />
      </div>
      {showModal && (
        <ModalLogin
          isDarkMode={isDarkMode}
          onClose={(user, justLoggedIn) => {
            setUsername(user);
            setShowModal(false);
            if (justLoggedIn) {
              setShowNotificaciones(true);
            }
          }}
        />
      )}
      {showNotificaciones && (
        <ModalNotificacionesInicio
          isOpen={showNotificaciones}
          onClose={() => setShowNotificaciones(false)}
          setNotificationDot={setNotificationDot}
        />
      )}
    </AuthContext.Provider>
  );
}
