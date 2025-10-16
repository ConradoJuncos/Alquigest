"use client"

import { useState } from "react"
import { Card, CardContent } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { ChevronDown, ChevronUp } from "lucide-react"
import TipoServicioIcon from "@/components/tipoServicioIcon"
import { Badge } from "../ui/badge"

interface ServicioPagoCardProps {
  pagoServicio: any
}

export default function ServicioPagoCard({ pagoServicio }: ServicioPagoCardProps) {
  const [isExpanded, setIsExpanded] = useState(false)
  const [monto, setMonto] = useState(pagoServicio.monto || "")
  const [fechaPago, setFechaPago] = useState("")
  const [vencido, setVencido] = useState("NO")
  const [loading, setLoading] = useState(false)

  const handleRegistrarPago = async () => {
    //ACORDARSE DE PONER ESTO EN UN USEEFFECT QUE VEA SI CAMBIA pagoServicio
    setLoading(true)
    // Aquí irá el PUT al endpoint futuro
    setTimeout(() => {
      setLoading(false)
      alert(`Pago registrado (simulado) $ ${monto}.` )
    }, 1000)
  }

  return (
    <Card className="bg-muted/50">
      <CardContent className="flex flex-col gap-2">
        <div className="flex items-center justify-between cursor-pointer" onClick={() => setIsExpanded(e => !e)}>
          
            <div className="flex items-center gap-3">
                <TipoServicioIcon tipoServicio={pagoServicio.servicioXContrato.tipoServicio.id} className="h-9 w-9"/>
                <div>
                <p className="font-bold text-base">{pagoServicio.servicioXContrato.tipoServicio.nombre}</p>
                <p className="text-sm text-muted-foreground">Período: {pagoServicio.periodo}</p>
                </div>
            </div>
            <div className="flex items-center gap-10">
              <div>
                  {pagoServicio.estaPagado ? (
                    <Badge className="bg-emerald-400">Pagado</Badge>
                  ) : (
                    <Badge className="bg-red-400">Pendiente de Pago</Badge>
                  )}
              </div>
            
            <div>
              {isExpanded ? <ChevronUp className="h-4 w-4" /> : <ChevronDown className="h-4 w-4" />}
            </div>
            </div>
        </div>
        {isExpanded && (
          <div className="mt-3 grid grid-cols-1 md:grid-cols-4 gap-4 items-end">
            <div>
              <Label>Monto</Label>
              <Input type="number" value={monto} onChange={e => setMonto(e.target.value)} min={0} />
            </div>
            <div>
              <Label>Fecha de pago</Label>
              <Input type="date" value={fechaPago} onChange={e => setFechaPago(e.target.value)} />
            </div>
            <div>
              <Label>¿Pagó vencido?</Label>
              <Select value={vencido} onValueChange={setVencido}>
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="NO">NO</SelectItem>
                  <SelectItem value="SI">SI</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div className="flex items-end w-full">
              <Button onClick={handleRegistrarPago} 
                      disabled={loading || pagoServicio.estaPagado || !monto || !fechaPago} 
                      className="w-fit bg-emerald-600 hover:bg-emerald-700">
                {loading ? "Registrando..." : "Registrar pago"}
              </Button>
            </div>
          </div>
        )}
      </CardContent>
    </Card>
  )
}
