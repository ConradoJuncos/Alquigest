export interface ServicioContrato {
  tipoServicioId: number; // 1=Agua, 2=Luz, 3=Gas, 4=Municipal, 5=Rentas
  nroCuenta: number | null;
  contratoId: number | null;
  nroContrato: string; // solo para mostrar en el resumen
  esDeInquilino: boolean; // true: Inquilino paga, false: Estudio jurídico
  esActivo: boolean; // habilitado para este contrato
  esAnual: boolean;
  fechaInicio: String; // fecha de inicio del servicio
}

export const TIPO_SERVICIO_LABEL: Record<number, string> = {
  1: "Luz",
  2: "Agua",
  3: "Gas",
  4: "Rentas",
  5: "Municipal",
};

export const BORDER_HOVER_CLASSES: Record<number, string> = {
    1: "hover:border-yellow-500",   // Luz
    2: "hover:border-sky-500",   // Agua
    3: "hover:border-orange-500",   // Gas
    4: "hover:border-purple-500",  // Rentas
    5: "hover:border-emerald-500",     // Municipal
  };