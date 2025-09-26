"use client";
import { useState, useEffect, createContext, ReactNode } from "react";
import ModalLogin from "@/components/modal-login";
import auth from "@/utils/functions/auth-functions/auth";
import HeaderAlquigest from "@/components/header";
import { usePathname } from "next/navigation";

export const AuthContext = createContext({
  username: "",
  setUsername: (user: string) => {},
});

export default function ClientRootLayout({ children }: { children: ReactNode }) {
  const [username, setUsername] = useState("");
  const [showModal, setShowModal] = useState(false);
    const pathname = usePathname(); // Obtener la ruta actual

  // Mapear las rutas a títulos específicos
  const getTituloPagina = (path: string) => {
    if (path.startsWith("/propietarios")) return "Propietarios";
    if (path.startsWith("/inmuebles")) return "Inmuebles";
    if (path.startsWith("/inquilinos")) return "Inquilinos";
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
  }, []);

  return (
    <AuthContext.Provider value={{ username, setUsername }}>
        {/* Header visible en todas las páginas */}
        <div className="overflow-y-scroll">
            <HeaderAlquigest tituloPagina={getTituloPagina(pathname)} username={username} />
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
