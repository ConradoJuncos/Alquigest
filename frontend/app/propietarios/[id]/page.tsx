'use client'
import HeaderAlquigest from "@/components/header";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Inmueble } from "@/types/Inmueble";
import { Propietario } from "@/types/Propietario";
import BACKEND_URL from "@/utils/backendURL";
import { ArrowLeft, Contact, User } from "lucide-react";
import Link from "next/link"
import { useParams } from "next/navigation";
import { useEffect, useState } from "react";
import { Label } from "recharts";

export default function PropietarioDetalles() {
    const params = useParams(); 
    const id = params.id as string; 
    const [propietario, setPropietario] = useState<Propietario | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        console.log("Ejecutando fetch de propietario...");

        fetch(`${BACKEND_URL}/propietarios/${id}`)
            .then((res) => {
            console.log("Respuesta recibida del backend:", res);
            if (!res.ok) {
                throw new Error(`Error HTTP: ${res.status}`);
            }
            return res.json();
            })
            .then((data) => {
            console.log("Datos parseados del backend:", data);
            setPropietario(data);
            setLoading(false);
            })
            .catch((err) => {
            console.error("Error al traer propietarios:", err);
            setLoading(false);
            });
    }, []);


    // PARA VER SUS INMUEBLES
    const [susInmuebles, setSusInmuebles] = useState<Inmueble[]>([]);
    useEffect(() => {
        console.log("Ejecutando fetch de propietario...");

        fetch(`${BACKEND_URL}/inmuebles/propietario/${id}`)
            .then((res) => {
            console.log("Respuesta recibida del backend:", res);
            if (!res.ok) {
                throw new Error(`Error HTTP: ${res.status}`);
            }
            return res.json();
            })
            .then((data) => {
            console.log("Datos parseados del backend:", data);
            setSusInmuebles(data);
            setLoading(false);
            })
            .catch((err) => {
            console.error("Error al traer propietarios:", err);
            setLoading(false);
            });
    }, []);
        
    if (loading) return <p>Cargando datos del propietario {id}...</p>;

    return(
        <div className="min-h-screen bg-background">
            <div>
                <HeaderAlquigest tituloPagina="Propietarios"/>
            </div>

            <main className="container mx-auto px-6 py-8 pt-30">
                {/* Page Title */}
                <div className="mb-8 flex flex-col gap-3">
                    <Link href="/propietarios">
                        <Button variant="outline">
                        <ArrowLeft className="h-4 w-4 mr-2" />
                            Volver
                        </Button>
                    </Link>
                    <div className="flex items-center m-5">
                            <User className="h-15 w-15 mr-2 text-yellow-700" />
                        <div className="">
                            <h2 className="text-3xl font-bold text-foreground font-sans">{propietario?.nombre} {propietario?.apellido}</h2>
                            <p className="text-muted-foreground font-serif">Propietario</p>
                        </div>
                    </div>
                </div>

                <Card className="max-w-4xl mx-auto">
                    <CardHeader>
                        <CardTitle className="font-bold">Datos Personales</CardTitle>
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
                    <CardHeader>
                    <CardTitle className="font-bold">Sus inmuebles</CardTitle>
                    </CardHeader>

                    <CardContent>
                        {susInmuebles.length === 0 ? (
                            <p className="text-muted-foreground">Este propietario no tiene inmuebles registrados.</p>
                        ) : (
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                            {susInmuebles.map((inmueble) => (
                                <Card key={inmueble.id} className="p-5 gap-2">
                                <h3 className="font-bold">Inmueble #{inmueble.id}</h3>
                                <p>Estado: {inmueble.estado}</p>
                                <p><span className="font-semibold">Dirección:</span> {inmueble.direccion}</p>
                                <p><span className="font-semibold">Tipo:</span> {inmueble.tipoInmuebleId}</p>
                                <p><span className="font-semibold">Superficie:</span> {inmueble.superficie} m²</p>

                                <Link href={`/inmuebles/${inmueble.id}`}>
                                    <Button variant="outline" size="sm" className="mt-2">
                                    Ver detalles
                                    </Button>
                                </Link>
                                </Card>
                            ))}
                            </div>
                        )}
                    </CardContent>

                </Card>

            </main>

        
        </div>

        )

}