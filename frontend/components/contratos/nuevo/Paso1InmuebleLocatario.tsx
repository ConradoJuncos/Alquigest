"use client";
import { Label } from '@/components/ui/label';
import { Input } from '@/components/ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Separator } from '@/components/ui/separator';
import { Button } from '@/components/ui/button';
import Link from 'next/link';
import NuevoInquilinoModal from '@/app/inquilinos/nuevoInquilinoModal';
import { BuildingIcon, User, Plus } from 'lucide-react';
import { TIPOS_INMUEBLES } from '@/utils/constantes';
import { DatosAdicionales } from '@/hooks/useNuevoContratoForm';
import { Contrato } from '@/types/Contrato';

interface Paso1Props {
  inmuebles: any[];
  propietarios: any[];
  inquilinos: any[];
  formData: Contrato;
  datosAdicionales: DatosAdicionales;
  onSelectInmueble: (inmueble: any, propietario: any) => void;
  onSelectInquilino: (inquilino: any) => void;
  onInquilinoCreado: (nuevo: any) => void;
}

export default function Paso1InmuebleLocatario({
  inmuebles,
  propietarios,
  inquilinos,
  formData,
  datosAdicionales,
  onSelectInmueble,
  onSelectInquilino,
  onInquilinoCreado,
}: Paso1Props) {
  return (
    <>
      <Separator aria-setsize={4} />
      <div className="flex items-center gap-2 mb-4">
        <BuildingIcon className="h-5 w-5" />
        <span className="font-semibold">Datos del Inmueble</span>
      </div>
      <div className="space-y-4">
        <Label>Inmueble a Alquilar *
          {(inmuebles.length === 0) && (<p className="text-red-500">Actualmente No hay inmuebles disponibles en el sistema</p>)}
        </Label>
        <div className="flex items-center gap-5">
          <Select
            required
            value={formData.inmuebleId === 0 ? '' : formData.inmuebleId.toString()}
            onValueChange={(value) => {
              const selectedInmueble = inmuebles.find(i => i.id.toString() === value);
              const propietario = propietarios.find(p => p.id === selectedInmueble?.propietarioId);
              onSelectInmueble(selectedInmueble, propietario);
            }}
          >
            <SelectTrigger>
              <SelectValue placeholder="Seleccionar inmueble" />
            </SelectTrigger>
            <SelectContent>
              {inmuebles.map(inmueble => (
                <SelectItem key={inmueble.id} value={inmueble.id.toString()}>{inmueble.direccion}</SelectItem>
              ))}
            </SelectContent>
          </Select>
          <div>
            <Link href={'/inmuebles/nuevo'}>
              <Button>
                <Plus />
                Nuevo
              </Button>
            </Link>
          </div>
        </div>
        <Label>Tipo de Inmueble</Label>
        <Input className="w-fit" value={TIPOS_INMUEBLES.find(t => t.id === datosAdicionales.tipoInmuebleId)?.nombre} readOnly />
        <Separator />
        <div className="flex items-center gap-2 mb-4 mt-6">
          <User className="h-5 w-5" />
          <span className="font-semibold">Datos del Locador</span>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="space-y-2">
            <Label>Nombre y Apellido</Label>
            <Input value={`${datosAdicionales.apellidoPropietario}, ${datosAdicionales.nombrePropietario}`} readOnly />
          </div>
          <div className="space-y-2">
            <Label>Cuil</Label>
            <Input value={datosAdicionales.dniPropietario} readOnly />
          </div>
        </div>
        <Separator />
        <div className="flex items-center gap-2 mb-4 mt-6">
          <User className="h-5 w-5" />
          <span className="font-semibold">Datos del Locatario</span>
        </div>
        <Label>Locatario *
          {(inquilinos.length === 0) && (<p className="text-red-500">Actualmente No hay Locatarios activos en el sistema</p>)}
        </Label>
        <div className="flex items-center gap-5">
          <div>
            <Select
              required
              value={formData.inquilinoId === 0 ? '' : formData.inquilinoId.toString()}
              onValueChange={(value) => {
                const selectedInquilino = inquilinos.find(i => i.id.toString() === value);
                onSelectInquilino(selectedInquilino);
              }}
            >
              <SelectTrigger>
                <SelectValue placeholder="Seleccione un locatario" />
              </SelectTrigger>
              <SelectContent>
                {inquilinos.map(inq => (
                  <SelectItem key={inq.id} value={inq.id.toString()}>
                    {inq.apellido}, {inq.nombre} | CUIL: {inq.cuil}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
          <NuevoInquilinoModal
            text="Nuevo"
            onInquilinoCreado={(nuevo) => {
              onInquilinoCreado(nuevo);
            }}
          />
        </div>
      </div>
    </>
  );
}
