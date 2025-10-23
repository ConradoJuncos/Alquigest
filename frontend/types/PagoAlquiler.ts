export type PagoAlquiler = {
  id: number
  contratoId: number
  fechaVencimientoPago: string
  monto: number
  estaPagado: boolean
  cuentaBanco: string
  titularDePago: string
  metodo: string
  createdAt: string
  updatedAt: string | null
  inmuebleId: number
  direccionInmueble: string
  inquilinoId: number
  nombreInquilino: string
  apellidoInquilino: string
}
