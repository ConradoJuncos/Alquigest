'use client'

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Building2, Search, Filter, Plus, MapPin, DollarSign, Calendar, User, Settings, Group, Ruler } from "lucide-react"
import Link from "next/link"
import HeaderAlquigest from "@/components/header"
import { Inmueble } from "@/types/Inmueble"
import { useEffect, useState } from "react"
import BACKEND_URL from "@/utils/backendURL"
import tiposInmueble from "@/utils/tiposInmuebles"
import { Propietario } from "@/types/Propietario"
import Loading from "@/components/loading"

export default function InmueblesPage() {
  const [inmueblesBD, setInmueblesBD] = useState<Inmueble[]>([]);
  const [loading, setLoading] = useState(true);

  // PARA DATOS PROPIETARIOS
  const [propietariosBD, setPropietariosBD] = useState<Propietario[]>([]);
  const [isNewOwnerOpen, setIsNewOwnerOpen] = useState(true)
  useEffect(() => {
    console.log("Ejecutando fetch de propietarios...");

    fetch(`${BACKEND_URL}/propietarios`)
      .then((res) => {
        console.log("Respuesta recibida del backend:", res);
        if (!res.ok) {
          throw new Error(`Error HTTP: ${res.status}`);
        }
        return res.json();
      })
      .then((data) => {
        console.log("Datos parseados del backend:", data);
        setPropietariosBD(data);
      })
      .catch((err) => {
        console.error("Error al traer propietarios:", err);
      });
      
  }, []);

  useEffect(() => {
    const fetchInmuebles = async () => {
      try {
        const response = await fetch(`${BACKEND_URL}/inmuebles`);
        if (!response.ok) throw new Error("Error al obtener inmuebles");

        const data: Inmueble[] = await response.json();
        console.log("Cantidad de inmuebles:", data.length);
        console.log("Datos:", data);

        setInmueblesBD(data);
        setLoading(false);
      } catch (error) {
        console.error(error);
      }
    };

    fetchInmuebles();
  }, []);

  if (loading) return(
      <div>
        <Loading text="Cargando Inmuebles" tituloHeader="Inmuebles"/>
      </div>
    )


  return (
    <div className="min-h-screen bg-background">
      <HeaderAlquigest tituloPagina="Inmuebles" />

      <main className="container mx-auto px-6 py-8 pt-30">
        {/* Page Title */}
        <div className="mb-8 flex  justify-between">
          <div>
            <h2 className="text-3xl font-bold text-foreground mb-2">Inmuebles</h2>
            <p className="text-muted-foreground font-serif">Actualmente hay {inmueblesBD.length} inmuebles en el sistema</p>
          </div>
          <Link href="/inmuebles/nuevo">
              <Button>
                <Plus className="h-4 w-4 mr-2" />
                Nuevo Inmueble
              </Button>
            </Link>
        </div>


        {/* Properties Grid */}
        <div className="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-6">
          {inmueblesBD.map((inmueble) => (
            <Card key={inmueble.id} className="hover:shadow-lg transition-shadow">
              <CardHeader>
                <div className="flex justify-between items-start">
                  <div className="flex-1">
                    <CardTitle className="text-lg font-sans mb-2">{inmueble.direccion}</CardTitle>
                    <div className="flex items-center text-sm text-muted-foreground mb-2">
                      <MapPin className="h-4 w-4 mr-1" />
                      Córdoba
                    </div>
                  </div>
                  <div className="flex flex-col items-end space-y-1">
                    <Badge
                      variant={inmueble.esActivo == true ? "default" : "secondary"}
                      className={inmueble.esActivo == true ? "bg-accent" : ""}
                    >
                      {inmueble.esActivo == true ? "Activo" : "Inactivo"}
                    </Badge>

                    <Badge
                      variant={inmueble.esAlquilado === true ? "default" : "secondary"}
                      className={inmueble.esAlquilado === true ? "bg-accent" : ""}
                    >
                      {inmueble.esAlquilado === true ? "Alquilado" : "Disponible"}
                    </Badge>
                  </div>
                </div>
              </CardHeader>

              <CardContent className="space-y-4">
                {/* TIPO */}
                <div className="flex items-center justify-between">
                  <div className="flex items-center">
                    <Building2 className="h-5 w-5 mr-3" />
                  <span className="text-sm text-muted-foreground">Tipo:</span>
                  </div>
                  <div className="flex items-center font-semibold">
                    {tiposInmueble.find(tipo => tipo.id === inmueble.tipoInmuebleId)?.nombre || "Desconocido"}
                  </div>
                </div>

                {/* PropietarioID */}
                <div className="flex items-center justify-between">
                  <div className="flex items-center">
                  <User className="h-5 w-5 mr-3" />
                  <span className="text-sm text-muted-foreground">PropietarioID:</span>
                  </div>
                  <div className="flex items-center">
                    <span className="text-sm font-medium">{inmueble.propietarioId}</span>
                  </div>
                </div>

                {/* Propietario */}
                <div className="flex items-center justify-between">
                  <div className="flex items-center">
                  <User className="h-5 w-5 mr-3" />
                  <span className="text-sm text-muted-foreground">Propietario:</span>
                  </div>
                  <div className="flex items-center">
                    <User className="h-4 w-4 mr-1" />
                    <span className="text-sm font-medium">{
                      propietariosBD.find(prop => prop.id === inmueble.propietarioId)
                        ? `${propietariosBD.find(prop => prop.id === inmueble.propietarioId)?.nombre} ${propietariosBD.find(prop => prop.id === inmueble.propietarioId)?.apellido}`
                        : "Desconocido"
                      }</span>
                  </div>
                </div>

                {/* Superficie */}
                <div className="flex items-center justify-between">
                  <div className="flex items-center">
                  <Ruler className="h-5 w-5 mr-3" />
                  <span className="text-sm text-muted-foreground">Superficie:</span>
                  </div>
                  <div className="flex items-center">
                    <span className="text-sm font-medium">{inmueble.superficie} m²</span>
                  </div>
                </div>


                {/* Actions */}
                <div className="flex gap-2 pt-2">
                  <Button variant="outline" size="sm" className="flex-1 bg-transparent">
                    Ver Detalles
                  </Button>
                  <Button variant="outline" size="sm" className="flex-1 bg-transparent">
                    Editar
                  </Button>
                </div>

                <div className="pt-2 border-t border-border">
                  <Link href={`/inmuebles/${inmueble.id}/servicios`}>
                    <Button
                      variant="outline"
                      size="sm"
                      className="w-full bg-accent/10 hover:bg-accent/20 border-accent/30"
                    >
                      <Settings className="h-4 w-4 mr-2" />
                      Gestionar Servicios
                    </Button>
                  </Link>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>

        {/* Navigation Back */}
        <div className="mt-8">
          <Link href="/">
            <Button variant="outline">← Volver al Panel Principal</Button>
          </Link>
        </div>
      </main>
    </div>
  )
}
