"use client"
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import BACKEND_URL from "@/utils/backendURL";
import { fetchWithToken } from "@/utils/functions/auth-functions/fetchWithToken";
import { ArrowLeft, ArrowUpDown, Building2, ChevronDown, FileClock, User } from "lucide-react";
import Link from "next/link";
import { useEffect, useState } from "react";

import {
  DropdownMenu,
  DropdownMenuTrigger,
  DropdownMenuContent,
  DropdownMenuItem,
} from "@/components/ui/dropdown-menu";
import { ContratoDetallado } from "@/types/ContratoDetallado";

export default function HistorialContratosPage() {
  const [contratosBD, setContatosBD] = useState<ContratoDetallado[]>([]);
  const [loading, setLoading] = useState(true);

  // 👇 ahora usamos un string en vez de boolean
  // Filtro por defecto ahora 'vigentes' para mostrar resultados iniciales
  const [filtroContrato, setFiltroContrato] = useState<"vigentes" | "no-vigentes" | "proximos-vencer">("vigentes");
  
  // Estados para ordenamiento
  const [ordenarPor, setOrdenarPor] = useState<"fechaInicio" | "fechaFin" | "nombrePropietario">("fechaInicio");
  const [ordenAscendente, setOrdenAscendente] = useState(true);

  // Función para ordenar contratos
  const ordenarContratos = (contratos: ContratoDetallado[]) => {
    return [...contratos].sort((a, b) => {
      let valorA: string;
      let valorB: string;
      
      switch (ordenarPor) {
        case "fechaInicio":
          valorA = a.fechaInicio || "";
          valorB = b.fechaInicio || "";
          break;
        case "fechaFin":
          valorA = a.fechaFin || "";
          valorB = b.fechaFin || "";
          break;
        case "nombrePropietario":
          valorA = `${a.apellidoPropietario} ${a.nombrePropietario}`.toLowerCase();
          valorB = `${b.apellidoPropietario} ${b.nombrePropietario}`.toLowerCase();
          break;
        default:
          return 0;
      }
      
      const resultado = valorA.localeCompare(valorB);
      return ordenAscendente ? resultado : -resultado;
    });
  };

  useEffect(() => {
    const fetchContratos = async () => {
      const url = `${BACKEND_URL}/contratos/${filtroContrato}`;

      console.log("Ejecutando fetch de Contratos...");
      try {
        const data = await fetchWithToken(url);
        console.log("Datos parseados del backend:", data);
        const datosOrdenados = ordenarContratos(data);
        setContatosBD(datosOrdenados);
      } catch (err: any) {
        console.error("Error al traer contratos:", err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchContratos();
  // Incluye filtroContrato para refetch al cambiar filtro y mantener orden por defecto
  }, [ordenarPor, ordenAscendente, filtroContrato]);

  const getTextoOrdenamiento = () => {
    const tipo = {
      "fechaInicio": "Fecha Inicio",
      "fechaFin": "Fecha Fin", 
      "nombrePropietario": "Nombre Propietario"
    }[ordenarPor];
    
    const direccion = ordenAscendente ? "(A-Z)" : "(Z-A)";
    return `${tipo} ${direccion}`;
  };

  // Reordenar cuando cambien los criterios de ordenamiento
  useEffect(() => {
    if (contratosBD.length > 0) {
      const datosOrdenados = ordenarContratos(contratosBD);
      setContatosBD(datosOrdenados);
    }
  }, [ordenarPor, ordenAscendente]);

  return (
    <div className="min-h-screen bg-background">
      <main className="container mx-auto px-6 py-8 pt-30">
        <div className="mb-8 flex justify-between gap-3">
          <div>
            <Button variant="outline" onClick={() => window.history.back()}>
              <ArrowLeft className="h-4 w-4 mr-2" /> Volver
            </Button>
          </div>

        </div>

        <div className="space-y-6">
          <div className="flex justify-between items-center">
            <div className="flex items-center gap-2">
              <FileClock className="h-7 w-7"/>
              <h2 className="text-2xl font-bold">Historial de Contratos</h2>
            </div>
            <div className="flex items-center gap-4">
              {/* Filtro */}
              <div className="flex items-center gap-2">
                <p className="text-secondary">Filtro:</p>
                <DropdownMenu>
                    <DropdownMenuTrigger >
                        <div className="flex">
                          {filtroContrato === "vigentes" && "Vigentes"}
                          {filtroContrato === "no-vigentes" && "No Vigentes"}
                          {filtroContrato === "proximos-vencer" && "Próximos a Vencer"}
                          <ChevronDown/>
                        </div>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent>
                    <DropdownMenuItem onClick={() => setFiltroContrato("vigentes")}>
                        Vigentes
                    </DropdownMenuItem>
                    <DropdownMenuItem onClick={() => setFiltroContrato("no-vigentes")}>
                        No Vigentes
                    </DropdownMenuItem>
                    <DropdownMenuItem onClick={() => setFiltroContrato("proximos-vencer")}>
                        Próximos a vencer
                    </DropdownMenuItem>
                    </DropdownMenuContent>
                </DropdownMenu>
              </div>
              
              {/* Ordenamiento */}
              <div className="flex items-center gap-2">
                <p className="text-secondary">Ordenar:</p>
                <DropdownMenu>
                    <DropdownMenuTrigger>
                        <div className="flex items-center gap-1">
                          <ArrowUpDown className="h-4 w-4" />
                          {getTextoOrdenamiento()}
                          <ChevronDown/>
                        </div>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent>
                      <DropdownMenuItem onClick={() => { setOrdenarPor("fechaInicio"); setOrdenAscendente(true); }}>
                          Fecha Inicio (A-Z)
                      </DropdownMenuItem>
                      <DropdownMenuItem onClick={() => { setOrdenarPor("fechaInicio"); setOrdenAscendente(false); }}>
                          Fecha Inicio (Z-A)
                      </DropdownMenuItem>
                      <DropdownMenuItem onClick={() => { setOrdenarPor("fechaFin"); setOrdenAscendente(true); }}>
                          Fecha Fin (A-Z)
                      </DropdownMenuItem>
                      <DropdownMenuItem onClick={() => { setOrdenarPor("fechaFin"); setOrdenAscendente(false); }}>
                          Fecha Fin (Z-A)
                      </DropdownMenuItem>
                      <DropdownMenuItem onClick={() => { setOrdenarPor("nombrePropietario"); setOrdenAscendente(true); }}>
                          Nombre Propietario (A-Z)
                      </DropdownMenuItem>
                      <DropdownMenuItem onClick={() => { setOrdenarPor("nombrePropietario"); setOrdenAscendente(false); }}>
                          Nombre Propietario (Z-A)
                      </DropdownMenuItem>
                    </DropdownMenuContent>
                </DropdownMenu>
              </div>
            </div>
          </div>
          <div>
            {(contratosBD.length === 0) && (
              <p className="text-lg text-secondary">
                No hay contratos {filtroContrato} para mostrar
              </p>
            )}
          </div>
        </div>

        <div className="grid gap-6">
          {contratosBD?.map((contrato) => (
            <Card
              key={contrato.id}
              className="hover:shadow-lg transition-shadow cursor-pointer"
            >
              {/* Header */}
              <CardHeader className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-[2fr_3fr_auto] items-center">
                {/* Dirección */}
                <div className="flex items-center gap-2">
                  <Building2 className="h-6 w-6 text-yellow-700" />
                  <CardTitle className="text-lg md:text-xl font-bold">
                    <Link href={`/inmuebles/${contrato.inmuebleId}`} className="hover:text-primary">
                      {contrato.direccionInmueble}
                    </Link>
                  </CardTitle>
                </div>

                {/* Locador / Locatario */}
                <div className="flex flex-col gap-5 md:flex-row">
                  <div className="flex items-center gap-1 text-sm">
                    <User className="h-5"/>
                    <p className="font-medium text-muted-foreground">Locador:</p>
                    <p className="font-medium">
                      {contrato.apellidoPropietario}, {contrato.nombrePropietario}
                    </p>
                  </div>
                  <div className="flex items-center gap-1 text-sm">
                    <User className="h-5"/>
                    <p className="font-medium text-muted-foreground">Locatario:</p>
                    <p className="font-medium">
                      {contrato.apellidoInquilino}, {contrato.nombreInquilino}
                    </p>
                  </div>
                </div>

                {/* Estado */}
                <div className="flex items-center justify-end sm:justify-end md:justify-end">
                  <p className="bg-secondary p-1 rounded-lg text-sm">
                    {contrato.estadoContratoNombre || "No disponible"}
                  </p>
                </div>
              </CardHeader>

              <Separator />

              <CardContent className="transition-max-height duration-300 overflow-hidden">
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4 text-md">
                  <div>
                    <p className="text-md font-medium text-muted-foreground">Inicio del Contrato:</p>
                    <p className="font-bold">{contrato.fechaInicio || "No especificado"}</p>
                  </div>
                  <div>
                    <p className="text-md font-medium text-muted-foreground">Finalización:</p>
                    <p className="font-bold">{contrato.fechaFin}</p>
                  </div>
                </div>

                <div className="grid grid-cols-1 items-center justify-between pt-4 border-t gap-2 md:flex md:justify-between">
                  <div className="flex gap-2">
                    <Link href={`/contratos/${contrato.id}`}>
                      <Button variant="outline" size="sm">Ver Contrato</Button>
                    </Link>
                    <Button variant="outline" size="sm">Historial Pagos</Button>
                  </div>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      </main>
    </div>
  );
}
