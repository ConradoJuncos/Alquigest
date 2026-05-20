import { fetchWithToken } from "@/utils/functions/auth-functions/fetchWithToken";
import { PagoAlquiler } from "@/types/PagoAlquiler";

export interface PagarAlquilerBody {
  cuentaBanco: string;
  titularDePago: string;
  metodo: string;
  fechaPago: string;
}

export const alquileresService = {
  getAlquileresPendientes: async (): Promise<PagoAlquiler[]> => {
    const data = await fetchWithToken(`/alquileres/pendientes`);
    return data || [];
  },

  getPendientesByContrato: async (contratoId: string | number): Promise<PagoAlquiler[]> => {
    const data = await fetchWithToken(`/alquileres/contrato/${contratoId}/pendientes`);
    return data || [];
  },

  getByContrato: async (contratoId: string | number): Promise<PagoAlquiler[]> => {
    const data = await fetchWithToken(`/alquileres/contrato/${contratoId}`);
    return data || [];
  },

  pagar: async (alquilerId: string | number, body: PagarAlquilerBody): Promise<void> => {
    await fetchWithToken(`/alquileres/${alquilerId}/pagar`, {
      method: "PATCH",
      body: JSON.stringify(body),
    });
  },

  getCountPendientes: async (): Promise<number> => {
    const data = await fetchWithToken(`/alquileres/count/pendientes`);
    return data ?? 0;
  },

  getNotificacionesMes: async (): Promise<PagoAlquiler[]> => {
    const data = await fetchWithToken(`/alquileres/notificaciones/mes`);
    return data || [];
  },

  getAumentosManualesPendientes: async (): Promise<PagoAlquiler[]> => {
    const data = await fetchWithToken(`/alquileres/aumento-manual/pendientes`);
    return data || [];
  },
};
