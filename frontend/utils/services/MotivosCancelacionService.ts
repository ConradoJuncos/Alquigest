import { fetchWithToken } from "@/utils/functions/auth-functions/fetchWithToken";

export interface MotivoCancelacion {
  id: number;
  nombre: string;
}

export const motivosCancelacionService = {
  getAll: async (): Promise<MotivoCancelacion[]> => {
    const data = await fetchWithToken(`/motivos-cancelacion`);
    return data || [];
  },
};
