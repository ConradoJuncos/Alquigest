"use client"

import { useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Building2, ChevronDown, ChevronUp, CreditCard, Receipt } from "lucide-react"
import Link from "next/link"
import { ContratoDetallado } from "@/types/ContratoDetallado"
import { fetchWithToken } from "@/utils/functions/auth-functions/fetchWithToken"
import BACKEND_URL from "@/utils/backendURL"
import Loading from "@/components/loading"
import TipoServicioIcon from "@/components/tipoServicioIcon"
import ServicioPagoCard from "@/components/pago-servicios/servicio-pago-card"

interface ContratoServiciosCardProps {
  contrato: ContratoDetallado
}

export default function ContratoServiciosCard({ contrato }: ContratoServiciosCardProps) {
  const [isExpanded, setIsExpanded] = useState(false)
  const [servicios, setServicios] = useState<any[]>([])
  const [loading, setLoading] = useState(false)
  const [serviciosCargados, setServiciosCargados] = useState(false)

  const toggleCard = async () => {
    const expanding = !isExpanded
    setIsExpanded(expanding)

    // Si se está expandiendo y no se han cargado los servicios, cargarlos
    if (expanding && !serviciosCargados) {
      setLoading(true)
      try {
        const data = await fetchWithToken(`${BACKEND_URL}/pagos-servicios/contrato/${contrato.id}`)
        setServicios(data)
        setServiciosCargados(true)
      } catch (err: any) {
        console.error(`Error al cargar servicios del contrato ${contrato.id}:`, err.message)
      } finally {
        setLoading(false)
      }
    }
  }

  return (
    <Card className="transition-all duration-200">
      <CardHeader>
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-4">
            <Building2 className="h-7 w-7 text-primary" />
            <div>
              <CardTitle className="text-lg">{contrato.direccionInmueble}</CardTitle>
              <CardDescription className="font-sans text-base flex gap-5">
                <div>
                  Locador: {contrato.apellidoPropietario}, {contrato.nombrePropietario}
                </div>
                <div>
                  Locatario: {contrato.apellidoInquilino}, {contrato.nombreInquilino}
                </div>
              </CardDescription>
            </div>
          </div>
          <div className="flex items-center space-x-3">
            <Badge>{serviciosCargados ? servicios.length : '...'} servicios a gestionar</Badge>
            <Button variant="ghost" size="lg" onClick={toggleCard}>
              {isExpanded ? <ChevronUp className="h-4 w-4" /> : <ChevronDown className="h-4 w-4" />}
            </Button>
          </div>
        </div>
      </CardHeader>

      {isExpanded && (
        <CardContent>
          <div className="space-y-4">
            <h4 className="font-semibold text-lg">Servicios controlados:</h4>
            {loading ? (
              <div className="text-center py-4">
                <Loading text="Cargando servicios..." />
              </div>
            ) : servicios.length === 0 ? (
              <p className="text-muted-foreground text-center py-4">
                No hay servicios registrados para este contrato
              </p>
            ) : (
              <div className="grid gap-3">
                {servicios.map((pagoServicio: any) => (
                  <ServicioPagoCard key={pagoServicio.id} pagoServicio={pagoServicio} />
                ))}
              </div>
            )}
            <div className="pt-4 border-t flex justify-end gap-5 items-center">
                {/* Aquí va el botón para generar MERCEDES LOCATIVAS */}
              <Link href={`/alquileres/${contrato.id}/generar-recibo`}>
                <Button>
                  <Receipt className="h-4 w-4 mr-2" />
                  Mercedes Locativas
                </Button>
              </Link>
            </div>
          </div>
        </CardContent>
      )}
    </Card>
  )
}
