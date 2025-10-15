"use client"

import { useState, useEffect } from "react"
import { useParams, useRouter } from "next/navigation"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { ArrowLeft, Save, Building2, DollarSign } from "lucide-react"
import { fetchWithToken } from "@/utils/functions/auth-functions/fetchWithToken"
import { ContratoDetallado } from "@/types/ContratoDetallado"
import BACKEND_URL from "@/utils/backendURL"
import Loading from "@/components/loading"

export default function RegistrarPagoAlquilerPage() {
  const params = useParams()
  const router = useRouter()
  const contratoId = params.id
  
  const [contratoBD, setContratoBD] = useState<ContratoDetallado>()
  const [loading, setLoading] = useState(true)
  
  const [formData, setFormData] = useState({
    monto: "",
    cuentaBanco: "",
    titularPago: "",
    metodoPago: "",
    fechaPago: new Date().toISOString().slice(0, 10)
  })

  useEffect(() => {
    const fetchContrato = async () => {
      console.log("Ejecutando fetch del Contrato...")
      try {
        const data = await fetchWithToken(`${BACKEND_URL}/contratos/${contratoId}`)
        console.log("Datos parseados del backend:", data)
        setContratoBD(data)
      } catch (err: any) {
        console.error("Error al traer el contrato:", err.message)
      } finally {
        setLoading(false)
      }
    }

    fetchContrato()
  }, [contratoId])

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

    console.log("Datos del pago a registrar:", formData)
    
    // Aquí iría la lógica para enviar los datos al backend
    alert("Pago del alquiler registrado correctamente")
    router.back()
  }

  if (loading) {
    return (
      <div>
        <Loading text={`Cargando datos del contrato Nro. ${contratoId}...`} />
      </div>
    )
  }

  if (!contratoBD) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <Card>
          <CardContent className="p-6">
            <p className="text-muted-foreground">Contrato no encontrado</p>
          </CardContent>
        </Card>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-background">
      <main className="container mx-auto px-6 py-8 pt-30">
        {/* Header */}
        <div className="mb-8 flex flex-col gap-3">
          <Button 
            variant="outline" 
            onClick={() => router.back()} 
            className="w-fit"
          >
            <ArrowLeft className="h-4 w-4 mr-2" />
            Volver
          </Button>
          
          <div className="flex items-center gap-3">
            <DollarSign className="h-8 w-8 text-green-600" />
            <div>
              <h1 className="text-3xl font-bold">Registrar Pago del Alquiler</h1>
              <p className="text-muted-foreground">Contrato Nro. {contratoId}</p>
            </div>
          </div>
        </div>

        {/* Información del contrato */}
        <Card className="mb-6">
          <CardHeader>
            <div className="flex items-center gap-3">
              <Building2 className="h-6 w-6 text-primary" />
              <div>
                <CardTitle>Información del Contrato</CardTitle>
              </div>
            </div>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <p className="text-sm text-muted-foreground">Inmueble</p>
                <p className="font-semibold">{contratoBD.direccionInmueble}</p>
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Locatario</p>
                <p className="font-semibold">
                  {contratoBD.apellidoInquilino}, {contratoBD.nombreInquilino}
                </p>
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Locador</p>
                <p className="font-semibold">
                  {contratoBD.apellidoPropietario}, {contratoBD.nombrePropietario}
                </p>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Formulario de registro de pago */}
        <Card>
          <CardHeader>
            <CardTitle>Datos del Pago</CardTitle>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSubmit} className="space-y-6">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
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
                <div className="space-y-2">
                  <Label htmlFor="fechaPago">Fecha de Pago</Label>
                  <Input
                    id="fechaPago"
                    type="date"
                    value={formData.fechaPago}
                    onChange={(e) => handleChange("fechaPago", e.target.value)}
                  />
                </div>
              </div>

              {/* Botones de acción */}
              <div className="flex justify-end gap-4 pt-4 border-t">
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => router.back()}
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
          </CardContent>
        </Card>

        {/* Nota informativa */}
        <Card className="mt-6 border-blue-200 bg-blue-50/30">
          <CardContent className="p-4">
            <p className="text-sm text-muted-foreground">
              <strong>Nota:</strong> El pago del alquiler debe realizarse antes del día 10 de cada mes.
              Asegúrese de que todos los datos sean correctos antes de confirmar el registro.
            </p>
          </CardContent>
        </Card>
      </main>
    </div>
  )
}
