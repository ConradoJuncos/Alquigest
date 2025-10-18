"use client"

import { useState, useEffect } from "react"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Save, Building2 } from "lucide-react"
import { ContratoDetallado } from "@/types/ContratoDetallado"

interface ModalRegistrarPagoAlquilerProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  contrato: ContratoDetallado
}

export default function ModalRegistrarPagoAlquiler({
  open,
  onOpenChange,
  contrato
}: ModalRegistrarPagoAlquilerProps) {
  
  const [formData, setFormData] = useState({
    monto: "",
    cuentaBanco: "",
    titularPago: "",
    metodoPago: "",
    fechaPago: new Date().toISOString().slice(0, 10)
  })

  // Reset form when modal closes
  useEffect(() => {
    if (!open) {
      setFormData({
        monto: "",
        cuentaBanco: "",
        titularPago: "",
        metodoPago: "",
        fechaPago: new Date().toISOString().slice(0, 10)
      })
    }
  }, [open])

  const handleChange = (field: string, value: string) => {
    setFormData((prev) => ({
      ...prev,
      [field]: value
    }))
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    
    // Validaciones básicas
    if (!formData.monto || !formData.titularPago || !formData.metodoPago) {
      alert("Por favor complete todos los campos obligatorios")
      return
    }

    console.log("Datos del pago a registrar:", {
      contratoId: contrato.id,
      ...formData
    })
    
    // Aquí iría la lógica para enviar los datos al backend
    alert("Pago del alquiler registrado correctamente")
    onOpenChange(false)
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-3xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle className="text-xl flex items-center gap-2">
            <Building2 className="h-6 w-6 text-primary" />
            Registrar Pago del Alquiler
          </DialogTitle>
        </DialogHeader>

        {/* Información del contrato */}
        <div className="bg-muted/50 p-4 rounded-lg mb-4 space-y-2">
          <div className="font-bold">
            Contrato Nro. {contrato.id} - {contrato.direccionInmueble}
          </div>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-3 text-sm">
            <div>
              <p className="text-muted-foreground">Locatario</p>
              <p className="font-semibold">
                {contrato.apellidoInquilino}, {contrato.nombreInquilino}
              </p>
            </div>
            <div>
              <p className="text-muted-foreground">Locador</p>
              <p className="font-semibold">
                {contrato.apellidoPropietario}, {contrato.nombrePropietario}
              </p>
            </div>
          </div>
        </div>

        {/* Formulario */}
        <form onSubmit={handleSubmit} className="space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {/* Monto */}
            <div className="space-y-2">
              <Label htmlFor="monto">
                Monto <span className="text-red-500">*</span>
              </Label>
              <Input
                id="monto"
                type="number"
                step="0.01"
                placeholder="0.00"
                min={0}
                value={formData.monto}
                onChange={(e) => handleChange("monto", e.target.value)}
                required
              />
            </div>

            {/* Método de Pago */}
            <div className="space-y-2">
              <Label htmlFor="metodoPago">
                Método de Pago <span className="text-red-500">*</span>
              </Label>
              <Select
                value={formData.metodoPago}
                onValueChange={(value) => handleChange("metodoPago", value)}
              >
                <SelectTrigger id="metodoPago">
                  <SelectValue placeholder="Seleccione un método" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="debito">Débito</SelectItem>
                  <SelectItem value="transferencia">Transferencia bancaria</SelectItem>
                  <SelectItem value="efectivo">Efectivo</SelectItem>
                </SelectContent>
              </Select>
            </div>

            {/* Titular del Pago */}
            <div className="space-y-2">
              <Label htmlFor="titularPago">
                Titular del Pago <span className="text-red-500">*</span>
              </Label>
              <Input
                id="titularPago"
                type="text"
                placeholder="Nombre completo del titular"
                value={formData.titularPago}
                onChange={(e) => handleChange("titularPago", e.target.value)}
                required
              />
            </div>

            {/* Cuenta de Banco */}
            <div className="space-y-2">
              <Label htmlFor="cuentaBanco">Cuenta de Banco</Label>
              <Input
                id="cuentaBanco"
                type="text"
                placeholder="Número de cuenta o CBU"
                value={formData.cuentaBanco}
                onChange={(e) => handleChange("cuentaBanco", e.target.value)}
              />
            </div>

            {/* Fecha de Pago */}
            <div className="space-y-2 md:col-span-2">
              <Label htmlFor="fechaPago">Fecha de Pago</Label>
              <Input
                id="fechaPago"
                type="date"
                value={formData.fechaPago}
                onChange={(e) => handleChange("fechaPago", e.target.value)}
              />
            </div>
          </div>

          {/* Nota informativa */}
          <div className="bg-muted p-3 rounded-lg">
            <p className="text-sm text-muted-foreground">
              <strong>Nota: </strong>
               Asegúrese de que todos los datos sean correctos antes de confirmar el registro.
            </p>
          </div>

          {/* Botones de acción */}
          <div className="flex justify-end gap-3 pt-4 border-t">
            <Button
              type="button"
              variant="outline"
              onClick={() => onOpenChange(false)}
            >
              Cancelar
            </Button>
            <Button
              type="submit"
              className="bg-green-600 hover:bg-green-700"
            >
              <Save className="h-4 w-4 mr-2" />
              Registrar Pago
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  )
}
