import { fetchWithToken } from "@/utils/functions/auth-functions/fetchWithToken";
import { PagoServicio } from "@/types/PagoServicio";

export interface ContadoresServicios {
  serviciosPendientes: number;
  serviciosTotales: number;
}

export interface ActualizarMontoBody {
  contratoId: number;
  actualizaciones: { tipoServicioId: number; nuevoMonto: number }[];
}

export interface PagarServicioBatchBody {
  pagos: { pagoId: number; datosPago: Record<string, unknown> }[];
}

export const pagoServiciosService = {
  getContadores: async (): Promise<ContadoresServicios> => {
    const data = await fetchWithToken(`/pagos-servicios/count/pendientes`);
    if (
      !data ||
      typeof data.serviciosPendientes === "undefined" ||
      typeof data.serviciosTotales === "undefined"
    ) {
      throw new Error("El servidor no retornó los contadores esperados");
    }
    return { serviciosPendientes: data.serviciosPendientes, serviciosTotales: data.serviciosTotales };
  },

  getServiciosNoPagadosPorContrato: async (): Promise<Record<string, number>> => {
    const data = await fetchWithToken(`/pagos-servicios/no-pagados/mes-actual/por-contrato`);
    return data || {};
  },

  getByContrato: async (contratoId: string | number): Promise<PagoServicio[]> => {
    const data = await fetchWithToken(`/pagos-servicios/contrato/${contratoId}`);
    return data || [];
  },

  getNoPagadosByContrato: async (contratoId: string | number): Promise<PagoServicio[]> => {
    const data = await fetchWithToken(`/pagos-servicios/contrato/${contratoId}/no-pagados`);
    return data || [];
  },

  updateById: async (id: string | number, body: Partial<PagoServicio>): Promise<PagoServicio> => {
    const data = await fetchWithToken(`/pagos-servicios/${id}`, {
      method: "PUT",
      body: JSON.stringify(body),
    });
    return data;
  },

  actualizarMontos: async (body: ActualizarMontoBody): Promise<void> => {
    await fetchWithToken(`/pagos-servicios/actualizar-montos`, {
      method: "PUT",
      body: JSON.stringify(body),
    });
  },

  pagarBatch: async (body: PagarServicioBatchBody): Promise<void> => {
    await fetchWithToken(`/pagos-servicios/batch`, {
      method: "PUT",
      body: JSON.stringify(body),
    });
  },

  refrescarDatos: async (): Promise<{
    contadores: ContadoresServicios;
    serviciosNoPagados: Record<string, number>;
  }> => {
    const [contadores, serviciosNoPagados] = await Promise.all([
      pagoServiciosService.getContadores(),
      pagoServiciosService.getServiciosNoPagadosPorContrato(),
    ]);
    return { contadores, serviciosNoPagados };
  },
};
