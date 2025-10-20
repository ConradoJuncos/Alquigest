## Cosas para hacer:

### Contrato - detalle
- [x] Mostrar motivos de rescincion en detalle Contrato
- [ ] Mostrar estado contrato actual

### Contrato
- [x] Agregar nuevo paso para carga de servicios
- [ ] Agregar en back y front el porcentaje de mora (1%)

### Inmueble
- [x] Crear nuevo inmueble con estado disponible o en reparacion

### Pago de servicios
- [x] Todo
- [ ] Agregar la posibilidad de agregar un nuevo servicio al contrato

### ALQUILERES
 - [x] Ver historial
 - [ ] Registrar Pago

### RESUMEN DE SERVICIOS POR PAGAR
- [x] Ver de hacer/generar el pdf.

## BACKEND
- [ ] Hacer que el endpoint contratos/inmueble/{inmuebleID} valide que el contrato sea vigente. [ IN2I ]
- [ ] Estado de inmueble inactivo queda disponible [ P4B ]
- [ ] Revisar los permisos de secretaria (crear inquilino y propietario) [U2H, U2K]
- [ ] Permiso crear_usuario
  
  #### SPRINT 3
  - [ ] Endpoint pago-servicio el PUT devuelve un 500 pero carga correctamente los pagos
  - [ ] Si la fecha inicio de contrato es el mes anterior, se crean los pagos del mes anterior
  - [ ] Ver error en el primer contrato creado (NO CREA TODOS LOS SERVICIOS)
  - [ ] Alquiler se genera el pago SOLO EN EL LOGIN.
  - [ ] Endpoint para ver pagos y alquileres por ULTIMO PERIODO (Ultimo mes)
    - [ ] Utilizar este endpoint para validar si se pagó o no el alquiler.
