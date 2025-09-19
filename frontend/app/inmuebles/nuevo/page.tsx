"use client"

import type React from "react"

import { useEffect, useState } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Textarea } from "@/components/ui/textarea"
import { Building2, ArrowLeft, Save } from "lucide-react"
import Link from "next/link"
import HeaderAlquigest from "@/components/header"
import { Propietario } from "@/types/Propietario"
import BACKEND_URL from "@/utils/backendURL"
import { PRERENDER_MANIFEST } from "next/dist/shared/lib/constants"
import NuevoPropietarioModal from "@/app/propietarios/nuevoPropietarioModal"

export default function NuevoInmueblePage() {

  // PARA DATOS PROPIETARIOS
  const [propietariosBD, setPropietariosBD] = useState<Propietario[]>([]);
  const [isNewOwnerOpen, setIsNewOwnerOpen] = useState(true)
  useEffect(() => {
    console.log("Ejecutando fetch de propietarios...");

    fetch(`${BACKEND_URL}/propietarios/activos`)
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
  const [formData, setFormData] = useState({
    propietarioId: "",
    direccion: "",
    tipoInmuebleId: "",
    estado: "",
    superficie: "",
    esActivo: "true",
    esAlquilado: "false",
  })

  // PARA CARGAR EL NUEVO INMUEBLE

  const handleNewInmueble = async () => {
    try {
      // Hacemos POST al backend
      const response = await fetch(`${BACKEND_URL}/inmuebles`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(formData),
      });

      if (!response.ok) {
        throw new Error(`Error HTTP: ${response.status}`);
      }

      // Recibimos el propietario creado desde el backend (con ID generado)
      const createdOwner = await response.json();

      // Actualizamos el estado local
      setPropietariosBD((prev) => [...prev, createdOwner]);

      // Limpiamos el formulario y cerramos el modal
      setFormData({
        propietarioId: "",
        direccion: "",
        tipoInmuebleId: "",
        estado: "",
        superficie: "",
        esActivo: "true",
        esAlquilado: "false",
      });
      setIsNewOwnerOpen(false);

    } catch (error) {
      console.error("Error al crear propietario:", error);
    }
  };

  


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
      <HeaderAlquigest tituloPagina="Inmuebles"/>

      <main className="container mx-auto px-6 py-8 pt-30">
        {/* Page Title */}
        <div className="mb-8 flex flex-col gap-3">
            <Link href="/inmuebles">
              <Button variant="outline">
                <ArrowLeft className="h-4 w-4 mr-2" />
                Volver
              </Button>
            </Link>
          <div>
            <h2 className="text-3xl font-bold text-foreground mb-2">Registrar Nuevo Inmueble</h2>
          </div>
        </div>
       

        {/* Form */}
        <Card className="max-w-4xl mx-auto">
          <CardHeader>
            <CardTitle className="font-sans">Complete los datos del inmueble a registrar</CardTitle>
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
                  <Label htmlFor="tipoInmueble">Tipo de Inmueble *</Label>
                  <Select 
                    required
                    value={formData.tipoInmuebleId} 
                    onValueChange={(value) => handleInputChange("tipoInmuebleId", value)}>
                    <SelectTrigger>
                      <SelectValue placeholder="Seleccionar tipo" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="1">Departamento</SelectItem>
                      <SelectItem value="2">Casa</SelectItem>
                      <SelectItem value="3">Local Comercial</SelectItem>
                      <SelectItem value="4">Oficina</SelectItem>
                      <SelectItem value="5">Depósito</SelectItem>
                      <SelectItem value="6">Terreno</SelectItem>
                    </SelectContent>
                  </Select>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="estado">Estado*</Label>
                  <Select
                    required 
                    value={formData.estado} 
                    defaultValue="1" 
                    onValueChange={(value) => handleInputChange("estado", value)}>
                    <SelectTrigger>
                      <SelectValue placeholder="Seleccionar estado" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="1">Disponible</SelectItem>
                      <SelectItem value="2">No Disponible</SelectItem>
                      <SelectItem value="3">En reparacion</SelectItem>
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
                  <Label htmlFor="propietario">Propietario *</Label>
                  <div className="flex flex-1 min-w-0 gap-2 ">
                    <Select
                      required
                      value={formData.propietarioId}
                      onValueChange={(value) => handleInputChange("propietarioId", value)}
                    >
                      <SelectTrigger className="w-55">
                        <SelectValue className="overflow-hidden text-ellipsis" placeholder="Seleccionar propietario" />
                      </SelectTrigger>
                      <SelectContent className="max-w-full overflow-scroll">
                        {propietariosBD.map((propietario) => (
                          <SelectItem
                            key={propietario.id}
                            value={propietario.id.toString()}
                            className="overflow-auto text-ellipsis"
                          >
                            {propietario.nombre} {propietario.apellido} | DNI: {propietario.dni}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>

                    {/* BOTON PARA ABRIR MODAL NUEVO PROPIETARIO */}
                    <NuevoPropietarioModal 
                      text="Nuevo" 
                      onPropietarioCreado={(nuevo) => setPropietariosBD(prev => [...prev, nuevo])}
                    /> 
                  </div>
                </div>

              </div>

              <div className="space-y-2">
                  <Label htmlFor="esActivo">¿Esta activo?</Label>
                  <Select value={formData.esActivo} onValueChange={(value) => handleInputChange("esActivo", value)}>
                    <SelectTrigger>
                      <SelectValue placeholder="Seleccionar tipo" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="true">Activo</SelectItem>
                      <SelectItem value="false">Inactivo</SelectItem>
                    </SelectContent>
                  </Select>
                </div>

              {/* Descripción 
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
              */}

              {/* Botones */}
              <div className="flex gap-4 pt-6">
                <Link href="/inmuebles" className="flex-1">
                  <Button onClick={() => setIsNewOwnerOpen(false)} type="button" variant="outline" className="w-full bg-transparent">
                    Cancelar
                  </Button>
                </Link>
                <Button onClick={handleNewInmueble} type="submit" className="flex-1">
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
