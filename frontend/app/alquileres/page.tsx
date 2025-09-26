"use client"

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Building2, Calendar, Users, Euro, ArrowLeft, Plus, Search, Filter, Receipt, AlertCircle, User } from "lucide-react"
import Link from "next/link"
import { useState } from "react"
import contratosCompletos from "./contratos-mock"

export default function AlquileresPage() {

  const [selectedAlquiler, setSelectedAlquiler] = useState<any>(null)
  const [servicios, setServicios] = useState({
    agua: 0,
    municipalidad: 0,
    rentas: 0,
    gas: 0,
  })

  const getEstadoBadge = (estado: string) => {
    switch (estado) {
      case "Vigente":
        return <Badge className="bg-green-100 text-green-800 hover:bg-green-100 text-sm font-bold">Vigente</Badge>
      case "Por Renovar":
        return <Badge className="bg-orange-100 text-orange-800 hover:bg-orange-100 text-sm font-bold">Por Renovar</Badge>
      case "Vencido":
        return <Badge className="bg-red-100 text-red-800 hover:bg-red-100 text-sm font-bold">Vencido</Badge>
      default:
        return <Badge className="text-sm font-bold" variant="secondary">{estado}</Badge>
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

      {/* Main Content */}
      <main className="container mx-auto px-6 py-8 pt-30">
        {/* Stats Summary */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-md md:text-md font-medium ">Contratos Activos</CardTitle>
              <Calendar className="h-6 w-6 text-gray-700" />
            </CardHeader>
            <CardContent className="flex flex-col items-center">
              <div className="text-3xl font-bold font-sans text-gray-800">15</div>
              <p className="text-sm text-muted-foreground">Vigentes actualmente</p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-md md:text-md font-medium ">Contratos por vencer</CardTitle>
              <AlertCircle className="h-6 w-6 text-orange-500" />
            </CardHeader>
            <CardContent className="flex flex-col items-center">
              <div className="text-3xl font-bold font-sans text-orange-600">2</div>
              <p className="text-sm text-muted-foreground">Vencen el mes que viene</p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-md md:text-md font-medium ">Alquileres No Pagos</CardTitle>
              <AlertCircle className="h-6 w-6 text-orange-500" />
            </CardHeader>
            <CardContent className="flex flex-col items-center">
              <div className="text-3xl font-bold font-sans text-orange-600">4</div>
              <p className="text-sm text-muted-foreground">No pagaron antes del día 10</p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-md md:text-md font-medium ">Servicios No Pagos</CardTitle>
              <AlertCircle className="h-6 w-6 text-orange-500" />
            </CardHeader>
            <CardContent className="flex flex-col items-center">
              <div className="text-3xl font-bold font-sans text-orange-600">4</div>
              <p className="text-sm text-muted-foreground">Pendientes de pagar</p>
            </CardContent>
          </Card>
        </div>

        {/* Alquileres List */}
        <div className="space-y-6">
          <div className="flex items-center justify-between">
            <h2 className="text-xl font-semibold font-sans">Contratos de Alquiler Activos</h2>
          </div>

          <div className="grid gap-6">
            {contratosCompletos.map((contrato) => (
              <Card key={contrato.id} className="hover:shadow-lg transition-shadow">
                <CardHeader>
                  <div className="flex items-start justify-between">
                    <div className="space-y-2">
                      <CardTitle className="text-xl font-bold flex items-center gap-2">
                        <Building2 className="h-6 w-6 text-yellow-700" />
                        {contrato.inmueble.direccion}
                      </CardTitle>
                      <div className="flex items-center gap-4 text-md">
                        <span className="flex items-center gap-1">
                          <User className="h-5 w-5" />
                          Locador: {contrato.propietario.nombre} {contrato.propietario.apellido}
                        </span>
                      </div>
                    </div>
                    <div className="flex items-center gap-2 ">
                      {getEstadoBadge(contrato.estado.nombre)}
                    </div>
                  </div>
                </CardHeader>
                <CardContent>
                  <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4 text-md">
                    <div>
                      <p className="text-sm font-medium text-muted-foreground">Locatario</p>
                      <p className="font-medium ">{contrato.inquilino.nombre} {contrato.inquilino.apellido}</p>
                    </div>
                    <div>
                      <p className="text-sm font-medium text-muted-foreground">Monto Alquiler</p>
                      <p className="font-bold text-green-600">Proximamente...</p>
                    </div>
                    <div>
                      <p className="text-sm font-medium text-muted-foreground">Próximo Aumento</p>
                      <p className="font-medium">{contrato.fechaAumento}</p>
                    </div>
                  </div>
                  <div className="grid grid-cols-1 gap-4 mb-4 text-md">
                    <div>
                      <span className="text-muted-foreground">Vencimiento: </span>
                      <span className="font-medium">{contrato.fechaFin}</span>
                    </div>
                  </div>

                  <div className="flex items-center justify-between pt-4 border-t">
                    <div className="flex gap-2">
                      <Link href={`/contratos/${contrato.id}`}>
                        <Button variant="outline" size="sm">
                          Ver Contrato
                        </Button>
                      
                      </Link>
                      <Button variant="outline" size="sm">
                        Historial Pagos
                      </Button>
                    </div>
                    <div className="flex gap-2">
                      <Button variant="outline" size="sm">
                        Editar
                      </Button>
                      <Button size="sm">Registrar Pago</Button>
                      <Link href={`/alquileres/${contrato.id}/generar-recibo`}>
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
