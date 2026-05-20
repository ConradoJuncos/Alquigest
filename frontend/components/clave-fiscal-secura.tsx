'use client'

import { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Eye, Copy, EyeOff } from 'lucide-react';
import { PropietariosService } from '@/utils/services/propietarioService';
import ModalInput from '@/components/modal-input';
import ModalError from '@/components/modal-error';

interface ClaveFiscalSecuraProps {
  propietarioId: string;
  claveFiscalEnmascarada: string | null;
}

export default function ClaveFiscalSecura({
  propietarioId,
  claveFiscalEnmascarada
}: ClaveFiscalSecuraProps) {
  const [claveFiscalVisible, setClaveFiscalVisible] = useState(false);
  const [claveFiscal, setClaveFiscal] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [timeLeft, setTimeLeft] = useState(30);
  const [modalPasswordOpen, setModalPasswordOpen] = useState(false);
  const [errorModal, setErrorModal] = useState({ open: false, mensaje: "" });

  // Timer para auto-ocultar después de 30 segundos
  useEffect(() => {
    let interval: NodeJS.Timeout | null = null;

    if (claveFiscalVisible && timeLeft > 0) {
      interval = setInterval(() => {
        setTimeLeft((prev) => {
          if (prev <= 1) {
            setClaveFiscalVisible(false);
            setClaveFiscal(null);
            return 30;
          }
          return prev - 1;
        });
      }, 1000);
    }

    return () => {
      if (interval) clearInterval(interval);
    };
  }, [claveFiscalVisible, timeLeft]);

  const revelarClaveFiscal = async (password: string) => {
    setLoading(true);

    try {
      const response = await PropietariosService.revelarClaveFiscal(propietarioId, password);

      setClaveFiscal(response.claveFiscal);
      setClaveFiscalVisible(true);
      setTimeLeft(30);

    } catch (error: any) {
      if (error.message?.includes('401') || error.message?.includes('Unauthorized')) {
        setErrorModal({ open: true, mensaje: "Contraseña incorrecta" });
      } else {
        setErrorModal({ open: true, mensaje: error.message || "Error al revelar la clave fiscal" });
      }
      console.error("Error revelando clave fiscal:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleRevelarClick = () => {
    setModalPasswordOpen(true);
  };

  const copiarClave = () => {
    if (claveFiscal) {
      navigator.clipboard.writeText(claveFiscal);
    }
  };

  const ocultarClave = () => {
    setClaveFiscalVisible(false);
    setClaveFiscal(null);
    setTimeLeft(30);
  };

  return (
    <div className="flex flex-col gap-2">
      {!claveFiscalVisible ? (
        <div className="flex items-center gap-2">
          <code className="px-3 py-2 bg-muted rounded-md font-mono text-sm">
            {claveFiscalEnmascarada || 'No registrada'}
          </code>
          <Button
            onClick={handleRevelarClick}
            disabled={loading || !claveFiscalEnmascarada}
            variant="outline"
            size="sm"
          >
            {loading ? (
              <>Cargando...</>
            ) : (
              <>
                <Eye className="h-4 w-4 mr-2" />
                Revelar
              </>
            )}
          </Button>
        </div>
      ) : (
        <div className="flex flex-col gap-3 p-3 bg-muted/50 rounded-md border">
          <div className="flex items-center justify-between text-sm text-muted-foreground">
            <span>Clave Fiscal</span>
            <span>Se ocultará en {timeLeft}s</span>
          </div>

          <code className="px-3 py-2 bg-background rounded-md font-mono text-base border select-all">
            {claveFiscal}
          </code>

          <div className="flex gap-2">
            <Button
              onClick={copiarClave}
              size="sm"
              variant="default"
            >
              <Copy className="h-4 w-4 mr-2" />
              Copiar
            </Button>
            <Button
              onClick={ocultarClave}
              size="sm"
              variant="outline"
            >
              <EyeOff className="h-4 w-4 mr-2" />
              Ocultar
            </Button>
          </div>
        </div>
      )}

      <ModalInput
        titulo="Verificación de Identidad"
        descripcion="Por seguridad, ingresa tu contraseña para revelar la clave fiscal"
        label="Contraseña"
        type="password"
        placeholder="Ingresa tu contraseña..."
        open={modalPasswordOpen}
        onOpenChange={setModalPasswordOpen}
        onConfirm={revelarClaveFiscal}
        textoConfirmar="Revelar"
      />

      {errorModal.open && (
        <ModalError
          titulo="Error"
          mensaje={errorModal.mensaje}
          onClose={() => setErrorModal({ open: false, mensaje: "" })}
        />
      )}
    </div>
  );
}

