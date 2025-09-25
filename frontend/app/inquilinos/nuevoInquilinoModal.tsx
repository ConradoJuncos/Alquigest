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

type NuevoInquilinoModalProps = {
  text?: string
  onInquilinoCreado?: (nuevo: any) => void
}

export default function NuevoInquilinoModal({ text = "Nuevo Inquilino", onInquilinoCreado }: NuevoInquilinoModalProps) {
  
  const [errorCarga, setErrorCarga] = useState("")
  const [mostrarError, setMostrarError] = useState(false)
  
  const [isNuevoInquilinoOpen, setIsNuevoInquilinoOpen] = useState(false)
  const [nuevoInquilino, setNuevoInquilino] = useState({
    nombre: "",
    apellido: "",
    cuil: "",
    telefono: "",

    esActivo: "true",
  })

  const handleNuevoInquilino = async () => {
    try {
      const response = await fetchWithToken(`${BACKEND_URL}/inquilinos`, {
        method: "POST",
        body: JSON.stringify(nuevoInquilino),
      });

      if (!response.ok) {
        const errorJson = await response
        const errorMessage = errorJson.message || "Error desconocido"
        setErrorCarga(errorMessage)
        setMostrarError(true) // Mostrar el modal de error
        return
      }

      const jsonNuevoInquilino = await response

      
      if (onInquilinoCreado) {
        onInquilinoCreado(jsonNuevoInquilino)
      }

      // Limpiar form y cerrar modal
      setNuevoInquilino({
        nombre: "",
        apellido: "",
        cuil: "",
        telefono: "",
        esActivo: "true",
      })
      setIsNuevoInquilinoOpen(false)

    } catch (error) {
      console.error("Error al crear Inquilino:", error)
      setErrorCarga("Error al conectar con el servidor")
      setMostrarError(true) // Mostrar el modal de error
    }
  }

return (
  <div>
    <Dialog open={isNuevoInquilinoOpen} onOpenChange={setIsNuevoInquilinoOpen}>
      <DialogTrigger asChild>
        <Button>
          <Plus className="h-4 w-4 mr-2" />
          {text}
        </Button>
      </DialogTrigger>

      <DialogContent className="max-w-md">
        <DialogHeader>
          <DialogTitle>Registrar Nuevo Inquilino</DialogTitle>
        </DialogHeader>

        {/* FORMULARIO */}
        <form
          className="space-y-4"
          onSubmit={(e) => {
            e.preventDefault()
            handleNuevoInquilino()
          }}
        >
          <div className="grid grid-cols-2 gap-4">
            <div>
              <Label htmlFor="nombre">Nombre</Label>
              <Input
                id="nombre"
                required
                value={nuevoInquilino.nombre}
                onChange={(e) =>
                  setNuevoInquilino({ ...nuevoInquilino, nombre: e.target.value })
                }
                placeholder="Nombre"
              />
            </div>

            <div>
              <Label htmlFor="apellido">Apellido</Label>
              <Input
                id="apellido"
                required
                value={nuevoInquilino.apellido}
                onChange={(e) =>
                  setNuevoInquilino({ ...nuevoInquilino, apellido: e.target.value })
                }
                placeholder="Apellido"
              />
            </div>
          </div>

          <div>
            <Label htmlFor="cuil">Cuil</Label>
            <Input
              id="cuil"
              type="text"
              required
              minLength={11}
              maxLength={13}
              value={nuevoInquilino.cuil}
              onChange={(e) => {
                const value = e.target.value.replace(/\D/g, "").slice(0, 13)
                setNuevoInquilino({ ...nuevoInquilino, cuil: value })
              }}
              placeholder="Ej. 20-12345678-0"
            />
          </div>

          <div>
            <Label htmlFor="telefono">Teléfono</Label>
            <Input
              id="telefono"
              type="tel"
              pattern="^\d{10}$"
              value={nuevoInquilino.telefono}
              onChange={(e) =>
                setNuevoInquilino({ ...nuevoInquilino, telefono: e.target.value })
              }
              placeholder="351-4455667"
            />
          </div>

          <div>
            <Label htmlFor="estado">Estado</Label>
            <Select
              disabled
              value={nuevoInquilino.esActivo}
              onValueChange={(value) =>
                setNuevoInquilino({ ...nuevoInquilino, esActivo: value })
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
              Registrar Inquilino
            </Button>
            <Button
              type="button"
              variant="outline"
              onClick={() => setIsNuevoInquilinoOpen(false)}
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
              titulo="Error al crear Inquilino"
              mensaje={errorCarga}
              onClose={() => setMostrarError(false)} // Restablecer el estado al cerrar el modal
            />
          )}
  </div>
)}