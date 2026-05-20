import { fetchWithToken } from "@/utils/functions/auth-functions/fetchWithToken";
import { ServicioContrato } from "@/types/ServicioContrato";

export interface CrearServicioContratoBody {
  contratoId: number;
  tipoServicioId: number;
  nroCuenta?: string | number | null;
  nroContratoServicio?: number | null;
  nroContrato?: string;
  esDeInquilino: boolean;
  esAnual: boolean;
  fechaInicio: string;
}

export interface ActualizarServicioContratoBody {
  nroCuenta?: string | number | null;
  nroContratoServicio?: number | null;
  esDeInquilino?: boolean;
  esAnual?: boolean;
}

export const serviciosContratoService = {
  getActivosByContrato: async (contratoId: string | number): Promise<ServicioContrato[]> => {
    const data = await fetchWithToken(`/servicios-contrato/contrato/${contratoId}/activos`);
    return data || [];
  },

  create: async (body: CrearServicioContratoBody | CrearServicioContratoBody[]): Promise<void> => {
    await fetchWithToken(`/servicios-contrato`, {
      method: "POST",
      body: JSON.stringify(body),
    });
  },

  update: async (
    servicioId: string | number,
    body: ActualizarServicioContratoBody
  ): Promise<void> => {
    await fetchWithToken(`/servicios-contrato/${servicioId}`, {
      method: "PUT",
      body: JSON.stringify(body),
    });
  },

  reactivar: async (servicioId: string | number, fechaInicio: string): Promise<void> => {
    await fetchWithToken(`/servicios-contrato/${servicioId}/reactivar`, {
      method: "PATCH",
      body: JSON.stringify({ fechaInicio }),
    });
  },

  desactivar: async (servicioId: string | number): Promise<void> => {
    await fetchWithToken(`/servicios-contrato/${servicioId}/desactivar`, {
      method: "PATCH",
    });
  },
};
