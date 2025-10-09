"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger, DialogFooter } from "@/components/ui/dialog";
import { Select, SelectTrigger, SelectValue, SelectContent, SelectItem } from "@/components/ui/select";
import { fetchWithToken } from "@/utils/functions/auth-functions/fetchWithToken";
import BACKEND_URL from "@/utils/backendURL";
import { Loader2, RefreshCcw } from "lucide-react";
import ModalDefault from "@/components/modal-default";

type Estado = { id: number; nombre: string };

const ESTADOS: Estado[] = [
  { id: 1, nombre: "Vigente" },
  { id: 2, nombre: "No Vigente" },
  { id: 3, nombre: "Cancelado" },
];

interface Props {
  contratoId: number;
  estadoActualId: number;
  onEstadoActualizado: (nuevoEstadoId: number) => void;
  disabled?: boolean;
}

export default function ChangeEstadoContrato({ contratoId, disabled , estadoActualId, onEstadoActualizado }: Props) {
  const [open, setOpen] = useState(false);
  const [nuevoEstadoId, setNuevoEstadoId] = useState<number>(estadoActualId);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [showConfirm, setShowConfirm] = useState(false);

  const estadoActual = ESTADOS.find(e => e.id === estadoActualId)?.nombre || "Desconocido";
  const cambioBloqueado = estadoActualId === 2 || estadoActualId === 3; // No Vigente o Cancelado

  const ejecutarCambio = async () => {
    if (!nuevoEstadoId || nuevoEstadoId === estadoActualId) {
      setOpen(false);
      return;
    }
    try {
      setLoading(true);
      setError("");
      await fetchWithToken(`${BACKEND_URL}/contratos/${contratoId}/estado`, {
        method: "PATCH",
        body: JSON.stringify({ estadoContratoId: nuevoEstadoId }),
      });
      onEstadoActualizado(nuevoEstadoId);
      setOpen(false);
    } catch (e: any) {
      setError(e.message || "Error al actualizar estado");
    } finally {
      setLoading(false);
    }
  };

  const handleGuardar = () => {
    // Mostrar confirmación antes de ejecutar el cambio
    if (!nuevoEstadoId || nuevoEstadoId === estadoActualId) {
      setOpen(false);
      return;
    }
    setShowConfirm(true);
  };

  const estadoNuevoNombre = ESTADOS.find(e => e.id === nuevoEstadoId)?.nombre || "(desconocido)";

  return (
    <>
      <Dialog open={open} onOpenChange={o => { if (cambioBloqueado) return; setOpen(o); if (o) { setNuevoEstadoId(estadoActualId); setError(""); } }}>
      <DialogTrigger asChild>
        <Button
            size="sm"
            className="gap-1"
            disabled={cambioBloqueado || disabled}
            title={cambioBloqueado ? "No se puede cambiar estado cuando el contrato está No Vigente o Cancelado" : "Cambiar estado"}
        >
          <RefreshCcw className="h-4 w-4" /> Cambiar Estado
        </Button>
      </DialogTrigger>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>Cambiar Estado del Contrato</DialogTitle>
          <p className="text-sm text-muted-foreground">Estado actual: <span className="font-medium">{estadoActual}</span></p>
        </DialogHeader>
        <div className="space-y-4 py-2">
          <div className="space-y-2">
            <label className="text-sm font-medium">Nuevo Estado</label>
            <Select value={String(nuevoEstadoId)} onValueChange={(v) => setNuevoEstadoId(Number(v))}>
              <SelectTrigger>
                <SelectValue placeholder="Seleccionar estado" />
              </SelectTrigger>
              <SelectContent>
                {ESTADOS.map(e => (
                  <SelectItem key={e.id} value={String(e.id)}>{e.nombre}</SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
          {error && <p className="text-sm text-red-500">{error}</p>}
        </div>
        <DialogFooter className="flex gap-2">
          <Button variant="outline" onClick={() => setOpen(false)} disabled={loading}>Cancelar</Button>
          <Button onClick={handleGuardar} disabled={loading || nuevoEstadoId === estadoActualId}>
            {loading && <Loader2 className="h-4 w-4 mr-2 animate-spin"/>}
            Guardar
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
      {showConfirm && (
          <ModalDefault
            titulo="Confirmar cambio de estado"
            mensaje={`¿Está seguro/a de cambiar el contrato a ${estadoNuevoNombre}?`}
            onClose={() => {
              setShowConfirm(false);
              ejecutarCambio();
            }}
          />
        )}
    </>
  );
}
