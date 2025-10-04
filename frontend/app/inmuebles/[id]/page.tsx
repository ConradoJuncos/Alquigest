"use client"
import InmuebleIcon from "@/components/inmueble-icon";
import Loading from "@/components/loading";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Inmueble } from "@/types/Inmueble";
import { Propietario } from "@/types/Propietario";
import BACKEND_URL from "@/utils/backendURL";
import { ESTADOS_INMUEBLE, TIPOS_INMUEBLES } from "@/utils/constantes";
import { fetchWithToken } from "@/utils/functions/auth-functions/fetchWithToken";
import { ArrowLeft, BuildingIcon, User } from "lucide-react";
import Link from "next/link";
import { useParams } from "next/navigation";
import { useEffect, useState } from "react";

export default function DetalleInmueble(){
    const params = useParams(); 
    const id = params.id as string;
    
    const [inmueble, setInmueble] = useState<Inmueble>({
        id: 1,
        propietarioId: 1,
        direccion: "",
        tipoInmuebleId: 1,
        tipo: "",
        estado: 1,
        superficie: 1,
        esAlquilado: true,
        esActivo: true
    });
    const [propietario, setPropietario] = useState<Propietario>()
    const [contratoActivo, setCotnratoActivo] = useState([])
    const [loading, setLoading] = useState(true);
    
    useEffect(() => {
        console.log("Ejecutando fetch de Inmueble...");

        fetchWithToken(`${BACKEND_URL}/inmuebles/${id}`)
            .then((data) => {
                console.log("Datos parseados del backend:", data);
                setInmueble(data);

            })
            .catch((err) => {
                console.error("Error al traer inmueble:", err);
            });
    }, [id]);

    useEffect(() => {
        console.log("Ejecutando fetch de propietario...");

        fetchWithToken(`${BACKEND_URL}/propietarios/${inmueble?.propietarioId}`)
            .then((data) => {
                console.log("Datos parseados del backend:", data);
                setPropietario(data);
                setLoading(false);
            })
            .catch((err) => {
                console.error("Error al traer propietario:", err);
                setLoading(false);
            });
    }, [inmueble]);

    useEffect(() => {
        console.log("Ejecutando fetch de contrato...");

        fetchWithToken(`${BACKEND_URL}/contratos/inmueble/${inmueble?.id}`)
            .then((data) => {
                console.log("Datos parseados del backend contratos:", data);
                setCotnratoActivo(data);
                setLoading(false);
            })
            .catch((err) => {
                console.error("Error al traer contrato vigente:", err);
                setLoading(false);
            });
    }, [inmueble]);

    if (loading) return(
            <div>
              <Loading text="Cargando datos del inmueble..." tituloHeader="Inmuebles"/>
            </div>
          )

    return(
        <div className="min-h-screen bg-background">
            <main className="container mx-auto px-6 py-8 pt-30">
                {/* Page Title */}
                <div className="mb-8 flex flex-col gap-3">
                        <Button variant="outline" onClick={() => window.history.back()} className="w-fit">
                        <ArrowLeft className="h-4 w-4 mr-2" />
                            Volver
                        </Button>
                    <div className="flex items-center m-5">
                            <InmuebleIcon tipoInmuebleId={inmueble?.tipoInmuebleId} className="h-15 w-15 mr-2" />
                        <div className="">
                            <h2 className="text-3xl font-bold text-foreground font-sans">{inmueble?.direccion}</h2>
                            <p className="text-muted-foreground">{TIPOS_INMUEBLES[(inmueble?.tipoInmuebleId)-1].nombre || "Inmueble"}</p>
                        </div>
                    </div>
                </div>
                <div>
                    <Card className="max-w-4xl mx-auto mb-10">
                        <CardHeader >
                            <div className="flex items-center gap-2">
                                <BuildingIcon className="h-5 w-5"/>
                                <CardTitle className="font-bold">Datos del Inmueble</CardTitle>
                            </div>
                        </CardHeader>

                        <CardContent>
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 justify-between">
                                <div className="flex gap-3">
                                    <h2 className="font-bold">Dirección:</h2>
                                    <p className="text-muted-foreground">{inmueble?.direccion}</p>
                                </div>
                                <div className="flex gap-3">
                                    <h2 className="font-bold">Tipo de Inmueble:</h2>
                                    <p className="text-muted-foreground">{TIPOS_INMUEBLES[(inmueble?.tipoInmuebleId)-1].nombre || "No disponible"}</p>
                                </div>
                                <div className="flex gap-3">
                                    <h2 className="font-bold">Estado:</h2>
                                    <p className="text-muted-foreground">{ESTADOS_INMUEBLE[inmueble?.estado-1].nombre || "No disponible"}</p>
                                </div>
                                <div className="flex gap-3">
                                    <h2 className="font-bold">Superficie:</h2>
                                    <p className="text-muted-foreground">{inmueble?.superficie ? `${inmueble.superficie} m²` : "No especificada"}</p>
                                </div>
                            </div>
                        </CardContent>
                    </Card>


                    <Card className="max-w-4xl mx-auto">
                        <CardHeader >
                            <div className="flex items-center gap-2">
                                <User className="h-5 w-5 text-muted-foreground"/>
                                <CardTitle className="font-bold flex gap-2 text-xl">
                                    <Link href={`/propietarios/${propietario?.id}`} className="flex gap-2 hover:text-primary">
                                        <p className="text-muted-foreground">Propietario:</p>
                                        <p>{propietario?.apellido}, {propietario?.nombre}</p>
                                    </Link>
                                </CardTitle>
                            </div>
                        </CardHeader>

                        <CardContent>
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 justify-between">
                                <div className="flex gap-3">
                                    <h2 className="font-bold">DNI:</h2>
                                    <p className="text-muted-foreground">{propietario?.dni}</p>
                                </div>
                                <div className="flex gap-3">
                                    <h2 className="font-bold">Telefono:</h2>
                                    <p className="text-muted-foreground">{propietario?.telefono}</p>
                                </div>
                                <div className="flex gap-3">
                                    <h2 className="font-bold">Email:</h2>
                                    <p className="text-muted-foreground">{propietario?.email}</p>
                                </div>
                                <div className="flex gap-3">
                                    <h2 className="font-bold">Dirección:</h2>
                                    <p className="text-muted-foreground">{propietario?.direccion}</p>
                                </div>
                            </div>
                        </CardContent>
                    </Card>

                    <Card className="max-w-4xl mx-auto mt-10">
                        <CardHeader >
                            <div className="flex items-center gap-2">
                                <User className="h-5 w-5 text-muted-foreground"/>
                                <CardTitle className="font-bold flex gap-2 text-xl">
                                    <Link href={`/propietarios/${propietario?.id}`} className="flex gap-2 hover:text-primary">
                                        <p className="text-muted-foreground">Contrato de Alquiler:</p>
                                    </Link>
                                </CardTitle>
                            </div>
                        </CardHeader>

                        <CardContent>
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 justify-between">
                                {contratoActivo.length === 0 && (
                                    <div>
                                        <p>El inmueble no se encuentra en un contrato de alquiler vigente</p>
                                    </div>
                            )}

                            {contratoActivo.length !== 0 && (
                                    <div className="flex flex-col w-full">
                                        <p>El inmueble se encuentra en un contrato vigente</p>
                                        <div className="flex gap-3">
                                            <h2 className="font-bold">Locatario:</h2>
                                            <p className="text-muted-foreground">{contratoActivo[0].apellidoInquilino}, {contratoActivo[0].nombreInquilino}</p>
                                        </div>
                                        <div className="flex gap-3">
                                            <h2 className="font-bold">Fecha de Inicio:</h2>
                                            <p className="text-muted-foreground">{contratoActivo[0].fechaInicio}</p>
                                        </div>
                                        <div className="flex gap-3">
                                            <h2 className="font-bold">Fecha Finalización:</h2>
                                            <p className="text-muted-foreground">{contratoActivo[0].fechaFin}</p>
                                        </div>
                                        <Link href={`/contratos/${contratoActivo[0].id}`} className="mt-2">
                                            <Button variant={"outline"}>
                                                Ver detalles
                                            </Button>
                                        </Link>
                                    </div>
                            )}
                            </div>
                        </CardContent>
                    </Card>
                </div>
            </main>
        </div>
    )
}