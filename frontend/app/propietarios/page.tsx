"use client"

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Building2, Search, Filter, Plus, Phone, Mail, Building, User, Edit, MapPin } from "lucide-react"
import Link from "next/link"
import { useEffect, useState } from "react"
import HeaderAlquigest from "@/components/header"
import { Propietario } from "@/types/Propietario"
import BACKEND_URL from "@/utils/backendURL"
import NuevoPropietarioModal from "./nuevoPropietarioModal"
import Loading from "@/components/loading"

export default function PropietariosPage() {

  //DATOS REALES
  const [propietariosBD, setPropietariosBD] = useState<Propietario[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    console.log("Ejecutando fetch de propietarios...");

    fetch(`${BACKEND_URL}/propietarios`)
      .then((res) => {
        console.log("Respuesta recibida del backend:", res);
        if (!res.ok) {
          throw new Error(`Error HTTP: ${res.status}`);
        }
        return res.json();
      })
      .then((data) => {
        console.log("Datos parseados del backend:", data);
        setPropietariosBD(data);
        setLoading(false);
      })
      .catch((err) => {
        console.error("Error al traer propietarios:", err);
        setLoading(false);
      });
  }, []);

  const [isEditOwnerOpen, setIsEditOwnerOpen] = useState(false)
  const [editingOwner, setEditingOwner] = useState({
    nombre: "",
    apellido: "",
    dni: "",
    telefono: "",
    email: "",
    direccion: "",
    esActivo: true,
  })

  const [newOwner, setNewOwner] = useState({
    nombre: "",
    apellido: "",
    dni: "",
    telefono: "",
    email: "",
    direccion: "",
    esActivo: "true",
  })
 
  const handleEditOwner = (owner) => {
    setEditingOwner(owner)
    setIsEditOwnerOpen(true)
  }

