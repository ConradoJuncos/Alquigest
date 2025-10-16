'use client'

import { Badge } from "@/components/ui/badge"
import { Calendar } from "lucide-react"

interface VencimientoBadgeProps {
  fechaFin: string | null | undefined
}

export default function VencimientoBadge({ fechaFin }: VencimientoBadgeProps) {
  if (!fechaFin) return null

  const fechaActual = new Date()
  
  // Convertir fecha DD/MM/YYYY a formato que entiende JavaScript
  const convertirFecha = (fechaStr: string) => {
    const [dia, mes, año] = fechaStr.split('/')
    return new Date(parseInt(año), parseInt(mes) - 1, parseInt(dia))
  }
  
  const fechaFinDate = convertirFecha(fechaFin)
  const diferenciaTiempo = fechaFinDate.getTime() - fechaActual.getTime() // Calcular la diferencia en milisegundos
  const diasDiferencia = Math.ceil(diferenciaTiempo / (1000 * 60 * 60 * 24)) // Convertir a días
  //const diasDiferencia = 13

  // Solo mostrar si es menor o igual a 60 días (aproximadamente 2 meses)
  if (diasDiferencia > 60 || diasDiferencia < 0) return null

  // Determinar el color del badge según los días restantes
  const getBadgeStyle = () => {
    if (diasDiferencia <= 15) {
      return "bg-red-500 text-white hover:bg-red-600 transition-colors"
    } else if (diasDiferencia <= 30) {
      return "bg-red-300 text-neutral-900 hover:bg-red-400 transition-colors"
    } else {
      return "bg-orange-300 text-red-950 hover:bg-orange-400 transition-colors"
    }
  }
  const getTexto = () => {
    if (diasDiferencia === 0) return "Finaliza HOY"
    if (diasDiferencia === 1) return "Finaliza mañana"
    if (diasDiferencia <= 7) return `Finaliza en ${diasDiferencia} días`
    return `Finaliza en ${diasDiferencia} días`
  }

  return (
    <Badge className={`${getBadgeStyle()} flex items-center gap-1`}>
      <Calendar className="h-3 w-3" />
      {getTexto()}
    </Badge>
  )
}