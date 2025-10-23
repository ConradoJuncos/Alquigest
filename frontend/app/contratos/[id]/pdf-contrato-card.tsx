import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import BACKEND_URL from "@/utils/backendURL";
import { fetchWithToken } from "@/utils/functions/auth-functions/fetchWithToken";
import { useEffect, useState } from "react";
import { File } from "lucide-react";

export default function PDFContratoCard({idContrato}: {idContrato: number}) {

        const [pdfUrl, setPdfUrl] = useState<string | null>(null);
        const [errorMsg, setErrorMsg] = useState<string | null>(null);
        const [loading, setLoading] = useState(false);

        // limpiar URL creada cuando el componente se desmonta o se reemplaza el pdf
        useEffect(() => {
            return () => {
                if (pdfUrl) URL.revokeObjectURL(pdfUrl);
            };
        }, [pdfUrl]);

        const handleVerPdf = async () => {
            setErrorMsg(null);
            setLoading(true);
            try {
                // Endpoint de descarga del PDF
                const blob = await fetchWithToken(`${BACKEND_URL}/contratos/${idContrato}/pdf`);
                if (blob instanceof Blob) {
                    const url = URL.createObjectURL(blob);
                    // liberar URL anterior si existía
                    if (pdfUrl) URL.revokeObjectURL(pdfUrl);
                    setPdfUrl(url);
                    // abrir en nueva pestaña
                    window.open(url, "_blank");
                } else {
                    setErrorMsg("La respuesta no fue un PDF válido");
                }
            } catch (err: any) {
                setErrorMsg(err.message || "No se pudo obtener el PDF del contrato");
            } finally {
                setLoading(false);
            }
        };

  return (
    <div className="w-full">
        <div className="flex flex-col items-center space-y-2">
            <div className="flex gap-2 items-center">
                <File className="text-primary"/>
                <h2 className="text-xl font-bold text-foreground font-sans">PDF Contrato</h2>
            </div>
            <Card className="w-4xl">
                        <CardHeader >
                            <div className="flex items-center gap-2">
                                <CardTitle className="font-bold">Clickee el botón para verificar si existe un PDF del contrato.</CardTitle>
                            </div>
                        </CardHeader>

                        <CardContent>
                            <div className="flex flex-col items-center gap-3">
                                <Button onClick={handleVerPdf} loading={loading} className="w-full">
                                    Ver PDF
                                </Button>
                                {pdfUrl && (
                                    <a
                                        href={pdfUrl}
                                        target="_blank"
                                        rel="noreferrer"
                                        className="text-sm text-primary underline"
                                    >
                                        Abrir en nueva pestaña
                                    </a>
                                )}
                            </div>
                            {errorMsg && (
                                <p className="mt-3 text-sm text-red-600">{errorMsg}</p>
                            )}
                        </CardContent>
                    </Card>


        </div>
    </div>
  )
}