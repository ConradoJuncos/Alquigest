import { fetchWithToken } from "@/utils/functions/auth-functions/fetchWithToken";

export interface CancelacionContrato {
  id: number;
  contratoId: number;
  fechaCancelacion: string;
  motivoCancelacionId: number;
  motivoCancelacionNombre: string;
  observaciones?: string;
}

export const cancelacionesService = {
  getByContrato: async (contratoId: string | number): Promise<CancelacionContrato | null> => {
    const data = await fetchWithToken(`/cancelaciones-contratos/contrato/${contratoId}`);
    return data || null;
  },
};
