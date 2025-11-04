# Generación Retroactiva de Alquileres

## Descripción

Esta funcionalidad permite crear automáticamente alquileres retroactivos cuando se crea un contrato con una fecha de inicio anterior a la fecha actual.

## Comportamiento

### Condiciones para Activación

La generación retroactiva se activa automáticamente cuando:
1. Se crea un nuevo contrato con estado "Vigente"
2. La `fechaInicio` del contrato es anterior a la fecha actual

### Lógica de Generación

#### Alquileres Generados

1. **Primer Alquiler**: Se crea con la misma fecha que la `fechaInicio` del contrato
2. **Alquileres Subsiguientes**: Se crean el día 1 de cada mes hasta la fecha actual
3. **Estado**: Todos los alquileres retroactivos se marcan como pagados (`estaPagado=true`)
4. **Fecha de Pago**: Se asigna la misma fecha que el vencimiento del alquiler

#### Aumentos Automáticos

Si el contrato tiene configurado un `periodoAumento`:
1. El sistema detecta automáticamente cuando corresponde un aumento
2. Calcula el nuevo monto aplicando el `porcentajeAumento` configurado
3. Registra el aumento en la tabla `aumento_alquiler`

**Fórmula de aumento:**
```
montoNuevo = montoActual * (1 + porcentajeAumento/100)
```

## Ejemplo Práctico

### Escenario
- **Fecha actual**: 04/11/2025
- **Fecha inicio contrato**: 15/02/2021
- **Monto inicial**: $50,000
- **Periodo de aumento**: 3 meses
- **Porcentaje de aumento**: 10%

### Alquileres Generados

| Nro | Fecha Vencimiento | Monto | Observaciones |
|-----|-------------------|-------|---------------|
| 1 | 15/02/2021 | $50,000.00 | Primer alquiler (misma fecha que inicio) |
| 2 | 01/03/2021 | $50,000.00 | Día 1 del mes siguiente |
| 3 | 01/04/2021 | $50,000.00 | Día 1 del mes |
| 4 | 01/05/2021 | **$55,000.00** | **Aumento aplicado** (3 meses) |
| 5 | 01/06/2021 | $55,000.00 | |
| 6 | 01/07/2021 | $55,000.00 | |
| 7 | 01/08/2021 | **$60,500.00** | **Aumento aplicado** (6 meses) |
| ... | ... | ... | Continúa hasta hoy |

### Registros de Aumento

| Fecha Aumento | Monto Anterior | Monto Nuevo | Porcentaje |
|---------------|----------------|-------------|------------|
| 01/05/2021 | $50,000.00 | $55,000.00 | 10% |
| 01/08/2021 | $55,000.00 | $60,500.00 | 10% |
| 01/11/2021 | $60,500.00 | $66,550.00 | 10% |
| ... | ... | ... | ... |

## Implementación Técnica

### Método Principal

```java
private void generarAlquileresRetroactivos(Contrato contrato, String fechaInicioISO)
```

Este método:
1. Verifica que la fecha de inicio sea anterior a hoy
2. Itera desde la fecha de inicio hasta la fecha actual
3. Detecta y aplica aumentos según el `periodoAumento`
4. Crea los objetos `Alquiler` marcados como pagados
5. Crea los objetos `AumentoAlquiler` para cada aumento detectado
6. Guarda todos los registros en batch para optimizar el rendimiento

### Integración

El método se invoca automáticamente desde `ContratoService.crearContrato()`:

```java
if (fechaInicioISO != null && FechaUtil.compararFechas(fechaInicioISO, fechaActualISO) < 0) {
    // Generar alquileres retroactivos desde fechaInicio hasta hoy
    generarAlquileresRetroactivos(contratoGuardado, fechaInicioISO);
} else {
    // Crear alquiler actual (lógica original)
    // ...
}
```

## Ventajas

1. **Automatización**: No es necesario crear manualmente alquileres pasados
2. **Consistencia**: Los aumentos se calculan y registran automáticamente
3. **Historial Completo**: Se mantiene un registro completo de todos los alquileres y aumentos
4. **Optimización**: Uso de batch inserts para mejorar el rendimiento
5. **Seguridad**: Manejo de errores que no afecta la creación del contrato

## Consideraciones

- Los alquileres retroactivos siempre se marcan como pagados
- Si hay un error en la generación, el contrato se crea de todos modos (el error se registra en logs)
- El primer alquiler siempre tiene la fecha de inicio del contrato
- Los siguientes alquileres son siempre del día 1 de cada mes
- Los aumentos se aplican en el mes que corresponde según `periodoAumento`

## Tests

Los tests están ubicados en:
```
backend/src/test/java/com/alquileres/service/ContratoServiceRetroactivoTest.java
```

Casos cubiertos:
1. Generación de múltiples alquileres retroactivos
2. Verificación de fechas (primer alquiler = fechaInicio, siguientes = día 1)
3. Verificación de estado pagado
4. Registro de aumentos automáticos
5. No generación cuando la fecha de inicio es futura
