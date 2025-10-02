"use client"

import { Moon, Sun, UserCircle2Icon, LogOut } from "lucide-react";
import Link from "next/link";
import {
  DropdownMenu,
  DropdownMenuTrigger,
  DropdownMenuContent,
  DropdownMenuItem,
} from "@/components/ui/dropdown-menu";
import auth from "@/utils/functions/auth-functions/auth";
import { useEffect, useState } from "react";

export default function PildoraUsuario({
  username = "",
  isDarkMode,
  toggleTheme,
}: { username: string; isDarkMode: boolean; toggleTheme: () => void }) {
  const handleLogout = () => {
    auth.logout(); // Llama al método logout
    window.location.href = "/"; // Redirige al usuario a la página de login
  };

  const [mounted, setMounted] = useState(false);
  useEffect(() => setMounted(true), []);

  const isRoleAdmin = auth.hasRol("ROLE_ADMINISTRADOR")
  
  var gradientVar = "bg-muted text-primary"
  if(isRoleAdmin){
    gradientVar = "bg-gradient-to-r from-yellow-400 via-orange-500 to-blue-500 text-black animate-gradient-diagonal"
  }
  

  return (
    <div>
      <div className="flex items-center gap-2 ">
        {/* Dropdown Menu para el usuario */}
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <div className={`hidden md:flex flex-col p-1 rounded-4xl ${gradientVar} hover:bg-accent hover:text-muted transition cursor-pointer`}>
              <div className="flex items-center space-x-2">
                <UserCircle2Icon className="h-8 w-8 rounded-full" />
                <p className="font-bold pr-2">{username || "Cargando..."}</p>
                {mounted && isRoleAdmin && (
                  <p>| Modo Administrador</p>
                )}
              </div>
            </div>
          </DropdownMenuTrigger>
          <DropdownMenuContent>
            <DropdownMenuItem onClick={handleLogout}>
              <LogOut className="h-4 w-4 mr-2" />
              Cerrar sesión
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>

        {/* Botón para alternar el tema */}
        <div>
          <button
            onClick={toggleTheme}
            className="p-2 rounded-full bg-muted hover:bg-muted-foreground transition"
          >
            {!isDarkMode ? (
              <Sun className="h-5 w-5 text-yellow-500" />
            ) : (
              <Moon className="h-5 w-5 text-yellow-500" />
            )}
          </button>
        </div>
      </div>
    </div>
  );
}