import { useEffect, useMemo, useState } from "react";
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
  formatMontoVisual: (v: number) => string;
}

const SERVICIOS_BASE: ServicioContrato[] = [
  { tipoServicio: 1, nroCuenta: null, nroContrato: null, esDeInquilino: true, esActivo: false, esAnual: false }, // Agua
  { tipoServicio: 2, nroCuenta: null, nroContrato: null, esDeInquilino: true, esActivo: false, esAnual: false }, // Luz
  { tipoServicio: 3, nroCuenta: null, nroContrato: null, esDeInquilino: true, esActivo: false, esAnual: false }, // Gas
  { tipoServicio: 4, nroCuenta: null, nroContrato: null, esDeInquilino: true, esActivo: false, esAnual: true },  // Municipal (suele ser anual)
  { tipoServicio: 5, nroCuenta: null, nroContrato: null, esDeInquilino: true, esActivo: false, esAnual: true },  // Rentas (suele ser anual)
];

export default function Paso4CargaServicios({ formData, datosAdicionales, formatMontoVisual }: Paso5Props) {
  // Estado local de servicios (sin persistencia aún)
  const [servicios, setServicios] = useState<ServicioContrato[]>(SERVICIOS_BASE);

  // Mapa de clases de borde para Tailwind (usar literales completas para que JIT no las purgue)


  // Helper para actualizar un servicio por tipo
  const updateServicio = (tipoServicio: number, patch: Partial<ServicioContrato>) => {
    setServicios((prev) =>
      prev.map((s) => (s.tipoServicio === tipoServicio ? { ...s, ...patch } : s))
    );
  };

  // Render de una card de servicio
  const ServicioCard = ({ s }: { s: ServicioContrato }) => {
    const expanded = s.esActivo;
    return (
      <Card 
        key={s.tipoServicio} 
        onClick={() => updateServicio(s.tipoServicio, { esActivo: !expanded })} 
        className={`${BORDER_HOVER_CLASSES[s.tipoServicio]} border-muted transition-all duration-200 hover:shadow-lg hover:cursor-pointer`}>

        <CardHeader className="flex flex-row items-center justify-between">
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

      <div className="grid gap-2">
        {servicios.map((s) => (
          <ServicioCard key={s.tipoServicio} s={s} />
        ))}
      </div>
    </>
  );
}