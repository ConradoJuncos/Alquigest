'use client'

import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { useEffect, useState } from "react"
import BACKEND_URL from "@/utils/backendURL"
import ModalError from "@/components/modal-error"

type EditarPropietarioModalProps = {
  propietario: any
  isOpen: boolean
  onClose: () => void
  onPropietarioActualizado: (propietarioActualizado: any) => void
}

export default function EditarPropietarioModal({
  propietario,
  isOpen,
  onClose,
  onPropietarioActualizado,
}: EditarPropietarioModalProps) {
  const [editingOwner, setEditingOwner] = useState(propietario)

  const [errorCarga, setErrorCarga] = useState("")
  const [mostrarError, setMostrarError] = useState(false)

  // Sincronizar el estado interno con la prop `propietario` cuando esta cambie
  useEffect(() => {
    setEditingOwner(propietario)
  }, [propietario])

  const handleUpdateOwner = async () => {
    try {
      const response = await fetch(`${BACKEND_URL}/propietarios/${editingOwner.id}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(editingOwner),
      })

      if (!response.ok) {
        const errorJson = await response.json()
        const errorMessage = errorJson.message || "Error desconocido"
        setErrorCarga(errorMessage)
        setMostrarError(true) 
      }

      const updatedOwner = await response.json()
      onPropietarioActualizado(updatedOwner)
      onClose()
    } catch (error) {
      console.error("Error al crear propietario:", error)
      setErrorCarga("Revise los datos e intente nuevamente.")
      setMostrarError(true) // Mostrar el modal de error
    }
  }

  return (
    <div>
        <Dialog open={isOpen} onOpenChange={onClose}>
        <DialogContent className="max-w-md">
            <DialogHeader>
            <DialogTitle>Editar Propietario</DialogTitle>
            </DialogHeader>

            {editingOwner && (
            <form
                className="space-y-4"
                onSubmit={(e) => {
                e.preventDefault()
                handleUpdateOwner()
                }}
            >
                <div className="grid grid-cols-2 gap-4">
                <div>
                    <Label htmlFor="edit-nombre">Nombre</Label>
                    <Input
                    required
                    id="edit-nombre"
                    value={editingOwner.nombre}
                    onChange={(e) => setEditingOwner({ ...editingOwner, nombre: e.target.value })}
                    />
                </div>
                <div>
                    <Label htmlFor="edit-apellido">Apellido</Label>
                    <Input
                    required
                    id="edit-apellido"
                    value={editingOwner.apellido}
                    onChange={(e) => setEditingOwner({ ...editingOwner, apellido: e.target.value })}
                    />
                </div>
                </div>

                <div>
                <Label htmlFor="edit-dni">DNI</Label>
                <Input id="edit-dni" value={editingOwner.dni} disabled className="bg-muted" />
                <p className="text-xs text-muted-foreground mt-1">El DNI no se puede modificar</p>
                </div>

                <div>
                <Label htmlFor="edit-telefono">Teléfono</Label>
                <Input
                    id="edit-telefono"
                    type="tel"
                    pattern="^\d{10}$"
                    value={editingOwner.telefono}
                    onChange={(e) => setEditingOwner({ ...editingOwner, telefono: e.target.value })}
                    placeholder="351-4455667"
                />
                </div>

                <div>
                <Label htmlFor="edit-email">Email</Label>
                <Input
                    required
                    id="edit-email"
                    type="email"
                    value={editingOwner.email}
                    onChange={(e) => setEditingOwner({ ...editingOwner, email: e.target.value })}
                />
                </div>

                <div>
                <Label htmlFor="edit-direccion">Dirección</Label>
                <Input
                    id="edit-direccion"
                    value={editingOwner.direccion}
                    onChange={(e) => setEditingOwner({ ...editingOwner, direccion: e.target.value })}
                />
                </div>

                <div>
                <Label htmlFor="edit-estado">Estado</Label>
                <Select
                    value={editingOwner.esActivo === true ? "true" : "false"}
                    onValueChange={(value) =>
                    setEditingOwner({ ...editingOwner, esActivo: value === "true" })
                    }
                >
                    <SelectTrigger>
                    <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                    <SelectItem value="true">Activo</SelectItem>
                    <SelectItem value="false">Inactivo</SelectItem>
                    </SelectContent>
                </Select>
                </div>

                <div className="flex gap-2 pt-4">
                <Button type="submit" className="flex-1">
                    Guardar Cambios
                </Button>
                <Button type="button" variant="outline" onClick={onClose} className="flex-1">
                    Cancelar
                </Button>
                </div>
            </form>
            )}
        </DialogContent>
        </Dialog>

        {/* Modal de error */}
            {mostrarError && (
                <ModalError
                titulo="Error al editar Propietario"
                mensaje={errorCarga}
                onClose={() => setMostrarError(false)} // Restablecer el estado al cerrar el modal
                />
            )}
    </div>
  )
}