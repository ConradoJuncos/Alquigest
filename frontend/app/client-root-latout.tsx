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
import NuevoInmueblePage from "./inmuebles/nuevo/page";
import Link from "next/link";
import { Button } from "@/components/ui/button";

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
        <div className="overflow-y-auto">
            <HeaderAlquigest tituloPagina={getTituloPagina(pathname)} username={username} toggleTheme={toggleTheme} // Pasar la función para alternar el tema
          isDarkMode={isDarkMode}/>
          
                  {/* Dropdown Menu */}
        <DropdownMenu>
          <DropdownMenuTrigger asChild className="fixed bg-primary bottom-15 right-15 rounded-full p-4 shadow-lg shadow-foreground/40 hover:scale-110 transform transition cursor-pointer">
            <div className="">
              <Plus className="w-7 h-7 text-background" />
            </div>
          </DropdownMenuTrigger>
          <DropdownMenuContent>
            <DropdownMenuItem>
              <NuevoPropietarioModal/>
            </DropdownMenuItem>
            <DropdownMenuItem>
              <NuevoInquilinoModal/>
            </DropdownMenuItem>
            <DropdownMenuItem>
              <Link href={"/inmuebles/nuevo"}>
              <Button variant="outline" size="sm">
                <Home className="h-5 w-5 mr-2" />
                Nuevo Inmueble
              </Button>
            </Link>
            </DropdownMenuItem>
            
          </DropdownMenuContent>
        </DropdownMenu>
        {children}
        </div>
      {showModal && (
        <ModalLogin
          isDarkMode={isDarkMode}
          onClose={(user) => {
            setUsername(user);
            setShowModal(false);
          }}
        />
      )}
    </AuthContext.Provider>
    
  );
}
