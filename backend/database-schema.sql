-- ============================================
-- SCRIPT DE CREACIÓN DE BASE DE DATOS - ALQUIGEST
-- Base de datos: PostgreSQL (Supabase)
-- ============================================

-- ============================================
-- TABLA: roles
-- Descripción: Roles de usuario en el sistema
-- ============================================
CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

-- ============================================
-- TABLA: usuarios
-- Descripción: Usuarios del sistema
-- ============================================
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(150) UNIQUE,
    es_activo BOOLEAN NOT NULL DEFAULT true
);

-- ============================================
-- TABLA: usuario_roles
-- Descripción: Relación muchos a muchos entre usuarios y roles
-- ============================================
CREATE TABLE IF NOT EXISTS usuario_roles (
    usuario_id BIGINT NOT NULL,
    rol_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, rol_id),
    CONSTRAINT fk_usuario_roles_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_usuario_roles_rol FOREIGN KEY (rol_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- ============================================
-- TABLA: propietarios
-- Descripción: Propietarios de inmuebles
-- ============================================
CREATE TABLE IF NOT EXISTS propietarios (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    dni VARCHAR(20) NOT NULL UNIQUE,
    telefono VARCHAR(20),
    email VARCHAR(100) UNIQUE,
    direccion VARCHAR(100),
    barrio VARCHAR(100),
    es_activo BOOLEAN NOT NULL DEFAULT true,
    created_at VARCHAR(50),
    updated_at VARCHAR(50)
);

-- ============================================
-- TABLA: inquilinos
-- Descripción: Inquilinos de las propiedades
-- ============================================
CREATE TABLE IF NOT EXISTS inquilinos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    cuil VARCHAR(20) UNIQUE,
    telefono VARCHAR(20),
    barrio VARCHAR(100),
    esta_alquilando BOOLEAN DEFAULT false,
    es_activo BOOLEAN DEFAULT true,
    created_at VARCHAR(50),
    updated_at VARCHAR(50)
);

