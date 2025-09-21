export interface Inmueble {
  id?: number;
  propietarioId: number;
  direccion: string;
  tipoInmuebleId: number;
  tipo: string;
  estado: number;
  superficie: number;
  esAlquilado: boolean;
  esActivo: boolean;
}