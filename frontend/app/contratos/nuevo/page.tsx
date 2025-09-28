"use client";

import { useEffect, useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { ArrowLeft, BuildingIcon, Calendar1Icon, Receipt, Save, User } from "lucide-react";
import Link from "next/link";
import ModalError from "@/components/modal-error";
import ModalDefault from "@/components/modal-default";
import { fetchWithToken } from "@/utils/functions/auth-functions/fetchWithToken";
import BACKEND_URL from "@/utils/backendURL";
import { Separator } from "@/components/ui/separator";

export default function NuevoContratoPage() {
  const [contratoCargado, setContratoCargado] = useState(false);
  const [errorCarga, setErrorCarga] = useState("");
  const [mostrarError, setMostrarError] = useState(false);

  const [inmueblesDisponibles, setInmueblesDisponibles] = useState([]);
  const [propietarios, setPropietarios] = useState([]);
  const [inquilinosDisponibles, setInquilinosDisponibles] = useState([]);

    const [formData, setFormData] = useState({
        id: 0,
        inmuebleId: 0,
        inquilinoId: 0,
        fechaInicio: "string",
        fechaFin: "string",
        monto: 0,
        porcentajeAumento: 0,
        estadoContratoId: 1,
        aumentaConIcl: true,
        pdfPath: "string",
        direccionInmueble: "string",
        nombreInquilino: "string",
        apellidoInquilino: "string",
        nombrePropietario: "string",
        apellidoPropietario: "string",
        estadoContratoNombre: ""
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
      .catch((err) => console.error("Error al traer inmuebles disponibles:", err));
  }, []);

  // Traer propietarios activos
  useEffect(() => {
    fetchWithToken(`${BACKEND_URL}/propietarios`)
      .then((data) => setPropietarios(data))
      .catch((err) => console.error("Error al traer propietarios:", err));
  }, []);

  // Traer inquilinos disponibles
  useEffect(() => {
    fetchWithToken(`${BACKEND_URL}/inquilinos`)
      .then((data) => setInquilinosDisponibles(data))
      .catch((err) => console.error("Error al traer inquilinos disponibles:", err));
  }, []);

  const handleInputChange = (field: string, value: string) => {
    setFormData((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  const handleNewContrato = async () => {
    try {
      const createdContrato = await fetchWithToken(`${BACKEND_URL}/contratos`, {
        method: "POST",
        body: JSON.stringify(formData),
      });
      console.log("Contrato creado con éxito:", createdContrato);

      setContratoCargado(true);

      // Limpiar el formulario
      setFormData({
        id: 0,
        inmuebleId: 0,
        inquilinoId: 0,
        fechaInicio: "string",
        fechaFin: "string",
        monto: 0,
        porcentajeAumento: 0,
        estadoContratoId: 1,
        aumentaConIcl: true,
        pdfPath: "string",
        direccionInmueble: "string",
        nombreInquilino: "string",
        apellidoInquilino: "string",
        nombrePropietario: "string",
        apellidoPropietario: "string",
        estadoContratoNombre: ""
      });
    } catch (error: any) {
      console.error("Error al crear contrato:", error);
      setErrorCarga(error.message || "No se pudo conectar con el servidor");
      setMostrarError(true);
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    if (
      !formData.inmuebleId ||
      !formData.inquilinoId ||
      !formData.fechaInicio ||
      !formData.fechaFin ||
      !formData.monto
    ) {
      setErrorCarga("Por favor, complete todos los campos obligatorios.");
      setMostrarError(true);
      return;
    }

    handleNewContrato();
  };

  return (
    <div className="min-h-screen bg-background">
      <main className="container mx-auto px-6 py-8 pt-30">
        <div className="mb-8 flex flex-col gap-3">
          <Link href="/contratos">
            <Button variant="outline">
              <ArrowLeft className="h-4 w-4 mr-2" />
              Volver
            </Button>
          </Link>
          <div>
            <h2 className="text-3xl font-bold text-foreground mb-2">Registrar Nuevo Contrato</h2>
          </div>
        </div>

        <Card className="max-w-4xl mx-auto">
          <CardHeader>
            <CardTitle className="font-sans">Complete los datos del Contrato a registrar</CardTitle>
          </CardHeader>
          <CardContent>
                <form onSubmit={handleSubmit} className="space-y-6">
                    {/* Datos del Inmueble e Inquilino */}
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    {/* Columna: Datos del Inmueble */}
                    <div className="space-y-4">
                        <div className="space-y-2">
                        <div>
                            <div className="flex items-center gap-2 mb-4">
                                <BuildingIcon className="h-5 w-5"/>
                                <span className="">Datos del Inmueble</span>
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="inmueble">Inmueble a Alquilar *</Label>
                                <Select
                                    required
                                    value={formData.inmuebleId}
                                    onValueChange={(value) => {
                                    const selectedInmueble = inmueblesDisponibles.find((inmueble) => inmueble.id.toString() === value);
                                    const propietario = propietarios.find((p) => p.id === selectedInmueble?.propietarioId);

                                    handleInputChange("inmuebleId", value);
                                    handleInputChange("direccionInmueble", selectedInmueble?.direccion || "");
                                    handleInputChange("nombrePropietario", propietario?.nombre || "");
                                    handleInputChange("apellidoPropietario", propietario?.apellido || "");
                                    // Actualizar datos adicionales del inquilino
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
                                        <p className="font-bold">{inmueble.direccion}</p>
                                        </SelectItem>
                                    ))}
                                    </SelectContent>
                                </Select>
                            </div>
                            </div>
                            <div className="space-y-2 w-fit">
                                <Label>Superficie del Inmueble (m²)</Label>
                                <Input value={`${datosAdicionales.superficieInmueble}`} readOnly />
                            </div>
                        </div>
                        

                        <div className="mt-6 space-y-2">
                            <div className="flex items-center gap-2">
                                <User className="h-5 w-5"/>
                                <span className="">Datos del Propietario/Locador</span>
                            </div>
                            
                            <div className="space-y-2 mt-4 w-fit">
                                <Label>Nombre y apellido</Label>
                                <Input value={`${formData.nombrePropietario} ${formData.apellidoPropietario}`} readOnly />
                            </div>
                            <div className="space-y-2 w-fit">
                                <Label>DNI del Locador</Label>
                                <Input value={`${datosAdicionales.dniPropietario}`} readOnly />
                            </div>
                        </div>
                        

                    </div>

                   

                    {/* Columna: Datos del Inquilino */}
                    <div className="space-y-4">
                        <div className="flex items-center gap-2">
                            <User className="h-5 w-5"/>
                            <span className="">Datos del Locatario</span>
                        </div>
                        <div className="space-y-2">
                        <Label htmlFor="inquilino">Locatario *</Label>
                        <Select
                            required
                            value={formData.inquilinoId}
                            onValueChange={(value) => {
                            const selectedInquilino = inquilinosDisponibles.find((inquilino) => inquilino.id.toString() === value);
                            handleInputChange("inquilinoId", value);
                            handleInputChange("nombreInquilino", selectedInquilino?.nombre || "");
                            handleInputChange("apellidoInquilino", selectedInquilino?.apellido || "");

                            // Actualizar datos adicionales del inquilino
                            setDatosAdicionales((prev) => ({
                            ...prev,
                            cuilInquilino: selectedInquilino?.cuil || "",
                            }));
                            }}
                        >
                            <SelectTrigger>
                            <SelectValue placeholder="Seleccione un locatario " />
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

                        <div className="space-y-2">
                        <Label>Nombre y Apellido</Label>
                        <Input value={`${formData.nombreInquilino} ${formData.apellidoInquilino}`} readOnly />
                        </div>
                        <div className="space-y-2">
                            <Label>CUIL Locatario</Label>
                            <Input value={`${datosAdicionales.cuilInquilino}`} readOnly />
                        </div>
                    </div>
                    </div>

                    <Separator aria-setsize={4}></Separator>

                    {/* Fechas*/}
                    <div>
                        <div className="flex items-center gap-2 mb-5">
                            <Calendar1Icon className="h-5 w-5"/>
                            <span className="">Fechas</span>
                        </div>
                            
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">

                            <div className="space-y-2">
                                <Label htmlFor="fechaInicio">Fecha de Inicio *</Label>
                                <Input
                                id="fechaInicio"
                                type="date"
                                value={formData.fechaInicio}
                                onChange={(e) => handleInputChange("fechaInicio", e.target.value)}
                                required
                                />
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="fechaFin">Fecha de Fin *</Label>
                                <Input
                                id="fechaFin"
                                type="date"
                                value={formData.fechaFin}
                                onChange={(e) => handleInputChange("fechaFin", e.target.value)}
                                required
                                />
                            </div>
                        </div>
                    </div>
                    
                    <Separator aria-setsize={4}></Separator>

                    {/*DATOS MONTOS ALQUILER */}
                    <div>
                        <div className="flex items-center gap-2 mb-5">
                            <Receipt className="h-5 w-5"/>
                            <span className="">Datos de Alquiler</span>
                        </div>
                        <div className="flex flex-col gap-3">
                            <div className="space-y-2">
                                <Label htmlFor="monto">Monto Inicial *</Label>
                                <Input
                                id="monto"
                                type="number"
                                min={0}
                                value={formData.monto}
                                onChange={(e) => handleInputChange("monto", e.target.value)}
                                required
                                />
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="tipoAumento">Tipo de Aumento *</Label>
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
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="porcentajeAumento">Porcentaje de Aumento</Label>
                                <Input
                                id="porcentajeAumento"
                                type="number"
                                value={formData.porcentajeAumento}
                                onChange={(e) => handleInputChange("porcentajeAumento", e.target.value)}
                                />
                            </div>
                        </div>
                    </div>

                    {/* Botones */}
                    <div className="flex gap-4 pt-6">
                    <Link href="/contratos" className="flex-1">
                        <Button type="button" variant="outline" className="w-full bg-transparent">
                        Cancelar
                        </Button>
                    </Link>
                    <Button type="submit" className="flex-1">
                        <Save className="h-4 w-4 mr-2" />
                        Registrar Contrato
                    </Button>
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