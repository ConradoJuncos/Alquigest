package com.alquileres.service;

import com.alquileres.dto.ContratoCreateDTO;
import com.alquileres.dto.ContratoDTO;
import com.alquileres.model.*;
import com.alquileres.repository.*;
import com.alquileres.security.EncryptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests para la generación retroactiva de alquileres en ContratoService
 */
@ExtendWith(MockitoExtension.class)
class ContratoServiceRetroactivoTest {

    @Mock
    private ContratoRepository contratoRepository;

    @Mock
    private InmuebleRepository inmuebleRepository;

    @Mock
    private InquilinoRepository inquilinoRepository;

    @Mock
    private EstadoContratoRepository estadoContratoRepository;

    @Mock
    private EstadoInmuebleRepository estadoInmuebleRepository;

    @Mock
    private PropietarioRepository propietarioRepository;

    @Mock
    private TipoInmuebleRepository tipoInmuebleRepository;

    @Mock
    private CancelacionContratoRepository cancelacionContratoRepository;

    @Mock
    private MotivoCancelacionRepository motivoCancelacionRepository;

    @Mock
    private AlquilerRepository alquilerRepository;

    @Mock
    private AlquilerActualizacionService alquilerActualizacionService;

    @Mock
    private ServicioXContratoService servicioXContratoService;

    @Mock
    private EncryptionService encryptionService;

    @Mock
    private PDFService pdfService;

    @Mock
    private AumentoAlquilerService aumentoAlquilerService;

    @InjectMocks
    private ContratoService contratoService;

    private Inmueble inmueble;
    private Inquilino inquilino;
    private EstadoContrato estadoVigente;
    private EstadoInmueble estadoAlquilado;

    @BeforeEach
    void setUp() {
        // Setup Inmueble
        inmueble = new Inmueble();
        inmueble.setId(1L);
        inmueble.setDireccion("Calle Test 123");
        inmueble.setPropietarioId(1L);
        inmueble.setTipoInmuebleId(1L);
        inmueble.setEstado(1);

        // Setup Inquilino
        inquilino = new Inquilino();
        inquilino.setId(1L);
        inquilino.setNombre("Juan");
        inquilino.setApellido("Pérez");

        // Setup Estado Vigente
        estadoVigente = new EstadoContrato();
        estadoVigente.setId(1);
        estadoVigente.setNombre("Vigente");

        // Setup Estado Alquilado
        estadoAlquilado = new EstadoInmueble();
        estadoAlquilado.setId(2);
        estadoAlquilado.setNombre("Alquilado");
    }

