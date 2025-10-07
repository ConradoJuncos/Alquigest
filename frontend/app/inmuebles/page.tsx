'use client'

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Label } from "@/components/ui/label"
import { Building2, Plus, MapPin, User, Settings, Ruler, ArrowLeft, SquareX, SquareCheck } from "lucide-react"
import Link from "next/link"
import { Inmueble } from "@/types/Inmueble"
import { useEffect, useState } from "react"
import BACKEND_URL from "@/utils/backendURL"
import tiposInmueble from "@/utils/tiposInmuebles"
import { Propietario } from "@/types/Propietario"
import Loading from "@/components/loading"
import { Switch } from "@/components/ui/switch"
import { fetchWithToken } from "@/utils/functions/auth-functions/fetchWithToken"
import { ESTADOS_INMUEBLE, TIPOS_INMUEBLES } from "@/utils/constantes"
import auth from "@/utils/functions/auth-functions/auth"
import ModalError from "@/components/modal-error"

export default function InmueblesPage() {
  const [inmueblesBD, setInmueblesBD] = useState<Inmueble[]>([]);
  const [loading, setLoading] = useState(true);
  const [filtroInactivos, setFiltroInactivos] = useState(false);
  const [errorCarga, setErrorCarga] = useState("")
  const [mostrarError, setMostrarError] = useState(false)

  const [isEditInmuebleOpen, setIsEditInmuebleOpen] = useState(false)
  const [editingInmueble, setEditingInmueble] = useState({
    propietarioId: "",
    direccion: "",
    tipoInmuebleId: "",
    estado: "",
    superficie: "",
    esAlquilado: true,
    esActivo: true,
  })

  const handleEditInmueble = (inmueble: Inmueble) => {
    setEditingInmueble(inmueble)
    setIsEditInmuebleOpen(true)
  }

  const handleUpdateInmueble = async () => {
  try {
    let updatedInmueble;

    // Caso: inmueble en estado "3" → se desactiva
    if (editingInmueble.estado === "3") {
      editingInmueble.esActivo = false;
      console.log("Inactivando inmueble...");

      await fetchWithToken(
        `${BACKEND_URL}/inmuebles/${editingInmueble.id}/desactivar`,
        {
          method: "PATCH",
        }
      );

      // ✅ Manejar respuesta 204 (sin contenido)
      // Como el backend retorna 204, crear manualmente el inmueble actualizado
      updatedInmueble = {
        ...editingInmueble,
        esActivo: false
      };

    } else {
      // Caso normal: actualización de datos
      editingInmueble.esActivo = true;
      console.log("Actualizando datos del inmueble...");

      updatedInmueble = await fetchWithToken(
        `${BACKEND_URL}/inmuebles/${editingInmueble.id}`,
        {
          method: "PUT",
          body: JSON.stringify(editingInmueble),
        }
      );
    }

    // ✅ VALIDACIÓN AGREGADA
    if (!updatedInmueble || !updatedInmueble.id) {
      console.error("Respuesta inválida del servidor:", updatedInmueble);
      throw new Error("El servidor no retornó el inmueble actualizado");
    }

    // Actualizar el estado local
    setInmueblesBD((prev) =>
      prev.map((p) => (p.id === updatedInmueble.id ? updatedInmueble : p))
    );

    // Resetear formulario
    setIsEditInmuebleOpen(false);
    setEditingInmueble({
      propietarioId: "",
      direccion: "",
      tipoInmuebleId: "",
      estado: "",
      superficie: "",
      esAlquilado: true,
      esActivo: true,
    });

  } catch (error) {
      console.error("Error al Editar Inmueble:", error);
      setErrorCarga(error.message || "Error del servidor...");
      setMostrarError(true);
  }
};


  // PARA DATOS PROPIETARIOS
  const [propietariosBD, setPropietariosBD] = useState<Propietario[]>([]);

  useEffect(() => {
    const fetchPropietarios = async () => {
      try {
        console.log("Ejecutando fetch de propietarios...");
        const data = await fetchWithToken(`${BACKEND_URL}/propietarios`);
        console.log("Datos parseados del backend:", data);
        setPropietariosBD(data);
      } catch (err) {
        console.error("Error al traer propietarios:", err);
      }
    };

    fetchPropietarios();
  }, [filtroInactivos]);

  useEffect(() => {
    const fetchInmuebles = async () => {
      const url = filtroInactivos
        ? `${BACKEND_URL}/inmuebles/inactivos`
        : `${BACKEND_URL}/inmuebles/activos`;

      try {
        console.log(filtroInactivos ? "Filtro inactivos Activado" : "Cargando inmuebles activos...");
        const data: Inmueble[] = await fetchWithToken(url);
        console.log("Cantidad de inmuebles:", data.length);
        console.log("Datos:", data);

        setInmueblesBD(data);
        setLoading(false);
      } catch (error) {
        console.error("Error al obtener inmuebles:", error);
      }
    };

    fetchInmuebles();
  }, [filtroInactivos]);

  if (loading)
    return (
      <div>
        <Loading text="Cargando Inmuebles" tituloHeader="Inmuebles" />
      </div>
    );

  return (
    <div className="min-h-screen bg-background">
      <main className="container mx-auto px-6 py-8 pt-30">
        {/* Page Title */}
        <div className="mb-8 flex flex-col gap-5">
          <div className="mt-8 flex items-center justify-between">
            <Link href="/">
              <Button variant="outline">
                <ArrowLeft className="h-4 w-4 mr-2" />
                Volver a Inicio</Button>
            </Link>
            {auth.tienePermiso("crear_inmueble") ? (
              <Link href="/inmuebles/nuevo">
                <Button>
                  <Plus className="h-4 w-4 mr-2" />
                  Nuevo Inmueble
                </Button>
              </Link>
            ) : (
              <Button disabled className="opacity-60 cursor-not-allowed">
                <Plus className="h-4 w-4 mr-2" />
                Nuevo Inmueble
              </Button>
            )}
          </div>
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-3xl font-bold text-foreground mb-2">{filtroInactivos? "Inmuebles Inactivos" : "Inmuebles Activos"}</h2>
              <p className="text-muted-foreground font-sans">
                Cantidad Actual: {inmueblesBD.length}
              </p>
            </div>
              <Button
                onClick={() => setFiltroInactivos(!filtroInactivos)} 
                className="transition-all"
                variant="outline">
                {!filtroInactivos? <div className="flex gap-2 items-center"><SquareX/>Ver Inactivos</div> : <div className="flex gap-2 items-center"><SquareCheck/>Ver Activos</div> }
              </Button>
          </div>
        </div>

        {/* Properties Grid */}
        <div className="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-6">
          {inmueblesBD.map((inmueble) => (
            <Card key={inmueble.id} className="hover:shadow-lg transition-shadow">
              <CardHeader>
                <div className="flex justify-between items-start">
                  <div className="flex-1">
                    <CardTitle className="text-lg mb-2">{inmueble.direccion}</CardTitle>
                    <div className="flex items-center text-sm text-muted-foreground mb-2">
                      <MapPin className="h-4 w-4 mr-1" />
                      Córdoba
                    </div>
                  </div>
                  <div className="flex flex-col items-end space-y-1">
                    <Badge
                      variant={inmueble.esActivo == true ? "default" : "secondary"}
                      className={inmueble.esActivo == true ? "bg-accent" : ""}
                    >
                      {ESTADOS_INMUEBLE[inmueble.estado-1].nombre}
                    </Badge>
                  </div>
                </div>
              </CardHeader>

              <CardContent className="space-y-4">
                {/* TIPO */}
                <div className="flex items-center justify-between">
                  <div className="flex items-center">
                    <Building2 className="h-5 w-5 mr-3" />
                    <span className="text-sm text-muted-foreground">Tipo:</span>
                  </div>
                  <div className="flex items-center font-semibold">
                    {TIPOS_INMUEBLES.find((tipo) => tipo.id === inmueble.tipoInmuebleId)?.nombre ||
                      "Desconocido"}
                  </div>
                </div>

                {/* Propietario */}
                <div className="flex items-center justify-between">
                  <div className="flex items-center">
                    <User className="h-5 w-5 mr-3" />
                    <span className="text-sm text-muted-foreground">Propietario:</span>
                  </div>
                  <div className="flex items-center">
                    <Link
                      className="hover:text-yellow-700 transition-colors flex"
                      href={`/propietarios/${inmueble.propietarioId}`}
                    >
                      <User className="h-4 w-4 mr-1" />
                      <span className="text-sm font-medium">
                        {propietariosBD.find((prop) => prop.id === inmueble.propietarioId)
                          ? `${propietariosBD.find((prop) => prop.id === inmueble.propietarioId)?.apellido}, ${propietariosBD.find((prop) => prop.id === inmueble.propietarioId)?.nombre}`
                          : "Desconocido"}
                      </span>
                    </Link>
                  </div>
                </div>

                {/* Superficie */}
                <div className="flex items-center justify-between">
                  <div className="flex items-center">
                    <Ruler className="h-5 w-5 mr-3" />
                    <span className="text-sm text-muted-foreground">Superficie:</span>
                  </div>
                  <div className="flex items-center">
                    <span className="text-sm font-medium">
                      {inmueble.superficie ? `${inmueble.superficie} m²` : "No especificada"}
                    </span>
                  </div>
                </div>

                {/* Actions */}
                <div className="flex gap-2 pt-2">
                  <Link href={`/inmuebles/${inmueble.id}`}>
                    <Button
                      variant="outline"
                      size="sm"
                      className="flex-1 bg-transparent"
                    >
                      Ver Detalles
                    </Button>
                  </Link>
                  <Button
                    onClick={() => handleEditInmueble(inmueble)}
                    variant="outline"
                    size="sm"
                    className="flex-1 bg-transparent"
                    disabled={!auth.tienePermiso("modificar_inmueble")}
                  >
                    Editar
                  </Button>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      </main>

      <Dialog open={isEditInmuebleOpen} onOpenChange={setIsEditInmuebleOpen}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>Editar Inmueble</DialogTitle>
          </DialogHeader>

          {editingInmueble && (
            <form
              className="space-y-4"
              onSubmit={(e) => {
                e.preventDefault();
                handleUpdateInmueble();
              }}
            >
              <div>
                <Label htmlFor="edit-direccion">Dirección</Label>
                <Input
                  id="edit-direccion"
                  value={editingInmueble.direccion}
                  onChange={(e) =>
                    setEditingInmueble({ ...editingInmueble, direccion: e.target.value })
                  }
                />
              </div>

              <div>
                <Label htmlFor="edit-propietario">Propietario</Label>
                <Input
                  id="edit-propietario"
                  value={
                    propietariosBD.find((prop) => prop.id.toString() == editingInmueble.propietarioId)
                      ? `${propietariosBD.find((prop) => prop.id.toString() == editingInmueble.propietarioId)?.nombre} ${propietariosBD.find((prop) => prop.id.toString() == editingInmueble.propietarioId)?.apellido}`
                      : "Desconocido"
                  }
                  disabled
                  className="bg-muted"
                />
              </div>

              <div>
                <Label htmlFor="edit-superficie">Superficie</Label>
                <Input
                  id="edit-superficie"
                  value={editingInmueble.superficie}
                  onChange={(e) =>
                    setEditingInmueble({ ...editingInmueble, superficie: e.target.value })
                  }
                />
              </div>


              <div>
                <Label htmlFor="edit-tipoInmueble">Tipo de Inmueble</Label>
                <Select
                  value={editingInmueble.tipoInmuebleId.toString()} // Valor actual del estado
                  onValueChange={(value) =>
                    setEditingInmueble({ ...editingInmueble, tipoInmuebleId: value }) // Actualizar el estado
                  }
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Seleccionar tipo de inmueble" />
                  </SelectTrigger>
                  <SelectContent>
                    {TIPOS_INMUEBLES.map((tipo) => (
                      <SelectItem
                        key={tipo.id}
                        value={tipo.id.toString()} // Valor que se asignará al estado
                        className="overflow-auto text-ellipsis"
                      >
                        {tipo.nombre}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>

              <div>
                <Label htmlFor="edit-estado">Estado</Label>
                <Select
                  value={editingInmueble.estado.toString()} // Valor actual del estado
                  onValueChange={(value) =>
                    setEditingInmueble({ ...editingInmueble, estado: value }) // Actualizar el estado
                  }
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Seleccionar estado" />
                  </SelectTrigger>
                  <SelectContent>
                    {ESTADOS_INMUEBLE.map((estado) => (
                      <SelectItem
                        key={estado.id}
                        value={estado.id.toString()} // Valor que se asignará al estado
                        className="overflow-auto text-ellipsis"
                      >
                        {estado.nombre}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>

              <div className="flex gap-2 pt-4">
                <Button type="submit" className="flex-1">
                  Guardar Cambios
                </Button>
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => setIsEditInmuebleOpen(false)}
                  className="flex-1"
                >
                  Cancelar
                </Button>
              </div>
            </form>
          )}
        </DialogContent>
      </Dialog>

      {mostrarError && (
                    <ModalError
                      titulo="Error al crear Inmueble"
                      mensaje={errorCarga}
                      onClose={() => setMostrarError(false)}
                    />
                  )}
    </div>
  );
}