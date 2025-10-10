"use client"

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { ArrowLeft, Receipt, Droplets, Building, Flame, FileText, DollarSign, Zap, HandCoins, Blocks } from "lucide-react"
import { useEffect, useState } from "react"
import { useParams } from "next/navigation"
import formatPrice from "@/utils/functions/price-convert"
import TipoServicioIcon from "@/components/tipoServicioIcon"
import { ServicioContrato, TIPO_SERVICIO_LABEL } from "@/types/ServicioContrato"
import { fetchWithToken } from "@/utils/functions/auth-functions/fetchWithToken"
import { ContratoDetallado } from "@/types/ContratoDetallado"
import BACKEND_URL from "@/utils/backendURL"
import Loading from "@/components/loading"

export default function GenerarReciboPage() {
  const params = useParams()
  const alquilerId = params.id
  const [contratoBD, setContratoBD] = useState<ContratoDetallado>()
  const [loading, setLoading] = useState(true);

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




  // Datos de ejemplo del alquiler (normalmente vendría de una API)
  const alquiler = {
    id: alquilerId,
    inmueble: "Apartamento Centro - Calle Mayor 123",
    inquilino: "María García López",
    propietario: "Juan Pérez Martín",
    montoMensual: (Math.random() * (900000 - 400000) + 400000).toFixed(1), // Monto aleatorio entre 400,000 y 900,000
    fechaInicio: "2024-01-15",
    fechaVencimiento: "2025-01-14",
  }

  const [servicios, setServicios] = useState({
    1: 0,
    2: 0,
    3: 0,
    4: 0,
    5: 0,
  })

const SERVICIOS_BASE: ServicioContrato[] = [
  { tipoServicio: 1, nroCuenta: null, nroContrato: null, esDeInquilino: true, esActivo: false, esAnual: false }, // Agua
  { tipoServicio: 2, nroCuenta: null, nroContrato: null, esDeInquilino: true, esActivo: false, esAnual: false }, // Luz
  { tipoServicio: 3, nroCuenta: null, nroContrato: null, esDeInquilino: true, esActivo: false, esAnual: false }, // Gas
  { tipoServicio: 4, nroCuenta: null, nroContrato: null, esDeInquilino: true, esActivo: false, esAnual: true },  // Municipal (suele ser anual)
  { tipoServicio: 5, nroCuenta: null, nroContrato: null, esDeInquilino: true, esActivo: false, esAnual: true },  // Rentas (suele ser anual)
];

  const handleServicioChange = (servicio: string, valor: string) => {
    setServicios((prev) => ({
      ...prev,
      [servicio]: Number.parseFloat(valor) || 0,
    }))
  }

  const calcularTotal = () => {
    const totalServicios = Object.values(servicios).reduce((sum, value) => sum + value, 0)
    return Number(alquiler.montoMensual + totalServicios)
  }

  const generarRecibo = () => {
    const total = calcularTotal()
    alert(`Recibo generado para ${contratoBD?.apellidoInquilino}, ${contratoBD?.nombreInquilino}\nTotal: $${total.toLocaleString()}`)
    // Aquí se implementaría la lógica real de generación del recibo
  }

    if(loading){
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
                    <p className="text-base font-medium text-muted-foreground">Locador:</p>
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
                        {formatPrice(Number(alquiler.montoMensual))}
                      </span>
                    </div>
                    <p className="text-xs text-green-700 mt-1">Calculado automáticamente</p>
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
                  {SERVICIOS_BASE.map((servicio) => {
                    
                    return (
                      <div key={servicio.tipoServicio} className={`p-3 rounded-lg border bg-background border-opacity-50`}>
                        <div className="flex items-center justify-between">
                          <div className="flex items-center gap-2">
                            <div>
                              <TipoServicioIcon tipoServicio={servicio.tipoServicio} className={`h-8 w-8`} />
                            </div>
                            <Label className="text-sm font-medium">{TIPO_SERVICIO_LABEL[servicio.tipoServicio]}</Label>
                          </div>
                          <div className="flex items-center gap-2">
                            <span className="text-sm font-medium">$</span>
                            <Input
                              type="number"
                              placeholder="0.00"
                              value={servicios[servicio.tipoServicio] || ""}
                              onChange={(e) => handleServicioChange(servicio.tipoServicio, e.target.value)}
                              className="text-sm h-8 w-40"
                              step="0.01"
                              min="0"
                            />
                          </div>
                        </div>
                      </div>
                    )
                  })}
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
                  <span className="font-bold text-green-600">${alquiler.montoMensual}</span>
                </div>

                <div className="border-t pt-2 space-y-1">
                  <p className="text-xs font-medium text-muted-foreground mb-1">Servicios:</p>
                  {SERVICIOS_BASE.map((servicio) => {
                    const valor = servicios[servicio.tipoServicio as keyof typeof servicios]
                    if (valor > 0) {
                      
                      return (
                        <div key={servicio.tipoServicio} className="flex justify-between items-center my-2">
                          <span className="flex items-center gap-2">
                            <TipoServicioIcon tipoServicio={servicio.tipoServicio} className={`h-6 w-6`} />
                            {TIPO_SERVICIO_LABEL[servicio.tipoServicio]}
                          </span>
                          <span className="font-medium">${valor.toLocaleString()}</span>
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

                {/* Botón Generar Recibo */}
                <Button onClick={generarRecibo} className="w-full mt-4" size="sm">
                  <Receipt className="h-4 w-4 mr-2" />
                  Generar Recibo
                </Button>

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
