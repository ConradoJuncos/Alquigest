"use client"

import { useState } from "react"
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card"
import { Checkbox } from "@/components/ui/checkbox"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Button } from "@/components/ui/button"
import TipoServicioIcon from "@/components/tipoServicioIcon"
import { PAGOS_SERVICIOS_MOCK } from "@/mocks/pagosServiciosMock"
import { BORDER_HOVER_CLASSES } from "@/types/ServicioContrato"
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "./ui/dropdown-menu"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "./ui/select"
import { Separator } from "./ui/separator"
import { Badge } from "./ui/badge"

export default function RegistrarPago() {
  const [expanded, setExpanded] = useState<number[]>([])
  const [pagos, setPagos] = useState(PAGOS_SERVICIOS_MOCK)

  const handleExpand = (id: number) => {
    setExpanded((prev) =>
      prev.includes(id) ? prev.filter((i) => i !== id) : [...prev, id]
    )
  }

  const handleChange = (id: number, field: string, value: any) => {
    setPagos((prev) =>
      prev.map((p) =>
        p.id === id ? { ...p, [field]: value } : p
      )
    )
  }

  const handleSubmit = (id: number) => {
    setPagos((prev) =>
      prev.map((p) =>
        p.id === id ? { ...p, estaPagado: true } : p
      )
    )
    setExpanded((prev) => prev.filter((i) => i !== id))
  }

  return (
    <div className="space-y-6">
      {pagos.map((servicio) => (
        <Card
          key={servicio.id}
          className={`${BORDER_HOVER_CLASSES[servicio.servicioContrato.tipoServicio]} border-muted transition-all duration-200 hover:shadow-lg`}
        >
          <CardHeader
            onClick={() => !servicio.estaPagado && handleExpand(servicio.id)}
            className="flex flex-row items-center justify-between"
          >
            <div className="flex items-center gap-3">
              <Checkbox
                className="border-primary"
                checked={expanded.includes(servicio.id)}
                onCheckedChange={() => !servicio.estaPagado && handleExpand(servicio.id)}
                disabled={servicio.estaPagado}
              />
              <TipoServicioIcon
                className="h-8 w-8" 
                tipoServicio={servicio.servicioContrato.tipoServicio} />
              <CardTitle>{servicio.servicioContrato.tipoServicioNombre}</CardTitle>
              <CardDescription className="flex gap-5">
                <p>Nro Cuenta: {servicio.servicioContrato.nroCuenta}</p>
                <p>Periodo: {servicio.periodo}</p>
              </CardDescription>
            </div>
            {servicio.estaPagado && (
              <Badge className="bg-emerald-600">Pagado</Badge>
            )}
          </CardHeader>
          {expanded.includes(servicio.id) && !servicio.estaPagado && (
            <CardContent>
              <form
                className="space-y-4"
                onSubmit={e => {
                  e.preventDefault()
                  handleSubmit(servicio.id)
                }}
              >
                <div className="flex flex-col gap-2">
                  <Label>Monto</Label>
                  <Input
                    type="number"
                    value={servicio.monto}
                    onChange={e => handleChange(servicio.id, "monto", parseFloat(e.target.value))}
                  />
                </div>
                <div className="flex flex-col gap-2">
                  <Label>Fecha de pago</Label>
                  <Input
                    type="date"
                    value={servicio.fechaPago || new Date().toISOString().slice(0, 10)}
                    onChange={e => handleChange(servicio.id, "fechaPago", e.target.value)}
                  />
                </div>

                <div className="flex flex-col gap-2">
                    <Label>Se pagó Vencido?</Label>
                    <Select
                        value="false"
                        >
                        <SelectTrigger>
                        <SelectValue />
                        </SelectTrigger>
                        <SelectContent>
                        <SelectItem value="true">SI</SelectItem>
                        <SelectItem value="false">NO</SelectItem>
                        </SelectContent>
                    </Select>
                </div>

                <Separator/>

                <Button type="submit" className="bg-emerald-700 hover:bg-emerald-800">
                  Registrar Pago
                </Button>
              </form>
            </CardContent>
          )}
        </Card>
      ))}
    </div>
  )
}
