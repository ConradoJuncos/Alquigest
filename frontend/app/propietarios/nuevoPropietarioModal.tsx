'use client'

import { Button } from "@/components/ui/button"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog"
import { Plus } from "lucide-react"
import { Label } from "@/components/ui/label"
import { Input } from "@/components/ui/input"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { useState } from "react"
import BACKEND_URL from "@/utils/backendURL"
import ModalError from "@/components/modal-error"


type NuevoPropietarioModalProps = {
  text?: string
  onPropietarioCreado?: (nuevo: any) => void
}

export default function NuevoPropietarioModal({ text = "Nuevo Propietario", onPropietarioCreado }: NuevoPropietarioModalProps) {
  const [errorCarga, setErrorCarga] = useState("")
  const [mostrarError, setMostrarError] = useState(false)

  const [isNuevoPropietarioOpen, setIsNuevoPropietarioOpen] = useState(false)
  const [nuevoPropietario, setNuevoPropietario] = useState({
    nombre: "",
    apellido: "",
    dni: "",
    telefono: "",
    email: "",
    direccion: "",
    esActivo: "true",
  })

  const handleNuevoPropietario = async () => {
    try {
      const response = await fetch(`${BACKEND_URL}/propietarios`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(nuevoPropietario),
      })

      if (!response.ok) {
        const errorJson = await response.json()
        const errorMessage = errorJson.message || "Error desconocido"
        setErrorCarga(errorMessage)
        setMostrarError(true) // Mostrar el modal de error
        return
      }

      const jsonNuevoPropietario = await response.json()

      if (onPropietarioCreado) {
        onPropietarioCreado(jsonNuevoPropietario)
      }

      // Limpiar form y cerrar modal
      setNuevoPropietario({
        nombre: "",
        apellido: "",
        dni: "",
        telefono: "",
        email: "",
        direccion: "",
        esActivo: "true",
      })
      setIsNuevoPropietarioOpen(false)
    } catch (error) {
      console.error("Error al crear propietario:", error)
      setErrorCarga("Error al conectar con el servidor")
      setMostrarError(true) // Mostrar el modal de error
    }
  }

  return (
    <div>
      <Dialog open={isNuevoPropietarioOpen} onOpenChange={setIsNuevoPropietarioOpen}>
        <DialogTrigger asChild>
          <Button>
            <Plus className="h-4 w-4 mr-2" />
            {text}
          </Button>
        </DialogTrigger>

        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>Registrar Nuevo Propietario</DialogTitle>
          </DialogHeader>

          {/* FORMULARIO */}
          <form
            className="space-y-4"
            onSubmit={(e) => {
              e.preventDefault()
              handleNuevoPropietario()
            }}
          >
            <div className="grid grid-cols-2 gap-4">
              <div>
                <Label htmlFor="nombre">Nombre</Label>
                <Input
                  id="nombre"
                  required
                  value={nuevoPropietario.nombre}
                  onChange={(e) =>
                    setNuevoPropietario({ ...nuevoPropietario, nombre: e.target.value })
                  }
                  placeholder="Nombre"
                />
              </div>

              <div>
                <Label htmlFor="apellido">Apellido</Label>
                <Input
                  id="apellido"
                  required
                  value={nuevoPropietario.apellido}
                  onChange={(e) =>
                    setNuevoPropietario({ ...nuevoPropietario, apellido: e.target.value })
                  }
                  placeholder="Apellido"
                />
              </div>
            </div>

            <div>
              <Label htmlFor="dni">DNI</Label>
              <Input
                id="dni"
                type="text"
                required
                pattern="\d{8}"
                maxLength={8}
                value={nuevoPropietario.dni}
                onChange={(e) => {
                  const value = e.target.value.replace(/\D/g, "").slice(0, 8)
                  setNuevoPropietario({ ...nuevoPropietario, dni: value })
                }}
                placeholder="Sin puntos ni guiones"
              />
            </div>

            <div>
              <Label htmlFor="telefono">Teléfono</Label>
              <Input
                id="telefono"
                type="tel"
                pattern="^\d{10}$"
                value={nuevoPropietario.telefono}
                onChange={(e) =>
                  setNuevoPropietario({ ...nuevoPropietario, telefono: e.target.value })
                }
                placeholder="351-4455667"
              />
            </div>

            <div>
              <Label htmlFor="email">Email</Label>
              <Input
                id="email"
                required
                type="email"
                value={nuevoPropietario.email}
                onChange={(e) =>
                  setNuevoPropietario({ ...nuevoPropietario, email: e.target.value })
                }
                placeholder="email@ejemplo.com"
              />
            </div>

            <div>
              <Label htmlFor="direccion">Dirección</Label>
              <Input
                id="direccion"
                value={nuevoPropietario.direccion}
                onChange={(e) =>
                  setNuevoPropietario({ ...nuevoPropietario, direccion: e.target.value })
                }
                placeholder="Calle, número, ciudad"
              />
            </div>

            <div>
              <Label htmlFor="estado">Estado</Label>
              <Select
                disabled
                value={nuevoPropietario.esActivo}
                onValueChange={(value) =>
                  setNuevoPropietario({ ...nuevoPropietario, esActivo: value })
                }
              >
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value={"true"}>Activo</SelectItem>
                  <SelectItem value={"false"}>Inactivo</SelectItem>
                </SelectContent>
              </Select>
            </div>

            <div className="flex gap-2 pt-4">
              <Button type="submit" className="flex-1">
                Registrar Propietario
              </Button>
              <Button
                type="button"
                variant="outline"
                onClick={() => setIsNuevoPropietarioOpen(false)}
                className="flex-1"
              >
                Cancelar
              </Button>
            </div>
          </form>
        </DialogContent>
      </Dialog>

      {/* Modal de error */}
      {mostrarError && (
        <ModalError
          titulo="Error al crear propietario"
          mensaje={errorCarga}
          onClose={() => setMostrarError(false)} // Restablecer el estado al cerrar el modal
        />
      )}
    </div>
  )
}