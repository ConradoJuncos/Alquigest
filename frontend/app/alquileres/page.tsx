"use client"

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Building2, Calendar, Users, Euro, ArrowLeft, Plus, Search, Filter, Receipt } from "lucide-react"
import Link from "next/link"
import { useState } from "react"

export default function AlquileresPage() {
  // Datos de ejemplo para alquileres
  const alquileres = [
    {
      id: 1,
      inmueble: "Apartamento Centro - Calle Mayor 123",
      inquilino: "María García López",
      propietario: "Juan Pérez Martín",
      montoMensual: 85000,
      fechaInicio: "2024-01-15",
      fechaVencimiento: "2025-01-14",
      estado: "Activo",
      proximoPago: "2024-12-15",
      diasVencimiento: 15,
    },
    {
      id: 2,
      inmueble: "Casa Residencial - Av. Libertad 456",
      inquilino: "Carlos Rodríguez Silva",
      propietario: "Ana Martínez González",
      montoMensual: 120000,
      fechaInicio: "2023-06-01",
      fechaVencimiento: "2024-05-31",
      estado: "Por Renovar",
      proximoPago: "2024-12-01",
      diasVencimiento: 1,
    },
    {
      id: 3,
      inmueble: "Oficina Comercial - Plaza España 789",
      inquilino: "Empresa Tech Solutions SL",
      propietario: "Pedro López Fernández",
      montoMensual: 250000,
      fechaInicio: "2024-03-01",
      fechaVencimiento: "2026-02-28",
      estado: "Activo",
      proximoPago: "2024-12-01",
      diasVencimiento: 1,
    },
    {
      id: 4,
      inmueble: "Apartamento Zona Norte - Calle Sol 321",
      inquilino: "Laura Sánchez Ruiz",
      propietario: "Miguel Torres Castro",
      montoMensual: 75000,
      fechaInicio: "2024-09-01",
      fechaVencimiento: "2025-08-31",
      estado: "Activo",
      proximoPago: "2024-12-01",
      diasVencimiento: 1,
    },
  ]

  const [selectedAlquiler, setSelectedAlquiler] = useState<any>(null)
  const [servicios, setServicios] = useState({
    agua: 0,
    municipalidad: 0,
    rentas: 0,
    gas: 0,
  })

  const getEstadoBadge = (estado: string) => {
    switch (estado) {
      case "Activo":
        return <Badge className="bg-green-100 text-green-800 hover:bg-green-100">Activo</Badge>
      case "Por Renovar":
        return <Badge className="bg-orange-100 text-orange-800 hover:bg-orange-100">Por Renovar</Badge>
      case "Vencido":
        return <Badge className="bg-red-100 text-red-800 hover:bg-red-100">Vencido</Badge>
      default:
        return <Badge variant="secondary">{estado}</Badge>
    }
  }

  const getPagoBadge = (dias: number) => {
    if (dias <= 3) {
      return <Badge className="bg-red-100 text-red-800 hover:bg-red-100">Urgente</Badge>
    } else if (dias <= 7) {
      return <Badge className="bg-orange-100 text-orange-800 hover:bg-orange-100">Próximo</Badge>
    } else {
      return <Badge className="bg-green-100 text-green-800 hover:bg-green-100">Al día</Badge>
    }
  }

  const calcularTotal = () => {
    const totalServicios = Object.values(servicios).reduce((sum, value) => sum + value, 0)
    return selectedAlquiler ? selectedAlquiler.montoMensual + totalServicios : 0
  }

  const handleServicioChange = (servicio: string, valor: string) => {
    setServicios((prev) => ({
      ...prev,
      [servicio]: Number.parseFloat(valor) || 0,
    }))
  }

  const generarRecibo = () => {
    const total = calcularTotal()
    alert(`Recibo generado para ${selectedAlquiler.inquilino}\nTotal: $${total.toLocaleString()}`)
    // Aquí se implementaría la lógica real de generación del recibo
  }

  return (
    <div className="min-h-screen bg-background">
      {/* Header */}
      <header className="border-b border-border bg-card">
        <div className="container mx-auto px-6 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-4">
              <Link href="/">
                <Button variant="ghost" size="sm">
                  <ArrowLeft className="h-4 w-4 mr-2" />
                  Volver al Panel
                </Button>
              </Link>
              <div className="flex items-center space-x-3">
                <Calendar className="h-8 w-8 text-blue-600" />
                <div>
                  <h1 className="text-2xl font-bold text-foreground font-sans">Alquileres</h1>
                  <p className="text-sm text-muted-foreground font-serif">Gestión de contratos de arrendamiento</p>
                </div>
              </div>
            </div>
            <div className="flex items-center space-x-3">
              <Button variant="outline" size="sm">
                <Search className="h-4 w-4 mr-2" />
                Buscar
              </Button>
              <Button variant="outline" size="sm">
                <Filter className="h-4 w-4 mr-2" />
                Filtrar
              </Button>
              <Button size="sm">
                <Plus className="h-4 w-4 mr-2" />
                Nuevo Contrato
              </Button>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="container mx-auto px-6 py-8">
        {/* Stats Summary */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium font-serif">Contratos Activos</CardTitle>
              <Calendar className="h-4 w-4 text-green-500" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold font-sans text-green-600">3</div>
              <p className="text-xs text-muted-foreground">Vigentes actualmente</p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium font-serif">Por Renovar</CardTitle>
              <Calendar className="h-4 w-4 text-orange-500" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold font-sans text-orange-600">1</div>
              <p className="text-xs text-muted-foreground">Próximos a vencer</p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium font-serif">Ingresos Mensuales</CardTitle>
              <Euro className="h-4 w-4 text-blue-500" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold font-sans text-blue-600">$530,000</div>
              <p className="text-xs text-muted-foreground">Total mensual</p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium font-serif">Pagos Pendientes</CardTitle>
              <Euro className="h-4 w-4 text-red-500" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold font-sans text-red-600">3</div>
              <p className="text-xs text-muted-foreground">Próximos vencimientos</p>
            </CardContent>
          </Card>
        </div>

        {/* Alquileres List */}
        <div className="space-y-6">
          <div className="flex items-center justify-between">
            <h2 className="text-xl font-semibold font-sans">Contratos de Alquiler</h2>
            <p className="text-sm text-muted-foreground">{alquileres.length} contratos registrados</p>
          </div>

          <div className="grid gap-6">
            {alquileres.map((alquiler) => (
              <Card key={alquiler.id} className="hover:shadow-lg transition-shadow">
                <CardHeader>
                  <div className="flex flex-row items-center justify-between space-y-0 pb-2">
                    <CardTitle className="text-sm font-medium font-serif">{alquiler.estado}</CardTitle>
                    <Calendar className="h-4 w-4 text-green-500" />
                  </div>
                  <div className="flex items-start justify-between">
                    <div className="space-y-2">
                      <CardTitle className="text-lg font-sans flex items-center gap-2">
                        <Building2 className="h-5 w-5 text-blue-600" />
                        {alquiler.inmueble}
                      </CardTitle>
                      <div className="flex items-center gap-4 text-sm text-muted-foreground">
                        <span className="flex items-center gap-1">
                          <Users className="h-4 w-4" />
                          Locatario: {alquiler.inquilino}
                        </span>
                      </div>
                    </div>
                    <div className="flex items-center gap-2">
                      {getEstadoBadge(alquiler.estado)}
                      {getPagoBadge(alquiler.diasVencimiento)}
                    </div>
                  </div>
                </CardHeader>
                <CardContent>
                  <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
                    <div>
                      <p className="text-sm font-medium text-muted-foreground">Locador</p>
                      <p className="font-medium">{alquiler.propietario}</p>
                    </div>
                    <div>
                      <p className="text-sm font-medium text-muted-foreground">Monto Mensual</p>
                      <p className="font-bold text-lg text-green-600">${alquiler.montoMensual.toLocaleString()}</p>
                    </div>
                    <div>
                      <p className="text-sm font-medium text-muted-foreground">Próximo Pago</p>
                      <p className="font-medium">{alquiler.proximoPago}</p>
                    </div>
                  </div>

                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4 text-sm">
                    <div>
                      <span className="text-muted-foreground">Inicio: </span>
                      <span className="font-medium">{alquiler.fechaInicio}</span>
                    </div>
                    <div>
                      <span className="text-muted-foreground">Vencimiento: </span>
                      <span className="font-medium">{alquiler.fechaVencimiento}</span>
                    </div>
                  </div>

                  <div className="flex items-center justify-between pt-4 border-t">
                    <div className="flex gap-2">
                      <Button variant="outline" size="sm">
                        Ver Contrato
                      </Button>
                      <Button variant="outline" size="sm">
                        Historial Pagos
                      </Button>
                    </div>
                    <div className="flex gap-2">
                      <Button variant="outline" size="sm">
                        Editar
                      </Button>
                      <Button size="sm">Registrar Pago</Button>
                      <Link href={`/alquileres/${alquiler.id}/generar-recibo`}>
                        <Button variant="outline" size="sm">
                          <Receipt className="h-4 w-4 mr-2" />
                          Generar Recibo
                        </Button>
                      </Link>
                    </div>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </main>
    </div>
  )
}
