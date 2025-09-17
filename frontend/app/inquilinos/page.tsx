"use client"

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Building2, Search, Filter, Plus, User, Eye, Edit, Award as IdCard } from "lucide-react"
import Link from "next/link"
import { useState } from "react"
import HeaderAlquigest from "@/components/header"

interface Inquilino {
  id: number
  nombre: string
  dni: string
  telefono: string
  email: string
  estado: "Activo" | "Inactivo" | "Pendiente"
}

export default function InquilinosPage() {
  const [inquilinos, setInquilinos] = useState<Inquilino[]>([
    {
      id: 1,
      nombre: "María López García",
      dni: "32345678",
      telefono: "+34 655 111 222",
      email: "maria.lopez@email.com",
      estado: "Activo",
    },
    {
      id: 2,
      nombre: "Pedro Sánchez Ruiz",
      dni: "27654321",
      telefono: "+34 677 555 666",
      email: "pedro.sanchez@email.com",
      estado: "Inactivo",
    },
    {
      id: 3,
      nombre: "Ana Martín Torres",
      dni: "44123456",
      telefono: "+34 688 999 111",
      email: "ana.martin@email.com",
      estado: "Activo",
    },
  ])

  const [isNewInquilinoOpen, setIsNewInquilinoOpen] = useState(false)
  const [isEditInquilinoOpen, setIsEditInquilinoOpen] = useState(false)
  const [isDetailsOpen, setIsDetailsOpen] = useState(false)
  const [selectedInquilino, setSelectedInquilino] = useState<Inquilino | null>(null)
  const [formData, setFormData] = useState({
    nombre: "",
    dni: "",
    telefono: "",
    email: "",
    estado: "Activo" as "Activo" | "Inactivo" | "Pendiente",
  })

  const handleNewInquilino = () => {
    if (formData.nombre && formData.dni && formData.telefono && formData.email) {
      const newInquilino: Inquilino = {
        id: inquilinos.length + 1,
        ...formData,
      }
      setInquilinos([...inquilinos, newInquilino])
      setFormData({ nombre: "", dni: "", telefono: "", email: "", estado: "Activo" })
      setIsNewInquilinoOpen(false)
    }
  }

  const handleEditInquilino = () => {
    if (selectedInquilino && formData.nombre && formData.telefono && formData.email) {
      setInquilinos(
        inquilinos.map((inquilino) =>
          inquilino.id === selectedInquilino.id
            ? {
                ...inquilino,
                nombre: formData.nombre,
                telefono: formData.telefono,
                email: formData.email,
                estado: formData.estado,
              }
            : inquilino,
        ),
      )
      setIsEditInquilinoOpen(false)
      setSelectedInquilino(null)
    }
  }

  const openEditDialog = (inquilino: Inquilino) => {
    setSelectedInquilino(inquilino)
    setFormData({
      nombre: inquilino.nombre,
      dni: inquilino.dni,
      telefono: inquilino.telefono,
      email: inquilino.email,
      estado: inquilino.estado,
    })
    setIsEditInquilinoOpen(true)
  }

  const openDetailsDialog = (inquilino: Inquilino) => {
    setSelectedInquilino(inquilino)
    setIsDetailsOpen(true)
  }

  return (
    <div className="min-h-screen bg-background">
      {/* Header */}
      <HeaderAlquigest tituloPagina="Inquilinos" />

      <main className="container mx-auto px-6 py-8 pt-30">
        {/* Page Title */}
        <div className="mb-8 flex justify-between items-center">
          <div>
            <h2 className="text-3xl font-bold text-foreground mb-2">Información de Inquilinos</h2>
            <p className="text-muted-foreground font-sans">
              Administra la información de los inquilinos y contratos de arrendamiento
            </p>
          </div>

          {/* New Inquilino BOTÓN Despliega un modal */}
          <div>
            <Dialog open={isNewInquilinoOpen} onOpenChange={setIsNewInquilinoOpen}>
              <DialogTrigger asChild>
                <Button>
                  <Plus className="h-4 w-4 mr-2" />
                  Nuevo Inquilino
                </Button>
              </DialogTrigger>
              <DialogContent className="sm:max-w-md">
                <DialogHeader>
                  <DialogTitle>Registrar Nuevo Inquilino</DialogTitle>
                </DialogHeader>
                <div className="space-y-4">
                  <div>
                    <Label htmlFor="nombre">Nombre completo</Label>
                    <Input
                      id="nombre"
                      value={formData.nombre}
                      onChange={(e) => setFormData({ ...formData, nombre: e.target.value })}
                      placeholder="Ingrese el nombre completo"
                    />
                  </div>
                  <div>
                    <Label htmlFor="dni">DNI</Label>
                    <Input
                      id="dni"
                      value={formData.dni}
                      onChange={(e) => setFormData({ ...formData, dni: e.target.value })}
                      placeholder="Ingrese el DNI"
                    />
                  </div>
                  <div>
                    <Label htmlFor="telefono">Teléfono</Label>
                    <Input
                      id="telefono"
                      value={formData.telefono}
                      onChange={(e) => setFormData({ ...formData, telefono: e.target.value })}
                      placeholder="Ingrese el teléfono"
                    />
                  </div>
                  <div>
                    <Label htmlFor="email">Email</Label>
                    <Input
                      id="email"
                      type="email"
                      value={formData.email}
                      onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                      placeholder="Ingrese el email"
                    />
                  </div>
                  <div>
                    <Label htmlFor="estado">Estado</Label>
                    <Select
                      value={formData.estado}
                      onValueChange={(value: "Activo" | "Inactivo" | "Pendiente") =>
                        setFormData({ ...formData, estado: value })
                      }
                    >
                      <SelectTrigger>
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="Activo">Activo</SelectItem>
                        <SelectItem value="Inactivo">Inactivo</SelectItem>
                        <SelectItem value="Pendiente">Pendiente</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                  <div className="flex gap-2 pt-4">
                    <Button onClick={handleNewInquilino} className="flex-1">
                      Registrar Inquilino
                    </Button>
                    <Button variant="outline" onClick={() => setIsNewInquilinoOpen(false)} className="flex-1">
                      Cancelar
                    </Button>
                  </div>
                </div>
              </DialogContent>
            </Dialog>
          </div>
        </div>

        {/* Search and Filters */}
        <div className="flex flex-col md:flex-row gap-4 mb-6">
          <div className="relative flex-1">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
            <Input placeholder="Buscar por nombre, DNI o email..." className="pl-10" />
          </div>
          <Button variant="outline">
            <Filter className="h-4 w-4 mr-2" />
            Filtros
          </Button>
        </div>

        {/* Inquilinos List */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <User className="h-5 w-5" />
              Lista de Inquilinos ({inquilinos.length})
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-2">
              {inquilinos.map((inquilino) => (
                <div
                  key={inquilino.id}
                  className="flex items-center justify-between p-4 border border-border rounded-lg hover:bg-muted/50 transition-colors"
                >
                  <div className="flex items-center space-x-4 flex-1">
                    <div className="p-2 bg-secondary/10 rounded-full">
                      <User className="h-4 w-4 text-secondary" />
                    </div>
                    <div className="flex-1 min-w-0">
                      <div className="flex items-center gap-3">
                        <h3 className="font-medium text-foreground truncate">{inquilino.nombre}</h3>
                        <div className="flex items-center text-sm text-muted-foreground">
                          <IdCard className="h-3 w-3 mr-1" />
                          {inquilino.dni}
                        </div>
                        <Badge
                          variant={inquilino.estado === "Activo" ? "default" : "secondary"}
                          className={inquilino.estado === "Activo" ? "bg-accent" : ""}
                        >
                          {inquilino.estado}
                        </Badge>
                      </div>
                    </div>
                  </div>
                  <div className="flex gap-2">
                    <Button variant="outline" size="sm" onClick={() => openDetailsDialog(inquilino)}>
                      <Eye className="h-4 w-4 mr-1" />
                      Ver Detalles
                    </Button>
                    <Button variant="outline" size="sm" onClick={() => openEditDialog(inquilino)}>
                      <Edit className="h-4 w-4 mr-1" />
                      Editar
                    </Button>
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>

        {/* Details Dialog */}
        <Dialog open={isDetailsOpen} onOpenChange={setIsDetailsOpen}>
          <DialogContent className="sm:max-w-md">
            <DialogHeader>
              <DialogTitle>Detalles del Inquilino</DialogTitle>
            </DialogHeader>
            {selectedInquilino && (
              <div className="space-y-4">
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <Label className="text-sm font-medium text-muted-foreground">Nombre</Label>
                    <p className="text-sm font-medium">{selectedInquilino.nombre}</p>
                  </div>
                  <div>
                    <Label className="text-sm font-medium text-muted-foreground">DNI</Label>
                    <p className="text-sm font-medium">{selectedInquilino.dni}</p>
                  </div>
                  <div>
                    <Label className="text-sm font-medium text-muted-foreground">Teléfono</Label>
                    <p className="text-sm font-medium">{selectedInquilino.telefono}</p>
                  </div>
                  <div>
                    <Label className="text-sm font-medium text-muted-foreground">Email</Label>
                    <p className="text-sm font-medium">{selectedInquilino.email}</p>
                  </div>
                  <div>
                    <Label className="text-sm font-medium text-muted-foreground">Estado</Label>
                    <Badge
                      variant={selectedInquilino.estado === "Activo" ? "default" : "secondary"}
                      className={selectedInquilino.estado === "Activo" ? "bg-accent" : ""}
                    >
                      {selectedInquilino.estado}
                    </Badge>
                  </div>
                </div>
                <Button onClick={() => setIsDetailsOpen(false)} className="w-full">
                  Cerrar
                </Button>
              </div>
            )}
          </DialogContent>
        </Dialog>

        {/* Edit Dialog */}
        <Dialog open={isEditInquilinoOpen} onOpenChange={setIsEditInquilinoOpen}>
          <DialogContent className="sm:max-w-md">
            <DialogHeader>
              <DialogTitle>Editar Inquilino</DialogTitle>
            </DialogHeader>
            <div className="space-y-4">
              <div>
                <Label htmlFor="edit-nombre">Nombre completo</Label>
                <Input
                  id="edit-nombre"
                  value={formData.nombre}
                  onChange={(e) => setFormData({ ...formData, nombre: e.target.value })}
                  placeholder="Ingrese el nombre completo"
                />
              </div>
              <div>
                <Label htmlFor="edit-dni">DNI</Label>
                <Input
                  id="edit-dni"
                  value={formData.dni}
                  disabled
                  className="bg-muted"
                  placeholder="El DNI no se puede modificar"
                />
              </div>
              <div>
                <Label htmlFor="edit-telefono">Teléfono</Label>
                <Input
                  id="edit-telefono"
                  value={formData.telefono}
                  onChange={(e) => setFormData({ ...formData, telefono: e.target.value })}
                  placeholder="Ingrese el teléfono"
                />
              </div>
              <div>
                <Label htmlFor="edit-email">Email</Label>
                <Input
                  id="edit-email"
                  type="email"
                  value={formData.email}
                  onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                  placeholder="Ingrese el email"
                />
              </div>
              <div>
                <Label htmlFor="edit-estado">Estado</Label>
                <Select
                  value={formData.estado}
                  onValueChange={(value: "Activo" | "Inactivo" | "Pendiente") =>
                    setFormData({ ...formData, estado: value })
                  }
                >
                  <SelectTrigger>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="Activo">Activo</SelectItem>
                    <SelectItem value="Inactivo">Inactivo</SelectItem>
                    <SelectItem value="Pendiente">Pendiente</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              <div className="flex gap-2 pt-4">
                <Button onClick={handleEditInquilino} className="flex-1">
                  Guardar Cambios
                </Button>
                <Button variant="outline" onClick={() => setIsEditInquilinoOpen(false)} className="flex-1">
                  Cancelar
                </Button>
              </div>
            </div>
          </DialogContent>
        </Dialog>

        {/* Navigation Back */}
        <div className="mt-8">
          <Link href="/">
            <Button variant="outline">← Volver al Panel Principal</Button>
          </Link>
        </div>
      </main>
    </div>
  )
}
