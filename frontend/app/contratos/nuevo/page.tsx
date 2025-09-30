"use client";

import { useEffect, useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Save, ArrowLeft, BuildingIcon, User, Calendar1Icon, Receipt, UsersIcon, CheckSquareIcon, ChartColumnIcon, Clock, Percent, Plus } from "lucide-react";
import Link from "next/link";
import { Separator } from "@/components/ui/separator";
import ModalError from "@/components/modal-error";
import ModalDefault from "@/components/modal-default";
import { fetchWithToken } from "@/utils/functions/auth-functions/fetchWithToken";
import BACKEND_URL from "@/utils/backendURL";
import { Progress } from "@/components/ui/progress";
import NuevoInquilinoModal from "@/app/inquilinos/nuevoInquilinoModal";

export default function NuevoContratoPage() {
  const [step, setStep] = useState(1); // 👈 Paso actual
  const [contratoCargado, setContratoCargado] = useState(false);
  const [errorCarga, setErrorCarga] = useState("");
  const [mostrarError, setMostrarError] = useState(false);
  const [datosCompletos, setDatosCompletos] = useState(false)
  const [inmueblesDisponibles, setInmueblesDisponibles] = useState<any[]>([]);
  const [propietarios, setPropietarios] = useState<any[]>([]);
  const [inquilinosDisponibles, setInquilinosDisponibles] = useState<any[]>([]);

  const [formData, setFormData] = useState<any>({
    id: 0,
    inmuebleId: 0,
    inquilinoId: 0,
    fechaInicio: "",
    fechaFin: "",
    monto: 0,
    porcentajeAumento: 0,
    estadoContratoId: 1,
    aumentaConIcl: true,
    pdfPath: "",
    direccionInmueble: "",
    nombreInquilino: "",
    apellidoInquilino: "",
    nombrePropietario: "",
    apellidoPropietario: "",
    estadoContratoNombre: "",
    tipoAumento: "",
    periodoAumento: 0,
  });

  const [datosAdicionales, setDatosAdicionales] = useState({
    dniPropietario: "",
    cuilInquilino: "",
    superficieInmueble: "",
  });

  // Traer inmuebles disponibles
  useEffect(() => {
    fetchWithToken(`${BACKEND_URL}/inmuebles/disponibles`)
      .then((data) => setInmueblesDisponibles(data))
      .catch((err) => console.error("Error inmuebles:", err));
  }, []);

  // Traer propietarios activos
  useEffect(() => {
    fetchWithToken(`${BACKEND_URL}/propietarios`)
      .then((data) => setPropietarios(data))
      .catch((err) => console.error("Error propietarios:", err));
  }, []);

  // Traer inquilinos disponibles
  useEffect(() => {
    fetchWithToken(`${BACKEND_URL}/inquilinos`)
      .then((data) => setInquilinosDisponibles(data))
      .catch((err) => console.error("Error inquilinos:", err));
  }, []);

  const handleInputChange = (field: string, value: string) => {
    setFormData((prev: any) => ({ ...prev, [field]: value }));
  };

  const handleNewContrato = async () => {
    try {
      const createdContrato = await fetchWithToken(`${BACKEND_URL}/contratos`, {
        method: "POST",
        body: JSON.stringify(formData),
      });
      console.log("Contrato creado con éxito:", createdContrato);
      setContratoCargado(true);
    } catch (error: any) {
      console.error("Error al crear contrato:", error);
      setErrorCarga(error.message || "No se pudo conectar con el servidor");
      setMostrarError(true);
    }
  };
  const isStepValid = () => {
  switch (step) {
    case 1: // Validar datos del inmueble e inquilino
      return formData.inmuebleId !== 0 && formData.inquilinoId !== 0;
    case 2: // Validar fechas
      return formData.fechaInicio !== "" && formData.fechaFin !== "";
    case 3: // Validar datos del contrato
      return formData.monto > 0 && formData.tipoAumento !== "";
    default:
      return true;
  }
};

  // 👇 Render dinámico por pasos
  const renderStep = () => {
    switch (step) {
      case 1:
  return (
    <>

      <Separator aria-setsize={4}/>

      <div className="flex items-center gap-2 mb-4">
        <BuildingIcon className="h-5 w-5" />
        <span className="font-semibold">Datos del Inmueble</span>
      </div>

      <div className="space-y-4">
        <Label>Inmueble a Alquilar *</Label>
        <div className="flex items-center gap-5">
          <Select
            required
            value={formData.inmuebleId}
            onValueChange={(value) => {
              const selectedInmueble = inmueblesDisponibles.find(
                (inmueble) => inmueble.id.toString() === value
              );
              const propietario = propietarios.find(
                (p) => p.id === selectedInmueble?.propietarioId
              );

              handleInputChange("inmuebleId", value);
              handleInputChange("direccionInmueble", selectedInmueble?.direccion || "");
              handleInputChange("nombrePropietario", propietario?.nombre || "");
              handleInputChange("apellidoPropietario", propietario?.apellido || "");
              setDatosAdicionales((prev) => ({
                ...prev,
                superficieInmueble: selectedInmueble?.superficie || "No especificada",
                dniPropietario: propietario?.dni || "",
              }));
            }}
          >
            <SelectTrigger>
              <SelectValue placeholder="Seleccionar inmueble" />
            </SelectTrigger>
            <SelectContent>
              {inmueblesDisponibles.map((inmueble) => (
                <SelectItem key={inmueble.id} value={inmueble.id.toString()}>
                  {inmueble.direccion}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
          <div>
            <Link href={"/inmuebles/nuevo"}>
              <Button>
                <Plus/>
                Nuevo
              </Button>
            </Link>
          </div>
        </div>

        <Label>Superficie (m²)</Label>
        <Input className="w-fit" value={`${datosAdicionales.superficieInmueble}`} readOnly />

        <Separator />

        <div className="flex items-center gap-2 mb-4 mt-6">
          <User className="h-5 w-5" />
          <span className="font-semibold">Datos del Propietario</span>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="space-y-2">
            <Label>Nombre y Apellido</Label>
            <Input value={`${formData.nombrePropietario} ${formData.apellidoPropietario}`} readOnly />
          </div>
          <div className="space-y-2">
            <Label>DNI</Label>
            <Input value={datosAdicionales.dniPropietario} readOnly />
          </div>
        </div>

        <Separator />
        
            <div className="flex items-center gap-2 mb-4 mt-6">
              <User className="h-5 w-5" />
              <span className="font-semibold">Datos del Locatario</span>
            </div>
            <Label>Locatario *</Label>
          <div className="flex items-center gap-5">
            <div>
              
              <Select
                required
                value={formData.inquilinoId}
                onValueChange={(value) => {
                  const selectedInquilino = inquilinosDisponibles.find(
                    (inquilino) => inquilino.id.toString() === value
                  );
                  handleInputChange("inquilinoId", value);
                  handleInputChange("nombreInquilino", selectedInquilino?.nombre || "");
                  handleInputChange("apellidoInquilino", selectedInquilino?.apellido || "");
                  setDatosAdicionales((prev) => ({
                    ...prev,
                    cuilInquilino: selectedInquilino?.cuil || "",
                  }));
                }}
              >
                <SelectTrigger>
                  <SelectValue placeholder="Seleccione un locatario" />
                </SelectTrigger>
                <SelectContent>
                  {inquilinosDisponibles.map((inquilino) => (
                    <SelectItem key={inquilino.id} value={inquilino.id.toString()}>
                      {inquilino.nombre} {inquilino.apellido} | CUIL: {inquilino.cuil}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <NuevoInquilinoModal 
            text="Nuevo"
            onInquilinoCreado={(nuevo) => {
                        // agrego a la lista y selecciono el nuevo propietario automáticamente
                        setInquilinosDisponibles(prev => [...prev, nuevo]);
                        setFormData(prev => ({ ...prev, propietarioId: nuevo.id.toString() }));
                      }}/>
          </div>
      </div>
    </>
  );


      case 2:
        return (
          <>
            <div className="flex items-center gap-2 mb-4">
              <Calendar1Icon className="h-5 w-5" />
              <span className="font-semibold">Paso 2: Fechas</span>
            </div>
            <div className="space-y-4">
              <Label>Inicio del Contrato *</Label>
              <Input
                type="date"
                value={formData.fechaInicio}
                onChange={(e) => handleInputChange("fechaInicio", e.target.value)}
                required
              />
              <Label>Fin del Contrato *</Label>
              <Input
                type="date"
                value={formData.fechaFin}
                onChange={(e) => handleInputChange("fechaFin", e.target.value)}
                required
              />
              <Label>Periodo de Aumento (meses)</Label>
              <Input
                placeholder="Ingrese cada cuantos meses se actualizará el alquiler"
                type="number"
                min={0}
                max={12}
                value={formData.periodoAumento}
                onChange={(e) => handleInputChange("periodoAumento", e.target.value)}
              />
            </div>
          </>
        );

      case 3:
        return (
          <>
            <div className="flex items-center gap-2 mb-4">
              <Receipt className="h-5 w-5" />
              <span className="font-semibold">Paso 3: Datos de Alquiler</span>
            </div>
            <div className="space-y-4">
              <Label>Monto Inicial *</Label>
              <Input
                type="number"
                min={0}
                value={formData.monto}
                onChange={(e) => handleInputChange("monto", e.target.value)}
                required
              />
              <Label>Tipo de Aumento *</Label>
              <Select
                value={formData.tipoAumento}
                onValueChange={(value) => handleInputChange("tipoAumento", value)}
              >
                <SelectTrigger>
                  <SelectValue placeholder="Seleccionar tipo de aumento" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="Porcentaje fijo">Porcentaje fijo</SelectItem>
                  <SelectItem value="ICL">ICL</SelectItem>
                </SelectContent>
              </Select>
              <div className="flex items-center gap-2">
                <Percent className="h-4 w-4"/>
                <Label>Porcentaje de Aumento</Label>
              </div>
              <Input
                type="number"
                value={formData.porcentajeAumento}
                onChange={(e) => handleInputChange("porcentajeAumento", e.target.value)}
              />
            </div>
          </>
        );

      case 4:
        return (
          <>
            <div className="flex items-center gap-2">
              <CheckSquareIcon className="h-5 w-5"/>
              <span className="font-semibold">Confirme los Datos</span>
            </div>
            <Separator className="my-4" />
            <div className="space-y-2">

              <div className="flex items-center gap-2">
                <BuildingIcon className="h-4 w-4"/>
                <p><b>Inmueble:</b> {formData.direccionInmueble}</p>
              </div>

              <div className="flex items-center gap-2">
                <User className="h-4 w-4"/>
                <p><b>Locador:</b> {formData.nombrePropietario} {formData.apellidoPropietario}</p>
              </div>

              <div className="flex items-center gap-2">
                <User className="h-4 w-4"/>
                <p><b>Locatario:</b> {formData.nombreInquilino} {formData.apellidoInquilino}</p>
              </div>
              
              <div className="flex items-center gap-2">
                <Calendar1Icon className="h-4 w-4"/>
                <p><b>Fechas:</b> Desde: {formData.fechaInicio} - Hasta:  {formData.fechaFin}</p>
              </div>
              
              <div className="flex items-center gap-2">
                <Receipt className="h-4 w-4"/>
                <p><b>Monto Inicial:</b> ${formData.monto}</p>
              </div>


              <div className="flex items-center gap-2">
                <ChartColumnIcon className="h-4 w-4"/>
                <p><b>Aumento:</b> {formData.tipoAumento} ({formData.porcentajeAumento}%)</p>
              </div>
              <div className="flex items-center gap-2">
                <Clock className="h-4 w-4"/>
                <p><b>Periodo de Aumento:</b> cada {formData.periodoAumento} meses</p>
              </div>
            </div>
          </>
        );

      default:
        return null;
    }
  };

  return (
    <div className="min-h-screen bg-background pt-25">
      <main className="container mx-auto px-6 py-8">
        <div className="mb-8 flex flex-col gap-3">
          <Link href="/contratos">
            <Button variant="outline">
              <ArrowLeft className="h-4 w-4 mr-2" />
              Volver
            </Button>
          </Link>
          <div>
            <h2 className="text-3xl font-bold text-foreground">
              Registrar Nuevo Contrato
            </h2>
            <p>Complete el formulario con los datos solicitados</p>
          </div>
        </div>

        <Card className="max-w-3xl mx-auto">
          <CardHeader>
            <CardTitle>Paso {step} de 4</CardTitle>
            <Progress value={(step * 100)/4}></Progress>
          </CardHeader>
          <CardContent>
            <form onSubmit={(e) => e.preventDefault()} className="space-y-6">
              {renderStep()}

              {/* Botones de navegación */}
              <div className="flex justify-between pt-6">
                {step > 1 && (
                  <Button type="button" variant="outline" onClick={() => setStep(step - 1)}>
                    Anterior
                  </Button>
                )}
                {step < 4 ? (
                  <Button type="button" onClick={() => setStep(step + 1)} disabled={!isStepValid()}>
                    Siguiente
                  </Button>
                ) : (
                  <Button type="button" onClick={handleNewContrato}>
                    <Save className="h-4 w-4 mr-2" />
                    Confirmar y Registrar
                  </Button>
                )}
              </div>
            </form>
          </CardContent>
        </Card>
      </main>

      {mostrarError && (
        <ModalError
          titulo="Error al crear Contrato"
          mensaje={errorCarga}
          onClose={() => setMostrarError(false)}
        />
      )}
      {contratoCargado && (
        <ModalDefault
          titulo="Nuevo Contrato"
          mensaje="El contrato se ha creado correctamente."
          onClose={() => setContratoCargado(false)}
        />
      )}
    </div>
  );
}
