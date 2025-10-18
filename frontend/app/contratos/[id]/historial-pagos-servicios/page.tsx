"use client"

import { useEffect, useState } from "react"
import { useParams } from "next/navigation"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Table, TableBody, TableCaption, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import Loading from "@/components/loading"
import { fetchWithToken } from "@/utils/functions/auth-functions/fetchWithToken"
import BACKEND_URL from "@/utils/backendURL"
import { Button } from "@/components/ui/button"
import { ArrowLeft, CalendarClockIcon } from "lucide-react"
import { Badge } from "@/components/ui/badge"
import TipoServicioIcon from "@/components/tipoServicioIcon"

interface PagoServicioItem {
  id: number
  estaPagado: boolean
  estaVencido: boolean
  fechaPago: string | null
  medioPago: string | null
  monto: number | null
  periodo: string
  servicioXContrato: {
    id: number
    nroCuenta: string | null
    esDeInquilino: boolean
    tipoServicio: {
      id: number
      nombre: string
    }
  }
}

export default function HistorialPagosServiciosPage() {
  const params = useParams<{ id: string }>()
  const contratoId = params?.id
  const [data, setData] = useState<PagoServicioItem[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchData = async () => {
      if (!contratoId) return
      try {
        const resp = await fetchWithToken(`${BACKEND_URL}/pagos-servicios/contrato/${contratoId}`)
        setData(resp)
      } catch (e) {
        console.error("Error cargando historial de pagos de servicios:", e)
      } finally {
        setLoading(false)
      }
    }
    fetchData()
  }, [contratoId])

  if (loading) return <Loading text="Cargando historial de pagos de servicios" tituloHeader="Historial de servicios" />

  return (
    <div className="min-h-screen bg-background">
      <main className="container mx-auto px-6 py-8 pt-30">
        <div> 
            <Button variant="outline" onClick={() => window.history.back()}> 
            <ArrowLeft className="h-4 w-4 mr-2" /> Volver </Button> 
        </div> 
        <Card className="mt-10">
          <CardHeader>
            <div className="flex items-center space-x-2">
                <CalendarClockIcon className="h-7 w-7" />
                <CardTitle className="text-xl">Historial de pagos de servicios</CardTitle>
            </div>
          </CardHeader>
          <CardContent>
            <Table >
              <TableHeader className="bg-primary">
                <TableRow>
                  <TableHead className="font-bold text-background">Período</TableHead>
                  <TableHead className="font-bold text-background">Servicio</TableHead>
                  <TableHead className="font-bold text-background">Monto</TableHead>
                  <TableHead className="font-bold text-background">Nro. Cuenta</TableHead>
                  <TableHead className="font-bold text-background">Estado</TableHead>
                  <TableHead className="font-bold text-background">Medio de Pago</TableHead>
                  <TableHead className="font-bold text-background">Fecha de pago</TableHead>
                  <TableHead className="font-bold text-background">A cargo de</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {data.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={7} className="text-center text-muted-foreground">No hay registros</TableCell>
                  </TableRow>
                ) : (
                  data.map((item) => {

                    const estadoPago = item.estaPagado 
                        ? item.estaVencido 
                          ? (<Badge className="bg-yellow-200 text-black">Pagado Vencido</Badge>) 
                          : (<Badge className="bg-emerald-300 text-black">Pagado</Badge>) 
                          : (<Badge className="bg-red-300 text-black">Pendiente</Badge>)

                    const aCargoDe = item.servicioXContrato.esDeInquilino 
                        ? (<Badge variant={"secondary"}>Locatario</Badge>) 
                        : (<Badge>Estudio Jurídico</Badge>)

                    const tipoServicio = <div className="flex gap-2 items-center"> 
                                            <TipoServicioIcon className="h-6 w-6" tipoServicio={item.servicioXContrato.tipoServicio.id}/>
                                            {item.servicioXContrato.tipoServicio.nombre}
                                          </div>
                    return (
                      <TableRow key={item.id}>
                        <TableCell className="font-semibold">{item.periodo}</TableCell>
                        <TableCell>{tipoServicio}</TableCell>
                        <TableCell>{item.monto ? `$${item.monto.toLocaleString()}` : "-"}</TableCell>
                        <TableCell>{item.servicioXContrato.nroCuenta ?? "-"}</TableCell>
                        <TableCell>{estadoPago}</TableCell>
                        <TableCell>{item.medioPago ?? "-"}</TableCell>
                        <TableCell>{item.fechaPago}</TableCell>
                        <TableCell>{aCargoDe }</TableCell>
                      </TableRow>
                    )
                  })
                )}
              </TableBody>
              <TableCaption>Incluye servicios pagados y pendientes del contrato #{contratoId}</TableCaption>
            </Table>
          </CardContent>
        </Card>
      </main>
    </div>
  )
}