-- ============================================
-- TABLA: tipo_inmueble
-- Descripción: Tipos de inmuebles (casa, departamento, local, etc.)
-- ============================================
CREATE TABLE IF NOT EXISTS tipo_inmueble (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

-- ============================================
-- TABLA: estado_inmueble
-- Descripción: Estados de inmuebles (disponible, alquilado, en mantenimiento, etc.)
-- ============================================
CREATE TABLE IF NOT EXISTS estado_inmueble (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL UNIQUE
);

-- ============================================
-- TABLA: inmuebles
-- Descripción: Propiedades en alquiler
-- ============================================
CREATE TABLE IF NOT EXISTS inmuebles (
    id BIGSERIAL PRIMARY KEY,
    propietario_id BIGINT NOT NULL,
    direccion VARCHAR(100) NOT NULL,
    tipo_inmueble_id BIGINT,
    estado INTEGER NOT NULL,
    superficie NUMERIC(10, 2),
    es_alquilado BOOLEAN NOT NULL DEFAULT false,
    es_activo BOOLEAN NOT NULL DEFAULT true,
    created_at VARCHAR(50),
    updated_at VARCHAR(50),
    CONSTRAINT fk_inmueble_propietario FOREIGN KEY (propietario_id) REFERENCES propietarios(id) ON DELETE RESTRICT,
    CONSTRAINT fk_inmueble_tipo FOREIGN KEY (tipo_inmueble_id) REFERENCES tipo_inmueble(id) ON DELETE SET NULL
);

-- ============================================
-- TABLA: estado_contrato
-- Descripción: Estados de contratos (activo, vencido, cancelado, etc.)
-- ============================================
CREATE TABLE IF NOT EXISTS estado_contrato (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

-- ============================================
-- TABLA: contratos
-- Descripción: Contratos de alquiler
-- ============================================
CREATE TABLE IF NOT EXISTS contratos (
    id BIGSERIAL PRIMARY KEY,
    inmueble BIGINT NOT NULL,
    inquilino BIGINT NOT NULL,
    fecha_inicio VARCHAR(50),
    fecha_fin VARCHAR(50),
    monto NUMERIC(12, 2),
    porcentaje_aumento NUMERIC(5, 2),
    estado INTEGER NOT NULL,
    aumenta_con_icl BOOLEAN NOT NULL DEFAULT true,
    pdf_path VARCHAR(500),
    periodo_aumento INTEGER,
    fecha_aumento VARCHAR(50),
    created_at VARCHAR(50),
    updated_at VARCHAR(50),
    CONSTRAINT fk_contrato_inmueble FOREIGN KEY (inmueble) REFERENCES inmuebles(id) ON DELETE RESTRICT,
    CONSTRAINT fk_contrato_inquilino FOREIGN KEY (inquilino) REFERENCES inquilinos(id) ON DELETE RESTRICT,
    CONSTRAINT fk_contrato_estado FOREIGN KEY (estado) REFERENCES estado_contrato(id) ON DELETE RESTRICT
);

-- ============================================
-- TABLA: tipo_servicio
-- Descripción: Tipos de servicios (luz, gas, agua, internet, etc.)
-- ============================================
CREATE TABLE IF NOT EXISTS tipo_servicio (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    created_at VARCHAR(50),
    updated_at VARCHAR(50)
);

-- ============================================
-- TABLA: servicio_x_contrato
-- Descripción: Servicios asociados a cada contrato
-- ============================================
CREATE TABLE IF NOT EXISTS servicio_x_contrato (
    id SERIAL PRIMARY KEY,
    contrato_id BIGINT NOT NULL,
    tipo_servicio_id INTEGER NOT NULL,
    nro_cuenta VARCHAR(50),
    nro_contrato VARCHAR(50),
    es_de_inquilino BOOLEAN NOT NULL DEFAULT false,
    es_anual BOOLEAN NOT NULL DEFAULT false,
    es_activo BOOLEAN NOT NULL DEFAULT true,
    created_at VARCHAR(50),
    updated_at VARCHAR(50),
    CONSTRAINT fk_servicio_contrato FOREIGN KEY (contrato_id) REFERENCES contratos(id) ON DELETE CASCADE,
    CONSTRAINT fk_servicio_tipo FOREIGN KEY (tipo_servicio_id) REFERENCES tipo_servicio(id) ON DELETE RESTRICT
);

-- ============================================
-- TABLA: pago_servicio
-- Descripción: Pagos de servicios
-- ============================================
CREATE TABLE IF NOT EXISTS pago_servicio (
    id SERIAL PRIMARY KEY,
    servicio_x_contrato_id INTEGER NOT NULL,
    periodo VARCHAR(7),
    fecha_pago VARCHAR(50),
    esta_pagado BOOLEAN NOT NULL DEFAULT false,
    esta_vencido BOOLEAN NOT NULL DEFAULT false,
    pdf_path VARCHAR(500),
    medio_pago VARCHAR(50),
    monto NUMERIC(12, 2),
    created_at VARCHAR(50),
    updated_at VARCHAR(50),
    CONSTRAINT fk_pago_servicio_contrato FOREIGN KEY (servicio_x_contrato_id) REFERENCES servicio_x_contrato(id) ON DELETE CASCADE
);

-- ============================================
-- TABLA: configuracion_pago_servicio
-- Descripción: Configuración de generación automática de pagos de servicios
-- ============================================
CREATE TABLE IF NOT EXISTS configuracion_pago_servicio (
    id SERIAL PRIMARY KEY,
    servicio_x_contrato_id INTEGER NOT NULL UNIQUE,
    fecha_inicio VARCHAR(50),
    fecha_fin VARCHAR(50),
    ultimo_pago_generado VARCHAR(50),
    proximo_pago VARCHAR(50),
    es_activo BOOLEAN NOT NULL DEFAULT true,
    created_at VARCHAR(50),
    updated_at VARCHAR(50),
    CONSTRAINT fk_config_pago_servicio FOREIGN KEY (servicio_x_contrato_id) REFERENCES servicio_x_contrato(id) ON DELETE CASCADE
);

-- ============================================
-- TABLA: motivo_cancelacion
-- Descripción: Motivos de cancelación de contratos
-- ============================================
CREATE TABLE IF NOT EXISTS motivo_cancelacion (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(500)
);

-- ============================================
-- TABLA: cancelacion_contrato
-- Descripción: Cancelaciones de contratos
-- ============================================
CREATE TABLE IF NOT EXISTS cancelacion_contrato (
    id BIGSERIAL PRIMARY KEY,
    contrato_id BIGINT NOT NULL UNIQUE,
    fecha_cancelacion VARCHAR(50) NOT NULL,
    motivo_cancelacion_id INTEGER NOT NULL,
    observaciones VARCHAR(1000),
    created_at VARCHAR(50),
    updated_at VARCHAR(50),
    CONSTRAINT fk_cancelacion_contrato FOREIGN KEY (contrato_id) REFERENCES contratos(id) ON DELETE RESTRICT,
    CONSTRAINT fk_cancelacion_motivo FOREIGN KEY (motivo_cancelacion_id) REFERENCES motivo_cancelacion(id) ON DELETE RESTRICT
);

-- ============================================
-- TABLA: configuracion_sistema
-- Descripción: Configuraciones generales del sistema
-- ============================================
CREATE TABLE IF NOT EXISTS configuracion_sistema (
    id SERIAL PRIMARY KEY,
    clave VARCHAR(100) NOT NULL UNIQUE,
    valor VARCHAR(500),
    descripcion VARCHAR(255),
    created_at VARCHAR(50),
    updated_at VARCHAR(50)
);

-- ============================================
-- ÍNDICES PARA MEJORAR EL RENDIMIENTO
-- ============================================

-- Índices para búsquedas frecuentes
CREATE INDEX IF NOT EXISTS idx_propietarios_dni ON propietarios(dni);
CREATE INDEX IF NOT EXISTS idx_propietarios_email ON propietarios(email);
CREATE INDEX IF NOT EXISTS idx_inquilinos_cuil ON inquilinos(cuil);
CREATE INDEX IF NOT EXISTS idx_inmuebles_propietario ON inmuebles(propietario_id);
CREATE INDEX IF NOT EXISTS idx_inmuebles_estado ON inmuebles(estado);
CREATE INDEX IF NOT EXISTS idx_contratos_inmueble ON contratos(inmueble);
CREATE INDEX IF NOT EXISTS idx_contratos_inquilino ON contratos(inquilino);
CREATE INDEX IF NOT EXISTS idx_contratos_estado ON contratos(estado);
CREATE INDEX IF NOT EXISTS idx_servicio_contrato ON servicio_x_contrato(contrato_id);
CREATE INDEX IF NOT EXISTS idx_pago_servicio_contrato ON pago_servicio(servicio_x_contrato_id);
CREATE INDEX IF NOT EXISTS idx_pago_servicio_periodo ON pago_servicio(periodo);

-- ============================================
-- DATOS INICIALES
-- ============================================

-- Insertar roles por defecto
INSERT INTO roles (nombre) VALUES ('ROLE_ADMIN'), ('ROLE_USER')
ON CONFLICT (nombre) DO NOTHING;

-- Insertar estados de contrato por defecto
INSERT INTO estado_contrato (nombre) VALUES
    ('Activo'),
    ('Vencido'),
    ('Cancelado'),
    ('Pendiente')
ON CONFLICT (nombre) DO NOTHING;

-- Insertar estados de inmueble por defecto
INSERT INTO estado_inmueble (nombre) VALUES
    ('Disponible'),
    ('Alquilado'),
    ('En Mantenimiento'),
    ('No Disponible')
ON CONFLICT (nombre) DO NOTHING;

-- Insertar tipos de inmueble por defecto
INSERT INTO tipo_inmueble (nombre) VALUES
    ('Casa'),
    ('Departamento'),
    ('Local Comercial'),
    ('Oficina'),
    ('Galpón'),
    ('Terreno')
ON CONFLICT (nombre) DO NOTHING;

-- Insertar tipos de servicio por defecto
INSERT INTO tipo_servicio (nombre) VALUES
    ('Luz'),
    ('Gas'),
    ('Agua'),
    ('Internet'),
    ('Cable'),
    ('Teléfono'),
    ('Expensas'),
    ('ABL'),
    ('Seguro')
ON CONFLICT (nombre) DO NOTHING;

-- Insertar motivos de cancelación por defecto
INSERT INTO motivo_cancelacion (nombre, descripcion) VALUES
    ('Vencimiento Natural', 'El contrato llegó a su fecha de finalización'),
    ('Incumplimiento del Inquilino', 'El inquilino no cumplió con las obligaciones contractuales'),
    ('Incumplimiento del Propietario', 'El propietario no cumplió con las obligaciones contractuales'),
    ('Acuerdo Mutuo', 'Ambas partes acordaron finalizar el contrato'),
    ('Venta de Propiedad', 'La propiedad fue vendida'),
    ('Necesidad del Propietario', 'El propietario necesita la propiedad para uso propio'),
    ('Otro', 'Otro motivo no especificado')
ON CONFLICT (nombre) DO NOTHING;

-- ============================================
-- COMENTARIOS EN LAS TABLAS
-- ============================================

COMMENT ON TABLE usuarios IS 'Usuarios del sistema con autenticación';
COMMENT ON TABLE roles IS 'Roles de usuario para control de acceso';
COMMENT ON TABLE propietarios IS 'Propietarios de inmuebles';
COMMENT ON TABLE inquilinos IS 'Inquilinos que alquilan propiedades';
COMMENT ON TABLE inmuebles IS 'Propiedades disponibles para alquiler';
COMMENT ON TABLE contratos IS 'Contratos de alquiler entre propietarios e inquilinos';
COMMENT ON TABLE servicio_x_contrato IS 'Servicios asociados a cada contrato';
COMMENT ON TABLE pago_servicio IS 'Pagos de servicios de cada contrato';
COMMENT ON TABLE cancelacion_contrato IS 'Registro de cancelaciones de contratos';

