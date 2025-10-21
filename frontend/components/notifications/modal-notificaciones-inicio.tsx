"use client"

import { useEffect, useState } from "react"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { AlertCircle, ArrowUpRightFromSquareIcon, Blocks, Calendar, CreditCard, FileText } from "lucide-react"
import { fetchWithToken } from "@/utils/functions/auth-functions/fetchWithToken"
import BACKEND_URL from "@/utils/backendURL"
import Link from "next/link"
import { Badge } from "../ui/badge"

interface ModalNotificacionesInicioProps {
  isOpen: boolean
  onClose: () => void
  setNotificationDot?: (show: boolean) => void
}

export default function ModalNotificacionesInicio({ isOpen, onClose, setNotificationDot }: ModalNotificacionesInicioProps) {
  const [loading, setLoading] = useState(true)
  const [serviciosPendientes, setServiciosPendientes] = useState(0)
  const [contratosProximosVencer, setContratosProximosVencer] = useState(0)

  useEffect(() => {
    const fetchNotificaciones = async () => {
      if (!isOpen) return
      setLoading(true)
      try {
        // Obtener cantidad de servicios pendientes
        const cantServicios = await fetchWithToken(`${BACKEND_URL}/pagos-servicios/count/pendientes`)
        setServiciosPendientes(cantServicios || 0)

        // Obtener cantidad de contratos próximos a vencer
        const cantContratos = await fetchWithToken(`${BACKEND_URL}/contratos/count/proximos-vencer`)
        setContratosProximosVencer(cantContratos || 0)

        // Mostrar punto de notificación si hay algo pendiente
        if (setNotificationDot) {
          setNotificationDot((cantServicios > 0) || (cantContratos > 0))
        }
      } catch (error) {
        console.error("Error al cargar notificaciones:", error)
      } finally {
        setLoading(false)
      }
    }
    fetchNotificaciones()
  }, [isOpen, setNotificationDot])

  const hayNotificaciones = serviciosPendientes > 0 || contratosProximosVencer > 0

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="max-w-md">
        <DialogHeader>
          <div className="flex items-center gap-2 mb-2">
            <AlertCircle className="h-6 w-6 text-orange-500" />
            <DialogTitle className="text-xl font-bold">Notificaciones Importantes</DialogTitle>
          </div>
          <DialogDescription>
            {hayNotificaciones
              ? "Hay tareas pendientes que requieren atención"
              : "No se requieren acciones relacionadas a Contratos y Pago de servicios."}
          </DialogDescription>
        </DialogHeader>

        {loading ? (
          <div className="flex items-center justify-center py-8">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
          </div>
        ) : hayNotificaciones ? (
          <div className="space-y-4 py-4">
            {/* Servicios Pendientes */}
            {serviciosPendientes > 0 && (
              <div className="p-4 rounded-lg border border-muted bg-orange-50 dark:bg-orange-950/10">
                <div className="flex items-start justify-between gap-3">
                  <div className="flex items-start gap-3">
                    <CreditCard className="h-5 w-5 text-orange-600 mt-0.5" />
                    <div className="flex-1 space-y-2">
                      <h3 className="font-semibold text-foreground">
                        Facturas Pendientes de Pago
                      </h3>
                      <p className="text-sm text-foreground">
                        Hay <Badge variant="outline" className="mx-1 bg-orange-200 text-orange-900">{serviciosPendientes}</Badge>
                        {serviciosPendientes === 1 ? 'servicio pendiente' : 'servicios pendientes'} de pagar
                      </p>
                    </div>
                  </div>
                </div>
                <Link href="/pago-servicios" onClick={onClose}>
                  <Button
                    className="w-full mt-3 bg-orange-600 hover:bg-orange-700 text-white"
                    size="sm"
                  >
                    <ArrowUpRightFromSquareIcon />
                    Ver Servicios
                  </Button>
                </Link>
              </div>
            )}

            {/* Contratos Próximos a Vencer */}
            {contratosProximosVencer > 0 && (
              <div className="p-4 rounded-lg border border-muted bg-red-50 dark:bg-red-950/20">
                <div className="flex items-start justify-between gap-3">
                  <div className="flex items-start gap-3">
                    <FileText className="h-5 w-5 text-red-600 mt-0.5" />
                    <div className="flex-1">
                      <h3 className="font-semibold text-red-900 dark:text-red-200">
                        Contratos Próximos a Vencer
                      </h3>
                      <p className="text-sm text-red-700 dark:text-red-300 mt-1">
                        Hay <Badge variant="outline" className="mx-1 bg-red-200 text-red-900">{contratosProximosVencer}</Badge>
                        {contratosProximosVencer === 1 ? 'contrato que vence' : 'contratos que vencen'} próximamente
                      </p>
                    </div>
                  </div>
                </div>
                <Link href="/alquileres" onClick={onClose}>
                  <Button
                    className="w-full mt-3 bg-red-600 hover:bg-red-700 text-white"
                    size="sm"
                  >
                    Ver Contratos
                  </Button>
                </Link>
              </div>
            )}
          </div>
        ) : (
          <div className="flex flex-col items-center justify-center py-8">
            <span className="text-muted-foreground text-center text-base">
              No se requieren acciones relacionadas a Contratos y Pago de servicios.
            </span>
          </div>
        )}

        <div className="flex justify-end pt-2 border-t">
          <Button variant="outline" onClick={onClose}>
            Cerrar
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  )
}
