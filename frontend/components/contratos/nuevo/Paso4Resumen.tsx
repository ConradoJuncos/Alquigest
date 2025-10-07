"use client";
import { Separator } from '@/components/ui/separator';
import { BuildingIcon, User, Calendar1Icon, Receipt, ChartColumnIcon, Clock } from 'lucide-react';
import { Contrato } from '@/types/Contrato';
import { DatosAdicionales } from '@/hooks/useNuevoContratoForm';

interface Paso4Props {
  formData: Contrato;
  datosAdicionales: DatosAdicionales;
  formatMontoVisual: (v: number) => string;
}

export default function Paso4Resumen({ formData, datosAdicionales, formatMontoVisual }: Paso4Props) {
  return (
    <>
      <div className="flex items-center gap-2">
        <span className="font-semibold">Confirme los Datos</span>
      </div>
      <Separator className="my-4" />
      <div className="space-y-2">
        <div className="flex items-center gap-2">
          <BuildingIcon className="h-4 w-4" />
          <p><b>Inmueble:</b> {datosAdicionales.direccionInmueble}</p>
        </div>
        <div className="flex items-center gap-2">
          <User className="h-4 w-4" />
          <p><b>Locador:</b> {datosAdicionales.nombrePropietario} {datosAdicionales.apellidoPropietario}</p>
        </div>
        <div className="flex items-center gap-2">
          <User className="h-4 w-4" />
          <p><b>Locatario:</b> {datosAdicionales.nombreInquilino} {datosAdicionales.apellidoInquilino}</p>
        </div>
        <div className="flex items-center gap-2">
          <Calendar1Icon className="h-4 w-4" />
          <p><b>Fechas:</b> Desde: {formData.fechaInicio} - Hasta: {formData.fechaFin}</p>
        </div>
        <div className="flex items-center gap-2">
          <Receipt className="h-4 w-4" />
          <p><b>Monto Inicial de Alquiler:</b> $ {formatMontoVisual(formData.monto)}</p>
        </div>
        <div className="flex items-center gap-2">
          <ChartColumnIcon className="h-4 w-4" />
          <p><b>Aumento:</b> {formData.tipoAumento} ({formData.porcentajeAumento}%)</p>
        </div>
        <div className="flex items-center gap-2">
          <Clock className="h-4 w-4" />
          <p><b>Periodo de Aumento:</b> cada {formData.periodoAumento} meses</p>
        </div>
      </div>
    </>
  );
}
