import { fetchWithToken } from "@/utils/functions/auth-functions/fetchWithToken";
import { ContratoDetallado } from "@/types/ContratoDetallado";

export type FiltroContratos = "vigentes" | "no-vigentes" | "cancelados" | "proximos-vencer";

export interface ActualizarEstadoContratoBody {
  estadoContratoId: number;
  motivoCancelacionId?: number;
  observaciones?: string;
}

export const contratosService = {
  getContratosVigentes: async (): Promise<ContratoDetallado[]> => {
    const data = await fetchWithToken(`/contratos/vigentes`);
    return data || [];
  },

  getByFiltro: async (filtro: FiltroContratos): Promise<ContratoDetallado[]> => {
    const data = await fetchWithToken(`/contratos/${filtro}`);
    return data || [];
  },

  getById: async (id: string | number): Promise<ContratoDetallado> => {
    const data = await fetchWithToken(`/contratos/${id}`);
    if (!data || !data.id) throw new Error("El servidor no retornó el contrato");
    return data;
  },

  create: async (body: Record<string, unknown>): Promise<ContratoDetallado> => {
    const data = await fetchWithToken(`/contratos`, {
      method: "POST",
      body: JSON.stringify(body),
    });
    if (!data || !data.id) throw new Error("El servidor no retornó el contrato creado");
    return data;
  },

  updateEstado: async (
    contratoId: string | number,
    body: ActualizarEstadoContratoBody
  ): Promise<void> => {
    await fetchWithToken(`/contratos/${contratoId}/estado`, {
      method: "PATCH",
      body: JSON.stringify(body),
    });
  },

  uploadPdf: async (contratoId: string | number, file: File): Promise<void> => {
    const formData = new FormData();
    formData.append("file", file);
    await fetchWithToken(`/contratos/${contratoId}/pdf`, {
      method: "POST",
      body: formData,
    });
  },

  getPdf: async (contratoId: string | number): Promise<Blob> => {
    const blob = await fetchWithToken(`/contratos/${contratoId}/pdf`);
    return blob;
  },

  getCountVigentes: async (): Promise<number> => {
    const data = await fetchWithToken(`/contratos/count/vigentes`);
    return data ?? 0;
  },

  getCountProximosVencer: async (): Promise<number> => {
    const data = await fetchWithToken(`/contratos/count/proximos-vencer`);
    return data ?? 0;
  },

  tieneContratoVigente: async (inmuebleId: string | number): Promise<boolean> => {
    const data = await fetchWithToken(
      `/contratos/inmueble/${inmuebleId}/tiene-contrato-vigente`
    );
    return !!data;
  },

  getByInmueble: async (inmuebleId: string | number): Promise<ContratoDetallado[]> => {
    const data = await fetchWithToken(`/contratos/inmueble/${inmuebleId}`);
    return data || [];
  },
};
