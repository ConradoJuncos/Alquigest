"use client"

import { useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import {
  Building2,
  ChevronDown,
  ChevronUp,
  CreditCard,
  ArrowLeft,
  Zap,
  Droplets,
  Flame,
  Wifi,
  Home,
} from "lucide-react"
import Link from "next/link"
import HeaderAlquigest from "@/components/header"

// Mock data para inmuebles a cargo del estudio jurídico
const inmuebles = [
  {
    id: 1,
    direccion: "Calle Mayor 123, Madrid",
    tipo: "Apartamento",
    propietario: "Juan Pérez",
    inquilino: "María García",
    servicios: [
      { tipo: "luz", nombre: "Electricidad", responsable: "estudio", estado: "pendiente", vencimiento: "2024-01-15" },
      { tipo: "agua", nombre: "Agua", responsable: "estudio", estado: "pagado", vencimiento: "2024-01-10" },
      {
        tipo: "gas",
        nombre: "Gas Natural",
        responsable: "propietario",
        estado: "pendiente",
        vencimiento: "2024-01-20",
      },
      {
        tipo: "internet",
        nombre: "Internet + TV",
        responsable: "inquilino",
        estado: "pagado",
        vencimiento: "2024-01-05",
      },
      { tipo: "renta", nombre: "Alquiler", responsable: "estudio", estado: "pendiente", vencimiento: "2024-01-31" },
    ],
  },
  {
    id: 2,
    direccion: "Avenida Libertad 456, Barcelona",
    tipo: "Casa",
    propietario: "Ana Martínez",
    inquilino: "Carlos López",
    servicios: [
      { tipo: "luz", nombre: "Electricidad", responsable: "estudio", estado: "pagado", vencimiento: "2024-01-12" },
      { tipo: "agua", nombre: "Agua", responsable: "estudio", estado: "pendiente", vencimiento: "2024-01-18" },
      { tipo: "gas", nombre: "Gas Natural", responsable: "estudio", estado: "pendiente", vencimiento: "2024-01-25" },
      {
        tipo: "internet",
        nombre: "Fibra Óptica",
        responsable: "inquilino",
        estado: "pagado",
        vencimiento: "2024-01-08",
      },
      { tipo: "renta", nombre: "Alquiler", responsable: "estudio", estado: "pagado", vencimiento: "2024-01-31" },
    ],
  },
  {
    id: 3,
    direccion: "Plaza Central 789, Valencia",
    tipo: "Local Comercial",
    propietario: "Roberto Silva",
    inquilino: "Empresa ABC S.L.",
    servicios: [
      {
        tipo: "luz",
        nombre: "Electricidad Comercial",
        responsable: "estudio",
        estado: "pendiente",
        vencimiento: "2024-01-20",
      },
      { tipo: "agua", nombre: "Agua", responsable: "propietario", estado: "pagado", vencimiento: "2024-01-15" },
      {
        tipo: "internet",
        nombre: "Internet Empresarial",
        responsable: "inquilino",
        estado: "pendiente",
        vencimiento: "2024-01-10",
      },
      {
        tipo: "renta",
        nombre: "Alquiler Comercial",
        responsable: "estudio",
        estado: "pendiente",
        vencimiento: "2024-01-31",
      },
    ],
  },
]

const getServiceIcon = (tipo: string) => {
  switch (tipo) {
    case "luz":
      return <Zap className="h-4 w-4" />
    case "agua":
      return <Droplets className="h-4 w-4" />
    case "gas":
      return <Flame className="h-4 w-4" />
    case "internet":
      return <Wifi className="h-4 w-4" />
    case "renta":
      return <Home className="h-4 w-4" />
    default:
      return <CreditCard className="h-4 w-4" />
  }
}

const getResponsableBadge = (responsable: string) => {
  switch (responsable) {
    case "estudio":
      return (
        <Badge variant="default" className="bg-blue-100 text-blue-800">
          Estudio Jurídico
        </Badge>
      )
    case "propietario":
      return (
        <Badge variant="secondary" className="bg-orange-100 text-orange-800">
          Propietario
        </Badge>
      )
    case "inquilino":
      return (
        <Badge variant="outline" className="bg-green-100 text-green-800">
          Inquilino
        </Badge>
      )
    default:
      return <Badge variant="outline">No definido</Badge>
  }
}

const getEstadoBadge = (estado: string) => {
  switch (estado) {
    case "pagado":
      return (
        <Badge variant="default" className="bg-green-100 text-green-800">
          Pagado
        </Badge>
      )
    case "pendiente":
      return (
        <Badge variant="destructive" className="bg-red-100 text-red-800">
          Pendiente
        </Badge>
      )
    default:
      return <Badge variant="outline">No definido</Badge>
  }
}

export default function PagoServiciosPage() {
  const [expandedCards, setExpandedCards] = useState<number[]>([])

  const toggleCard = (inmuebleId: number) => {
    setExpandedCards((prev) =>
      prev.includes(inmuebleId) ? prev.filter((id) => id !== inmuebleId) : [...prev, inmuebleId],
    )
  }

  const serviciosEstudio = (servicios: any[]) => servicios.filter((servicio) => servicio.responsable === "estudio")

  return (
    <div className="min-h-screen bg-background">
      {/* Header */}
      <HeaderAlquigest tituloPagina="Pago de Servicios" />

      {/* Main Content */}
      <main className="container mx-auto px-6 py-8 pt-30">
        {/* Stats */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium font-serif">Inmuebles Gestionados</CardTitle>
              <Building2 className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold font-sans">{inmuebles.length}</div>
              <p className="text-xs text-muted-foreground">Propiedades bajo gestión</p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium font-serif">Servicios Pendientes</CardTitle>
              <CreditCard className="h-4 w-4 text-red-500" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold font-sans text-red-600">
                {inmuebles.reduce(
                  (total, inmueble) =>
                    total + serviciosEstudio(inmueble.servicios).filter((s) => s.estado === "pendiente").length,
                  0,
                )}
              </div>
              <p className="text-xs text-muted-foreground">Pagos por realizar</p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium font-serif">Servicios Pagados</CardTitle>
              <CreditCard className="h-4 w-4 text-green-500" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold font-sans text-green-600">
                {inmuebles.reduce(
                  (total, inmueble) =>
                    total + serviciosEstudio(inmueble.servicios).filter((s) => s.estado === "pagado").length,
                  0,
                )}
              </div>
              <p className="text-xs text-muted-foreground">Pagos realizados</p>
            </CardContent>
          </Card>
        </div>

        {/* Inmuebles List */}
        <div className="space-y-6">
          <h2 className="text-xl font-bold text-foreground font-sans">Inmuebles con Servicios a Gestionar</h2>

          {inmuebles.map((inmueble) => {
            const serviciosAGestionar = serviciosEstudio(inmueble.servicios)
            const isExpanded = expandedCards.includes(inmueble.id)

            return (
              <Card key={inmueble.id} className="transition-all duration-200">
                <CardHeader>
                  <div className="flex items-center justify-between">
                    <div className="flex items-center space-x-4">
                      <Building2 className="h-6 w-6 text-primary" />
                      <div>
                        <CardTitle className="font-sans">{inmueble.direccion}</CardTitle>
                        <CardDescription className="font-serif">
                          {inmueble.tipo} • Propietario: {inmueble.propietario} • Inquilino: {inmueble.inquilino}
                        </CardDescription>
                      </div>
                    </div>
                    <div className="flex items-center space-x-3">
                      <Badge variant="outline">{serviciosAGestionar.length} servicios a gestionar</Badge>
                      <Button variant="ghost" size="sm" onClick={() => toggleCard(inmueble.id)}>
                        {isExpanded ? <ChevronUp className="h-4 w-4" /> : <ChevronDown className="h-4 w-4" />}
                      </Button>
                    </div>
                  </div>
                </CardHeader>

                {isExpanded && (
                  <CardContent>
                    <div className="space-y-4">
                      <h4 className="font-semibold font-sans">Servicios gestionados por el estudio:</h4>
                      <div className="grid gap-3">
                        {serviciosAGestionar.map((servicio, index) => (
                          <div key={index} className="flex items-center justify-between p-3 bg-muted/50 rounded-lg">
                            <div className="flex items-center space-x-3">
                              {getServiceIcon(servicio.tipo)}
                              <div>
                                <p className="font-medium font-sans">{servicio.nombre}</p>
                                <p className="text-sm text-muted-foreground">Vence: {servicio.vencimiento}</p>
                              </div>
                            </div>
                            <div className="flex items-center space-x-3">
                              {getResponsableBadge(servicio.responsable)}
                              {getEstadoBadge(servicio.estado)}
                            </div>
                          </div>
                        ))}
                      </div>

                      <div className="pt-4 border-t">
                        <Link href={`/pago-servicios/${inmueble.id}/nuevo-pago`}>
                          <Button className="w-full">
                            <CreditCard className="h-4 w-4 mr-2" />
                            Registrar Nuevo Pago
                          </Button>
                        </Link>
                      </div>
                    </div>
                  </CardContent>
                )}
              </Card>
            )
          })}
        </div>
      </main>
    </div>
  )
}
