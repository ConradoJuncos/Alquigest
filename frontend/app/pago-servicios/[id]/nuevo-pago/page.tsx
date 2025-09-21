"use client"

import { useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Checkbox } from "@/components/ui/checkbox"
import { Building2, ArrowLeft, Zap, Droplets, Flame, Wifi, Home, CreditCard, Save } from "lucide-react"
import Link from "next/link"
import { useParams } from "next/navigation"

// Mock data (en una app real vendría de una API)
const inmuebleData = {
  1: {
    direccion: "Calle Mayor 123, Madrid",
    tipo: "Apartamento",
    propietario: "Juan Pérez",
    inquilino: "María García",
    servicios: [
      {
        id: 1,
        tipo: "luz",
        nombre: "Electricidad",
        responsable: "estudio",
        estado: "pendiente",
        vencimiento: "2024-01-15",
        monto: 0,
      },
      {
        id: 2,
        tipo: "agua",
        nombre: "Agua",
        responsable: "estudio",
        estado: "pagado",
        vencimiento: "2024-01-10",
        monto: 45.5,
      },
      {
        id: 3,
        tipo: "gas",
        nombre: "Gas Natural",
        responsable: "propietario",
        estado: "pendiente",
        vencimiento: "2024-01-20",
        monto: 0,
      },
      {
        id: 4,
        tipo: "internet",
        nombre: "Internet + TV",
        responsable: "inquilino",
        estado: "pagado",
        vencimiento: "2024-01-05",
        monto: 0,
      },
      {
        id: 5,
        tipo: "renta",
        nombre: "Alquiler",
        responsable: "estudio",
        estado: "pendiente",
        vencimiento: "2024-01-31",
        monto: 0,
      },
    ],
  },
  2: {
    direccion: "Avenida Libertad 456, Barcelona",
    tipo: "Casa",
    propietario: "Ana Martínez",
    inquilino: "Carlos López",
    servicios: [
      {
        id: 6,
        tipo: "luz",
        nombre: "Electricidad",
        responsable: "estudio",
        estado: "pagado",
        vencimiento: "2024-01-12",
        monto: 78.3,
      },
      {
        id: 7,
        tipo: "agua",
        nombre: "Agua",
        responsable: "estudio",
        estado: "pendiente",
        vencimiento: "2024-01-18",
        monto: 0,
      },
      {
        id: 8,
        tipo: "gas",
        nombre: "Gas Natural",
        responsable: "estudio",
        estado: "pendiente",
        vencimiento: "2024-01-25",
        monto: 0,
      },
      {
        id: 9,
        tipo: "internet",
        nombre: "Fibra Óptica",
        responsable: "inquilino",
        estado: "pagado",
        vencimiento: "2024-01-08",
        monto: 0,
      },
      {
        id: 10,
        tipo: "renta",
        nombre: "Alquiler",
        responsable: "estudio",
        estado: "pagado",
        vencimiento: "2024-01-31",
        monto: 850.0,
      },
    ],
  },
  3: {
    direccion: "Plaza Central 789, Valencia",
    tipo: "Local Comercial",
    propietario: "Roberto Silva",
    inquilino: "Empresa ABC S.L.",
    servicios: [
      {
        id: 11,
        tipo: "luz",
        nombre: "Electricidad Comercial",
        responsable: "estudio",
        estado: "pendiente",
        vencimiento: "2024-01-20",
        monto: 0,
      },
      {
        id: 12,
        tipo: "agua",
        nombre: "Agua",
        responsable: "propietario",
        estado: "pagado",
        vencimiento: "2024-01-15",
        monto: 0,
      },
      {
        id: 13,
        tipo: "internet",
        nombre: "Internet Empresarial",
        responsable: "inquilino",
        estado: "pendiente",
        vencimiento: "2024-01-10",
        monto: 0,
      },
      {
        id: 14,
        tipo: "renta",
        nombre: "Alquiler Comercial",
        responsable: "estudio",
        estado: "pendiente",
        vencimiento: "2024-01-31",
        monto: 0,
      },
    ],
  },
}

