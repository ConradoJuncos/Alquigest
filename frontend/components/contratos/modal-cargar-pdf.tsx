"use client";

import { useRef, useState } from "react";
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { fetchWithToken } from "@/utils/functions/auth-functions/fetchWithToken";
import BACKEND_URL from "@/utils/backendURL";
import { FileUp, FileText } from "lucide-react";

interface ModalCargarPdfProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  contratoId: number;
  onUploaded?: (respuesta: any) => void; // callback para exito
  onError?: (mensaje: string) => void; // callback para error
}

export default function ModalCargarPdf({ open, onOpenChange, contratoId, onUploaded, onError }: ModalCargarPdfProps) {
  const [file, setFile] = useState<File | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const inputRef = useRef<HTMLInputElement | null>(null);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const f = e.target.files?.[0] || null;
    if (f && f.type !== "application/pdf") {
      onError?.("El archivo debe ser un PDF");
      setFile(null);
      // limpiar input
      if (inputRef.current) inputRef.current.value = "";
      return;
    }
    setFile(f);
  };

  const handleUpload = async () => {
    if (!file) {
      onError?.("Seleccioná un archivo PDF para continuar");
      return;
    }
    setIsLoading(true);
    try {
      const form = new FormData();
      // Usamos nombre de campo 'file' por defecto; ajustar si el backend espera otro nombre
      form.append("file", file);

      const resp = await fetchWithToken(`${BACKEND_URL}/contratos/${contratoId}/pdf`, {
        method: "POST",
        body: form,
      });

      onUploaded?.(resp);
      // cerrar modal
      onOpenChange(false);
      // reset local
      setFile(null);
      if (inputRef.current) inputRef.current.value = "";
    } catch (err: any) {
      onError?.(err.message || "No se pudo cargar el PDF");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2">
            <FileText className="h-5 w-5" /> Cargar PDF del contrato
          </DialogTitle>
          <DialogDescription>
            Seleccioná el archivo PDF correspondiente al contrato. Sólo se admite formato .pdf.
          </DialogDescription>
        </DialogHeader>

        <div className="flex flex-col gap-3">
          <Input
            ref={inputRef}
            type="file"
            accept="application/pdf"
            onChange={handleFileChange}
          />
          {file && (
            <p className="text-sm text-muted-foreground truncate">Archivo seleccionado: <span className="font-medium text-foreground">{file.name}</span></p>
          )}
        </div>

        <DialogFooter className="mt-4">
          <Button variant="outline" onClick={() => onOpenChange(false)} disabled={isLoading}>
            Cancelar
          </Button>
          <Button onClick={handleUpload} loading={isLoading}>
            <FileUp className="h-4 w-4 mr-2" /> Cargar PDF
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
