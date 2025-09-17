"use client"

import type React from "react"

import { useState } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Textarea } from "@/components/ui/textarea"
import { Building2, ArrowLeft, Save } from "lucide-react"
import Link from "next/link"

export default function NuevoInmueblePage() {
  const [formData, setFormData] = useState({
    direccion: "",
    tipo: "",
    superficie: "",
    habitaciones: "",
    precio: "",
    descripcion: "",
    propietario: "",
  })

  // Lista de propietarios disponibles (en una app real vendría de la base de datos)
  const propietarios = [
    { id: 1, nombre: "Juan García" },
    { id: 2, nombre: "Ana Martín" },
    { id: 3, nombre: "Carlos Ruiz" },
    { id: 4, nombre: "María González" },
    { id: 5, nombre: "Pedro López" },
  ]

  const handleInputChange = (field: string, value: string) => {
    setFormData((prev) => ({
      ...prev,
      [field]: value,
    }))
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    // Aquí iría la lógica para guardar el inmueble
    console.log("Datos del inmueble:", formData)
    // Redirigir a la página de inmuebles después de guardar
  }

  return (
    <div className="min-h-screen bg-background">
      {/* Header */}
      <header className="border-b border-border bg-card">
        <div className="container mx-auto px-6 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-3">
              <Link href="/" className="flex items-center space-x-3">
                <Building2 className="h-8 w-8 text-primary" />
                <div>
                  <h1 className="text-2xl font-bold text-foreground font-sans">AlquiGest</h1>
                  <p className="text-sm text-muted-foreground font-serif">Nuevo Inmueble</p>
                </div>
              </Link>
            </div>
            <Link href="/inmuebles">
              <Button variant="outline">
                <ArrowLeft className="h-4 w-4 mr-2" />
                Volver
              </Button>
            </Link>
          </div>
        </div>
      </header>

      <main className="container mx-auto px-6 py-8">
        {/* Page Title */}
        <div className="mb-8">
          <h2 className="text-3xl font-bold text-foreground mb-2 font-sans">Registrar Nuevo Inmueble</h2>
          <p className="text-muted-foreground font-serif">Complete los datos del inmueble a registrar</p>
        </div>

        {/* Form */}
        <Card className="max-w-4xl mx-auto">
          <CardHeader>
            <CardTitle className="font-sans">Información del Inmueble</CardTitle>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSubmit} className="space-y-6">
              {/* Información básica */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="space-y-2">
                  <Label htmlFor="direccion">Dirección *</Label>
                  <Input
                    id="direccion"
                    placeholder="Ej: Calle Mayor 123, Madrid"
                    value={formData.direccion}
                    onChange={(e) => handleInputChange("direccion", e.target.value)}
                    required
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="tipo">Tipo de Inmueble *</Label>
                  <Select value={formData.tipo} onValueChange={(value) => handleInputChange("tipo", value)}>
                    <SelectTrigger>
                      <SelectValue placeholder="Seleccionar tipo" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="apartamento">Apartamento</SelectItem>
                      <SelectItem value="casa">Casa</SelectItem>
                      <SelectItem value="local-comercial">Local Comercial</SelectItem>
                      <SelectItem value="oficina">Oficina</SelectItem>
                      <SelectItem value="deposito">Depósito</SelectItem>
                      <SelectItem value="terreno">Terreno</SelectItem>
                    </SelectContent>
                  </Select>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="superficie">Superficie (m²) *</Label>
                  <Input
                    id="superficie"
                    type="number"
                    placeholder="Ej: 85"
                    value={formData.superficie}
                    onChange={(e) => handleInputChange("superficie", e.target.value)}
                    required
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="habitaciones">Habitaciones</Label>
                  <Input
                    id="habitaciones"
                    type="number"
                    placeholder="Ej: 3"
                    value={formData.habitaciones}
                    onChange={(e) => handleInputChange("habitaciones", e.target.value)}
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="precio">Precio Mensual ($) *</Label>
                  <Input
                    id="precio"
                    type="number"
                    placeholder="Ej: 120000"
                    value={formData.precio}
                    onChange={(e) => handleInputChange("precio", e.target.value)}
                    required
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="propietario">Propietario *</Label>
                  <Select
                    value={formData.propietario}
                    onValueChange={(value) => handleInputChange("propietario", value)}
                  >
                    <SelectTrigger>
                      <SelectValue placeholder="Seleccionar propietario" />
                    </SelectTrigger>
                    <SelectContent>
                      {propietarios.map((propietario) => (
                        <SelectItem key={propietario.id} value={propietario.nombre}>
                          {propietario.nombre}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>
              </div>

              {/* Descripción */}
              <div className="space-y-2">
                <Label htmlFor="descripcion">Descripción</Label>
                <Textarea
                  id="descripcion"
                  placeholder="Descripción adicional del inmueble..."
                  value={formData.descripcion}
                  onChange={(e) => handleInputChange("descripcion", e.target.value)}
                  rows={4}
                />
              </div>

              {/* Botones */}
              <div className="flex gap-4 pt-6">
                <Link href="/inmuebles" className="flex-1">
                  <Button type="button" variant="outline" className="w-full bg-transparent">
                    Cancelar
                  </Button>
                </Link>
                <Button type="submit" className="flex-1">
                  <Save className="h-4 w-4 mr-2" />
                  Registrar Inmueble
                </Button>
              </div>
            </form>
          </CardContent>
        </Card>
      </main>
    </div>
  )
}
