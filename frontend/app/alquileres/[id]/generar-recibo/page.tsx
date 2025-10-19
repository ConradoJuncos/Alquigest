"use client"

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { ArrowLeft, Receipt, Building, DollarSign, Blocks, AlertCircle } from "lucide-react"
import { useEffect, useState } from "react"
import { useParams } from "next/navigation"
import formatPrice from "@/utils/functions/price-convert"
import TipoServicioIcon from "@/components/tipoServicioIcon"
import { TIPO_SERVICIO_LABEL } from "@/types/ServicioContrato"
import { fetchWithToken } from "@/utils/functions/auth-functions/fetchWithToken"
import { ContratoDetallado } from "@/types/ContratoDetallado"
import BACKEND_URL from "@/utils/backendURL"
import Loading from "@/components/loading"
import ExportarReciboPDF from "@/components/exportar-recibo-pdf"

export default function GenerarReciboPage() {
  const params = useParams()
  const alquilerId = params.id
  const [contratoBD, setContratoBD] = useState<ContratoDetallado>()
  const [loading, setLoading] = useState(true);
  const [loadingDatos, setLoadingDatos] = useState(true);


  type ServicioBase = { tipoServicioId: number }

  const [serviciosBase, setServiciosBase] = useState<ServicioBase[]>([])
  const [servicios, setServicios] = useState<Record<number, number | "">>({})
  const [alquilerMonto, setAlquilerMonto] = useState<number>(0)
  const [alquilerPagado, setAlquilerPagado] = useState<boolean>(false)

  useEffect(() => {
        const fetchContrato = async () => {
            console.log("Ejecutando fetch de Contratos...");
            try {
                const data = await fetchWithToken(`${BACKEND_URL}/contratos/${alquilerId}`);
                console.log("Datos parseados del backend:", data);
                setContratoBD(data);
                
            } catch (err: any) {
                console.error("Error al traer Contratos:", err.message);
            } finally {
                setLoading(false);
            }
        };

        fetchContrato();
    }, [alquilerId]);

  // Cargar servicios no pagados del contrato y el alquiler pendiente
  useEffect(() => {
    const cargarDatos = async () => {
      if (!alquilerId) return
      setLoadingDatos(true)
      try {
        // 1) Servicios no pagados del contrato
        const serviciosNoPagados: PagoServicio[] = await fetchWithToken(`${BACKEND_URL}/pagos-servicios/contrato/${alquilerId}/no-pagados`)
        const base: ServicioBase[] = (serviciosNoPagados || []).map((item) => ({
          tipoServicioId: item.servicioXContrato?.tipoServicio?.id,
        }))
        setServiciosBase(base)
        // Inicializar montos desde backend; si viene null, dejamos "" para no setear 0
        const initMontos: Record<number, number | ""> = {}
        serviciosNoPagados.forEach((p) => {
          const id = p.servicioXContrato?.tipoServicio?.id
          if (id) initMontos[id] = p.monto ?? ""
        })
        setServicios(initMontos)

        // 2) Alquiler pendiente: tomar el último; si no hay, marcar como pagado
        const alquileresPend = await fetchWithToken(`${BACKEND_URL}/alquileres/contrato/${alquilerId}/pendientes`)
        if (Array.isArray(alquileresPend) && alquileresPend.length > 0) {
          const ultimo = alquileresPend[alquileresPend.length - 1]
          setAlquilerMonto(Number(ultimo.monto) || 0)
          setAlquilerPagado(false)
        } else {
          setAlquilerMonto(0)
          setAlquilerPagado(true)
        }
      } catch (e) {
        console.error("Error cargando datos para generar recibo:", e)
      } finally {
        setLoadingDatos(false)
      }
    }
    cargarDatos()
  }, [alquilerId])

  // Handler de cambio de monto de servicios

  const handleServicioChange = (servicio: string | number, valor: string) => {
    setServicios((prev) => ({
      ...prev,
      [servicio]: valor === "" ? "" : (Number.parseFloat(valor) || 0),
    }))
  }

  const calcularTotal = () => {
    const totalServicios = Object.values(servicios || {})
      .map(v => (v === "" ? 0 : Number(v)))
      .reduce((sum, value) => sum + value, 0)
    return Number((alquilerMonto || 0) + totalServicios)
  }

  const generarRecibo = () => {
    const total = calcularTotal()
    alert(`Recibo generado para ${contratoBD?.apellidoInquilino}, ${contratoBD?.nombreInquilino}\nTotal: $${total.toLocaleString()}`)
  }

    if(loading || loadingDatos){
      return(
        <div>
          <Loading text={`Cargando datos del contrato Nro. ${alquilerId}...`}/>
        </div>
      )
    }

  return (
    <div className="min-h-screen bg-background">

      <main className="container mx-auto px-6 py-4 max-w-6xl pt-30">
        <div className="mb-8 flex flex-col gap-3">
          <Button variant="outline" onClick={() => window.history.back()} className="w-fit">
          <ArrowLeft className="h-4 w-4 mr-2" />
              Volver
          </Button>
        </div>
         
        <div className="grid grid-cols-1 lg:grid-cols-4 gap-4">
          {/* Información del Alquiler y Servicios */}
          <div className="lg:col-span-3">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {/* Información del Alquiler */}
              <Card className="h-fit">
                <CardHeader className="pb-3">
                  <CardTitle className="flex items-center gap-2 text-lg">
                    <Building className="h-7 w-7 text-[var(--amarillo-alqui)]" />
                    Información del Alquiler
                  </CardTitle>
                </CardHeader>
                <CardContent className="space-y-2 text-sm">
                  <div>
                    <p className="text-base font-medium text-muted-foreground">Inmueble</p>
                    <p className="text-base font-bold">{contratoBD?.direccionInmueble}</p>
                  </div>
                  <div>
                    <p className="text-base font-medium text-muted-foreground">Locatario:</p>
                    <p className="font-bold text-base">{contratoBD?.apellidoInquilino}, {contratoBD?.nombreInquilino}</p>
                  </div>
                </CardContent>
              </Card>

              {/* Valor del Alquiler */}
              <Card className="h-fit">
                <CardHeader className="pb-3">
                  <CardTitle className="flex items-center gap-2 text-lg">
                    <DollarSign className="h-7 w-7 text-green-600" />
                    Valor del Alquiler
                  </CardTitle>
                </CardHeader>
                <CardContent>
                    <div className="p-3 bg-background rounded-lg border border-green-200">
                    <div className="flex items-center justify-between">
                      <span className="text-sm font-medium">Alquiler Mensual</span>
                      <span className="text-xl font-bold text-green-600">
                        {formatPrice(Number(alquilerMonto || 0))}
                      </span>
                    </div>
                    <div className="flex items-center gap-2 mt-1">
                      
                      {alquilerPagado ? (
                        <span className="inline-flex items-center text-xs text-black bg-red-400 px-2 py-0.5 rounded">
                          <AlertCircle className="h-3 w-3 mr-1" /> Alquiler ya pagado
                        </span>
                      ) : (<p className="text-xs text-green-700">Calculado automáticamente</p>)}
                    </div>
                  </div>
                </CardContent>
              </Card>
            </div>

            {/* Servicios a cargo del inquilino */}
            <Card className="mt-4">
              <CardHeader className="pb-3">
                <CardTitle className="flex items-center gap-2 text-lg">
                  <Blocks className="h-7 w-7 text-[var(--amarillo-alqui)]" />
                  Servicios controlados
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-3">
                  {serviciosBase.length === 0 ? (
                    <p className="text-sm text-muted-foreground">No hay servicios no pagados para este período.</p>
                  ) : (
                    serviciosBase.map((servicio) => (
                      <div key={servicio.tipoServicioId} className={`p-3 rounded-lg border bg-background border-opacity-50`}>
                        <div className="flex items-center justify-between">
                          <div className="flex items-center gap-2">
                            <div>
                              <TipoServicioIcon tipoServicio={servicio.tipoServicioId} className={`h-8 w-8`} />
                            </div>
                            <Label className="text-sm font-medium">{TIPO_SERVICIO_LABEL[servicio.tipoServicioId]}</Label>
                          </div>
                          <div className="flex items-center gap-2">
                            <span className="text-sm font-medium">$</span>
                            <Input
                              type="number"
                              placeholder="0.00"
                              value={servicios[servicio.tipoServicioId] ?? ""}
                              onChange={(e) => handleServicioChange(servicio.tipoServicioId, e.target.value)}
                              className="text-sm h-8 w-40"
                              step="0.01"
                              min={0}
                            />
                          </div>
                        </div>
                      </div>
                    ))
                  )}
                </div>
              </CardContent>
            </Card>
          </div>

          {/* Resumen y Total */}
          <div className="lg:col-span-1">
            <Card className="sticky top-4">
              <CardHeader className="pb-3">
                <CardTitle className="flex items-center gap-2 text-lg">
                  <Receipt className="h-7 w-7 text-[var(--amarillo-alqui)]" />
                  Resumen
                </CardTitle>
              </CardHeader>
              <CardContent className="space-y-3">
                {/* Alquiler */}
                <div className="flex justify-between items-center py-1">
                  <span className="text-sm font-medium">Alquiler</span>
                  <span className="font-bold text-green-600">${(alquilerMonto || 0).toLocaleString()}</span>
                </div>

                <div className="border-t pt-2 space-y-1">
                  <p className="text-xs font-medium text-muted-foreground mb-1">Servicios:</p>
                  {serviciosBase.map((servicio) => {
                    const valor = servicios[servicio.tipoServicioId]
                    if (Number(valor ?? 0) > 0) {
                      
                      return (
                        <div key={servicio.tipoServicioId} className="flex justify-between items-center my-2">
                          <span className="flex items-center gap-2">
                            <TipoServicioIcon tipoServicio={servicio.tipoServicioId} className={`h-6 w-6`} />
                            {TIPO_SERVICIO_LABEL[servicio.tipoServicioId]}
                          </span>
                          <span className="font-medium">${Number(valor).toLocaleString()}</span>
                        </div>
                      )
                    }
                    return null
                  })}
                </div>

                {/* Total */}
                <div className="border-t pt-3">
                  <div className="flex justify-between items-center">
                    <span className="text-sm font-bold">Total:</span>
                    <span className="text-lg font-bold text-blue-600">{formatPrice(calcularTotal())}</span>
                  </div>
                </div>

                {/* Botón Exportar PDF */}
                {contratoBD && (
                  <ExportarReciboPDF
                    contrato={{
                      direccionInmueble: contratoBD.direccionInmueble,
                      nombreInquilino: contratoBD.nombreInquilino,
                      apellidoInquilino: contratoBD.apellidoInquilino,
                      nombrePropietario: contratoBD.nombrePropietario,
                      apellidoPropietario: contratoBD.apellidoPropietario,
                    }}
                    alquilerMonto={(alquilerMonto || 0).toString()}
                    servicios={servicios}
                    serviciosBase={serviciosBase}
                    total={calcularTotal()}
                    onBeforeGenerate={async () => {
                      // Construir payload con solo los servicios que tengan monto > 0
                      const actualizaciones = serviciosBase
                        .map(s => {
                          const val = servicios[s.tipoServicioId]
                          const montoNum = val === "" ? 0 : Number(val)
                          return { tipoServicioId: s.tipoServicioId, nuevoMonto: montoNum }
                        })
                        .filter(item => item.nuevoMonto > 0)

                      if (actualizaciones.length === 0) return

                      try {
                        await fetchWithToken(`${BACKEND_URL}/pagos-servicios/actualizar-montos`, {
                          method: 'PUT',
                          headers: { 'Content-Type': 'application/json' },
                          body: JSON.stringify({ contratoId: Number(alquilerId), actualizaciones })
                        })
                      } catch (e) {
                        console.error('Error actualizando montos de servicios antes del PDF:', e)
                        // Continuamos de todas formas con la generación
                      }
                    }}
                  />
                )}

                {/* Información Adicional */}
                <div className="text-xs text-muted-foreground space-y-1 mt-3 pt-3 border-t">
                  <p>• Servicios controlados por el estudio</p>
                  <p>• Alquiler calculado automáticamente</p>
                  <p>• Dejar en 0 si no aplica</p>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      </main>
    </div>
  )
}
