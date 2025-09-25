"use client"

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import {  Phone, User, Edit } from "lucide-react"
import Link from "next/link"
import { useEffect, useState } from "react"
import HeaderAlquigest from "@/components/header"
import BACKEND_URL from "@/utils/backendURL"
import Loading from "@/components/loading"
import NuevoInquilinoModal from "./nuevoInquilinoModal"
import { Inquilino } from "@/types/Inquilino"
import { fetchWithToken } from "@/utils/functions/auth-functions/fetchWithToken"

export default function InquilinosPage() {

  //DATOS REALES
  const [InquilinosBD, setInquilinosBD] = useState<Inquilino[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    console.log("Ejecutando fetch de Inquilinos...");

    const fetchInquilinos = async () => {
      try{
        console.log("Ejecutando fetch de inquilinos...")
          const data = await fetchWithToken(`${BACKEND_URL}/inquilinos`)
          console.log("Datos parseados del backend:", data)
          setInquilinosBD(data)
          setLoading(false)
      } catch(err) {
        console.log("Error al traer inqiilinos: ", err)
        setLoading(false)
      
      } finally {
        setLoading(false);
      }
    }
    fetchInquilinos()
  }, []);

  const [isEditInquilinoOpen, setIsEditInquilinoOpen] = useState(false)
  const [editingInquilino, setEditingInquilino] = useState({
    nombre: "",
    apellido: "",
    cuil: "",
    telefono: "",
    esActivo: true,
  })

 
  const handleEditInquilino = (inquilino) => {
    setEditingInquilino(inquilino)
    setIsEditInquilinoOpen(true)
  }

const handleUpdateInquilino = async () => {
  try {
        const response = await fetchWithToken(`${BACKEND_URL}/inquilinos/${editingInquilino.id}`, {
        method: "PUT",
        body: JSON.stringify(editingInquilino),
      });

    const updatedInquilino = await response.json();

    // Actualizar el estado local
    setInquilinosBD((prev) =>
      prev.map((p) => (p.id === updatedInquilino.id ? updatedInquilino : p))
    );

    setIsEditInquilinoOpen(false);
    setEditingInquilino({
    nombre: "",
    apellido: "",
    cuil: "",
    telefono: "",
    esActivo: true,
  });
  } catch (error) {
    console.error("Error al actualizar Inquilino:", error);
  }
};

  if (loading) return(
    <div>
      <Loading text="Cargando Inquilinos..." tituloHeader="Inquilinos"/>
    </div>
  )

  return (
    <div className="min-h-screen bg-background">
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
              <h2 className="text-3xl font-bold text-foreground mb-2">Inquilinos en el sistema</h2>
              <p className="text-muted-foreground text-sm md:text-xl font-sans">Actualmente el sistema cuenta con información de {InquilinosBD.length} Inquilinos</p>
            </div>
              <NuevoInquilinoModal
                onInquilinoCreado={(nuevo) => setInquilinosBD(prev => [...prev, nuevo])}
              />
          </div>
        </div>


        {/* Inquilinos Grid */}
        <div className="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-6">
          {InquilinosBD.map((inquilino) => (
            <Card key={inquilino.id} className="hover:shadow-lg transition-shadow">
              <CardHeader>
                <div className="flex justify-between items-start">
                  <div className="flex items-center space-x-3">
                    <div className="p-2 bg-primary/10 rounded-full">
                      <User className="h-8 w-8 text-primary" />
                    </div>
  
                    <div>
                      <CardTitle className="text-lg">
                        {inquilino.nombre} {inquilino.apellido}
                      </CardTitle>
                      <p className="text-sm text-muted-foreground">cuil: {inquilino.cuil}</p>
                    </div>
                
                  </div>
                  <Badge
                    variant={inquilino.esActivo === true? "default" : "secondary"}
                    className={inquilino.esActivo === true ? "bg-accent" : ""}
                  >
                    {inquilino.esActivo=== true ? "Activo" : "Inactivo"}
                  </Badge>
                </div>
              </CardHeader>

              <CardContent className="space-y-4">
                {/* Contact Info */}
                <div className="space-y-2">
                  <div className="flex items-center text-sm">
                    <Phone className="h-4 w-4 mr-2 text-muted-foreground" />
                    <span className="text-muted-foreground">{inquilino.telefono}</span>
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
                  <Link href={`/Inquilinos/${inquilino.id}`}>
                    <Button variant="outline" size="sm" className="flex-1 bg-transparent">
                      Ver Detalles
                    </Button>
                  </Link>
                  <Button
                    variant="outline"
                    size="sm"
                    className="flex-1 bg-transparent"
                    onClick={() => handleEditInquilino(inquilino)}
                  >
                    <Edit className="h-3 w-3 mr-1" />
                    Editar
                  </Button>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>

        <Dialog open={isEditInquilinoOpen} onOpenChange={setIsEditInquilinoOpen}>
          <DialogContent className="max-w-md">
            <DialogHeader>
              <DialogTitle>Editar Inquilino</DialogTitle>
            </DialogHeader>

            {editingInquilino && (
              <form
                className="space-y-4"
                onSubmit={(e) => {
                  e.preventDefault()
                  handleUpdateInquilino()
                }}
              >
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <Label htmlFor="edit-nombre">Nombre</Label>
                    <Input
                      required
                      id="edit-nombre"
                      value={editingInquilino.nombre}
                      onChange={(e) => setEditingInquilino({ ...editingInquilino, nombre: e.target.value })}
                    />
                  </div>
              
                  <div>
                    <Label htmlFor="edit-apellido">Apellido</Label>
                    <Input
                      required
                      id="edit-apellido"
                      value={editingInquilino.apellido}
                      onChange={(e) => setEditingInquilino({ ...editingInquilino, apellido: e.target.value })}
                    />
                  </div>
                  
                </div>

                <div>
                  <Label htmlFor="edit-cuil">cuil</Label>
                  <Input id="edit-cuil" value={editingInquilino.cuil} disabled className="bg-muted" />
                  <p className="text-xs text-muted-foreground mt-1">El cuil no se puede modificar</p>
                </div>

                <div>
                  <Label htmlFor="edit-telefono">Teléfono</Label>
                  <Input
                    id="edit-telefono"
                    type="tel"
                    pattern="^\d{10}$"
                    value={editingInquilino.telefono}
                    onChange={(e) => setEditingInquilino({ ...editingInquilino, telefono: e.target.value })}
                    placeholder="351-4455667"
                  />
                </div>

                <div>
                  <Label htmlFor="edit-estado">Estado</Label>
                  <Select
                    value={editingInquilino.esActivo ? "true" : "false"}
                    onValueChange={(value) => setEditingInquilino({ ...editingInquilino, esActivo: value === "true" })}
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
                    onClick={() => setIsEditInquilinoOpen(false)}
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
