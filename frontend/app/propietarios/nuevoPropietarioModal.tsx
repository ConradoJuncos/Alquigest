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
import { fetchWithToken } from "@/utils/functions/auth-functions/fetchWithToken"
import auth from "@/utils/functions/auth-functions/auth"


type NuevoPropietarioModalProps = {
  text?: string
  disabled?: boolean
  onPropietarioCreado?: (nuevo: any) => void
}

export default function NuevoPropietarioModal({ text = "Nuevo Locador", onPropietarioCreado, disabled }: NuevoPropietarioModalProps) {
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
  })

  const handleNuevoPropietario = async () => {
    try {
      const response = await fetchWithToken(`${BACKEND_URL}/propietarios`, {
        method: "POST",
        body: JSON.stringify(nuevoPropietario),
      })

      const jsonNuevoPropietario = await response

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
      })
      setIsNuevoPropietarioOpen(false)
    } catch (error) {
      console.error("Error al crear propietario:", error)
      const mensajeError = error.message || "Error al conectarse al servidor"
      setErrorCarga(mensajeError)
      setMostrarError(true) // Mostrar el modal de error
    }
  }

  return (
    <div>
      <Dialog open={isNuevoPropietarioOpen} onOpenChange={setIsNuevoPropietarioOpen}>
        <DialogTrigger asChild>
          <Button disabled={!auth.tienePermiso("crear_propietario")}>
            <Plus className="h-4 w-4 mr-2" />
            {text}
          </Button>
        </DialogTrigger>

        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>Registrar Nuevo Locador</DialogTitle>
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
                minLength={8}
                maxLength={9}
                value={nuevoPropietario.dni}
                onChange={(e) => {
                  const value = e.target.value.replace(/\D/g, "").slice(0, 9)
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
                maxLength={15} // Limitar la longitud máxima del teléfono
                value={nuevoPropietario.telefono}
                onChange={(e) => {
                  const value = e.target.value.replace(/[^0-9()+-\s]/g, ""); // Permitir solo números, guiones, paréntesis y espacios
                  setNuevoPropietario({ ...nuevoPropietario, telefono: value });
                }}
                placeholder="(351) 4455667"
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
          titulo="Error al crear Propietario"
          mensaje={errorCarga}
          onClose={() => setMostrarError(false)} // Restablecer el estado al cerrar el modal
        />
      )}
    </div>
  )
}