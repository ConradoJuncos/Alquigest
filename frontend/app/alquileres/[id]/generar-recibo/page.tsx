"use client"

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { ArrowLeft, Receipt, Droplets, Building, Flame, FileText, DollarSign, Zap } from "lucide-react"
import Link from "next/link"
import { useState } from "react"
import { useParams } from "next/navigation"
import HeaderAlquigest from "@/components/header"

export default function GenerarReciboPage() {
  const params = useParams()
  const alquilerId = params.id

  // Datos de ejemplo del alquiler (normalmente vendría de una API)
  const alquiler = {
    id: alquilerId,
    inmueble: "Apartamento Centro - Calle Mayor 123",
    inquilino: "María García López",
    propietario: "Juan Pérez Martín",
    montoMensual: 85000,
    fechaInicio: "2024-01-15",
    fechaVencimiento: "2025-01-14",
  }

  const [servicios, setServicios] = useState({
    agua: 0,
    municipalidad: 0,
    rentas: 0,
    gas: 0,
    electricidad: 0,
  })

  const serviciosConfig = [
    {
      key: "agua",
      nombre: "Agua",
      icon: Droplets,
      color: "text-blue-500",
      bgColor: "bg-blue-50",
    },
    {
      key: "municipalidad",
      nombre: "Municipalidad",
      icon: Building,
      color: "text-gray-600",
      bgColor: "bg-gray-50",
    },
    {
      key: "rentas",
      nombre: "Rentas",
      icon: FileText,
      color: "text-green-600",
      bgColor: "bg-green-50",
    },
    {
      key: "gas",
      nombre: "Gas",
      icon: Flame,
      color: "text-orange-500",
      bgColor: "bg-orange-50",
    },
    {
      key: "electricidad",
      nombre: "Electricidad",
      icon: Zap,
      color: "text-yellow-500",
      bgColor: "bg-yellow-50",
    },
  ]

  const handleServicioChange = (servicio: string, valor: string) => {
    setServicios((prev) => ({
      ...prev,
      [servicio]: Number.parseFloat(valor) || 0,
    }))
  }

  const calcularTotal = () => {
    const totalServicios = Object.values(servicios).reduce((sum, value) => sum + value, 0)
    return alquiler.montoMensual + totalServicios
  }

  const generarRecibo = () => {
    const total = calcularTotal()
    alert(`Recibo generado para ${alquiler.inquilino}\nTotal: $${total.toLocaleString()}`)
    // Aquí se implementaría la lógica real de generación del recibo
  }

  return (
    <div className="min-h-screen bg-background">

      <main className="container mx-auto px-6 py-4 max-w-6xl pt-30">
        <div className="grid grid-cols-1 lg:grid-cols-4 gap-4">
          {/* Información del Alquiler y Servicios */}
          <div className="lg:col-span-3">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {/* Información del Alquiler */}
              <Card className="h-fit">
                <CardHeader className="pb-3">
                  <CardTitle className="flex items-center gap-2 font-sans text-base">
                    <Building className="h-7 w-7 text-[var(--amarillo-alqui)]" />
                    Información del Alquiler
                  </CardTitle>
                </CardHeader>
                <CardContent className="space-y-2 text-sm">
                  <div>
                    <p className="text-xs font-medium text-muted-foreground">Inmueble</p>
                    <p className="font-medium">{alquiler.inmueble}</p>
                  </div>
                  <div>
                    <p className="text-xs font-medium text-muted-foreground">Inquilino</p>
                    <p className="font-medium">{alquiler.inquilino}</p>
                  </div>
                </CardContent>
              </Card>

              {/* Valor del Alquiler */}
              <Card className="h-fit">
                <CardHeader className="pb-3">
                  <CardTitle className="flex items-center gap-2 font-sans text-base">
                    <DollarSign className="h-7 w-7 text-green-600" />
                    Valor del Alquiler
                  </CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="p-3 bg-background rounded-lg border border-green-200">
                    <div className="flex items-center justify-between">
                      <span className="text-sm font-medium">Alquiler Mensual</span>
                      <span className="text-xl font-bold text-green-600">
                        ${alquiler.montoMensual.toLocaleString()}
                      </span>
                    </div>
                    <p className="text-xs text-green-700 mt-1">Calculado automáticamente</p>
                  </div>
                </CardContent>
              </Card>
            </div>

            {/* Servicios a cargo del inquilino */}
            <Card className="mt-4">
              <CardHeader className="pb-3">
                <CardTitle className="flex items-center gap-2 font-sans text-base">
                  <Receipt className="h-7 w-7 text-[var(--amarillo-alqui)]" />
                  Servicios a Cargo
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-3">
                  {serviciosConfig.map((servicio) => {
                    const IconComponent = servicio.icon
                    return (
                      <div key={servicio.key} className={`p-3 rounded-lg border bg-background border-opacity-50`}>
                        <div className="flex items-center justify-between">
                          <div className="flex items-center gap-2">
                            <div className="p-1.5 rounded-full bg-muted">
                              <IconComponent className={`h-5 w-5 ${servicio.color}`} />
                            </div>
                            <Label className="text-sm font-medium">{servicio.nombre}</Label>
                          </div>
                          <div className="flex items-center gap-2">
                            <span className="text-sm font-medium">$</span>
                            <Input
                              type="number"
                              placeholder="0.00"
                              value={servicios[servicio.key as keyof typeof servicios] || ""}
                              onChange={(e) => handleServicioChange(servicio.key, e.target.value)}
                              className="text-sm h-8 w-20"
                              step="0.01"
                              min="0"
                            />
                          </div>
                        </div>
                      </div>
                    )
                  })}
                </div>
              </CardContent>
            </Card>
          </div>

          {/* Resumen y Total */}
          <div className="lg:col-span-1">
            <Card className="sticky top-4">
              <CardHeader className="pb-3">
                <CardTitle className="flex items-center gap-2 font-sans text-base">
                  <Receipt className="h-7 w-7 text-[var(--amarillo-alqui)]" />
                  Resumen
                </CardTitle>
              </CardHeader>
              <CardContent className="space-y-3">
                {/* Alquiler */}
                <div className="flex justify-between items-center py-1">
                  <span className="text-sm font-medium">Alquiler</span>
                  <span className="font-bold text-green-600">${alquiler.montoMensual.toLocaleString()}</span>
                </div>

                <div className="border-t pt-2 space-y-1">
                  <p className="text-xs font-medium text-muted-foreground mb-1">Servicios:</p>
                  {serviciosConfig.map((servicio) => {
                    const valor = servicios[servicio.key as keyof typeof servicios]
                    if (valor > 0) {
                      const IconComponent = servicio.icon
                      return (
                        <div key={servicio.key} className="flex justify-between items-center text-xs">
                          <span className="flex items-center gap-1">
                            <IconComponent className={`h-3 w-3 ${servicio.color}`} />
                            {servicio.nombre}
                          </span>
                          <span className="font-medium">${valor.toLocaleString()}</span>
                        </div>
                      )
                    }
                    return null
                  })}
                </div>

                {/* Total */}
                <div className="border-t pt-3">
                  <div className="flex justify-between items-center">
                    <span className="text-sm font-bold">Total:</span>
                    <span className="text-lg font-bold text-blue-600">${calcularTotal().toLocaleString()}</span>
                  </div>
                </div>

                {/* Botón Generar Recibo */}
                <Button onClick={generarRecibo} className="w-full mt-4" size="sm">
                  <Receipt className="h-4 w-4 mr-2" />
                  Generar Recibo
                </Button>

                {/* Información Adicional */}
                <div className="text-xs text-muted-foreground space-y-1 mt-3 pt-3 border-t">
                  <p>• Servicios a cargo del inquilino</p>
                  <p>• Alquiler calculado automáticamente</p>
                  <p>• Dejar en 0 si no aplica</p>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      </main>
    </div>
  )
}
