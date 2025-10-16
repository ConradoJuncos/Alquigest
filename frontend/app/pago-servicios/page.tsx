"use client"

import { useEffect, useState } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { CreditCard, Blocks, ArrowLeft } from "lucide-react"
import { ContratoDetallado } from "@/types/ContratoDetallado"
import { fetchWithToken } from "@/utils/functions/auth-functions/fetchWithToken"
import BACKEND_URL from "@/utils/backendURL"
import Loading from "@/components/loading"
import ContratoServiciosCard from "@/components/pago-servicios/contrato-servicios-card"
import { Button } from "@/components/ui/button"


export default function PagoServiciosPage() {
  const [contratos, setContratos] = useState<ContratoDetallado[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchContratos = async () => {
      console.log("Ejecutando fetch de Contratos...")
      try {
        const data = await fetchWithToken(`${BACKEND_URL}/contratos/vigentes`)
        console.log("Datos parseados del backend:", data)
        setContratos(data)
      } catch (err: any) {
        console.error("Error al traer contratos:", err.message)
      } finally {
        setLoading(false)
      }
    }

    fetchContratos()
  }, [])

  if(loading){
      return(
        <div>
          <Loading text="Cargando contratos de alquiler"/>
        </div>
      )
    }

  return (
    <div className="min-h-screen bg-background">

      {/* Main Content */}
      <main className="container mx-auto px-6 py-8 pt-30">
        <div className="mb-8 flex flex-col gap-3">
          <Button variant="outline" onClick={() => window.history.back()} className="w-fit">
          <ArrowLeft className="h-4 w-4 mr-2" />
              Volver
          </Button>
        </div>

        {/* Stats */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0">
              <CardTitle className="text-base font-medium">Total de servicios</CardTitle>
              <Blocks className="h-6 w-6 text-muted-foreground" />
            </CardHeader>
            <CardContent className="flex flex-col items-center justify-center">
              <div className="text-2xl font-bold font-sans">N/A</div>
              <p className="text-xs text-muted-foreground">Bajo control del estudio jurídico</p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0">
              <CardTitle className="text-base font-medium">Servicios Pendientes</CardTitle>
              <CreditCard className="h-6 w-6 text-red-400" />
            </CardHeader>
            <CardContent className="flex flex-col items-center justify-center">
              <div className="text-2xl font-bold  text-red-400 font-sans">
                N/A
              </div>
              <p className="text-xs text-muted-foreground">Aún no fueron pagados</p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-base font-medium">Servicios Pagados</CardTitle>
              <CreditCard className="h-6 w-6 text-green-500" />
            </CardHeader>
            <CardContent className="flex flex-col items-center justify-center">
              <div className="text-2xl font-bold font-sans text-green-600">
                N/A
              </div>
              <p className="text-xs text-muted-foreground">Pagos realizados</p>
            </CardContent>
          </Card>
        </div>

        {/* Contratos List */}
        <div className="space-y-6">
          <h2 className="text-xl font-bold text-foreground">Alquileres con servicios bajo control</h2>

          {contratos.map((contrato: ContratoDetallado) => (
            <ContratoServiciosCard key={contrato.id} contrato={contrato} />
          ))}
        </div>
      </main>
    </div>
  )
}
