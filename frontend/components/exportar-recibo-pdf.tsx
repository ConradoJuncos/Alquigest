"use client"

import { Button } from "@/components/ui/button"
import { FileDown } from "lucide-react"
import jsPDF from 'jspdf'
import TipoServicioIcon from "@/components/tipoServicioIcon"
import { ServicioContrato, TIPO_SERVICIO_LABEL } from "@/types/ServicioContrato"
import formatPrice from "@/utils/functions/price-convert"

interface ExportarReciboPDFProps {
  contrato: {
    direccionInmueble: string
    nombreInquilino: string
    apellidoInquilino: string
    nombrePropietario: string
    apellidoPropietario: string
  }
  alquilerMonto: string
  servicios: { [key: number]: number }
  serviciosBase: ServicioContrato[]
  total: number
}

export default function ExportarReciboPDF({
  contrato,
  alquilerMonto,
  servicios,
  serviciosBase,
  total
}: ExportarReciboPDFProps) {
  
  const cargarImagen = (src: string): Promise<string> => {
    return new Promise((resolve, reject) => {
      const img = new Image()
      img.crossOrigin = 'anonymous'
      img.onload = () => {
        const canvas = document.createElement('canvas')
        const ctx = canvas.getContext('2d')
        canvas.width = img.width
        canvas.height = img.height
        ctx?.drawImage(img, 0, 0)
        resolve(canvas.toDataURL('image/png'))
      }
      img.onerror = reject
      img.src = src
    })
  }
  
  const generarPDF = async () => {
    const doc = new jsPDF()
    const fechaActual = new Date()
    const mesActual = fechaActual.toLocaleDateString('es-ES', { month: 'long', year: 'numeric' })
    const fechaCompleta = fechaActual.toLocaleDateString('es-ES')
    
    try {
      // Cargar el logo
      const logoBase64 = await cargarImagen('/alquigest-dark.png')
      
      // Agregar logo al PDF (posición superior derecha)
      doc.addImage(logoBase64, 'PNG', 15, 270, 25, 5)
    } catch (error) {
      console.warn('No se pudo cargar el logo:', error)
    }
    
    // Configuración de fuentes y colores
    doc.setFont('helvetica', 'bold')
    doc.setFontSize(18)
    
    // Encabezado
    doc.setTextColor(0, 50, 100)
    doc.text('Estudio Jurídico de Carina Andrea Torres', 20, 25)
    
    doc.setFont('helvetica', 'normal')
    doc.setFontSize(12)
    doc.setTextColor(0, 0, 0)
    doc.text(`Resumen correspondiente a ${mesActual}`, 20, 42)
    doc.text(`Fecha de Generación: ${fechaCompleta}`, 20, 35)
    
    // Información del alquiler
    doc.setFont('helvetica', 'bold')
    doc.setFontSize(12)
    doc.text('Información del alquiler', 20, 58)

    // Línea separadora
    doc.setLineWidth(0.3)
    doc.line(20, 60, 190, 60)
    
    doc.setFont('helvetica', 'normal')
    doc.setFontSize(11)
    doc.setFont('helvetica', 'bold')
    doc.text(`Inmueble: ${contrato.direccionInmueble}`, 20, 70)
    doc.setFont('helvetica', 'normal')
    doc.text(`Locador: ${contrato.apellidoPropietario}, ${contrato.nombrePropietario}`, 20, 78)
    doc.text(`Locatario: ${contrato.apellidoInquilino}, ${contrato.nombreInquilino}`, 20, 88)

    // Información del Montos
    doc.setFont('helvetica', 'bold')
    doc.setFontSize(12)
    doc.text('Montos a pagar este mes', 20, 103)

    // Línea separadora
    doc.setLineWidth(0.3)
    doc.line(20, 105, 190, 105)

    // Valor del alquiler
    doc.setFont('helvetica', 'bold')
    doc.setFontSize(13)
    doc.text('Valor del Alquiler', 20, 113)
    doc.text(`${formatPrice(Number(alquilerMonto))}`, 160, 113)
    
    // Servicios
    let yPosition = 120
    const serviciosConMonto = serviciosBase.filter(servicio => servicios[servicio.tipoServicio] > 0)
    
    if (serviciosConMonto.length > 0) {
      doc.setFont('helvetica', 'bold')
      doc.setFontSize(11)
      doc.text('Servicios controlados por el estudio jurídico', 20, yPosition)
      yPosition += 5

      doc.setFont('helvetica', 'normal')
      doc.setFontSize(9)
      doc.text('A continuación se detallan los montos de las facturas correspondientes a cada servicio gestionado por el estudio jurídico', 20, yPosition)
      yPosition += 10
      
      doc.setFont('helvetica', 'normal')
      doc.setFontSize(12)
      
      serviciosConMonto.forEach(servicio => {
        const monto = servicios[servicio.tipoServicio]
        // Concepto del servicio (izquierda)
        doc.text(`• ${TIPO_SERVICIO_LABEL[servicio.tipoServicio]}`, 25, yPosition)
        // Monto del servicio (derecha)
        doc.text(`$${monto.toLocaleString()}`, 160, yPosition)
        yPosition += 8
      })
    }
    
    // Total
    yPosition += 10
    doc.setLineWidth(0.3)
    doc.line(20, yPosition - 5, 190, yPosition - 5)
    
    doc.setFont('helvetica', 'bold')
    doc.setFontSize(16)
    doc.setTextColor(0, 100, 0)
    doc.text('Total:', 20, yPosition + 5)
    doc.text(`${formatPrice(total)}`, 155, yPosition + 5)

    doc.setLineWidth(0.3)
    doc.line(20, yPosition + 10, 190, yPosition + 10)
    
    // Nota importante
    yPosition += 20
    doc.setFont('helvetica', 'normal')
    doc.setFontSize(10)
    doc.setTextColor(200, 0, 0)
    doc.text('Recuerde que el pago del alquiler y los servicios debe de realizarse antes del día 10', 20, yPosition)
    
    // Información de transferencia bancaria
    yPosition += 10
    doc.setFont('helvetica', 'bold')
    doc.setFontSize(10)
    doc.setTextColor(0, 0, 0)
    doc.text('El monto debe transferirse a la siguiente cuenta:', 20, yPosition)
    
    yPosition += 7
    doc.setFont('helvetica', 'normal')
    doc.setFontSize(10)
    doc.text('Titular de la cuenta: Juanito Perez', 20, yPosition)
    
    yPosition += 7
    doc.text('Nro de Cuenta: 9121831', 20, yPosition)
    
    yPosition += 7
     doc.setFont('helvetica', 'bold')
    doc.text('Alias: VIVIR.JUGAR.GRANDEZA', 20, yPosition)
    
    yPosition += 7
    doc.text('CBU: 243343000000000003321', 20, yPosition)

    // Línea divisoria antes de datos bancarios
    yPosition += 15
    doc.setLineWidth(0.3)
    doc.setDrawColor(100, 100, 100)
    doc.line(20, yPosition, 190, yPosition)
    
    // Pie de página
    yPosition += 20
    doc.setFont('helvetica', 'normal')
    doc.setTextColor(100, 100, 100)
    doc.setFontSize(9)
    doc.text('Resumen generado por el sistema AlquiGest. Gestione alquileres de forma simple.', 15, 280)
    
    // Guardar el PDF
    const nombreArchivo = `Resumen_${contrato.apellidoInquilino}_${fechaActual.getMonth() + 1}_${fechaActual.getFullYear()}.pdf`
    doc.save(nombreArchivo)
  }

  const handleGenerarPDF = async () => {
    await generarPDF()
  }

  return (
    <Button onClick={handleGenerarPDF} className="w-full bg-blue-600 hover:bg-blue-700">
      <FileDown className="h-4 w-4 mr-2" />
      Exportar a PDF
    </Button>
  )
}