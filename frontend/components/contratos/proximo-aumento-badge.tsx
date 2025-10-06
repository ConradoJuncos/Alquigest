'use client'

import { Badge } from "@/components/ui/badge"
import { Calendar } from "lucide-react"

interface ProximoAumentoBadgeProps {
  fechaAumento: string | null | undefined
}

export default function ProximoAumentoBadge({ fechaAumento }: ProximoAumentoBadgeProps) {
  if (!fechaAumento) return null

  const fechaActual = new Date()
  
  // Convertir fecha DD/MM/YYYY a formato que entiende JavaScript
  const convertirFecha = (fechaStr: string) => {
    const [dia, mes, año] = fechaStr.split('/')
    return new Date(parseInt(año), parseInt(mes) - 1, parseInt(dia))
  }
  
  const fechaAumentoDate = convertirFecha(fechaAumento)
  const diferenciaTiempo = fechaAumentoDate.getTime() - fechaActual.getTime() // Calcular la diferencia en milisegundos
  const diasDiferencia = Math.ceil(diferenciaTiempo / (1000 * 60 * 60 * 24)) // Convertir a días
  //const diasDiferencia = 13
  
  // Solo mostrar si es menor o igual a 30 días (aproximadamente 1 mes)
  if (diasDiferencia > 31 || diasDiferencia < 0) return null

  // Determinar el color del badge según los días restantes
  const getBadgeStyle = () => {
    if (diasDiferencia <= 7) {
      return "bg-red-500 text-white hover:bg-red-600 transition-colors"
    } else if (diasDiferencia <= 15) {
      return "bg-red-300 text-neutral-900 hover:bg-red-400 transition-colors"
    } else {
      return "bg-orange-300 text-red-950 hover:bg-orange-400 transition-colors"
    }
  }
  const getTexto = () => {
    if (diasDiferencia === 0) return "Aumenta HOY"
    if (diasDiferencia === 1) return "Aumenta mañana"
    if (diasDiferencia <= 7) return `Aumenta en ${diasDiferencia} días`
    return `Aumenta en ${diasDiferencia} días`
  }

  return (
    <Badge className={`${getBadgeStyle()} flex items-center gap-1`}>
      <Calendar className="h-3 w-3" />
      {getTexto()}
    </Badge>
  )
}