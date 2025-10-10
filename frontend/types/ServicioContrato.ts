export interface ServicioContrato {
  tipoServicio: number; // 1=Agua, 2=Luz, 3=Gas, 4=Municipal, 5=Rentas
  nroCuenta: number | null;
  nroContrato: number | null;
  esDeInquilino: boolean; // true: Inquilino paga, false: Estudio jurídico
  esActivo: boolean; // habilitado para este contrato
  esAnual: boolean; // true: anual, false: bimestral
}

export const TIPO_SERVICIO_LABEL: Record<number, string> = {
  1: "Agua",
  2: "Luz",
  3: "Gas",
  4: "Municipal",
  5: "Rentas",
};

export const BORDER_HOVER_CLASSES: Record<number, string> = {
    1: "hover:border-sky-500",      // Agua
    2: "hover:border-yellow-500",   // Luz
    3: "hover:border-orange-500",   // Gas
    4: "hover:border-purple-500",  // Municipal
    5: "hover:border-emerald-500",     // Rentas
  };