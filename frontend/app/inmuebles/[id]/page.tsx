"use client"
import Loading from "@/components/loading";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Inmueble } from "@/types/Inmueble";
import BACKEND_URL from "@/utils/backendURL";
import { ESTADOS_INMUEBLE, TIPOS_INMUEBLES } from "@/utils/constantes";
import { fetchWithToken } from "@/utils/functions/auth-functions/fetchWithToken";
import { ArrowLeft, BuildingIcon, User } from "lucide-react";
import { useParams } from "next/navigation";
import { useEffect, useState } from "react";

export default function DetalleInmueble(){
    const params = useParams(); 
    const id = params.id as string;
    
    const [inmueble, setInmueble] = useState<Inmueble | null>(null);
    const [loading, setLoading] = useState(true);
    
    useEffect(() => {
        console.log("Ejecutando fetch de Inmueble...");

        fetchWithToken(`${BACKEND_URL}/inmuebles/${id}`)
            .then((data) => {
                console.log("Datos parseados del backend:", data);
                setInmueble(data);
                setLoading(false);
            })
            .catch((err) => {
                console.error("Error al traer inmueble:", err);
                setLoading(false);
            });
    }, [id]);

    if (loading) return(
            <div>
              <Loading text="Cargando datos del propietario..." tituloHeader="Propietarios"/>
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
                            <BuildingIcon className="h-15 w-15 mr-2 text-yellow-700" />
                        <div className="">
                            <h2 className="text-3xl font-bold text-foreground font-sans">{inmueble?.direccion}</h2>
                        </div>
                    </div>
                </div>



                <Card className="max-w-4xl mx-auto">
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
                                <p className="text-muted-foreground">{TIPOS_INMUEBLES[inmueble?.tipoInmuebleId-1].nombre}</p>
                            </div>
                            <div className="flex gap-3">
                                <h2 className="font-bold">Estado:</h2>
                                <p className="text-muted-foreground">{ESTADOS_INMUEBLE[inmueble.estado-1].nombre}</p>
                            </div>
                            <div className="flex gap-3">
                                <h2 className="font-bold">Superficie:</h2>
                                <p className="text-muted-foreground">{inmueble.superficie ? `${inmueble.superficie} m²` : "No especificada"}</p>
                            </div>
                        </div>
                    </CardContent>
                </Card>
            </main>
        </div>
    )
}