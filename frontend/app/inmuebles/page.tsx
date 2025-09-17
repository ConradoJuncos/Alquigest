'use client'

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Building2, Search, Filter, Plus, MapPin, DollarSign, Calendar, User, Settings } from "lucide-react"
import Link from "next/link"
import HeaderAlquigest from "@/components/header"
import { Inmueble } from "@/types/Inmueble"
import { useEffect, useState } from "react"
import BACKEND_URL from "@/utils/backendURL"

export default function InmueblesPage() {
  const [inmueblesBD, setInmueblesBD] = useState<Inmueble[]>([]);
  const [loading, setLoading] = useState(true);

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

  const inmuebles = [
    {
      id: 1,
      direccion: "Calle Mayor 123, Madrid",
      tipo: "Apartamento",
      superficie: "85 m²",
      habitaciones: 3,
      precio: "120,000",
      estado: "Ocupado",
      propietario: "Juan García",
      inquilino: "María López",
      fechaContrato: "2023-06-15",
    },
    {
      id: 2,
      direccion: "Avenida Libertad 45, Barcelona",
      tipo: "Local Comercial",
      superficie: "120 m²",
      habitaciones: null,
      precio: "250,000",
      estado: "Disponible",
      propietario: "Ana Martín",
      inquilino: null,
      fechaContrato: null,
    },
    {
      id: 3,
      direccion: "Plaza España 8, Valencia",
      tipo: "Oficina",
      superficie: "200 m²",
      habitaciones: 6,
      precio: "180,000",
      estado: "Ocupado",
      propietario: "Carlos Ruiz",
      inquilino: "Empresa Tech SL",
      fechaContrato: "2023-03-01",
    },
  ]

  if (loading) return <p>Cargando inmuebles...</p>;


  return (
    <div className="min-h-screen bg-background">
      <HeaderAlquigest tituloPagina="Inmuebles" />

      <main className="container mx-auto px-6 py-8 pt-30">
        {/* Page Title */}
        <div className="mb-8 flex  justify-between">
          <div>
            <h2 className="text-3xl font-bold text-foreground mb-2">Inmuebles</h2>
            <p className="text-muted-foreground font-serif"></p>
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
                      {inmueble.tipo} • {inmueble.superficie} m²
                    </div>
                  </div>
                  <div className="flex flex-col items-end space-y-1">
                    <Badge
                      variant={inmueble.estado == 0 ? "default" : "secondary"}
                      className={inmueble.estado == 0 ? "bg-accent" : ""}
                    >
                      {inmueble.estado == 0 ? "Activo" : "Inactivo"}
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
                {/* Precio */}
                <div className="flex items-center justify-between">
                  <span className="text-sm text-muted-foreground">Tipo:</span>
                  <div className="flex items-center font-semibold">
                    {inmueble.tipoInmuebleId == 1 ? "Residencial" : "Comercial"}
                  </div>
                </div>

                {/* Propietario */}
                <div className="flex items-center justify-between">
                  <span className="text-sm text-muted-foreground">Propietario:</span>
                  <div className="flex items-center">
                    <User className="h-4 w-4 mr-1" />
                    <span className="text-sm font-medium">{inmueble.propietario}</span>
                  </div>
                </div>

                {/* Inquilino */}
                <div className="flex items-center justify-between">
                  <span className="text-sm text-muted-foreground">Inquilino:</span>
                  <div className="flex items-center">
                    <User className="h-4 w-4 mr-1" />
                    <span className="text-sm font-medium">{inmueble.inquilino || "Sin inquilino"}</span>
                  </div>
                </div>

                {/* Fecha de contrato */}
                {inmueble.fechaContrato && (
                  <div className="flex items-center justify-between">
                    <span className="text-sm text-muted-foreground">Contrato desde:</span>
                    <div className="flex items-center">
                      <Calendar className="h-4 w-4 mr-1" />
                      <span className="text-sm font-medium">
                        {new Date(inmueble.fechaContrato).toLocaleDateString("es-ES")}
                      </span>
                    </div>
                  </div>
                )}

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
