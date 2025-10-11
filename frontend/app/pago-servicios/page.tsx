"use client"

import { useEffect, useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Building2, ChevronDown, ChevronUp, CreditCard, Blocks, Receipt } from "lucide-react"
import Link from "next/link"

import { ContratoDetallado } from "@/types/ContratoDetallado"
import { fetchWithToken } from "@/utils/functions/auth-functions/fetchWithToken"
import BACKEND_URL from "@/utils/backendURL"
import Loading from "@/components/loading"
import TipoServicioIcon from "@/components/tipoServicioIcon"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Checkbox } from "@/components/ui/checkbox"
import { PAGOS_SERVICIOS_MOCK } from "@/mocks/pagosServiciosMock"
import RegistrarPago from "@/components/registrar-pago"


export default function PagoServiciosPage() {
  const [expandedCards, setExpandedCards] = useState<number[]>([])
  const [expandedService, setExpandedService] = useState<String[]>([])
  const [contratosBD, setContratosBD] = useState<ContratoDetallado[] | any>([])
  const [totalContratos, setTotalContratos] = useState(0)
  const [loading, setLoading] = useState(true);

  const toggleCard = (inmuebleId: number) => {
    setExpandedCards((prev) =>
      prev.includes(inmuebleId) ? prev.filter((id) => id !== inmuebleId) : [...prev, inmuebleId],
    )
  }
  const serviciosEstudio = (servicios: any[]) => servicios.filter((servicio) => servicio.responsable === "estudio")

  useEffect(() => {
    const fetchContratos = async () => {

      console.log("Ejecutando fetch de Contratos...");
      try {
        const data = await fetchWithToken(`${BACKEND_URL}/contratos/vigentes`);
        const total = await fetchWithToken(`${BACKEND_URL}/contratos/count/vigentes`);
        console.log("Datos parseados del backend:", data);
        setContratosBD(data);
        setTotalContratos(total);
      } catch (err: any) {
        console.error("Error al traer propietarios:", err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchContratos();
  }, []);

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

        {/* Inmuebles List */}
        <div className="space-y-6">
          <h2 className="text-xl font-bold text-foreground">Alquileres con servicios bajo control</h2>

          {contratosBD.map((contrato: ContratoDetallado) => {
            const isExpanded = expandedCards.includes(contrato.id)

            return (
              <Card key={contrato.id} className="transition-all duration-200">
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
                      <Badge >{PAGOS_SERVICIOS_MOCK.length} servicios a gestionar</Badge>
                      <Button variant="ghost" size="lg" onClick={() => toggleCard(contrato.id)}>
                        {isExpanded ? <ChevronUp className="h-4 w-4" /> : <ChevronDown className="h-4 w-4" />}
                      </Button>
                    </div>
                  </div>
                </CardHeader>

                {isExpanded && (
                  

                  <CardContent>
                    <div className="space-y-4">
                      <h4 className="font-semibold font-sans">Servicios controlados:</h4>
                      <div className="grid gap-3">
                        {PAGOS_SERVICIOS_MOCK.map((servicio, index) => (
                          
                          <div key={index} className="flex items-center justify-between p-3 bg-muted/50 rounded-lg">
                            <div className="flex items-center gap-5">
                              <div className="flex items-center space-x-3">
                                <TipoServicioIcon tipoServicio={servicio.servicioContrato.tipoServicio}/>
                                <div>
                                  <p className="font-medium font-sans">{servicio.servicioContrato.tipoServicioNombre}</p>
                                </div>
                              </div>
                            </div>
                            <div className="flex items-center space-x-3">
                              Nro de Cuenta: {servicio.servicioContrato.nroCuenta}
                            </div>
                            <div>
                              <p>Bajo control del {servicio.servicioContrato.esDeInquilino ? "Locatario" : "Estudio Jurídico"}</p>
                            </div>  
                          </div>
                        ))}
                      </div>

                      <div className="pt-4 border-t flex justify-end gap-5 items-center">
                        <Link href={`/pago-servicios/${contrato.id}/nuevo-pago`}>
                          <Button className="bg-emerald-700 hover:bg-emerald-800">
                            <CreditCard className="h-4 w-4 mr-2" />
                            Registrar Nuevo Pago
                          </Button>
                        </Link>

                        <Link href={`/alquileres/${contrato.id}/generar-recibo`}>
                          <Button className="" >
                            <Receipt className="h-4 w-4 mr-2" />
                            Generar Recibo
                          </Button>
                        </Link>
                      </div>
                    </div>
                  </CardContent>
                )}
              </Card>
            )
          })}
        </div>
      </main>
    </div>
  )
}