const handleUpdateOwner = async () => {
  try {
    const response = await fetch(`${BACKEND_URL}/propietarios/${editingOwner.id}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(editingOwner),
    });

    if (!response.ok) {
      throw new Error(`Error HTTP: ${response.status}`);
    }

    const updatedOwner = await response.json();

    // Actualizar el estado local
    setPropietariosBD((prev) =>
      prev.map((p) => (p.id === updatedOwner.id ? updatedOwner : p))
    );

    setIsEditOwnerOpen(false);
    setEditingOwner({
    nombre: "",
    apellido: "",
    dni: "",
    telefono: "",
    email: "",
    direccion: "",
    esActivo: true,
  });
  } catch (error) {
    console.error("Error al actualizar propietario:", error);
  }
};

  if (loading) return(
    <div>
      <Loading text="Cargando propietarios..." tituloHeader="Propietarios"/>
    </div>
  )

  return (
    <div className="min-h-screen bg-background">
      {/* Header */}
      <HeaderAlquigest tituloPagina="Propietarios"/>

      <main className="container mx-auto px-6 py-8 pt-30">
        {/* Page Title */}
        <div className="mb-8 flex flex-col gap-5">
          <div className="mt-8">
            <Link href="/">
              <Button variant="outline">← Volver a Inicio</Button>
            </Link>
          </div>

          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-3xl font-bold text-foreground mb-2">Propietarios en el sistema</h2>
              <p className="text-muted-foreground text-sm md:text-xl font-serif">Actualmente el sistema cuenta con información de {propietariosBD.length} propietarios</p>
            </div>
              <NuevoPropietarioModal
                onPropietarioCreado={(nuevo) => setPropietariosBD(prev => [...prev, nuevo])}
              />
          </div>
        </div>


        {/* Owners Grid */}
        <div className="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-6">
          {propietariosBD.map((propietario) => (
            <Card key={propietario.id} className="hover:shadow-lg transition-shadow">
              <CardHeader>
                <div className="flex justify-between items-start">
                  <div className="flex items-center space-x-3">
                    <div className="p-2 bg-primary/10 rounded-full">
                      <User className="h-8 w-8 text-primary" />
                    </div>
                    <div>
                      <CardTitle className="text-lg">
                        {propietario.nombre} {propietario.apellido}
                      </CardTitle>
                      <p className="text-sm text-muted-foreground">DNI: {propietario.dni}</p>
                    </div>
                  </div>
                  <Badge
                    variant={propietario.esActivo === true? "default" : "secondary"}
                    className={propietario.esActivo === true ? "bg-accent" : ""}
                  >
                    {propietario.esActivo=== true ? "Activo" : "Inactivo"}
                  </Badge>
                </div>
              </CardHeader>

              <CardContent className="space-y-4">
                {/* Contact Info */}
                <div className="space-y-2">
                  <div className="flex items-center text-sm">
                    <Mail className="h-4 w-4 mr-2 text-muted-foreground" />
                    <span className="text-muted-foreground truncate">{propietario.email}</span>
                  </div>
                  <div className="flex items-center text-sm">
                    <Phone className="h-4 w-4 mr-2 text-muted-foreground" />
                    <span className="text-muted-foreground">{propietario.telefono}</span>
                  </div>
                  <div className="flex items-start text-sm">
                    <MapPin className="h-4 w-4 mr-2 text-muted-foreground mt-0.5" />
                    <span className="text-muted-foreground text-xs leading-relaxed">{propietario.direccion}</span>
                  </div>
                </div>

                {/* Properties Count 
                <div className="flex items-center justify-between">
                  <span className="text-sm text-muted-foreground">Propiedades:</span>
                  <div className="flex items-center font-semibold">
                    <Building className="h-4 w-4 mr-1" />
                    {propietario.propiedades}
                  </div>
                </div>
                */}

                {/* Actions */}
                <div className="flex gap-2 pt-2">
                  <Link href={`/propietarios/${propietario.id}`}>
                    <Button variant="outline" size="sm" className="flex-1 bg-transparent">
                      Ver Detalles
                    </Button>
                  </Link>
                  <Button
                    variant="outline"
                    size="sm"
                    className="flex-1 bg-transparent"
                    onClick={() => handleEditOwner(propietario)}
                  >
                    <Edit className="h-3 w-3 mr-1" />
                    Editar
                  </Button>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>

        <Dialog open={isEditOwnerOpen} onOpenChange={setIsEditOwnerOpen}>
          <DialogContent className="max-w-md">
            <DialogHeader>
              <DialogTitle>Editar Propietario</DialogTitle>
            </DialogHeader>

            {editingOwner && (
              <form
                className="space-y-4"
                onSubmit={(e) => {
                  e.preventDefault()
                  handleUpdateOwner()
                }}
              >
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <Label htmlFor="edit-nombre">Nombre</Label>
                    <Input
                      required
                      id="edit-nombre"
                      value={editingOwner.nombre}
                      onChange={(e) => setEditingOwner({ ...editingOwner, nombre: e.target.value })}
                    />
                  </div>
                  <div>
                    <Label htmlFor="edit-apellido">Apellido</Label>
                    <Input
                      required
                      id="edit-apellido"
                      value={editingOwner.apellido}
                      onChange={(e) => setEditingOwner({ ...editingOwner, apellido: e.target.value })}
                    />
                  </div>
                </div>

                <div>
                  <Label htmlFor="edit-dni">DNI</Label>
                  <Input id="edit-dni" value={editingOwner.dni} disabled className="bg-muted" />
                  <p className="text-xs text-muted-foreground mt-1">El DNI no se puede modificar</p>
                </div>

                <div>
                  <Label htmlFor="edit-telefono">Teléfono</Label>
                  <Input
                    id="edit-telefono"
                    type="tel"
                    pattern="^\d{10}$"
                    value={editingOwner.telefono}
                    onChange={(e) => setEditingOwner({ ...editingOwner, telefono: e.target.value })}
                    placeholder="351-4455667"
                  />
                </div>

                <div>
                  <Label htmlFor="edit-email">Email</Label>
                  <Input
                    required
                    id="edit-email"
                    type="email"
                    value={editingOwner.email}
                    onChange={(e) => setEditingOwner({ ...editingOwner, email: e.target.value })}
                  />
                </div>

                <div>
                  <Label htmlFor="edit-direccion">Dirección</Label>
                  <Input
                    id="edit-direccion"
                    value={editingOwner.direccion}
                    onChange={(e) => setEditingOwner({ ...editingOwner, direccion: e.target.value })}
                  />
                </div>

                <div>
                  <Label htmlFor="edit-estado">Estado</Label>
                  <Select
                    value={editingOwner.esActivo === true ? "true" : "false"}
                    onValueChange={(value) => setEditingOwner({ ...editingOwner, esActivo: value })}
                  >
                    <SelectTrigger>
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="true">Activo</SelectItem>
                      <SelectItem value="false">Inactivo</SelectItem>
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
                    onClick={() => setIsEditOwnerOpen(false)}
                    className="flex-1"
                  >
                    Cancelar
                  </Button>
                </div>
              </form>
            )}
          </DialogContent>
        </Dialog>

      </main>
    </div>
  )
}