const getServiceIcon = (tipo: string) => {
  switch (tipo) {
    case "luz":
      return <Zap className="h-5 w-5" />
    case "agua":
      return <Droplets className="h-5 w-5" />
    case "gas":
      return <Flame className="h-5 w-5" />
    case "internet":
      return <Wifi className="h-5 w-5" />
    case "renta":
      return <Home className="h-5 w-5" />
    default:
      return <CreditCard className="h-5 w-5" />
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

export default function NuevoPagoPage() {
  const params = useParams()
  const inmuebleId = params.id as string
  const inmueble = inmuebleData[inmuebleId as keyof typeof inmuebleData]

  const [pagos, setPagos] = useState<{ [key: number]: { pagado: boolean; monto: string; fechaVencimiento: string } }>(
    {},
  )

  if (!inmueble) {
    return <div>Inmueble no encontrado</div>
  }

  const handlePagoChange = (servicioId: number, field: string, value: string | boolean) => {
    setPagos((prev) => ({
      ...prev,
      [servicioId]: {
        ...prev[servicioId],
        [field]: value,
      },
    }))
  }

  const handleGuardar = () => {
    console.log("[v0] Guardando pagos:", pagos)
    // Aquí se enviarían los datos a la API
    alert("Pagos registrados correctamente")
  }

  return (
    <div className="min-h-screen bg-background">
      {/* Header */}
      <header className="border-b border-border bg-card">
        <div className="container mx-auto px-6 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-3">
              <Link href="/pago-servicios">
                <Button variant="ghost" size="sm">
                  <ArrowLeft className="h-4 w-4 mr-2" />
                  Volver a Pago de Servicios
                </Button>
              </Link>
              <div className="flex items-center space-x-3">
                <CreditCard className="h-8 w-8 text-green-600" />
                <div>
                  <h1 className="text-2xl font-bold text-foreground font-sans">Nuevo Pago de Servicios</h1>
                  <p className="text-sm text-muted-foreground font-serif">{inmueble.direccion}</p>
                </div>
              </div>
            </div>
            <Button onClick={handleGuardar} className="bg-green-600 hover:bg-green-700">
              <Save className="h-4 w-4 mr-2" />
              Guardar Pagos
            </Button>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="container mx-auto px-6 py-8">
        {/* Información del Inmueble */}
        <Card className="mb-8">
          <CardHeader>
            <div className="flex items-center space-x-4">
              <Building2 className="h-6 w-6 text-primary" />
              <div>
                <CardTitle className="font-sans">{inmueble.direccion}</CardTitle>
                <CardDescription className="font-serif">
                  {inmueble.tipo} • Propietario: {inmueble.propietario} • Inquilino: {inmueble.inquilino}
                </CardDescription>
              </div>
            </div>
          </CardHeader>
        </Card>

        {/* Lista de Servicios */}
        <div className="space-y-6">
          <h2 className="text-xl font-bold text-foreground font-sans">Servicios del Inmueble</h2>

          {inmueble.servicios.map((servicio) => (
            <Card
              key={servicio.id}
              className={`transition-all duration-200 ${
                servicio.responsable === "estudio" ? "border-blue-200 bg-blue-50/30" : ""
              }`}
            >
              <CardHeader>
                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-4">
                    {getServiceIcon(servicio.tipo)}
                    <div>
                      <CardTitle className="text-lg font-sans">{servicio.nombre}</CardTitle>
                      <CardDescription className="font-serif">Vencimiento: {servicio.vencimiento}</CardDescription>
                    </div>
                  </div>
                  <div className="flex items-center space-x-3">
                    {getResponsableBadge(servicio.responsable)}
                    {servicio.estado === "pagado" && (
                      <Badge variant="default" className="bg-green-100 text-green-800">
                        Ya Pagado {servicio.monto > 0 && `($${servicio.monto.toLocaleString()})`}
                      </Badge>
                    )}
                  </div>
                </div>
              </CardHeader>

              {servicio.responsable === "estudio" && servicio.estado === "pendiente" && (
                <CardContent>
                  <div className="space-y-4 p-4 bg-white rounded-lg border">
                    <div className="flex items-center space-x-2">
                      <Checkbox
                        id={`pago-${servicio.id}`}
                        checked={pagos[servicio.id]?.pagado || false}
                        onCheckedChange={(checked) => handlePagoChange(servicio.id, "pagado", checked as boolean)}
                      />
                      <Label htmlFor={`pago-${servicio.id}`} className="font-medium">
                        Marcar como pagado
                      </Label>
                    </div>

                    {pagos[servicio.id]?.pagado && (
                      <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-4">
                        <div className="space-y-2">
                          <Label htmlFor={`monto-${servicio.id}`}>Monto Pagado ($)</Label>
                          <Input
                            id={`monto-${servicio.id}`}
                            type="number"
                            step="0.01"
                            placeholder="0.00"
                            value={pagos[servicio.id]?.monto || ""}
                            onChange={(e) => handlePagoChange(servicio.id, "monto", e.target.value)}
                          />
                        </div>
                        <div className="space-y-2">
                          <Label htmlFor={`fecha-${servicio.id}`}>Próximo Vencimiento</Label>
                          <Input
                            id={`fecha-${servicio.id}`}
                            type="date"
                            value={pagos[servicio.id]?.fechaVencimiento || ""}
                            onChange={(e) => handlePagoChange(servicio.id, "fechaVencimiento", e.target.value)}
                          />
                        </div>
                      </div>
                    )}
                  </div>
                </CardContent>
              )}

              {servicio.responsable !== "estudio" && (
                <CardContent>
                  <div className="p-4 bg-gray-50 rounded-lg">
                    <p className="text-sm text-muted-foreground">
                      Este servicio es gestionado por el {servicio.responsable}. No requiere acción del estudio
                      jurídico.
                    </p>
                  </div>
                </CardContent>
              )}
            </Card>
          ))}
        </div>

        {/* Resumen */}
        <Card className="mt-8">
          <CardHeader>
            <CardTitle className="font-sans">Resumen de Pagos</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div className="text-center p-4 bg-blue-50 rounded-lg">
                <p className="text-2xl font-bold text-blue-600 font-sans">
                  {Object.values(pagos).filter((p) => p.pagado).length}
                </p>
                <p className="text-sm text-muted-foreground">Servicios a marcar como pagados</p>
              </div>
              <div className="text-center p-4 bg-green-50 rounded-lg">
                <p className="text-2xl font-bold text-green-600 font-sans">
                  $
                  {Object.values(pagos)
                    .reduce((total, pago) => total + (pago.pagado ? Number.parseFloat(pago.monto || "0") : 0), 0)
                    .toLocaleString()}
                </p>
                <p className="text-sm text-muted-foreground">Total a registrar</p>
              </div>
              <div className="text-center p-4 bg-orange-50 rounded-lg">
                <p className="text-2xl font-bold text-orange-600 font-sans">
                  {inmueble.servicios.filter((s) => s.responsable === "estudio" && s.estado === "pendiente").length -
                    Object.values(pagos).filter((p) => p.pagado).length}
                </p>
                <p className="text-sm text-muted-foreground">Servicios pendientes</p>
              </div>
            </div>
          </CardContent>
        </Card>
      </main>
    </div>
  )
}
