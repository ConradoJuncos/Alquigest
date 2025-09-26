"use client"

import type React from "react"

import { useEffect, useState } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { ArrowLeft, Save } from "lucide-react"
import Link from "next/link"
import HeaderAlquigest from "@/components/header"
import { Propietario } from "@/types/Propietario"
import BACKEND_URL from "@/utils/backendURL"
import NuevoPropietarioModal from "@/app/propietarios/nuevoPropietarioModal"
import ModalError from "@/components/modal-error"
import ModalDefault from "@/components/modal-default"
import { fetchWithToken } from "@/utils/functions/auth-functions/fetchWithToken"

export default function NuevoInmueblePage() {

   const [inmuebleCargado, setInmuebleCargado] = useState(false)
  const [errorCarga, setErrorCarga] = useState("")
  const [mostrarError, setMostrarError] = useState(false)

  // PARA DATOS PROPIETARIOS
  const [propietariosBD, setPropietariosBD] = useState<Propietario[]>([]);
  const [isNewOwnerOpen, setIsNewOwnerOpen] = useState(true)

  useEffect(() => {
    console.log("Ejecutando fetch de propietarios...");

    fetchWithToken(`${BACKEND_URL}/propietarios/activos`)
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
    superficie: "",
    esActivo: "true",
    esAlquilado: "false",
  });

  const handleNewInmueble = async () => {
  try {
    // Hacemos POST al backend
    const createdInmueble = await fetchWithToken(`${BACKEND_URL}/inmuebles`, {
      method: "POST",
      body: JSON.stringify(formData),
    });

    // Si llegamos aquí, significa que la respuesta fue exitosa (fetchWithToken lanza un error si no lo es)
    console.log("Inmueble creado con éxito:", createdInmueble);

    // Actualizamos el estado local
    setInmuebleCargado(true);

    // Limpiamos el formulario
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
  } catch (error: any) {
    console.error("Error al crear Inmueble:", error);
    setErrorCarga(error.message || "No se pudo conectar con el servidor");
    setMostrarError(true);
  }
};

  const handleInputChange = (field: string, value: string) => {
    setFormData((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    // Validar que todos los campos requeridos estén completos
    if (
      !formData.direccion ||
      !formData.tipoInmuebleId ||
      !formData.estado ||
      !formData.propietarioId
    ) {
      setErrorCarga("Por favor, complete todos los campos obligatorios.");
      setMostrarError(true);
      return;
    }

    // Si pasa la validación, enviar los datos
    console.log("Datos del inmueble:", formData);
    handleNewInmueble();
  };
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
                  <Label htmlFor="superficie">Superficie (m²)</Label>
                  <Input
                    id="superficie"
                    type="number"
                    placeholder="Ej: 85"
                    value={formData.superficie}
                    onChange={(e) => handleInputChange("superficie", e.target.value)}
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

              {/* Botones */}
              <div className="flex gap-4 pt-6">
                <Link href="/inmuebles" className="flex-1">
                  <Button onClick={() => setIsNewOwnerOpen(false)} type="submit" variant="outline" className="w-full bg-transparent">
                    Cancelar
                  </Button>
                </Link>
                <Button onClick={handleSubmit} type="submit" className="flex-1">
                  <Save className="h-4 w-4 mr-2" />
                  Registrar Inmueble
                </Button>
              </div>
            </form>
          </CardContent>
        </Card>
      </main>

      {/* Modal de error */}
      {mostrarError && (
        <ModalError
          titulo="Error al crear Inmueble"
          mensaje={errorCarga}
          onClose={() => setMostrarError(false)} // Restablecer el estado al cerrar el modal
        />
        )}

        {inmuebleCargado && (
          <ModalDefault
            titulo="Nuevo Inmueble"
            mensaje="El inmueble se ha creado correctamente."
            onClose={() => setInmuebleCargado(false)}
          />
        )}

    </div>
  )
}
