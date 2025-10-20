import NuevoInquilinoModal from "@/app/inquilinos/nuevoInquilinoModal";
import NuevoPropietarioModal from "@/app/propietarios/nuevoPropietarioModal";
import { useMemo, useState } from "react";
import auth from "@/utils/functions/auth-functions/auth";
import Link from "next/link";
import { Button } from "./ui/button";
import { FastForward, FileText, Home, Plus, SquareArrowOutUpRight, UserCircle2, UserPlus } from "lucide-react";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "./ui/dropdown-menu";

export default function QuickActions() {
  const [openInquilino, setOpenInquilino] = useState(false)
  const [openPropietario, setOpenPropietario] = useState(false)
  // permisos
  const perms = useMemo(() => ({
    crearPropietario: auth.tienePermiso("crear_propietario"),
    crearInquilino: auth.tienePermiso("crear_inquilino"),
    crearInmueble: auth.tienePermiso("crear_inmueble"),
    crearUsuario: auth.tienePermiso("crear_usuario_abogada"), //CAMBIAR PROVISORIO!!!!
    crearContrato: auth.tienePermiso("crear_contrato"),
  }), [])
  return (
    <div>
        {/* Dropdown Menu */}
        <DropdownMenu>
          <DropdownMenuTrigger className="fixed bg-primary bottom-15 right-15 rounded-2xl p-4 shadow-lg shadow-foreground/40 hover:scale-110 transform transition cursor-pointer z-999">
            <div className="flex items-center space-x-2 text-background">
              <SquareArrowOutUpRight className="w-6 h-6 " />
              <span>Accesos Directos</span>
            </div>
          </DropdownMenuTrigger>
          <DropdownMenuContent>
            {/* Nuevo Locador */}
            <DropdownMenuItem onSelect={(e) => { e.preventDefault(); if (perms.crearPropietario) setOpenPropietario(true) }}>
              <Button  size="sm" className="w-full" disabled={!perms.crearPropietario}>
                <UserCircle2 className="h-5 w-5 mr-2 text-background" />
                Nuevo Locador
              </Button>
            </DropdownMenuItem>
            {/* Nuevo Locatario */}
            <DropdownMenuItem onSelect={(e) => { e.preventDefault(); if (perms.crearInquilino) setOpenInquilino(true) }}>
              <Button size="sm" className="w-full" disabled={!perms.crearInquilino}>
                <UserCircle2 className="h-5 w-5 mr-2 text-background" />
                Nuevo Locatario
              </Button>
            </DropdownMenuItem>
            {/* Nuevo Inmueble */}
            <DropdownMenuItem>
              {perms.crearInmueble ? (
                <Link href={"/inmuebles/nuevo"}>
                  <Button size="sm" className="w-full">
                    <Home className="h-5 w-5 mr-2 text-background" />
                    Nuevo Inmueble
                  </Button>
                </Link>
              ) : (
                <Button size="sm" className="w-full" disabled>
                  <Home className="h-5 w-5 mr-2 text-background" />
                  Nuevo Inmueble
                </Button>
              )}
            </DropdownMenuItem>
            {/* Nuevo Contrato */}
            <DropdownMenuItem>
              {perms.crearContrato ? (
                <Link href={"/contratos/nuevo"}>
                  <Button size="sm" className="w-full">
                    <FileText className="h-5 w-5 mr-2 text-background" />
                    Nuevo Contrato
                  </Button>
                </Link>
              ) : (
                <Button size="sm" className="w-full" disabled>
                  <FileText className="h-5 w-5 mr-2 text-background" />
                  Nuevo Contrato
                </Button>
              )}
            </DropdownMenuItem>
            {/* Nuevo Usuario */}
            <DropdownMenuItem>
              {perms.crearUsuario ? (
                <Link href={"/auth/signup"}>
                  <Button variant="outline" size="sm">
                    <UserPlus className="h-5 w-5 mr-2" />
                    Nuevo Usuario
                  </Button>
                </Link>
              ) : (
                <Button variant="outline" size="sm" disabled>
                  <UserPlus className="h-5 w-5 mr-2" />
                  Nuevo Usuario
                </Button>
              )}
            </DropdownMenuItem>
            {/* Generar Informe */}
            <DropdownMenuItem>
              <Button variant="outline" size="sm" disabled>
                <FileText className="h-5 w-5 mr-2" />
                Generar Informe
              </Button>
            </DropdownMenuItem>
            
          </DropdownMenuContent>
        </DropdownMenu>

        {/* Modales controlados y fuera del dropdown para evitar unmount */}
        <div>
          <NuevoPropietarioModal open={openPropietario} onOpenChange={setOpenPropietario} showTrigger={false} />
          <NuevoInquilinoModal open={openInquilino} onOpenChange={setOpenInquilino} showTrigger={false} />
        </div>
        </div>
    )

}