import { fetchWithToken } from "@/utils/functions/auth-functions/fetchWithToken";

export interface HonorarioPorInmueble {
  inmuebleId: number;
  direccionInmueble: string;
  contratoId: number;
  nombrePropietario: string;
  apellidoPropietario: string;
  nombreInquilino: string;
  apellidoInquilino: string;
  montoAlquiler: number;
  honorario: number;
}

export interface InformeHonorarios {
  periodo: string;
  honorariosPorInmueble: HonorarioPorInmueble[];
  totalHonorarios: number;
}

export interface DetalleAumento {
  aumentoId: number;
  fechaAumento: string;
  montoAnterior: number;
  montoNuevo: number;
  porcentajeAumento: number;
}

export interface AumentoPorContrato {
  contratoId: number;
  direccionInmueble: string;
  nombreInquilino: string;
  apellidoInquilino: string;
  nombrePropietario: string;
  apellidoPropietario: string;
  aumentos: DetalleAumento[];
}

export interface InformeAumentos {
  periodoDesde: string;
  periodoHasta: string;
  aumentosPorContrato: AumentoPorContrato[];
}

export const informesService = {
  getHonorarios: async (): Promise<InformeHonorarios> => {
    const data = await fetchWithToken(`/informes/honorarios`);
    if (!data) throw new Error("El servidor no retornó el informe de honorarios");
    return data;
  },

  getAumentos: async (meses?: number): Promise<InformeAumentos> => {
    const params = meses ? `?meses=${meses}` : "";
    const data = await fetchWithToken(`/informes/aumentos${params}`);
    if (!data) throw new Error("El servidor no retornó el informe de aumentos");
    return data;
  },
};
