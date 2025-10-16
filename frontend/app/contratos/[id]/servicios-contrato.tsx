"use client";

import { fetchWithToken } from "@/utils/functions/auth-functions/fetchWithToken";
import BACKEND_URL from "@/utils/backendURL";
import { useState } from "react";
import { useParams } from "next/navigation";
import { Blocks } from "lucide-react";
import { useEffect } from "react";
import Loading from "@/components/loading";
import TipoServicioIcon from "@/components/tipoServicioIcon";

export default function ServiciosContratoPage({esVigente, idContrato}: {esVigente: boolean, idContrato: number}) {
    const [serviciosContrato, setServiciosContrato] = useState<any[]>([]);
    const [loading, setLoading] = useState(true);


        useEffect(() => {
        const fetchServiciosContrato = async () => {
            console.log("Ejecutando fetch de Servicios del Contrato...");
            try {
                const data = await fetchWithToken(`${BACKEND_URL}/servicios-contrato/contrato/${idContrato}`);
                console.log("Datos parseados de servicios:", data);
                setServiciosContrato(data);
            } catch (err: any) {
                console.error("Error al traer Servicios del Contrato:", err.message);
            } finally {
                setLoading(false);
            }
        };

        fetchServiciosContrato();
    }, []);

    if(loading){
        return(
            <div>
                <Loading text={`Cargando datos de servicios del contrato Nro. ${idContrato}...`}/>
            </div>
    )}

    return(
    <div className="w-full">
        {esVigente && 
            <div className="flex flex-col m-5 mt-20">
                <div className="flex items-center">
                            <Blocks className="h-10 w-10 mr-2 text-green-700" />
                        <div className="">
                            <h2 className="text-2xl font-bold text-foreground font-sans">Servicios Controlados</h2>
                        </div>
                </div>

                <div>
                    {serviciosContrato.length === 0 ? (
                        <p className="mt-5 text-lg">No hay servicios asociados a este contrato.</p>
                    ) : (
                        <div className="mt-5">
                            {serviciosContrato.map((servicio) => (
                                <div key={servicio.id} className="mb-4 p-4 border rounded-xl shadow-sm bg-card">
                                    <div className="flex gap-2 items-center mb-4">
                                        <TipoServicioIcon tipoServicio={servicio.tipoServicio.id} className="h-9 w-9" />
                                        <h3 className="text-xl font-semibold">{servicio.tipoServicio.nombre}</h3>
                                    </div>
                                    <div className="flex flex-col gap-2">
                                        <p><span className="font-semibold">Número de Cuenta:</span> {servicio.nroCuenta || "No asignado"}</p>
                                        <p><span className="font-semibold">Responsable del Pago:</span> {servicio.esDeInquilino ? "Inquilino" : "Estudio Jurídico"}</p>
                                        <p><span className="font-semibold">Activo:</span> {servicio.esActivo ? "Sí" : "No"}</p>
                                        <p><span className="font-semibold">Tipo de Cobro:</span> {servicio.esAnual ? "Anual" : "Mensual"}</p>
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}

                </div>
                
            </div>
                    
        }
    </div>
    )
}