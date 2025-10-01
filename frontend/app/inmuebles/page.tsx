'use client'

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Label } from "@/components/ui/label"
import { Building2, Plus, MapPin, User, Settings, Ruler, ArrowLeft } from "lucide-react"
import Link from "next/link"
import { Inmueble } from "@/types/Inmueble"
import { useEffect, useState } from "react"
import BACKEND_URL from "@/utils/backendURL"
import tiposInmueble from "@/utils/tiposInmuebles"
import { Propietario } from "@/types/Propietario"
import Loading from "@/components/loading"
import { Switch } from "@/components/ui/switch"
import { fetchWithToken } from "@/utils/functions/auth-functions/fetchWithToken"
import { ESTADOS_INMUEBLE } from "@/utils/constantes"

export default function InmueblesPage() {
  const [inmueblesBD, setInmueblesBD] = useState<Inmueble[]>([]);
  const [loading, setLoading] = useState(true);
  const [filtroInactivos, setFiltroInactivos] = useState(false);

  const [isEditInmuebleOpen, setIsEditInmuebleOpen] = useState(false)
  const [editingInmueble, setEditingInmueble] = useState({
    propietarioId: "",
    direccion: "",
    tiposInmuebleId: "",
    tipo: "",
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

    if(editingInmueble.estado === "3"){
      editingInmueble.esActivo = false
    }
    if(editingInmueble.estado !== "3"){
      editingInmueble.esActivo = true
    }

    try {
      const response = await fetchWithToken(`${BACKEND_URL}/inmuebles/${editingInmueble.id}`, {
        method: "PUT",
        body: JSON.stringify(editingInmueble),
      });

      // Actualizar el estado local
      setInmueblesBD((prev) =>
        prev.map((p) => (p.id === response.id ? response : p))
      );

      setIsEditInmuebleOpen(false);
      setEditingInmueble({
        propietarioId: "",
        direccion: "",
        tiposInmuebleId: "",
        tipo: "",
        estado: "",
        superficie: "",
        esAlquilado: true,
        esActivo: true,
      });
    } catch (error) {
      console.error("Error al actualizar inmueble:", error);
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
          <div className="mt-8">
            <Link href="/">
              <Button variant="outline">
                <ArrowLeft className="h-4 w-4 mr-2" />
                Volver a Inicio</Button>
            </Link>
          </div>
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-3xl font-bold text-foreground mb-2">{filtroInactivos? "Inmuebles Inactivos" : "Inmuebles Activos"}</h2>
              <p className="text-muted-foreground font-sans">
                Cantidad Actual: {inmueblesBD.length}
              </p>
            </div>
            <div className="flex items-center gap-4">
              <p className="text-gray-700">Ver Inactivos</p>
              <Switch
                checked={filtroInactivos} // true o false
                onCheckedChange={(checked) => setFiltroInactivos(checked)}
                className="data-[state=unchecked]:bg-gray-300"
              />
            </div>

            <Link href="/inmuebles/nuevo">
              <Button>
                <Plus className="h-4 w-4 mr-2" />
                Nuevo Inmueble
              </Button>
            </Link>
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
                    {tiposInmueble.find((tipo) => tipo.id === inmueble.tipoInmuebleId)?.nombre ||
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
                          ? `${propietariosBD.find((prop) => prop.id === inmueble.propietarioId)?.nombre} ${propietariosBD.find((prop) => prop.id === inmueble.propietarioId)?.apellido}`
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
                  <Button
                    disabled
                    variant="outline"
                    size="sm"
                    className="flex-1 bg-transparent"
                  >
                    Ver Detalles
                  </Button>
                  <Button
                    onClick={() => handleEditInmueble(inmueble)}
                    variant="outline"
                    size="sm"
                    className="flex-1 bg-transparent"
                  >
                    Editar
                  </Button>
                </div>

                <div className="pt-2 border-t border-border">
                  <Link href={`/inmuebles/${inmueble.id}/servicios`}>
                    <Button
                      disabled
                      variant="outline"
                      size="sm"
                      className="w-full bg-accent/10 hover:bg-accent/20 border-accent/30"
                    >
                      <Settings className="h-4 w-4 mr-2" />
                      Gestionar Servicios
                    </Button>
                  </Link>
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
                <Label htmlFor="edit-estado">Tipo de Inmueble</Label>
                <Select
                  value={editingInmueble.estado.toString()} // Valor actual del estado
                  onValueChange={(value) =>
                    setEditingInmueble({ ...editingInmueble, estado: value }) // Actualizar el estado
                  }
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Seleccionar tipo de inmueble" />
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
    </div>
  );
}