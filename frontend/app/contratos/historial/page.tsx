"use client"
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import BACKEND_URL from "@/utils/backendURL";
import { fetchWithToken } from "@/utils/functions/auth-functions/fetchWithToken";
import { ArrowLeft, Building2, ChevronDown, User } from "lucide-react";
import Link from "next/link";
import { useEffect, useState } from "react";

import {
  DropdownMenu,
  DropdownMenuTrigger,
  DropdownMenuContent,
  DropdownMenuItem,
} from "@/components/ui/dropdown-menu";

export default function HistorialContratosPage() {
  const [contratosBD, setContatosBD] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  // 👇 ahora usamos un string en vez de boolean
  const [filtroContrato, setFiltroContrato] = useState<"vigentes" | "no-vigentes" | "proximos-vencer">("no-vigentes");

  useEffect(() => {
    const fetchContratos = async () => {
      const url = `${BACKEND_URL}/contratos/${filtroContrato}`;

      console.log("Ejecutando fetch de Contratos...");
      try {
        const data = await fetchWithToken(url);
        console.log("Datos parseados del backend:", data);
        setContatosBD(data);
      } catch (err: any) {
        console.error("Error al traer contratos:", err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchContratos();
  }, [filtroContrato]);

  return (
    <div className="min-h-screen bg-background">
      <main className="container mx-auto px-6 py-8 pt-30">
        <div className="mb-8 flex justify-between gap-3">
          <div>
            <Button variant="outline" onClick={() => window.history.back()}>
              <ArrowLeft className="h-4 w-4 mr-2" /> Volver
            </Button>
          </div>
            <div className="flex items-center gap-2">
                <p className="text-secondary">Filtro:</p>
                {/* 🔽 Dropdown para elegir filtro */}
                <DropdownMenu>
                    <DropdownMenuTrigger >
                        
                    <Button variant="outline" className="transition-all">
                        {filtroContrato === "vigentes" && "Vigentes"}
                        {filtroContrato === "no-vigentes" && "No Vigentes"}
                        {filtroContrato === "proximos-vencer" && "Próximos a Vencer"}
                        <ChevronDown/>
                    </Button>
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
        </div>

        <div className="space-y-6">
          <div className="flex items-center justify-between">
            <h2 className="text-xl font-semibold font-sans">Historial de Contratos</h2>
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
              <CardHeader className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-[2fr_3fr_auto] gap-4 items-center">
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
                  <p className="bg-secondary p-1 rounded-xl text-sm">
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
