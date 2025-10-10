// No local React state needed here; we rely on lifted state from the hook
import { DatosAdicionales } from "@/hooks/useNuevoContratoForm";
import { Contrato } from "@/types/Contrato";
import { BORDER_HOVER_CLASSES, ServicioContrato, TIPO_SERVICIO_LABEL } from "@/types/ServicioContrato";
import { Blocks } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Checkbox } from "@/components/ui/checkbox";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import TipoServicioIcon from "@/components/tipoServicioIcon";

interface Paso5Props {
  formData: Contrato;
  datosAdicionales: DatosAdicionales;
  serviciosContrato: ServicioContrato[];
  setServiciosContrato: (servicios: ServicioContrato[]) => void; // <-- AGREGA EL SETTER
  formatMontoVisual: (v: number) => string;
}

export default function Paso4CargaServicios({ formData, datosAdicionales, serviciosContrato  ,setServiciosContrato }: Paso5Props) {
  // Mapa de clases de borde para Tailwind (usar literales completas para que JIT no las purgue)


  // Helper para actualizar un servicio por tipo
  const updateServicio = (tipoServicio: number, patch: Partial<ServicioContrato>) => {
    setServiciosContrato(
      serviciosContrato.map((s) =>
        s.tipoServicio === tipoServicio ? { ...s, ...patch } : s
      )
    );
  };

  // Render de una card de servicio
  const ServicioCard = ({ s }: { s: ServicioContrato }) => {
    const expanded = s.esActivo;
    return (
      <Card 
        key={s.tipoServicio} 
        className={`${BORDER_HOVER_CLASSES[s.tipoServicio]} border-muted transition-all duration-200 hover:shadow-lg`}>

        <CardHeader
          className="flex flex-row items-center justify-between hover:cursor-pointer"
          onClick={() => updateServicio(s.tipoServicio, { esActivo: !expanded })}
        >
          <CardTitle className="text-base font-semibold flex items-center gap-2">
            <div className="flex items-center gap-3">
              <TipoServicioIcon tipoServicio={s.tipoServicio} className="h-8 w-8" />
              {TIPO_SERVICIO_LABEL[s.tipoServicio]}
            </div>
          </CardTitle>
          <div className="flex items-center">
            <Checkbox
                checked={s.esActivo}
                onCheckedChange={(v) => updateServicio(s.tipoServicio, { esActivo: Boolean(v) })}
                onClick={(e) => e.stopPropagation()}
                className="mr-2 transition-all"
              />
            <div className="text-xs text-muted-foreground">
              {expanded ? "Con control" : "No controlado"}
            </div>
            </div>
        </CardHeader>
        {expanded && (
          <CardContent className="grid gap-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor={`nroCuenta-${s.tipoServicio}`}>Nro. de Cuenta</Label>
                <Input
                  id={`nroCuenta-${s.tipoServicio}`}
                  type="text"
                  inputMode="numeric"
                  value={s.nroCuenta ?? ""}
                  onChange={(e) => {
                    const onlyDigits = e.target.value.replace(/\D/g, "");
                    updateServicio(s.tipoServicio, { nroCuenta: onlyDigits ? Number(onlyDigits) : null });
                  }}
                  placeholder="Ej: 123456789"
                />
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div className="space-y-2">
                <Label>¿Quién paga?</Label>
                <Select
                  value={s.esDeInquilino ? "inquilino" : "estudio"}
                  onValueChange={(v) => updateServicio(s.tipoServicio, { esDeInquilino: v === "inquilino" })}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Seleccionar responsable" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="inquilino">Inquilino</SelectItem>
                    <SelectItem value="estudio">Estudio jurídico</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div className="space-y-2 flex-col">
                <Label>Periodicidad</Label>
                <Select
                  value={s.esAnual ? "anual" : "bimestral"}
                  onValueChange={(v) => updateServicio(s.tipoServicio, { esAnual: v === "anual" })}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Seleccionar periodicidad" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="anual">Anual</SelectItem>
                    <SelectItem value="bimestral">Mensual/Bimensual</SelectItem>
                  </SelectContent>
                </Select>
              </div>

            </div>
          </CardContent>
        )}
      </Card>
    );
  };

  return (
    <>
      <div className="flex items-center gap-2 mb-4">
        <Blocks className="h-5 w-5" />
        <span className="font-semibold">Servicios del inmueble</span>
      </div>
      <p>Seleccione los servicios que serán controlados</p>
      <p className="text-sm text-muted-foreground">EN DESARROLLO...</p>

      <div className="grid gap-2">
        {serviciosContrato.map((s) => (
          <ServicioCard key={s.tipoServicio} s={s} />
        ))}
      </div>
    </>
  );
}