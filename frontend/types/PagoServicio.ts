  type PagoServicio = {
    id: number
    estaPagado: boolean
    estaVencido: boolean
    fechaPago: string | null
    medioPago: string | null
    monto: number | null
    pdfPath: string | null
    periodo: string
    servicioXContrato: {
      id: number
      nroCuenta: string | null
      esDeInquilino: boolean
      esAnual: boolean
      esActivo: boolean
      tipoServicio: { id: number; nombre: string }
    }
  }