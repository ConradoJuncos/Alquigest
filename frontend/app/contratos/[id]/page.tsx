"use client"

import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { ArrowLeft, Blocks, Building, FileText, User } from "lucide-react";

const esVigente = true

export default function DetalleContratoPage(){
    return(
        <div className="min-h-screen bg-background">
            {/* Main Content */}
            <main className="container mx-auto px-6 py-8 pt-30">
                    <div className="mb-8 flex flex-col gap-3">
                        <Button variant="outline" onClick={() => window.history.back()} className="w-fit">
                        <ArrowLeft className="h-4 w-4 mr-2" />
                            Volver
                        </Button>
                    <div className="flex items-center m-5">
                            <FileText className="h-15 w-15 mr-2 text-yellow-700" />
                        <div className="">
                            <h2 className="text-2xl font-bold text-foreground font-sans">Contrato Nro. 721</h2>
                            <p className="text-xl font-medium font-sans text-secondary">Inmueble: Chacabuco 654</p>
                        </div>
                    </div>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                    {/*Card DATOS CONTRATO*/}
                    <Card className="max-w-4xl ">
                    <CardHeader >
                        <div className="flex items-center gap-2">
                            <FileText className="h-5 w-5"/>
                            <CardTitle className="font-bold">Datos del Contrato</CardTitle>
                        </div>
                    </CardHeader>

                    <CardContent>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 justify-between">
                            <div className="flex gap-3">
                                <h2 className="font-bold">Inicio:</h2>
                                <p className="text-gray-700">24/02/2023</p>
                            </div>
                            <div className="flex gap-3">
                                <h2 className="font-bold">Finalización:</h2>
                                <p className="text-orange-700 font-bold">24/02/2026</p>
                            </div>
                            <div className="flex gap-3">
                                <h2 className="font-bold">Próximo Aumento:</h2>
                                <p className="text-gray-700">24/10/2025</p>
                            </div>
                                                        <div className="flex gap-3">
                                <h2 className="font-bold">Peridos de Aumentos:</h2>
                                <p className="text-gray-700">Cada 4 Meses</p>
                            </div>

                            <div className="flex gap-3">
                                <h2 className="font-bold">Tipo de Aumento:</h2>
                                <p className="text-gray-700">ICL</p>
                            </div>
                            <div className="flex gap-3">
                                <h2 className="font-bold">% Aumento:</h2>
                                <p className="text-gray-700">No corresponde (ICL)</p>
                            </div>
                                                        <div className="flex gap-3">
                                <h2 className="font-bold">Monto Inicial:</h2>
                                <p className="text-gray-700">$250.000</p>
                            </div>
                        </div>
                    </CardContent>
                </Card>

                {/*Card DATOS INMUEBLE*/}
                <Card className="max-w-4xl">
                    <CardHeader >
                        <div className="flex items-center gap-2">
                            <Building className="h-5 w-5"/>
                            <CardTitle className="font-bold">Datos del Inmueble</CardTitle>
                        </div>
                    </CardHeader>

                    <CardContent>
                        <div className="grid grid-cols-1 gap-4 justify-between">
                            <div className="flex gap-3">
                                <h2 className="font-bold">Dirección:</h2>
                                <p className="text-gray-700 font-bold">Chacabuco 654</p>
                            </div>
                            <div className="flex gap-3">
                                <h2 className="font-bold">Tipo:</h2>
                                <p className="text-gray-700">Local Comercial</p>
                            </div>
                            <div className="flex gap-3">
                                <h2 className="font-bold">Superficie</h2>
                                <p className="text-gray-700">25 m2</p>
                            </div>
                        </div>
                    </CardContent>
                </Card>

                {/*Card DATOS LOCATARIO*/}
                <Card className="max-w-4xl">
                    <CardHeader >
                        <div className="flex items-center gap-2">
                            <Building className="h-5 w-5"/>
                            <CardTitle className="font-bold">Datos del Locatario</CardTitle>
                        </div>
                    </CardHeader>

                    <CardContent>
                        <div className="grid grid-cols-1 gap-4 justify-between">
                            <div className="flex gap-3">
                                <h2 className="font-bold">Nombre:</h2>
                                <p className="text-gray-700 font-bold">Cristian Delgado</p>
                            </div>
                            <div className="flex gap-3">
                                <h2 className="font-bold">Cuil:</h2>
                                <p className="text-gray-700">20-24321215-7</p>
                            </div>
                            <div className="flex gap-3">
                                <h2 className="font-bold">Telefono:</h2>
                                <p className="text-gray-700">351 2257653</p>
                            </div>
                        </div>
                    </CardContent>
                </Card>

                {/*Card DATOS LOCADOR*/}
                <Card className="max-w-4xl">
                    <CardHeader >
                        <div className="flex items-center gap-2">
                            <User className="h-5 w-5"/>
                            <CardTitle className="font-bold">Datos del Locador</CardTitle>
                        </div>
                    </CardHeader>

                    <CardContent>
                        <div className="grid grid-cols-1 gap-4 justify-between">
                            <div className="flex gap-3">
                                <h2 className="font-bold">Nombre:</h2>
                                <p className="text-gray-700 font-bold">Ramiro Díaz</p>
                            </div>
                            <div className="flex gap-3">
                                <h2 className="font-bold">DNI: </h2>
                                <p className="text-gray-700">24546422</p>
                            </div>
                            <div className="flex gap-3">
                                <h2 className="font-bold">Telefono: </h2>
                                <p className="text-gray-700">351 6534321</p>
                            </div>
                            <div className="flex gap-3">
                                <h2 className="font-bold">Email:</h2>
                                <p className="text-gray-700">diazramiro@gmail.com</p>
                            </div>
                            <div className="flex gap-3">
                                <h2 className="font-bold">Dirección:</h2>
                                <p className="text-gray-700">Dean Funes 876, Córdoba</p>
                            </div>
                        </div>
                    </CardContent>
                </Card>
                </div>
                {esVigente && 
                <div className="flex flex-col m-5 mt-20">
                    <div className="flex items-center">
                                <Blocks className="h-10 w-10 mr-2 text-green-700" />
                            <div className="">
                                <h2 className="text-2xl font-bold text-foreground font-sans">Servicios a Cargo</h2>
                            </div>
                    </div>
                    <p>Proximamente...</p>
                </div>
                
                }

            </main>
        </div>
    )
}
