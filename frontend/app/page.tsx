import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import {
  Building2,
  Users,
  Home,
  FileText,
  BarChart3,
  Settings,
  CreditCard,
  Calendar,
  AlertCircle,
  CheckCircle2,
} from "lucide-react"
import Link from "next/link"
import HeaderAlquigest from "@/components/header"

export default function HomePage() {
  return (
    <div className="min-h-screen bg-background">
      {/* Header */}
      <HeaderAlquigest tituloPagina="Inicio" />

      {/* Main Content */}
      <main className="container mx-auto px-6 py-8 pt-30">
        {/* Welcome Section */}
        <div className="mb-16 flex justify-between gap-6">
          <div>
            <h2 className="text-3xl font-bold text-foreground mb-2 ">¡Bienvenido!</h2>
            <p className="text-muted-foreground font-sans text-xs md:text-lg">
              Gestione propiedades, propietarios e inquilinos desde un solo lugar.
            </p>
          </div>
           <div className="flex  items-center space-x-4">
              <Button size="sm">
                <FileText className="h-4 w-4 mr-2" />
                Nuevo Contrato
              </Button>
            </div>
        </div>

        {/* Cards DATOS ACTUALES */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-5 mb-10">
          <Card>
            <CardHeader className="flex flex-row items-center justify-between">
              <CardTitle className="text-md md:text-lg font-medium ">Facturas Pendientes</CardTitle>
              <AlertCircle className="h-6 w-6 text-orange-500" />
            </CardHeader>
            <CardContent className="flex flex-col items-center">
                <div className="text-3xl font-bold font-sans text-orange-600">999</div>
                <p className="text-sm text-muted-foreground">Pagos por vencer</p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="flex flex-row items-center justify-between">
              <CardTitle className="text-md md:text-lg font-medium ">Alquileres Activos</CardTitle>
              <CheckCircle2 className="h-6 w-6 text-green-500" />
            </CardHeader>
            <CardContent className="flex flex-col items-center">
              <div className="text-3xl font-bold font-sans text-green-600">N/A</div>
              <p className="text-sm text-muted-foreground">Contratos vigentes</p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="flex flex-row items-center justify-between">
              <CardTitle className="text-md md:text-lg font-medium">Inmuebles Gestionados</CardTitle>
              <Building2 className="h-6 w-6 text-muted-foreground" />
            </CardHeader>
            <CardContent className="flex flex-col items-center">
              <div className="text-3xl font-bold font-sans">18</div>
              <p className="text-sm text-muted-foreground">Bajo administración</p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="flex flex-row items-center justify-between">
              <CardTitle className="text-md md:text-lg font-medium ">Ingresos Mensuales</CardTitle>
              <BarChart3 className="h-6 w-6 text-muted-foreground" />
            </CardHeader>
            <CardContent className="flex flex-col items-center">
              <div className="text-2xl font-bold font-sans">$4,523,100</div>
              <p className="text-sm text-muted-foreground">Por honorarios</p>
            </CardContent>
          </Card>
        </div>

        {/* Main Navigation Cards */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-6 gap-10">
          {/* Inmuebles Card - Most Important (2 columns) */}
          <Link href="/inmuebles" className="group lg:col-span-2">
            <Card className="h-full transition-all duration-200 hover:shadow-lg hover:scale-105 border-2 border-[var(--amarillo-alqui)]/20 hover:border-[var(--amarillo-alqui)]">
              <CardHeader className="text-center pb-4">
                <div className="mx-auto mb-4 p-4 bg-accent/10 rounded-full w-fit group-hover:bg-accent/20 transition-colors">
                  <Building2 className="h-12 w-12 text-accent" />
                </div>
                <CardTitle className="text-2xl md:text-3xl font-bold">Inmuebles</CardTitle>
                <CardDescription className="text-base">
                  Administra las propiedades gestionadas.
                </CardDescription>
              </CardHeader>
              <CardContent className="text-center">
                <Button className="w-full bg-[var(--amarillo-alqui)]/80 hover:bg-[var(--amarillo-alqui)] text-black">
                  Ir a Inmuebles
                </Button>
              </CardContent>
            </Card>
          </Link>

          {/* Pago de Servicios Card - Important (2 columns) */}
          <Link href="/pago-servicios" className="group lg:col-span-2">
            <Card className="h-full transition-all duration-200 hover:shadow-lg hover:scale-105 border-2 border-green-500/20 hover:border-green-500">
              <CardHeader className="text-center pb-4">
                <div className="mx-auto mb-4 p-4 bg-green-500/10 rounded-full w-fit group-hover:bg-green-500/20 transition-colors">
                  <CreditCard className="h-12 w-12 text-green-600" />
                </div>
                <CardTitle className="text-2xl md:text-3xl font-bold">Pago de Servicios</CardTitle>
                <CardDescription className="text-base">Gestiona los pagos de servicios de inmuebles</CardDescription>
              </CardHeader>
              <CardContent className="text-center">
                <Button className="w-full bg-green-600 hover:bg-green-700 text-white">Ir a Pagos</Button>
              </CardContent>
            </Card>
          </Link>

          {/* Alquileres Card - Important (2 columns) */}
          <Link href="/alquileres" className="group lg:col-span-2">
            <Card className="h-full transition-all duration-200 hover:shadow-lg hover:scale-105 border-2 border-[var(--amarillo-alqui)]/20 hover:border-[var(--amarillo-alqui)]">
              <CardHeader className="text-center pb-4">
                <div className="mx-auto mb-4 p-4 bg-[var(--amarillo-alqui)]/10 rounded-full w-fit group-hover:bg-[var(--amarillo-alqui)]/20 transition-colors">
                  <Calendar className="h-12 w-12 text-[var(--amarillo-alqui)]" />
                </div>
                <CardTitle className="text-2xl md:text-3xl font-bold">Alquileres</CardTitle>
                <CardDescription className="text-base">Administra contratos y pagos de alquileres</CardDescription>
              </CardHeader>
              <CardContent className="text-center">
                <Button className="w-full bg-[var(--amarillo-alqui)]/80 hover:bg-[var(--amarillo-alqui)] text-black">Ir a Alquileres</Button>
              </CardContent>
            </Card>
          </Link>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-10 mt-12">
          {/* Propietarios Card */}
          <Link href="/propietarios" className="group">
            <Card className="h-full transition-all duration-200 hover:shadow-lg hover:scale-105">
              <CardHeader className="text-center pb-4">
                <div className="mx-auto mb-4 p-4 bg-primary/10 rounded-full w-fit group-hover:bg-primary/20 transition-colors">
                  <Users className="h-12 w-12 text-primary" />
                </div>
                <CardTitle className="text-xl font-bold">Propietarios</CardTitle>
                <CardDescription className="text-base">Administra la información de los propietarios</CardDescription>
              </CardHeader>
              <CardContent className="text-center">
                <Button variant="outline" className="w-full bg-transparent">
                  Ver Propietarios
                </Button>
              </CardContent>
            </Card>
          </Link>

          {/* Inquilinos Card */}
          <Link href="/inquilinos" className="group">
            <Card className="h-full transition-all duration-200 hover:shadow-lg hover:scale-105">
              <CardHeader className="text-center pb-4">
                <div className="mx-auto mb-4 p-4 bg-secondary/10 rounded-full w-fit group-hover:bg-secondary/20 transition-colors">
                  <Home className="h-12 w-12 text-secondary" />
                </div>
                <CardTitle className="text-xl font-bold">Inquilinos</CardTitle>
                <CardDescription className="text-base">Administra la información de los inquilinos</CardDescription>
              </CardHeader>
              <CardContent className="text-center">
                <Button variant="outline" className="w-full bg-transparent">
                  Ver Inquilinos
                </Button>
              </CardContent>
            </Card>
          </Link>
        </div>

        {/* Quick Actions */}
        <div className="mt-8">
          <h3 className="text-lg font-semibold mb-4 font-sans">Acciones Rápidas</h3>
          <div className="flex flex-wrap gap-3">
            <Button variant="outline" size="sm">
              <Building2 className="h-4 w-4 mr-2" />
              Nuevo Inmueble
            </Button>
            <Button variant="outline" size="sm">
              <Users className="h-4 w-4 mr-2" />
              Nuevo Propietario
            </Button>
            <Button variant="outline" size="sm">
              <Home className="h-4 w-4 mr-2" />
              Nuevo Inquilino
            </Button>
            <Button variant="outline" size="sm">
              <FileText className="h-4 w-4 mr-2" />
              Generar Informe
            </Button>
          </div>
        </div>
      </main>
    </div>
  )
}