    @Test
    void crearContrato_conFechaInicioAnterior_debeGenerarAlquileresRetroactivos() {
        // Arrange
        // Fecha de inicio: 3 meses atrás (ejemplo: 15/02/2021 si hoy es 15/05/2021)
        LocalDate fechaInicio = LocalDate.now().minusMonths(3);
        String fechaInicioUsuario = fechaInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String fechaFinUsuario = LocalDate.now().plusYears(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        ContratoCreateDTO contratoDTO = new ContratoCreateDTO();
        contratoDTO.setInmuebleId(1L);
        contratoDTO.setInquilinoId(1L);
        contratoDTO.setFechaInicio(fechaInicioUsuario);
        contratoDTO.setFechaFin(fechaFinUsuario);
        contratoDTO.setMonto(new BigDecimal("50000.00"));
        contratoDTO.setPorcentajeAumento(new BigDecimal("10.00"));
        contratoDTO.setPeriodoAumento(3); // Aumenta cada 3 meses
        contratoDTO.setAumentaConIcl(false);

        // Configurar mocks
        when(inmuebleRepository.findById(1L)).thenReturn(Optional.of(inmueble));
        when(inquilinoRepository.findById(1L)).thenReturn(Optional.of(inquilino));
        when(contratoRepository.existsContratoVigenteByInmueble(any())).thenReturn(false);
        when(estadoContratoRepository.findByNombre("Vigente")).thenReturn(Optional.of(estadoVigente));
        when(estadoInmuebleRepository.findByNombre("Alquilado")).thenReturn(Optional.of(estadoAlquilado));

        Contrato contratoGuardado = new Contrato();
        contratoGuardado.setId(1L);
        contratoGuardado.setInmueble(inmueble);
        contratoGuardado.setInquilino(inquilino);
        contratoGuardado.setFechaInicio(fechaInicio.format(DateTimeFormatter.ISO_LOCAL_DATE));
        contratoGuardado.setMonto(new BigDecimal("50000.00"));
        contratoGuardado.setPorcentajeAumento(new BigDecimal("10.00"));
        contratoGuardado.setPeriodoAumento(3);
        contratoGuardado.setEstadoContrato(estadoVigente);

        when(contratoRepository.save(any(Contrato.class))).thenReturn(contratoGuardado);
        when(inmuebleRepository.save(any(Inmueble.class))).thenReturn(inmueble);
        when(inquilinoRepository.save(any(Inquilino.class))).thenReturn(inquilino);

        // Capturar los alquileres guardados
        List<Alquiler> alquileresCaptured = new ArrayList<>();
        when(alquilerRepository.saveAll(anyList())).thenAnswer(invocation -> {
            List<Alquiler> alquileres = invocation.getArgument(0);
            alquileresCaptured.addAll(alquileres);
            return alquileres;
        });

        doNothing().when(aumentoAlquilerService).guardarAumentosEnBatch(anyList());

        // Act
        ContratoDTO resultado = contratoService.crearContrato(contratoDTO);

        // Assert
        assertNotNull(resultado);
        
        // Verificar que se guardaron alquileres
        verify(alquilerRepository, atLeastOnce()).saveAll(anyList());
        
        // Verificar que se generaron alquileres retroactivos
        assertTrue(alquileresCaptured.size() > 1, 
            "Debe generar múltiples alquileres retroactivos (generó: " + alquileresCaptured.size() + ")");
        
        // Verificar que los alquileres están marcados como pagados
        for (Alquiler alquiler : alquileresCaptured) {
            assertTrue(alquiler.getEstaPagado(), "Los alquileres retroactivos deben estar marcados como pagados");
            assertNotNull(alquiler.getFechaPago(), "Los alquileres retroactivos deben tener fecha de pago");
        }
        
        // Verificar que el primer alquiler tiene la fecha de inicio del contrato
        Alquiler primerAlquiler = alquileresCaptured.get(0);
        assertEquals(fechaInicio.format(DateTimeFormatter.ISO_LOCAL_DATE), 
            primerAlquiler.getFechaVencimientoPago(),
            "El primer alquiler debe tener la misma fecha que el inicio del contrato");
        
        // Verificar que los siguientes alquileres son del día 1 de cada mes
        if (alquileresCaptured.size() > 1) {
            for (int i = 1; i < alquileresCaptured.size(); i++) {
                LocalDate fechaAlquiler = LocalDate.parse(
                    alquileresCaptured.get(i).getFechaVencimientoPago(), 
                    DateTimeFormatter.ISO_LOCAL_DATE
                );
                assertEquals(1, fechaAlquiler.getDayOfMonth(),
                    "Los alquileres posteriores al primero deben ser del día 1 del mes");
            }
        }
    }

    @Test
    void crearContrato_conFechaInicioAnteriorYAumento_debeRegistrarAumentos() {
        // Arrange
        // Fecha de inicio: 4 meses atrás para que haya al menos un aumento
        LocalDate fechaInicio = LocalDate.now().minusMonths(4);
        String fechaInicioUsuario = fechaInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String fechaFinUsuario = LocalDate.now().plusYears(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        ContratoCreateDTO contratoDTO = new ContratoCreateDTO();
        contratoDTO.setInmuebleId(1L);
        contratoDTO.setInquilinoId(1L);
        contratoDTO.setFechaInicio(fechaInicioUsuario);
        contratoDTO.setFechaFin(fechaFinUsuario);
        contratoDTO.setMonto(new BigDecimal("50000.00"));
        contratoDTO.setPorcentajeAumento(new BigDecimal("10.00"));
        contratoDTO.setPeriodoAumento(3); // Aumenta cada 3 meses
        contratoDTO.setAumentaConIcl(false);

        // Configurar mocks
        when(inmuebleRepository.findById(1L)).thenReturn(Optional.of(inmueble));
        when(inquilinoRepository.findById(1L)).thenReturn(Optional.of(inquilino));
        when(contratoRepository.existsContratoVigenteByInmueble(any())).thenReturn(false);
        when(estadoContratoRepository.findByNombre("Vigente")).thenReturn(Optional.of(estadoVigente));
        when(estadoInmuebleRepository.findByNombre("Alquilado")).thenReturn(Optional.of(estadoAlquilado));

        Contrato contratoGuardado = new Contrato();
        contratoGuardado.setId(1L);
        contratoGuardado.setInmueble(inmueble);
        contratoGuardado.setInquilino(inquilino);
        contratoGuardado.setFechaInicio(fechaInicio.format(DateTimeFormatter.ISO_LOCAL_DATE));
        contratoGuardado.setMonto(new BigDecimal("50000.00"));
        contratoGuardado.setPorcentajeAumento(new BigDecimal("10.00"));
        contratoGuardado.setPeriodoAumento(3);
        contratoGuardado.setEstadoContrato(estadoVigente);

        when(contratoRepository.save(any(Contrato.class))).thenReturn(contratoGuardado);
        when(inmuebleRepository.save(any(Inmueble.class))).thenReturn(inmueble);
        when(inquilinoRepository.save(any(Inquilino.class))).thenReturn(inquilino);
        when(alquilerRepository.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));

        // Capturar los aumentos guardados
        List<AumentoAlquiler> aumentosCaptured = new ArrayList<>();
        doAnswer(invocation -> {
            List<AumentoAlquiler> aumentos = invocation.getArgument(0);
            aumentosCaptured.addAll(aumentos);
            return null;
        }).when(aumentoAlquilerService).guardarAumentosEnBatch(anyList());

        // Act
        ContratoDTO resultado = contratoService.crearContrato(contratoDTO);

        // Assert
        assertNotNull(resultado);
        
        // Verificar que se registraron aumentos (al menos 1 porque pasaron más de 3 meses)
        assertTrue(aumentosCaptured.size() >= 1, 
            "Debe registrar al menos un aumento (registró: " + aumentosCaptured.size() + ")");
        
        // Verificar que los aumentos tienen los datos correctos
        for (AumentoAlquiler aumento : aumentosCaptured) {
            assertNotNull(aumento.getMontoAnterior(), "El aumento debe tener monto anterior");
            assertNotNull(aumento.getMontoNuevo(), "El aumento debe tener monto nuevo");
            assertTrue(aumento.getMontoNuevo().compareTo(aumento.getMontoAnterior()) > 0,
                "El monto nuevo debe ser mayor al anterior");
        }
    }

    @Test
    void crearContrato_conFechaInicioFutura_noDebeGenerarAlquileresRetroactivos() {
        // Arrange
        LocalDate fechaInicio = LocalDate.now().plusMonths(1);
        String fechaInicioUsuario = fechaInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String fechaFinUsuario = LocalDate.now().plusYears(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        ContratoCreateDTO contratoDTO = new ContratoCreateDTO();
        contratoDTO.setInmuebleId(1L);
        contratoDTO.setInquilinoId(1L);
        contratoDTO.setFechaInicio(fechaInicioUsuario);
        contratoDTO.setFechaFin(fechaFinUsuario);
        contratoDTO.setMonto(new BigDecimal("50000.00"));
        contratoDTO.setPeriodoAumento(3);
        contratoDTO.setAumentaConIcl(false);

        // Configurar mocks
        when(inmuebleRepository.findById(1L)).thenReturn(Optional.of(inmueble));
        when(inquilinoRepository.findById(1L)).thenReturn(Optional.of(inquilino));
        when(contratoRepository.existsContratoVigenteByInmueble(any())).thenReturn(false);
        when(estadoContratoRepository.findByNombre("Vigente")).thenReturn(Optional.of(estadoVigente));
        when(estadoInmuebleRepository.findByNombre("Alquilado")).thenReturn(Optional.of(estadoAlquilado));

        Contrato contratoGuardado = new Contrato();
        contratoGuardado.setId(1L);
        contratoGuardado.setInmueble(inmueble);
        contratoGuardado.setInquilino(inquilino);
        contratoGuardado.setFechaInicio(fechaInicio.format(DateTimeFormatter.ISO_LOCAL_DATE));
        contratoGuardado.setMonto(new BigDecimal("50000.00"));
        contratoGuardado.setPeriodoAumento(3);
        contratoGuardado.setEstadoContrato(estadoVigente);

        when(contratoRepository.save(any(Contrato.class))).thenReturn(contratoGuardado);
        when(inmuebleRepository.save(any(Inmueble.class))).thenReturn(inmueble);
        when(inquilinoRepository.save(any(Inquilino.class))).thenReturn(inquilino);
        when(alquilerRepository.save(any(Alquiler.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        ContratoDTO resultado = contratoService.crearContrato(contratoDTO);

        // Assert
        assertNotNull(resultado);
        
        // Verificar que NO se llamó al método de batch (solo se guardó un alquiler individual)
        verify(alquilerRepository, never()).saveAll(anyList());
        verify(alquilerRepository, times(1)).save(any(Alquiler.class));
    }
}
