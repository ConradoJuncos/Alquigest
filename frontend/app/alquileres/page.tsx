"use client"

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Building2, Calendar, Users, Euro, ArrowLeft, Plus, Search, Filter, Receipt, AlertCircle, User, FileText, ChevronDown } from "lucide-react"
import Link from "next/link"
import { useEffect, useState } from "react"
import contratosCompletos from "./contratos-mock"
import { fetchWithToken } from "@/utils/functions/auth-functions/fetchWithToken"
import BACKEND_URL from "@/utils/backendURL"
import Loading from "@/components/loading"
import { Separator } from "@/components/ui/separator"
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu"
import InmuebleIcon from "@/components/inmueble-icon";

export default function AlquileresPage() {

  const [contratosBD, setContatosBD] = useState(null)
  const [loading, setLoading] = useState(true);
  const [selectedAlquiler, setSelectedAlquiler] = useState<any>(null)
  const [servicios, setServicios] = useState({
    agua: 0,
    municipalidad: 0,
    rentas: 0,
    gas: 0,
  })

  const [expandedCard, setExpandedCard] = useState<number | null>(null); // id del contrato expandido
  const [filtroContrato, setFiltroContrato] = useState<"vigentes" | "proximos-vencer">("vigentes");
  const toggleCard = (id: number) => {
    setExpandedCard(expandedCard === id ? null : id);
  }

  useEffect(() => {
  const fetchContratos = async () => {

    console.log("Ejecutando fetch de Contratos...");
    try {
      const data = await fetchWithToken(`${BACKEND_URL}/contratos/${filtroContrato}`);
      console.log("Datos parseados del backend:", data);
      setContatosBD(data);
    } catch (err: any) {
      console.error("Error al traer propietarios:", err.message);
    } finally {
      setLoading(false);
    }
  };

  fetchContratos();
}, [filtroContrato]);

  const getEstadoBadge = (estado: string) => {
    switch (estado) {
      case "Vigente":
        return <Badge className="bg-green-100 text-green-800 hover:bg-green-100 text-sm font-bold">Vigente</Badge>
      case "Por Renovar":
        return <Badge className="bg-orange-100 text-orange-800 hover:bg-orange-100 text-sm font-bold">Por Renovar</Badge>
      case "Vencido":
        return <Badge className="bg-red-100 text-red-800 hover:bg-red-100 text-sm font-bold">Vencido</Badge>
      default:
        return <Badge className="text-sm font-bold" variant="secondary">{estado}</Badge>
    }
  }

  const getPagoBadge = (dias: number) => {
    if (dias <= 3) {
      return <Badge className="bg-red-100 text-red-800 hover:bg-red-100">Urgente</Badge>
    } else if (dias <= 7) {
      return <Badge className="bg-orange-100 text-orange-800 hover:bg-orange-100">Próximo</Badge>
    } else {
      return <Badge className="bg-green-100 text-green-800 hover:bg-green-100">Al día</Badge>
    }
  }

  const calcularTotal = () => {
    const totalServicios = Object.values(servicios).reduce((sum, value) => sum + value, 0)
    return selectedAlquiler ? selectedAlquiler.montoMensual + totalServicios : 0
  }

  const handleServicioChange = (servicio: string, valor: string) => {
    setServicios((prev) => ({
      ...prev,
      [servicio]: Number.parseFloat(valor) || 0,
    }))
  }

  const generarRecibo = () => {
    const total = calcularTotal()
    alert(`Recibo generado para ${selectedAlquiler.inquilino}\nTotal: $${total.toLocaleString()}`)
    // Aquí se implementaría la lógica real de generación del recibo
  }

  if(loading){
    return(
      <div>
        <Loading text="Cargando contratos de alquiler"/>
      </div>
    )
  }



  return (
    <div className="min-h-screen bg-background">
      <main className="container mx-auto px-6 py-8 pt-30">
        <div className="mb-8 flex justify-between gap-3"> 
          <div> <Button variant="outline" onClick={() => window.history.back()}> 
            <ArrowLeft className="h-4 w-4 mr-2" /> Volver </Button> 
          </div> 
          <div className="flex items-center space-x-4"> 
            <Link href={"/contratos/nuevo"}> 
            <Button size="sm"> <FileText className="h-4 w-4 mr-2" /> Nuevo Contrato </Button> 
            </Link> 
          </div>

        </div> 
        
        {/* Stats Summary */} 
        <div className="grid grid-cols-2 md:grid-cols-4 gap-6 mb-8"> 
          <Card> 
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2"> 
            <CardTitle className="text-md md:text-md font-medium ">Contratos Activos</CardTitle> 
            <Calendar className="h-6 w-6 text-foreground" /> </CardHeader> 
            <CardContent className="flex flex-col items-center"> 
              <div className="text-3xl font-bold font-sans text-foreground">
                {contratosBD.length || "N/A"}
              </div> 
              <p className="text-sm text-muted-foreground">Vigentes actualmente</p> 
              </CardContent> 
            </Card> 
            <Card> 
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2"> 
                <CardTitle className="text-md md:text-md font-medium ">Contratos por vencer</CardTitle> 
                <AlertCircle className="h-6 w-6 text-orange-500" /> 
              </CardHeader> 
              <CardContent className="flex flex-col items-center"> 
                <div className="text-3xl font-bold font-sans text-orange-600">2</div> 
                <p className="text-sm text-muted-foreground">Vencen el mes que viene</p> 
              </CardContent> 
            </Card> 
            <Card> 
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2"> 
                <CardTitle className="text-md md:text-md font-medium ">Alquileres No Pagos</CardTitle> 
                <AlertCircle className="h-6 w-6 text-orange-500" /> </CardHeader> 
                <CardContent className="flex flex-col items-center"> 
                  <div className="text-3xl font-bold font-sans text-orange-600">4</div> 
                  <p className="text-sm text-muted-foreground">No pagaron antes del día 10</p> 
                </CardContent> 
              </Card> 
              <Card> 
                <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2"> 
                  <CardTitle className="text-md md:text-md font-medium ">Servicios No Pagos</CardTitle>                  
                  <AlertCircle className="h-6 w-6 text-orange-500" /> 
                </CardHeader> 
                <CardContent className="flex flex-col items-center"> 
                  <div className="text-3xl font-bold font-sans text-orange-600">4</div> 
                  <p className="text-sm text-muted-foreground">Pendientes de pagar</p> 
                </CardContent> 
              </Card> 
        </div> 
        {/* Alquileres List */} 
        <div className="space-y-6"> 
          <div className="flex justify-between mb-10">
            <div className="flex items-center justify-between"> 
              <h2 className="text-xl font-semibold font-sans">Contratos de Alquiler Activos</h2> 
            </div>
            <div className="flex items-center gap-2">
                  <p className="text-secondary">Filtro:</p>
                  {/* 🔽 Dropdown para elegir filtro */}
                  <DropdownMenu>
                      <DropdownMenuTrigger asChild>
                        <div className="flex hover:cursor-pointer">
                          {filtroContrato === "vigentes" && "Vigentes"}
                          {filtroContrato === "proximos-vencer" && "Próximos a Vencer"}
                          <ChevronDown/>
                        </div>
                      </DropdownMenuTrigger>
                      <DropdownMenuContent>
                      <DropdownMenuItem onClick={() => setFiltroContrato("vigentes")}>
                          Vigentes
                      </DropdownMenuItem>
                      <DropdownMenuItem onClick={() => setFiltroContrato("proximos-vencer")}>
                          Próximos a vencer
                      </DropdownMenuItem>
                      </DropdownMenuContent>
                  </DropdownMenu>
              </div>
          </div>  
          <div> {(contratosBD.length == 0) && ( <p className="text-lg text-secondary">No hay contratos activos actualmente</p> )} 
        </div>
        </div>

        <div className="grid gap-6">
          {contratosBD?.map((contrato) => {
            const isExpanded = expandedCard === contrato.id;
            return (
              <Card 
                key={contrato.id} 
                className="hover:shadow-lg transition-shadow cursor-pointer"
                onClick={() => toggleCard(contrato.id)}
              >
                {/* Header */}
                <CardHeader className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-[2fr_3fr_auto] gap-4 items-center">
                  {/* Dirección */}
                  <div className="flex items-center gap-2">
                    <InmuebleIcon tipoInmuebleString={contrato?.tipoInmueble} className="h-7 w-7" />
                    <CardTitle className="text-xl md:text-2xl font-bold">
                      <Link href={`/inmuebles/${contrato.inmuebleId}`} className="hover:text-primary">
                        {contrato.direccionInmueble}
                      </Link>
                    </CardTitle>
                  </div>

                  {/* Locador / Locatario */}
                  <div className="flex flex-col gap-5  md:flex-row">
                     <div className="flex items-center gap-1 text-sm md:text-lg">
                        <User className="h-5"/>
                        <p className="font-medium text-muted-foreground">Locador:</p>
                        <p className="font-medium">{contrato.apellidoPropietario}, {contrato.nombrePropietario} </p>
                      </div>
                    <div className="flex items-center gap-1 text-sm md:text-lg">
                      <User className="h-5"/>
                      <p className="font-medium text-muted-foreground">Locatario:</p>
                      <p className="font-medium">{contrato.apellidoInquilino}, {contrato.nombreInquilino} </p>
                    </div>
                  </div>
                  
                  {/* Estado */}
                  <div className="flex items-center justify-end sm:justify-end md:justify-end">
                    {getEstadoBadge(contrato.estadoContratoNombre)}
                  </div>
                </CardHeader>

                <Separator/>
                {/* Contenido expandible solo en móviles o cuando está expandida */}
                <CardContent 
                  className={`transition-max-height duration-300 overflow-hidden ${
                    isExpanded ? "max-h-[1000px]" : "max-h-0 md:max-h-full"
                  }`}
                >
                  <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4 text-md">
                    <div>
                      <p className="text-sm font-medium text-muted-foreground">Monto Alquiler</p>
                      <p className="font-bold text-green-600">Proximamente...</p>
                    </div>
                    <div>
                      <p className="text-md font-medium text-muted-foreground">Próximo Aumento</p>
                      <p className="text-lg font-bold text-orange-500">{contrato.fechaAumento || "No especifiado"}</p>
                    </div>
                    <div>
                      <p className="text-md font-medium text-muted-foreground">Vencimiento: </p>
                      <p className="font-bold text-red-500">{contrato.fechaFin}</p>
                    </div>
                  </div>

                  <div className="grid grid-cols-1 items-center justify-between pt-4 border-t gap-2 md:flex md:justify-between">
                    <div className="flex gap-2">
                      <Link href={`/contratos/${contrato.id}`}>
                        <Button variant="outline" size="sm">Ver Contrato</Button>
                      </Link>
                      <Button variant="outline" size="sm">Historial Pagos</Button>
                      
                    </div>
                    <div className="flex gap-2">  
                      <Button size="sm">Registrar Pago</Button>
                      <Link href={`/alquileres/${contrato.id}/generar-recibo`}>
                        <Button variant="outline" size="sm">
                          <Receipt className="h-4 w-4 mr-2" />
                          Generar Recibo
                        </Button>
                      </Link>
                    </div>
                  </div>
                </CardContent>
              </Card>
            )
          })}
        </div>
      </main>
    </div>
  )
}
