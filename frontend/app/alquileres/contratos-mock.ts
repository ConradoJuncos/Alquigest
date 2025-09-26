const contratosCompletos = [
    {
        id: 1,
        fechaInicio: "01-01-2025",
        fechaFin: "01-01-2027",
        fechaAumento: "01-10-2025",
        montoInicial: 85000.00,
        periodoAumento: 4,
        porcentajeAumento: null,
        aumentaConICL: true,
        estado: {
            nombre: "Vigente"
        },

        inmueble: {
            id: 1,
            direccion: "Colón 6553",
        },
        inquilino: {
            id: 1,
            nombre: "Juan",
            apellido: "Sosa",
            cuil: "20-34223345-0"
        },
        propietario: {
            id: 5,
            nombre: "Damián",
            apellido: "Aguirre",
            dni: "24332211"
        },
    },
    {
        id: 2,
        fechaInicio: "15-03-2024",
        fechaFin: "15-03-2026",
        fechaAumento: "15-09-2024",
        montoInicial: 95000.00,
        periodoAumento: 6,
        porcentajeAumento: 10,
        aumentaConICL: false,
        estado: {
            nombre: "Vigente"
        },

        inmueble: {
            id: 2,
            direccion: "Sagrada Familia 6743",
        },
        inquilino: {
            id: 2,
            nombre: "María",
            apellido: "Gómez",
            cuil: "27-33445566-1"
        },
        propietario: {
            id: 6,
            nombre: "Carla",
            apellido: "Fernández",
            dni: "30112233"
        },
    },
    {
        id: 3,
        fechaInicio: "01-06-2023",
        fechaFin: "01-06-2025",
        fechaAumento: "01-12-2023",
        montoInicial: 78000.00,
        periodoAumento: 12,
        porcentajeAumento: 15,
        aumentaConICL: false,
        estado: {
            nombre: "Finalizado"
        },

        inmueble: {
            id: 3,
            direccion: "Bv. San Juan 543",
        },
        inquilino: {
            id: 3,
            nombre: "Carlos",
            apellido: "Pérez",
            cuil: "23-44556677-2"
        },
        propietario: {
            id: 7,
            nombre: "Lucía",
            apellido: "Martínez",
            dni: "27123456"
        },
    }
];

export default contratosCompletos;